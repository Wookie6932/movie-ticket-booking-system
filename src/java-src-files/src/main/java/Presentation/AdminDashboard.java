package Presentation;
 
import Account.User;
import Movie.Movie;
import Movie.MovieService;
import Movie.Showtime;
import Movie.ShowtimeService;
 
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
 
public class AdminDashboard extends JPanel {
    private final MainFrame mainFrame;
    private final User currentUser;
 
    private final MovieService movieService = new MovieService();
    private final ShowtimeService showtimeService = new ShowtimeService();
 
    private CardLayout contentLayout;
    private JPanel contentPanel;
 
    private DefaultTableModel movieTableModel;
    private JTable movieTable;

    private DefaultTableModel showtimeTableModel;
    private JTable showtimeTable;

    private EditMoviePanel addEditMoviePanel;
    private EditShowtimePanel addEditShowtimePanel;
 
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

    private static final int MOVIE_COL_ID = 0;
    private static final int MOVIE_COL_TITLE = 1;
    private static final int MOVIE_COL_RATING = 2;
    private static final int MOVIE_COL_DURATION = 3;
    private static final int MOVIE_COL_DESCRIPTION = 4;

    private static final int SHOW_COL_ID = 0;
    private static final int SHOW_COL_MOVIE = 1;
    private static final int SHOW_COL_AUDITORIUM = 2;
    private static final int SHOW_COL_START = 3;
    private static final int SHOW_COL_STATUS = 4;
    private static final int SHOW_COL_MOVIE_ID = 5;
 
    public AdminDashboard(MainFrame mainFrame, User currentUser) {
        this.mainFrame = mainFrame;
        this.currentUser = currentUser;
        buildUI();
        showMovieList();
    }
 
    private void buildUI() {
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.GRAY);
        topBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
 
        JLabel titleLabel = new JLabel("Movie Ticket Booking System - Admin Dashboard");
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

        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        navBar.setBackground(Color.LIGHT_GRAY);
 
        JButton moviesBtn = new JButton("Movies");
        JButton showtimesBtn = new JButton("Showtimes");
 
        moviesBtn.setFocusPainted(false);
        showtimesBtn.setFocusPainted(false);
 
        moviesBtn.addActionListener(e -> showMovieList());
        showtimesBtn.addActionListener(e -> showShowtimeList());
 
        navBar.add(moviesBtn);
        navBar.add(showtimesBtn);
 
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
 
        contentPanel.add(buildMovieListPanel(), "MOVIE_LIST");
        contentPanel.add(buildShowtimeListPanel(), "SHOWTIME_LIST");
 
        addEditMoviePanel = new EditMoviePanel(
                this, movieService);
        addEditShowtimePanel = new EditShowtimePanel(
                this, showtimeService, movieService);
 
        contentPanel.add(addEditMoviePanel, "MOVIE_FORM");
        contentPanel.add(addEditShowtimePanel, "SHOWTIME_FORM");
 
        JPanel center = new JPanel(new BorderLayout());
        center.add(navBar, BorderLayout.NORTH);
        center.add(contentPanel, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }
 
    private JPanel buildMovieListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
 
        String[] columns = {"ID", "Title", "Rating", "Duration (min)", "Description"};
        movieTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
 
        movieTable = new JTable(movieTableModel);
        movieTable.setFont(new Font("Arial", Font.PLAIN, 14));
        movieTable.setRowHeight(24);
        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
 
        movieTable.getColumnModel().getColumn(MOVIE_COL_ID).setMinWidth(0);
        movieTable.getColumnModel().getColumn(MOVIE_COL_ID).setMaxWidth(0);
 
        panel.add(new JScrollPane(movieTable), BorderLayout.CENTER);
 
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        btnPanel.setBackground(Color.WHITE);
 
        JButton addBtn = new JButton("Add Movie");
        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton refreshBtn = new JButton("Refresh");
 
        addBtn.addActionListener(e -> {
            addEditMoviePanel.prepareForAdd();
            contentLayout.show(contentPanel, "MOVIE_FORM");
        });
 
