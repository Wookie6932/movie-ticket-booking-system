package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import Movie.Showtime;

public class ShowtimeDAO {

    public List<Showtime> getAllShowtimes() throws SQLException {

        String sql = """
                SELECT s.showtime_id, s.movie_id, s.auditorium_id,
                       s.start_time, s.status, m.title AS movie_title
                FROM showtime s
                JOIN movie m ON s.movie_id = m.movie_id
                ORDER BY s.start_time
                """;

        List<Showtime> showtimes = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                showtimes.add(createShowtime(resultSet));
            }
        }

        return showtimes;
    }
    
    public List<Showtime> getShowtimesByMovie(int movieId) throws SQLException {
 
        String sql = """
                SELECT s.showtime_id, s.movie_id, s.auditorium_id,
                       s.start_time, s.status, m.title AS movie_title
                FROM showtime s
                JOIN movie m ON s.movie_id = m.movie_id
                WHERE s.movie_id = ? AND s.status = 'ACTIVE'
                ORDER BY s.start_time
                """;
 
        List<Showtime> showtimes = new ArrayList<>();
 
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
 
            statement.setInt(1, movieId);
 
            try (ResultSet resultSet = statement.executeQuery()) {
 
                while (resultSet.next()) {
                    showtimes.add(createShowtime(resultSet));
                }
            }
        }
 
        return showtimes;
    }
    
        public Showtime getShowtimeById(int showtimeId) throws SQLException {

            String sql = """
                    SELECT s.showtime_id, s.movie_id, s.auditorium_id,
                           s.start_time, s.status, m.title AS movie_title
                    FROM showtime s
                    JOIN movie m ON s.movie_id = m.movie_id
                    WHERE s.showtime_id = ?
                    """;

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setInt(1, showtimeId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return createShowtime(resultSet);
                    } else {
                        return null;
                    }
                }
            }
        }

        public boolean auditoriumExists(int auditoriumID) throws SQLException {

            String sql = """
                    SELECT COUNT(*) AS count
                    FROM auditorium
                    WHERE auditorium_id = ?
                    """;

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setInt(1, auditoriumID);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("count") > 0;
                    }
                    return false;
                }
            }

        }
                
        public List<Showtime> getActiveShowtimesByAuditorium(int auditoriumId) throws SQLException {

            String sql = """
                    SELECT s.showtime_id, s.movie_id, s.auditorium_id,
                           s.start_time, s.status, m.title AS movie_title
                    FROM showtime s
                    JOIN movie m ON s.movie_id = m.movie_id
                    WHERE s.auditorium_id = ? AND s.status = 'ACTIVE'
                    ORDER BY s.start_time
                    """;

            List<Showtime> showtimes = new ArrayList<>();

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setInt(1, auditoriumId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        showtimes.add(createShowtime(resultSet));
                    }
                }
            }

            return showtimes;
        }

        public void addShowtime(Showtime showtime) throws SQLException {
 
        String sql = """
                INSERT INTO showtime (movie_id, auditorium_id, start_time, status)
                VALUES (?, ?, ?, 'ACTIVE')
                """;
 
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
 
            statement.setInt(1, showtime.getMovieId());
            statement.setInt(2, showtime.getAuditoriumId());
            statement.setTimestamp(3, Timestamp.valueOf(showtime.getStartTime()));
 
            statement.executeUpdate();
        }
    }
 
    public void updateShowtime(Showtime showtime) throws SQLException {
 
        String sql = """
                UPDATE showtime
                SET movie_id = ?, auditorium_id = ?, start_time = ?
                WHERE showtime_id = ?
                """;
 
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
 
            statement.setInt(1, showtime.getMovieId());
            statement.setInt(2, showtime.getAuditoriumId());
            statement.setTimestamp(3, Timestamp.valueOf(showtime.getStartTime()));
            statement.setInt(4, showtime.getShowtimeId());
 
            statement.executeUpdate();
        }
    }
    
        public void cancelShowtime(int showtimeId) throws SQLException {
 
        String sql = """
                UPDATE showtime SET status = 'CANCELED'
                WHERE showtime_id = ?
                """;
 
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
 
            statement.setInt(1, showtimeId);
            statement.executeUpdate();
        }
    }

        private Showtime createShowtime(ResultSet resultSet) throws SQLException {
            return new Showtime(
                    resultSet.getInt("showtime_id"),
                    resultSet.getInt("movie_id"),
                    resultSet.getInt("auditorium_id"),
                    resultSet.getTimestamp("start_time").toLocalDateTime(),
                    resultSet.getString("status"),
                    resultSet.getString("movie_title")
            );
        }
}