package m7group6.project1.model;

import static m7group6.project1.util.DbFieldLimits.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "books")
public class BookEntity {
    @Id
    @Column(name = "book_id", nullable = false)
    private Integer id; // assigned by application (NOT generated)

    @NotBlank
    @Size(max = BOOK_TITLE_MAX)
    @Column(name = "title", nullable = false, length = BOOK_TITLE_MAX)
    private String title;

    @NotBlank
    @Size(max = BOOK_AUTHOR_MAX)
    @Column(name = "author", nullable = false, length = BOOK_AUTHOR_MAX)
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

    @PrePersist
    @PreUpdate
    void normalize() {
        if (title != null) title = title.trim();
        if (author != null) author = author.trim();
        if (isAvailable == null) isAvailable = Boolean.TRUE;
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