package m9jt.project1.model;

import static m9jt.project1.util.DbFieldLimits.BOOK_AUTHOR_MAX;
import static m9jt.project1.util.DbFieldLimits.BOOK_TITLE_MAX;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "books")
public class BookEntity {

    @Id
    @Column(name = "book_id", nullable = false)
    private Integer id; // assigned (NOT auto-generated)

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

    /* =========================
       Constructors
       ========================= */

    public BookEntity() {
    }

    // Application-assigned ID constructor
    public BookEntity(Integer id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isAvailable = true;
    }

    /* =========================
       Lifecycle Hooks
       ========================= */

    @PrePersist
    @PreUpdate
    private void normalize() {
        if (title != null) {
            title = title.trim();
        }
        if (author != null) {
            author = author.trim();
        }
        if (isAvailable == null) {
            isAvailable = true;
        }
    }

    /* =========================
       Getters / Setters
       ========================= */

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean available) {
        isAvailable = available;
    }
}