package Movie;
 
import java.sql.SQLException;
import java.util.List;
 
import Database.MovieDAO;
 
public class MovieService {
 
    private final MovieDAO movieDAO;
 
    public MovieService() {
        this.movieDAO = new MovieDAO();
    }
 
    public List<Movie> getAllMovies() throws SQLException {
        return movieDAO.getAllMovies();
    }

    public Movie getMovieById(int movieId) throws SQLException {
        return movieDAO.getMovieById(movieId);
    }
 
    public void addMovie(String title, String description,
            int durationMinutes, String rating) throws SQLException {
 
        validateMovie(title, durationMinutes, rating);

        Movie movie = new Movie(0, title, description, durationMinutes, rating);
        movieDAO.addMovie(movie);
    }
 
    public void updateMovie(int movieId, String title, String description,
            int durationMinutes, String rating) throws SQLException {
 
        validateMovie(title, durationMinutes, rating);

        Movie movie = new Movie(movieId, title, description, durationMinutes, rating);
        movieDAO.updateMovie(movie);
    }
 
    public void deleteMovie(int movieId) throws SQLException {
        movieDAO.deleteMovie(movieId);
    }

    private void validateMovie(String title, int durationMinutes, String rating) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("A title is required.");
        }
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration has to be a positive number.");
        }
        if (rating == null || rating.trim().isEmpty()) {
            throw new IllegalArgumentException("A rating is required.");
        }
    }
}