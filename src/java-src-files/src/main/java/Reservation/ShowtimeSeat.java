package Reservation;

public class ShowtimeSeat {
    
    private final int showtimeSeatId;
    private final String rowLabel;
    private final int seatNumber;
    private final String status;

    public ShowtimeSeat(int showtimeSeatId, String rowLabel, int seatNumber, String status) {
        this.showtimeSeatId = showtimeSeatId;
        this.rowLabel = rowLabel;
        this.seatNumber = seatNumber;
        this.status = status;
    }

    public int getShowtimeSeatId() {
        return showtimeSeatId;
    }

    public String getRowLabel() {
        return rowLabel;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public String getSeatLabel() {
        return rowLabel + seatNumber;
    }

    public String getStatus() {
        return status;
    }

    public boolean isAvailable() {
        return "AVAILABLE".equals(status);
    }
}