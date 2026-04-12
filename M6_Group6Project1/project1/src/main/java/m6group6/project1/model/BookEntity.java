package m6group6.project1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "books")
public class BookEntity {

    @Id
    @Column(name = "book_id", nullable = false)
    private Integer id; // assigned by application (NOT generated)

    @NotBlank
    @Size(max = 300)
    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @NotBlank
    @Size(max = 200)
    @Column(name = "author", nullable = false, length = 200)
    private String author;

    @NotNull
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    public BookEntity() { }

    public BookEntity(Integer id, String title, String author) {
        this.id = id;
        this.title = title == null ? null : title.trim();
        this.author = author == null ? null : author.trim();
    }
   
    public Boolean getIsAvailable() {
        return isAvailable;
    }
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public int getId() {
        return id == null ? 0 : id.intValue();
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = (title == null ? null : title.trim());
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = (author == null ? null : author.trim());
    }
}