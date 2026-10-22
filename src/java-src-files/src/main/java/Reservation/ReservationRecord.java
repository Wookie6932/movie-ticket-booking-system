package Reservation;

import java.time.LocalDateTime;

public class ReservationRecord {
    
    private final int reservationId;
    private final String movieTitle;
    private final LocalDateTime showtimeStart;
    private final String status;
    private final LocalDateTime reservationDate;
    private final int seatCount;

    public ReservationRecord(int reservationId, String movieTitle, LocalDateTime showtimeStart, String status, LocalDateTime reservationDate, int seatCount) {
        this.reservationId = reservationId;
        this.movieTitle = movieTitle;
        this.showtimeStart = showtimeStart;
        this.status = status;
        this.reservationDate = reservationDate;
        this.seatCount = seatCount;
    }

    public int getReservationId() { return reservationId; }
    public String getMovieTitle() { return movieTitle; }
    public LocalDateTime getShowtimeStart() { return showtimeStart; }
    public String getStatus() { return status; }
    public LocalDateTime getReservationDate() { return reservationDate; }
    public int getSeatCount() { return seatCount; }
}