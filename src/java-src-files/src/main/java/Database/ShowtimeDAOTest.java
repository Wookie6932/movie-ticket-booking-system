package Database;

import java.sql.SQLException;
import java.util.List;

import Movie.Showtime;

/*  
    Requires an auditorium and showtime records linked to existing movies.
    Example data:

    INSERT INTO auditorium (name)
    VALUES ('Auditorium 1');

    INSERT INTO showtime (movie_id, auditorium_id, start_time, status)
    VALUES
    (1, 1, '2026-10-09 18:00:00', 'ACTIVE'),
    (1, 1, '2026-10-09 21:00:00', 'ACTIVE'),
    (2, 1, '2026-10-09 19:00:00', 'ACTIVE'),
    (2, 1, '2026-10-10 20:00:00', 'ACTIVE');
*/
public class ShowtimeDAOTest {

    public static void main(String[] args) {

        ShowtimeDAO showtimeDAO = new ShowtimeDAO();
        
        int movieId = 1;

        try {
            List<Showtime> showtimes =
                    showtimeDAO.getShowtimesByMovie(movieId);

            if (showtimes.isEmpty()) {
                System.out.println("No showtimes found for movie ID " + movieId);
            } else {
                for (Showtime showtime : showtimes) {
                    System.out.println(
                            "Showtime ID: " + showtime.getShowtimeId()
                            + " | Movie ID: " + showtime.getMovieId()
                            + " | Auditorium ID: " + showtime.getAuditoriumId()
                            + " | Start: " + showtime.getStartTime()
                            + " | Status: " + showtime.getStatus()
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to retrieve showtimes.");
            e.printStackTrace();
        }
    }
}