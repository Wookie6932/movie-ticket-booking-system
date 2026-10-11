package Presentation;
 
import Movie.Movie;
import Movie.MovieService;
import Movie.Showtime;
import Movie.ShowtimeService;
 
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
 
public class EditShowtimePanel extends JPanel {
 
    private final AdminDashboard adminDashboard;
    private final ShowtimeService showtimeService;
    private final MovieService movieService;
 
    private Showtime showtimeToEdit;
 
    private JComboBox<Movie> movieComboBox;
    private JTextField auditoriumField;
    private JTextField startTimeField;
    private JLabel errorLabel;
    private JLabel formTitleLabel;
 
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
 
    public EditShowtimePanel(AdminDashboard adminDashboard, ShowtimeService 
            showtimeService, MovieService movieService) {
        this.adminDashboard = adminDashboard;
        this.showtimeService = showtimeService;
        this.movieService = movieService;
        buildUI();
    }
 
    private void buildUI() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
 
        formTitleLabel = new JLabel("Add Showtime");
        formTitleLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(formTitleLabel, gbc);
 
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Movie:"), gbc);
 
        gbc.gridx = 1; gbc.weightx = 1.0;
        movieComboBox = new JComboBox<>();
        add(movieComboBox, gbc);
 
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        add(new JLabel("Auditorium ID:"), gbc);
 
        gbc.gridx = 1; gbc.weightx = 1.0;
        auditoriumField = new JTextField(25);
        add(auditoriumField, gbc);
 
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        add(new JLabel("Start Time:"), gbc);
 
        gbc.gridx = 1; gbc.weightx = 1.0;
        startTimeField = new JTextField(25);
        startTimeField.setToolTipText("Format: yyyy-MM-dd HH:mm  e.g. 2026-11-01 14:30");
        add(startTimeField, gbc);
 
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JLabel formatHint = new JLabel("Start time format: yyyy-MM-dd HH:mm  (e.g. 2026-11-01 14:30)");
        formatHint.setForeground(Color.GRAY);
        formatHint.setFont(new Font("Arial", Font.PLAIN, 11));
        add(formatHint, gbc);
 
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        add(errorLabel, gbc);
 
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonPanel.setBackground(Color.WHITE);
 
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> save());
 
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> adminDashboard.showShowtimeList());
 
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, gbc);
    }
 
    private void loadMovies() {
        movieComboBox.removeAllItems();
        try {
            List<Movie> movies = movieService.getAllMovies();
            for (Movie movie : movies) {
                movieComboBox.addItem(movie);
            }
        } catch (Exception e) {
            errorLabel.setText("Could not load movies: " + e.getMessage());
        }
    }
 
    public void prepareForAdd() {
        showtimeToEdit = null;
        formTitleLabel.setText("Add Showtime");
        loadMovies();
        auditoriumField.setText("");
        startTimeField.setText("");
        errorLabel.setText("");
    }
 
    public void prepareForEdit(Showtime showtime) {
        showtimeToEdit = showtime;
        formTitleLabel.setText("Edit Showtime");
        loadMovies();
 
        for (int i = 0; i < movieComboBox.getItemCount(); i++) {
            if (movieComboBox.getItemAt(i).getMovieId() == showtime.getMovieId()) {
                movieComboBox.setSelectedIndex(i);
                break;
            }
        }
 
        auditoriumField.setText(String.valueOf(showtime.getAuditoriumId()));
        startTimeField.setText(showtime.getStartTime().format(FORMATTER));
        errorLabel.setText("");
    }
 
    private void save() {
 
        Movie selectedMovie = (Movie) movieComboBox.getSelectedItem();
        String auditoriumStr = auditoriumField.getText().trim();
        String startTimeStr  = startTimeField.getText().trim();
 
        if (selectedMovie == null || auditoriumStr.isEmpty() || startTimeStr.isEmpty()) {
            errorLabel.setText("All fields are required.");
            return;
        }
 
        int auditoriumId;
        try {
            auditoriumId = Integer.parseInt(auditoriumStr);
            if (auditoriumId <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            errorLabel.setText("Auditorium ID must be a positive number.");
            return;
        }
 
        LocalDateTime startTime;
        try {
            startTime = LocalDateTime.parse(startTimeStr, FORMATTER);
        } catch (DateTimeParseException e) {
            errorLabel.setText("Invalid date format. Use yyyy-MM-dd HH:mm");
            return;
        }
 
        try {
            if (showtimeToEdit == null) {
                showtimeService.addShowtime(selectedMovie.getMovieId(), auditoriumId, startTime);
            } else {
                showtimeService.updateShowtime(showtimeToEdit.getShowtimeId(), 
                        selectedMovie.getMovieId(), auditoriumId, startTime);
            }
            adminDashboard.showShowtimeList();
 
        } catch (Exception e) {
            errorLabel.setText("Database error: " + e.getMessage());
        }
    }
}