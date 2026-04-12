package m3_group6project1;

public class Loan {
    private static int loanCounter = 1; // auto-increment for loan IDs
    
    private int loanId;  
    private Book book;    
    private User user;  

    public Loan(Book book, User user) {
        this.loanId = loanCounter++; 
        this.book = book;
        this.user = user;
    }
    
    public Loan () {
    	
    }

    public int getLoanId() { 
    	return loanId; 
    }
    
    public Book getBook() { 
    	return book; 
    }
    public User getUser() { 
    	return user; 
    }
      
}
