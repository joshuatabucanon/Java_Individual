package ph.com.bpi.training.model;
import jakarta.persistence.*;

@Entity
@Table(name = "movies")
public class Movie {
	
	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
    @Column(name = "title")
	private String title;
    
    @Column(name = "director")
	private String director;
    
    @Column(name = "showing_date")
	private String showingDate;
	
	public Movie() {
	}
	
	public Movie(Long id, String title, String director, String showingDate) {
		this.id = id;
		this.title = title;
		this.director = director;
		this.showingDate = showingDate;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDirector() {
		return director;
	}
	public void setDirector(String director) {
		this.director = director;
	}
	public String getShowingDate() {
		return showingDate;
	}
	public void setShowingDate(String showingDate) {
		this.showingDate = showingDate;
	}
	
	
	
	
}
