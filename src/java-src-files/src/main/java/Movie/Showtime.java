package Movie;

import java.time.LocalDateTime;

public class Showtime {

    private int showtimeId;
    private int movieId;
    private int auditoriumId;
    private LocalDateTime startTime;
    private String status;

    public Showtime(int showtimeId, int movieId, int auditoriumId,
                    LocalDateTime startTime, String status) {
        this.showtimeId = showtimeId;
        this.movieId = movieId;
        this.auditoriumId = auditoriumId;
        this.startTime = startTime;
        this.status = status;
    }

    public int getShowtimeId() {
        return showtimeId;
    }

    public int getMovieId() {
        return movieId;
    }

    public int getAuditoriumId() {
        return auditoriumId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getStatus() {
        return status;
    }
}