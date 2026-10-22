package Presentation;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import Account.User;
import Movie.Movie;
import Movie.MovieService;
import Movie.Showtime;
import Movie.ShowtimeService;
import Reservation.ReservationService;
import Reservation.ShowtimeSeat;

public class CustomerDashboard extends JPanel {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

    private final MainFrame mainFrame;
    private final User currentUser;

    private final MovieService movieService = new MovieService();
    private final ShowtimeService showtimeService = new ShowtimeService();
    private final ReservationService reservationService = new ReservationService();

    private final DefaultListModel<Movie> movieListModel = new DefaultListModel<>();
    private final JList<Movie> movieList = new JList<>(movieListModel);

    private final JTextArea detailsArea = new JTextArea();

    private final DefaultTableModel showtimeTableModel =
            new DefaultTableModel(new String[]{"Date and Time", "Auditorium"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
    private final JTable showtimeTable = new JTable(showtimeTableModel);
    private final List<Showtime> currentShowtimes = new ArrayList<>();

    private final CardLayout rightLayout = new CardLayout();
    private final JPanel rightPanel = new JPanel(rightLayout);
    private SeatMapPanel seatMapPanel;
    private ReservationSummaryPanel summaryPanel;

    public CustomerDashboard(MainFrame mainFrame, User currentUser) {
        this.mainFrame = mainFrame;
        this.currentUser = currentUser;

        buildUI();
        loadMovies();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.GRAY);
        topBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Movie Ticket Booking System - Customer Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.BLACK);
        topBar.add(titleLabel, BorderLayout.WEST);

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        logoutPanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername());
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        welcomeLabel.setForeground(Color.BLACK);
        logoutPanel.add(welcomeLabel);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 15));
        logoutButton.setPreferredSize(new Dimension(100, 35));
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> mainFrame.showLogin());
        logoutPanel.add(logoutButton);

        topBar.add(logoutPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        movieList.setFont(new Font("Arial", Font.PLAIN, 18));
        movieList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane movieScrollPane = new JScrollPane(movieList);
        movieScrollPane.setPreferredSize(new Dimension(200, 400));

        detailsArea.setFont(new Font("Arial", Font.PLAIN, 15));
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setText("Select a movie to view details and showtimes.");

        JScrollPane detailsScrollPane = new JScrollPane(detailsArea);
        detailsScrollPane.setPreferredSize(new Dimension(340, 260));

        showtimeTable.setFont(new Font("Arial", Font.PLAIN, 14));
        showtimeTable.setRowHeight(24);
        showtimeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JLabel showtimeLabel = new JLabel("Showtimes (select one to choose seats)");
        showtimeLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel showtimePanel = new JPanel(new BorderLayout(0, 5));
        showtimePanel.setBackground(Color.WHITE);
        showtimePanel.add(showtimeLabel, BorderLayout.NORTH);
        showtimePanel.add(new JScrollPane(showtimeTable), BorderLayout.CENTER);

        JPanel middlePanel = new JPanel(new BorderLayout(0, 10));
        middlePanel.setBackground(Color.WHITE);
        middlePanel.add(detailsScrollPane, BorderLayout.NORTH);
        middlePanel.add(showtimePanel, BorderLayout.CENTER);

        seatMapPanel = new SeatMapPanel(this, reservationService);
        summaryPanel = new ReservationSummaryPanel(this, reservationService, currentUser);

        JLabel emptyLabel = new JLabel("Select a showtime to choose your seats.", SwingConstants.CENTER);
        emptyLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        rightPanel.add(emptyLabel, "EMPTY");
        rightPanel.add(seatMapPanel, "SEATS");
        rightPanel.add(summaryPanel, "SUMMARY");
        rightLayout.show(rightPanel, "EMPTY");

        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(middlePanel, BorderLayout.WEST);
        contentPanel.add(rightPanel, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centerPanel.add(movieScrollPane, BorderLayout.WEST);
        centerPanel.add(contentPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        movieList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Movie selectedMovie = movieList.getSelectedValue();

                if (selectedMovie != null) {
                    loadMovieDetails(selectedMovie);
                }
            }
        });
        
        showtimeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = showtimeTable.getSelectedRow();
 
                if (row >= 0 && row < currentShowtimes.size()) {
                    showSeatMap(currentShowtimes.get(row));
                }
            }
        });
    }

    private void loadMovies() {
        try {
            List<Movie> movies = movieService.getAllMovies();

            movieListModel.clear();

            for (Movie movie : movies) {
                movieListModel.addElement(movie);
            }

            if (movies.isEmpty()) {
                detailsArea.setText("No movies are currently available.");
            }

        } catch (SQLException e) {
            detailsArea.setText("Unable to load movies.");
            e.printStackTrace();
        }
    }

    private void loadMovieDetails(Movie movie) {
        try {
            List<Showtime> showtimes = showtimeService.getShowtimesByMovie(movie.getMovieId());

            detailsArea.setText(movie.getTitle() + "\n\n" 
                    + "Rating: " + movie.getRating() + "\n"
                    + "Runtime: " + movie.getDurationMinutes() + " minutes\n\n" 
                    + "Description:\n" + movie.getDescription());
            detailsArea.setCaretPosition(0);

            showtimeTableModel.setRowCount(0);
            currentShowtimes.clear();

            for (Showtime showtime : showtimes) {
                currentShowtimes.add(showtime);
                showtimeTableModel.addRow(new Object[]{
                    showtime.getStartTime().format(FORMATTER),
                    showtime.getAuditoriumId()
                });
            }

            rightLayout.show(rightPanel, "EMPTY");

        } catch (SQLException e) {
            detailsArea.setText("Unable to load showtimes.");
            e.printStackTrace();
        }
    }

    public void showSeatMap(Showtime showtime) {
        seatMapPanel.showSeatsFor(showtime);
        rightLayout.show(rightPanel, "SEATS");
    }

    public void showSummary(Showtime showtime, List<ShowtimeSeat> seats) {
        summaryPanel.showSummary(showtime, seats);
        rightLayout.show(rightPanel, "SUMMARY");
    }
}