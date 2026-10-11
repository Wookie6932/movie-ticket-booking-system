package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
                movies.add(new Movie(
                        resultSet.getInt("movie_id"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getInt("duration_minutes"),
                        resultSet.getString("rating")
                ));
            }
        }

        return movies;
    }

    public void addMovie(Movie movie) throws SQLException {

        String sql = """
                INSERT INTO movie (title, description, duration_minutes, rating)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, movie.getTitle());
            statement.setString(2, movie.getDescription());
            statement.setInt(3, movie.getDurationMinutes());
            statement.setString(4, movie.getRating());

            statement.executeUpdate();
        }
    }

    public void updateMovie(Movie movie) throws SQLException {

        String sql = """
                UPDATE movie
                SET title = ?, description = ?, duration_minutes = ?, rating = ?
                WHERE movie_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, movie.getTitle());
            statement.setString(2, movie.getDescription());
            statement.setInt(3, movie.getDurationMinutes());
            statement.setString(4, movie.getRating());
            statement.setInt(5, movie.getMovieId());

            statement.executeUpdate();
        }
    }

    public void deleteMovie(int movieId) throws SQLException {

        String sql = """
                DELETE FROM movie WHERE movie_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, movieId);
            statement.executeUpdate();
        }
    }
}