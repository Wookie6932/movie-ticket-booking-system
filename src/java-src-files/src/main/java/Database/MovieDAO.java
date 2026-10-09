package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Movie.Movie;

public class MovieDAO {

    public List<Movie> getAllMovies() throws SQLException {

        String sql = """
                SELECT movie_id, title, description, duration_minutes, rating
                FROM movie
                ORDER BY title
                """;

        List<Movie> movies = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Movie movie = new Movie(
                        resultSet.getInt("movie_id"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getInt("duration_minutes"),
                        resultSet.getString("rating")
                );

                movies.add(movie);
            }
        }

        return movies;
    }
}