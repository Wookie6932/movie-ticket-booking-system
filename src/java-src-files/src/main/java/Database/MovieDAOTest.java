package Database;

import java.sql.SQLException;
import java.util.List;

import Movie.Movie;

/*  
    Requires movie records in the movie table.
    Example data:
    INSERT INTO movie (title, description, duration_minutes, rating)
    VALUES
    ('Digger', 'Test data', 129, 'R'),
    ('Resident Evil', 'Test data', 97, 'R')
*/
public class MovieDAOTest {

    public static void main(String[] args) {

        MovieDAO movieDAO = new MovieDAO();

        try {
            List<Movie> movies = movieDAO.getAllMovies();

            if (movies.isEmpty()) {
                System.out.println("No movies found in the database");
            } else {
                for (Movie movie : movies) {
                    System.out.println(
                            movie.getMovieId() + " | "
                            + movie.getTitle() + " | "
                            + movie.getDurationMinutes() + " min | "
                            + movie.getRating()
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to retrieve movies from the database");
            e.printStackTrace();
        }
    }
}