package m2_group6project1;


public class Book {
	
	// adding this "id" to give you an idea on what options you can do
	
	private String author;
	private Boolean isAvailable = true;
	private Integer id;
	
	public Boolean getIsAvailable() {
		return isAvailable;
	}
	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	private String title;
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Book (Integer id, String title, String author) {
		
		this.id = id;
		this.title = title;
		this.author = author;
		
	}
}
