package Presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

import Account.User;
import Database.MovieDAO;
import Database.ShowtimeDAO;
import Movie.Movie;
import Movie.Showtime;

public class CustomerDashboard extends JPanel {

    private final MainFrame mainFrame;
    private final User currentUser;

    private final MovieDAO movieDAO = new MovieDAO();
    private final ShowtimeDAO showtimeDAO = new ShowtimeDAO();

    private final DefaultListModel<Movie> movieListModel = new DefaultListModel<>();
    private final JList<Movie> movieList = new JList<>(movieListModel);

    private final JTextArea detailsArea = new JTextArea();

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

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername());
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        welcomeLabel.setForeground(Color.BLACK);
        rightPanel.add(welcomeLabel);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 15));
        logoutButton.setPreferredSize(new Dimension(100, 35));
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> mainFrame.showLogin());
        rightPanel.add(logoutButton);

        topBar.add(rightPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        movieList.setFont(new Font("Arial", Font.PLAIN, 18));
        movieList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane movieScrollPane = new JScrollPane(movieList);
        movieScrollPane.setPreferredSize(new Dimension(250, 400));

        detailsArea.setFont(new Font("Arial", Font.PLAIN, 16));
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setText("Select a movie to view details and showtimes.");

        JScrollPane detailsScrollPane = new JScrollPane(detailsArea);

        centerPanel.add(movieScrollPane, BorderLayout.WEST);
        centerPanel.add(detailsScrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        movieList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Movie selectedMovie = movieList.getSelectedValue();

                if (selectedMovie != null) {
                    loadMovieDetails(selectedMovie);
                }
            }
        });
    }

    private void loadMovies() {
        try {
            List<Movie> movies = movieDAO.getAllMovies();

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
            List<Showtime> showtimes =
                    showtimeDAO.getShowtimesByMovie(movie.getMovieId());

            StringBuilder details = new StringBuilder();

            details.append(movie.getTitle()).append("\n\n");

            details.append("Rating: ")
                    .append(movie.getRating())
                    .append("\n");

            details.append("Runtime: ")
                    .append(movie.getDurationMinutes())
                    .append(" minutes\n\n");

            details.append("Description:\n")
                    .append(movie.getDescription())
                    .append("\n\n");

            details.append("Showtimes:\n");

            if (showtimes.isEmpty()) {
                details.append("No active showtimes available.");
            } else {
                DateTimeFormatter formatter =
                        DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

                for (Showtime showtime : showtimes) {
                    details.append(
                            showtime.getStartTime().format(formatter)
                    );

                    details.append(" - Auditorium ")
                            .append(showtime.getAuditoriumId())
                            .append("\n");
                }
            }

            detailsArea.setText(details.toString());

        } catch (SQLException e) {
            detailsArea.setText("Unable to load showtimes.");
            e.printStackTrace();
        }
    }
}