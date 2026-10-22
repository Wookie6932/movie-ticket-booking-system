package Reservation;
 
import java.sql.SQLException;
import java.util.List;

import Database.ReservationDAO;
import Database.ShowtimeDAO;
import Movie.Showtime;

public class ReservationService {
    
    private final ReservationDAO reservationDAO;
    private final ShowtimeDAO showtimeDAO;
 
    public ReservationService() {
        this.reservationDAO = new ReservationDAO();
        this.showtimeDAO = new ShowtimeDAO();
    }

    public List<ShowtimeSeat> getSeatsForShowtime(int showtimeId)
            throws SQLException {
        return reservationDAO.getSeatsForShowtime(showtimeId);
    }

    public void createReservation(int userId, int showtimeId, List<Integer> showtimeSeatIds) 
            throws SQLException, SeatUnavailableException {
        if (showtimeSeatIds == null || showtimeSeatIds.isEmpty()) {
            throw new IllegalArgumentException("Select at least one seat.");
        }
 
        Showtime showtime = showtimeDAO.getShowtimeById(showtimeId);
 
        if (showtime == null) {
            throw new IllegalArgumentException("Select an existing showtime.");
        }

        if (!"ACTIVE".equals(showtime.getStatus())) {
            throw new IllegalStateException("This showtime is no longer available for booking.");
        }

        reservationDAO.createReservation(userId, showtimeId, showtimeSeatIds);
    }

    public List<ReservationRecord> getActiveReservations(int userId)
            throws SQLException {
        return reservationDAO.getActiveReservationsByUser(userId);
    }

    public void cancelReservation(int reservationId, int userId)
            throws SQLException {

        boolean canceled = reservationDAO.cancelReservation(reservationId, userId);

        if (!canceled) {
            throw new IllegalStateException("This reservation could not be canceled.");
        }
    }
}