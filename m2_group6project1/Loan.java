package m2_group6project1;

public class Loan implements LoanPolicy {
    private static int loanCounter = 1; // auto-increment for loan IDs
    private static int borrowLimit = 5;
    
    private int loanId;  
    private Book book;    
    private String user;  

    public Loan(Book book, String user) {
        this.loanId = loanCounter++; 
        this.book = book;
        this.user = user;
        book.setIsAvailable(false); 
    }
    
    public Loan () {
    	
    }

    public int getLoanId() { 
    	return loanId; 
    }
    
    public Book getBook() { 
    	return book; 
    }
    public String getUser() { 
    	return user; 
    }
    
    public static int getBorrowLimit() {
        return Loan.borrowLimit;
    }

    @Override
    public void setBorrowLimit(int newLimit) {
        Loan.borrowLimit = newLimit;
    }
  
}
