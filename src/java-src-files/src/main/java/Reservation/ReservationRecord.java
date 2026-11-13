package Reservation;

import java.time.LocalDateTime;

public class ReservationRecord {    
    private final int reservationId;
    private final String movieTitle;
    private final LocalDateTime showtimeStart;
    private final int auditoriumId;
    private final LocalDateTime reservationDate;
    private final String seatLabels;

    public ReservationRecord(int reservationId, String movieTitle, LocalDateTime showtimeStart, int auditoriumId, LocalDateTime reservationDate, String seatLabels) {
        this.reservationId = reservationId;
        this.movieTitle = movieTitle;
        this.showtimeStart = showtimeStart;
        this.auditoriumId = auditoriumId;
        this.reservationDate = reservationDate;
        this.seatLabels = seatLabels;
    }

    public int getReservationId() {
        return reservationId;
    }
 
    public String getMovieTitle() {
        return movieTitle;
    }
 
    public LocalDateTime getShowtimeStart() {
        return showtimeStart;
    }
 
    public int getAuditoriumId() {
        return auditoriumId;
    }
 
    public LocalDateTime getReservationDate() {
        return reservationDate;
    }
 
    public String getSeatLabels() {
        return seatLabels;
    }
}