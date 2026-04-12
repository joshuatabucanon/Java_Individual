package m5group6.project1.model;

public class Loan {
    private int loanId;   // DB-generated id
    private Book book;
    private User user;

    public Loan() {}

    public Loan(int loanId, Book book, User user) {
        this.loanId = loanId;
        this.book = book;
        this.user = user;
    }

    public int getLoanId() {
        return loanId;
    }
    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public Book getBook() {
        return book;
    }
    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
