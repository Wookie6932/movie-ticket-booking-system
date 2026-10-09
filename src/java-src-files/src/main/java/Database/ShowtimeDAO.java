package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Movie.Showtime;

public class ShowtimeDAO {

    public List<Showtime> getShowtimesByMovie(int movieId) throws SQLException {

        String sql = """
                SELECT showtime_id, movie_id, auditorium_id, start_time, status
                FROM showtime
                WHERE movie_id = ? AND status = 'ACTIVE'
                ORDER BY start_time
                """;

        List<Showtime> showtimes = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, movieId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    Showtime showtime = new Showtime(
                            resultSet.getInt("showtime_id"),
                            resultSet.getInt("movie_id"),
                            resultSet.getInt("auditorium_id"),
                            resultSet.getTimestamp("start_time").toLocalDateTime(),
                            resultSet.getString("status")
                    );

                    showtimes.add(showtime);
                }
            }
        }

        return showtimes;
    }
}