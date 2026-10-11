package Presentation;
 
import Movie.Movie;
import Movie.MovieService;
 
import javax.swing.*;
import java.awt.*;
 
public class EditMoviePanel extends JPanel {
    private final AdminDashboard adminDashboard;
    private final MovieService movieService;
 
    private Movie movieToEdit;
 
    private JTextField titleField;
    private JTextField ratingField;
    private JTextField durationField;
    private JTextArea descriptionArea;
    private JLabel errorLabel;
    private JLabel formTitleLabel;
 
    public EditMoviePanel(AdminDashboard adminDashboard, MovieService movieService) {
        this.adminDashboard = adminDashboard;
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
 
        formTitleLabel = new JLabel("Add Movie");
        formTitleLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(formTitleLabel, gbc);
 
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Title:"), gbc);
 
        gbc.gridx = 1; gbc.weightx = 1.0;
        titleField = new JTextField(25);
        add(titleField, gbc);
 
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        add(new JLabel("Rating:"), gbc);
 
        gbc.gridx = 1; gbc.weightx = 1.0;
        ratingField = new JTextField(25);
        ratingField.setToolTipText("e.g. G, PG, PG-13, R");
        add(ratingField, gbc);
 
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        add(new JLabel("Duration (minutes):"), gbc);
 
        gbc.gridx = 1; gbc.weightx = 1.0;
        durationField = new JTextField(25);
        add(durationField, gbc);
 
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(new JLabel("Description:"), gbc);
 
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        descriptionArea = new JTextArea(5, 25);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        add(new JScrollPane(descriptionArea), gbc);
 
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
        cancelButton.addActionListener(e -> adminDashboard.showMovieList());
 
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, gbc);
    }
 
    public void prepareForAdd() {
        movieToEdit = null;
        formTitleLabel.setText("Add Movie");
        clearFields();
    }
 
    public void prepareForEdit(Movie movie) {
        movieToEdit = movie;
        formTitleLabel.setText("Edit Movie");
        titleField.setText(movie.getTitle());
        ratingField.setText(movie.getRating());
        durationField.setText(String.valueOf(movie.getDurationMinutes()));
        descriptionArea.setText(movie.getDescription());
        errorLabel.setText("");
    }
 
    private void save() {
        String title = titleField.getText().trim();
        String rating = ratingField.getText().trim();
        String durationStr = durationField.getText().trim();
        String description = descriptionArea.getText().trim();
 
        if (title.isEmpty() || rating.isEmpty() || durationStr.isEmpty()) {
            errorLabel.setText("Title, rating, and duration must be entered.");
            return;
        }
 
        int duration;
        try {
            duration = Integer.parseInt(durationStr);
            if (duration <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            errorLabel.setText("Duration must be a positive number.");
            return;
        }
 
        try {
            if (movieToEdit == null) {
                movieService.addMovie(title, description, duration, rating);
            } else {
                movieService.updateMovie(
                    movieToEdit.getMovieId(), title, description,
                    duration, rating);
            }
            adminDashboard.showMovieList();
 
        } catch (Exception e) {
            errorLabel.setText("Database error: " + e.getMessage());
        }
    }
 
    private void clearFields() {
        titleField.setText("");
        ratingField.setText("");
        durationField.setText("");
        descriptionArea.setText("");
        errorLabel.setText("");
    }
}