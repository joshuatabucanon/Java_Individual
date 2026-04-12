package m8jt.project1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(
    name = "loans",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_loans_book",
            columnNames = "book_id"
        )
    }
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

    /* =========================
       Constructors
       ========================= */

    public LoanEntity() {
    }

    // Convenience constructor
    public LoanEntity(BookEntity book, UserEntity user) {
        this.book = book;
        this.user = user;
    }

    /* =========================
       Getters / Setters
       ========================= */

    public Integer getLoanId() {
        return loanId;
    }

    public void setLoanId(Integer loanId) {
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