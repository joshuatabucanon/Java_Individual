package ph.com.bpi.training.dto;

public class MovieRequestDTO {
    private String title;
    private String director;
    private String showingDate;

    public MovieRequestDTO() { }

    public MovieRequestDTO(String title, String director, String showingDate) {
        this.title = title;
        this.director = director;
        this.showingDate = showingDate;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public String getShowingDate() { return showingDate; }
    public void setShowingDate(String showingDate) { this.showingDate = showingDate; }
}