package Movie;

public class Movie {

    private int movieId;
    private String title;
    private String description;
    private int durationMinutes;
    private String rating;

    public Movie(int movieId, String title, String description,
                 int durationMinutes, String rating) {
        this.movieId = movieId;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.rating = rating;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public String getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return title;
    }
}