        editBtn.addActionListener(e -> {
            Movie selected = getSelectedMovie();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a movie to edit.");
                return;
            }
            addEditMoviePanel.prepareForEdit(selected);
            contentLayout.show(contentPanel, "MOVIE_FORM");
        });
 
        deleteBtn.addActionListener(e -> deleteSelectedMovie());
        refreshBtn.addActionListener(e -> loadMovies());
 
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);
 
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }
 
    private JPanel buildShowtimeListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
 
        String[] columns = {"ID", "Movie", "Auditorium", "Start Time", "Status", "Movie ID"};
        showtimeTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
 
        showtimeTable = new JTable(showtimeTableModel);
        showtimeTable.setFont(new Font("Arial", Font.PLAIN, 14));
        showtimeTable.setRowHeight(24);
        showtimeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
 
        showtimeTable.getColumnModel().getColumn(SHOW_COL_ID).setMinWidth(0);
        showtimeTable.getColumnModel().getColumn(SHOW_COL_ID).setMaxWidth(0);
        showtimeTable.getColumnModel().getColumn(SHOW_COL_MOVIE_ID).setMinWidth(0);
        showtimeTable.getColumnModel().getColumn(SHOW_COL_MOVIE_ID).setMaxWidth(0);
 
        panel.add(new JScrollPane(showtimeTable), BorderLayout.CENTER);
 
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        btnPanel.setBackground(Color.WHITE);
 
        JButton addBtn = new JButton("Add Showtime");
        JButton editBtn = new JButton("Edit Selected");
        JButton cancelBtn = new JButton("Cancel Selected");
        JButton refreshBtn = new JButton("Refresh");
 
        addBtn.addActionListener(e -> {
            addEditShowtimePanel.prepareForAdd();
            contentLayout.show(contentPanel, "SHOWTIME_FORM");
        });
 
        editBtn.addActionListener(e -> {
            Showtime selected = getSelectedShowtime();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a showtime to edit.");
                return;
            }
            addEditShowtimePanel.prepareForEdit(selected);
            contentLayout.show(contentPanel, "SHOWTIME_FORM");
        });
 
        cancelBtn.addActionListener(e -> cancelSelectedShowtime());
        refreshBtn.addActionListener(e -> loadShowtimes());
 
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(cancelBtn);
        btnPanel.add(refreshBtn);
 
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }
 
    private void loadMovies() {
        movieTableModel.setRowCount(0);
        try {
            List<Movie> movies = movieService.getAllMovies();
            for (Movie movie : movies) {
                movieTableModel.addRow(new Object[]{
                        movie.getMovieId(),
                        movie.getTitle(),
                        movie.getRating(),
                        movie.getDurationMinutes(),
                        movie.getDescription()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading movies: " + e.getMessage());
        }
    }
 
    private void loadShowtimes() {
        showtimeTableModel.setRowCount(0);
        try {
            List<Showtime> showtimes = showtimeService.getAllShowtimes();
            for (Showtime showtime : showtimes) {
                showtimeTableModel.addRow(new Object[]{
                        showtime.getShowtimeId(),
                        showtime.getMovieTitle(),
                        showtime.getAuditoriumId(),
                        showtime.getStartTime().format(FORMATTER),
                        showtime.getStatus(),
                        showtime.getMovieId()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading showtimes: " + e.getMessage());
        }
    }
 
    private Movie getSelectedMovie() {
        int row = movieTable.getSelectedRow();
        if (row == -1) return null;
        return new Movie(
                (int) movieTableModel.getValueAt(row, MOVIE_COL_ID),
                (String) movieTableModel.getValueAt(row, MOVIE_COL_TITLE),
                (String) movieTableModel.getValueAt(row, MOVIE_COL_DESCRIPTION),
                (int) movieTableModel.getValueAt(row, MOVIE_COL_DURATION),
                (String) movieTableModel.getValueAt(row, MOVIE_COL_RATING)
        );
    }
 
    private Showtime getSelectedShowtime() {
        int row = showtimeTable.getSelectedRow();
        if (row == -1) return null;
 
        String startTimeStr = (String) showtimeTableModel.getValueAt(
                row, SHOW_COL_START);
 
        return new Showtime(
                (int) showtimeTableModel.getValueAt(row, SHOW_COL_ID),
                (int) showtimeTableModel.getValueAt(row, SHOW_COL_MOVIE_ID),
                (int) showtimeTableModel.getValueAt(row, SHOW_COL_AUDITORIUM),
                java.time.LocalDateTime.parse(startTimeStr, FORMATTER),
                (String) showtimeTableModel.getValueAt(row, SHOW_COL_STATUS),
                (String) showtimeTableModel.getValueAt(row, SHOW_COL_MOVIE)
        );
    }
 
    private void deleteSelectedMovie() {
        Movie selected = getSelectedMovie();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a movie to delete.");
            return;
        }
 
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete \"" + selected.getTitle() + "\"? This cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
 
        if (confirm != JOptionPane.YES_OPTION) return;
 
        try {
            movieService.deleteMovie(selected.getMovieId());
            loadMovies();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting movie: " + e.getMessage());
        }
    }
 
    private void cancelSelectedShowtime() {
        Showtime selected = getSelectedShowtime();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a showtime to cancel.");
            return;
        }
 
        if ("CANCELED".equals(selected.getStatus())) {
            JOptionPane.showMessageDialog(this, "This showtime is already canceled.");
            return;
        }
 
        int confirm = JOptionPane.showConfirmDialog(this,
                "Cancel the showtime for \"" + selected.getMovieTitle()
                + "\" on " + selected.getStartTime().format(FORMATTER) + "?",
                "Confirm Cancel", JOptionPane.YES_NO_OPTION);
 
        if (confirm != JOptionPane.YES_OPTION) return;
 
        try {
            showtimeService.cancelShowtime(selected.getShowtimeId());
            loadShowtimes();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error canceling showtime: " + e.getMessage());
        }
    }
 
    public void showMovieList() {
        loadMovies();
        contentLayout.show(contentPanel, "MOVIE_LIST");
    }
 
    public void showShowtimeList() {
        loadShowtimes();
        contentLayout.show(contentPanel, "SHOWTIME_LIST");
    }
}