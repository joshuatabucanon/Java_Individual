package m7group6.project1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(
    name = "loans",
    uniqueConstraints = @UniqueConstraint(name = "uq_loans_book", columnNames = "book_id")
)
public class LoanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_id", updatable = false, nullable = false)
    private Integer loanId;

    @NotNull
    @OneToOne(optional = false) 
    @JoinColumn(
        name = "book_id",
        nullable = false,
        unique = true, 
        foreignKey = @ForeignKey(name = "fk_loans_book")
    )
    private BookEntity book;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_loans_user")
    )
    private UserEntity user;

    public LoanEntity() { 
    }

    // kept for compatibility with your constructor usage (even though ID is generated)
    public LoanEntity(int loanId, BookEntity book, UserEntity user) {
        this.loanId = loanId;
        this.book = book;
        this.user = user;
    }

    // --- keep existing method names/signatures ---

    public int getLoanId() {
        return loanId == null ? 0 : loanId.intValue();
    }
    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public BookEntity getBook() {
        return book;
    }
    public void setBook(BookEntity book) {
        this.book = book;
    }

    public UserEntity getUser() {
        return user;
    }
    public void setUser(UserEntity user) {
        this.user = user;
    }
}