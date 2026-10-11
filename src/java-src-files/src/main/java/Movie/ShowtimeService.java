package Movie;
 
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
 
import Database.ShowtimeDAO;
 
public class ShowtimeService {
    private final ShowtimeDAO showtimeDAO;
 
    public ShowtimeService() {
        this.showtimeDAO = new ShowtimeDAO();
    }
 
    public List<Showtime> getAllShowtimes() throws SQLException {
        return showtimeDAO.getAllShowtimes();
    }
 
    public void addShowtime(int movieId, int auditoriumId,
                LocalDateTime startTime) throws SQLException {
 
        Showtime showtime = new Showtime(
                0, movieId, auditoriumId, startTime, "ACTIVE", null);
 
        showtimeDAO.addShowtime(showtime);
    }
 
    public void updateShowtime(int showtimeId, int movieId, int auditoriumId,
                LocalDateTime startTime) throws SQLException {
 
        Showtime showtime = new Showtime(
                showtimeId, movieId, auditoriumId, startTime, "ACTIVE", null);
 
        showtimeDAO.updateShowtime(showtime);
    }
 
    public void cancelShowtime(int showtimeId) throws SQLException {
        showtimeDAO.cancelShowtime(showtimeId);
    }
}