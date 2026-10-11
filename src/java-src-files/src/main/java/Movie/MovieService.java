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
 
    public void addMovie(String title, String description,
            int durationMinutes, String rating) throws SQLException {
 
        Movie movie = new Movie(0, title, description, durationMinutes, rating);
        movieDAO.addMovie(movie);
    }
 
    public void updateMovie(int movieId, String title, String description,
            int durationMinutes, String rating) throws SQLException {
 
        Movie movie = new Movie(movieId, title, description, durationMinutes, rating);
        movieDAO.updateMovie(movie);
    }
 
    public void deleteMovie(int movieId) throws SQLException {
        movieDAO.deleteMovie(movieId);
    }
}