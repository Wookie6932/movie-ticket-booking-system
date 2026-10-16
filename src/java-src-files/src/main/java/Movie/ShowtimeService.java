package Movie;
 
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import Database.MovieDAO;
import Database.ShowtimeDAO;
 
public class ShowtimeService {

    private final ShowtimeDAO showtimeDAO;
    private final MovieDAO movieDAO;
 
    public ShowtimeService() {
        this.showtimeDAO = new ShowtimeDAO();
        this.movieDAO = new MovieDAO();
    }
 
    public List<Showtime> getAllShowtimes() throws SQLException {
        return showtimeDAO.getAllShowtimes();
    }

    public List<Showtime> getShowtimesByMovie(int movieId) throws SQLException {
        return showtimeDAO.getShowtimesByMovie(movieId);
    }
 
    public void addShowtime(int movieId, int auditoriumId,
                LocalDateTime startTime) throws SQLException {
 
            validateShowtime(movieId, auditoriumId, startTime, -1);

        Showtime showtime = new Showtime(
                0, movieId, auditoriumId, startTime, "ACTIVE", null);
 
        showtimeDAO.addShowtime(showtime);
    }
 
    public void updateShowtime(int showtimeId, int movieId, int auditoriumId,
            LocalDateTime startTime) throws SQLException {

        Showtime existingShowtime = showtimeDAO.getShowtimeById(showtimeId);

        if (existingShowtime == null) {
            throw new IllegalArgumentException("Select an existing showtime.");
        }

        if ("CANCELED".equals(existingShowtime.getStatus())) {
            throw new IllegalStateException("Canceled showtimes cannot be edited.");
        }

        validateShowtime(movieId, auditoriumId, startTime, showtimeId);

        Showtime showtime = new Showtime(
                showtimeId, movieId, auditoriumId, startTime, "ACTIVE", null);

        showtimeDAO.updateShowtime(showtime);
    }
 
    public void cancelShowtime(int showtimeId) throws SQLException {

        Showtime showtime = showtimeDAO.getShowtimeById(showtimeId);

        if (showtime == null) {
            throw new IllegalArgumentException("Select an existing showtime.");
        }

        if ("CANCELED".equals(showtime.getStatus())) {
            throw new IllegalStateException("This showtime is already canceled.");
        }

        showtimeDAO.cancelShowtime(showtimeId);
    }

    private void validateShowtime(int movieId, int auditoriumId,
            LocalDateTime startTime, int ignoredShowtimeId) throws SQLException {

        if (startTime == null) {
            throw new IllegalArgumentException("Start time is required.");
        }

        Movie movie = movieDAO.getMovieById(movieId);

        if (movie == null) {
            throw new IllegalArgumentException("Select an existing movie.");
        }

        if (auditoriumId <= 0 || !showtimeDAO.auditoriumExists(auditoriumId)) {
            throw new IllegalArgumentException("Select an existing auditorium.");
        }

        LocalDateTime endTime =
                startTime.plusMinutes(movie.getDurationMinutes());

        if (hasScheduleConflict(
                auditoriumId, startTime, endTime, ignoredShowtimeId)) {

            throw new IllegalArgumentException(
                    "This auditorium already has an overlapping active showtime.");
        }
    }

    private boolean hasScheduleConflict(int auditoriumId,
            LocalDateTime newStartTime, LocalDateTime newEndTime,
            int ignoredShowtimeId) throws SQLException {

        List<Showtime> existingShowtimes =
                showtimeDAO.getActiveShowtimesByAuditorium(auditoriumId);

        for (Showtime existing : existingShowtimes) {
            if (existing.getShowtimeId() == ignoredShowtimeId) {
                continue;
            }

            Movie existingMovie = movieDAO.getMovieById(existing.getMovieId());

            if (existingMovie == null) {
                continue;
            }

            LocalDateTime existingEndTime = existing.getStartTime()
                    .plusMinutes(existingMovie.getDurationMinutes());

            boolean overlaps = newStartTime.isBefore(existingEndTime)
                    && newEndTime.isAfter(existing.getStartTime());

            if (overlaps) {
                return true;
            }
        }

        return false;
    }
}