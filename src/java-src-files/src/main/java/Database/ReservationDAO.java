package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Reservation.ReservationRecord;
import Reservation.SeatUnavailableException;
import Reservation.ShowtimeSeat;

public class ReservationDAO {
    
    public List<ShowtimeSeat> getSeatsForShowtime(int showtimeId) throws SQLException {
        String sql = """
                SELECT ss.showtime_seat_id, s.row_label, s.seat_number, ss.status
                FROM showtime_seat ss
                JOIN seat s ON ss.seat_id = s.seat_id
                WHERE ss.showtime_id = ?
                ORDER BY s.row_label, s.seat_number
                """;

        List<ShowtimeSeat> seats = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, showtimeId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    seats.add(new ShowtimeSeat(
                            resultSet.getInt("showtime_seat_id"),
                            resultSet.getString("row_label"),
                            resultSet.getInt("seat_number"),
                            resultSet.getString("status")
                    ));
                }
            }
        }

        return seats;
    }

    public void createReservation(int userId, int showtimeId, List<Integer> showtimeSeatIds) 
            throws SQLException, SeatUnavailableException {
        String insertReservation = """
                INSERT INTO reservation (user_id, showtime_id, status)
                VALUES (?, ?, 'ACTIVE')
                """;

        String reserveSeat = """
                UPDATE showtime_seat SET status = 'RESERVED'
                WHERE showtime_seat_id = ? AND showtime_id = ?
                AND status = 'AVAILABLE'
                """;

        String insertReservationSeat = """
                INSERT INTO reservation_seat (reservation_id, showtime_seat_id)
                VALUES (?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                int reservationId;

                try (PreparedStatement statement = connection.prepareStatement(
                        insertReservation, Statement.RETURN_GENERATED_KEYS)) {

                    statement.setInt(1, userId);
                    statement.setInt(2, showtimeId);
                    statement.executeUpdate();

                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        keys.next();
                        reservationId = keys.getInt(1);
                    }
                }

                try (PreparedStatement reserveStatement =
                             connection.prepareStatement(reserveSeat);
                     PreparedStatement linkStatement =
                             connection.prepareStatement(insertReservationSeat)) {

                    for (int showtimeSeatId : showtimeSeatIds) {
                        reserveStatement.setInt(1, showtimeSeatId);
                        reserveStatement.setInt(2, showtimeId);

                        if (reserveStatement.executeUpdate() != 1) {
                            throw new SeatUnavailableException("One or more selected seats are no longer available.");
                        }

                        linkStatement.setInt(1, reservationId);
                        linkStatement.setInt(2, showtimeSeatId);
                        linkStatement.executeUpdate();
                    }
                }

                connection.commit();

            } catch (SQLException | SeatUnavailableException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    public List<ReservationRecord> getActiveReservationsByUser(int userId)
            throws SQLException {
        String sql = """
                SELECT r.reservation_id, m.title AS movie_title, s.start_time,
                       r.status, r.reservation_date,
                       COUNT(rs.reservation_seat_id) AS seat_count
                FROM reservation r
                JOIN showtime s ON r.showtime_id = s.showtime_id
                JOIN movie m ON s.movie_id = m.movie_id
                JOIN reservation_seat rs ON r.reservation_id = rs.reservation_id
                WHERE r.user_id = ? AND r.status = 'ACTIVE'
                GROUP BY r.reservation_id, m.title, s.start_time,
                         r.status, r.reservation_date
                ORDER BY s.start_time
                """;

        List<ReservationRecord> reservations = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reservations.add(new ReservationRecord(
                            resultSet.getInt("reservation_id"),
                            resultSet.getString("movie_title"),
                            resultSet.getTimestamp("start_time").toLocalDateTime(),
                            resultSet.getString("status"),
                            resultSet.getTimestamp("reservation_date").toLocalDateTime(),
                            resultSet.getInt("seat_count")
                    ));
                }
            }
        }

        return reservations;
    }

    public boolean cancelReservation(int reservationId, int userId)
            throws SQLException {
        String cancelReservation = """
                UPDATE reservation SET status = 'CANCELED'
                WHERE reservation_id = ? AND user_id = ? AND status = 'ACTIVE'
                """;

        String releaseSeats = """
                UPDATE showtime_seat SET status = 'AVAILABLE'
                WHERE showtime_seat_id IN (
                    SELECT showtime_seat_id FROM reservation_seat
                    WHERE reservation_id = ?
                )
                """;

        String removeSeatLinks = """
                DELETE FROM reservation_seat WHERE reservation_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement statement =
                             connection.prepareStatement(cancelReservation)) {

                    statement.setInt(1, reservationId);
                    statement.setInt(2, userId);

                    if (statement.executeUpdate() != 1) {
                        connection.rollback();
                        return false;
                    }
                }

                try (PreparedStatement statement =
                             connection.prepareStatement(releaseSeats)) {
                    statement.setInt(1, reservationId);
                    statement.executeUpdate();
                }

                try (PreparedStatement statement =
                             connection.prepareStatement(removeSeatLinks)) {
                    statement.setInt(1, reservationId);
                    statement.executeUpdate();
                }

                connection.commit();
                return true;

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }
}