package Reservation;

public class SeatUnavailableException extends Exception {
    
    public SeatUnavailableException(String message) {
        super(message);
    }
}