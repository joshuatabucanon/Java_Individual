package m6group6.project1.util;

import java.util.List;

import m6group6.project1.model.BookEntity;
import m6group6.project1.model.LoanEntity;

public class DisplayFormatter {
	
	// Prints a double line divider
    public void doubleLineBreak() {
        System.out.println("════════════════════════════════════════════════════════════════════════════════════════════════════════════════");
    }

    // Prints a single line divider
    public void singleLineBreak() {
        System.out.println("────────────────────────────────────────────────────────────────────────────────────────────────────────────────");
    }

    public void header(String title) {
        System.out.println("\t\t\t" + title); 
    }

    public void displayAllBooks(List<BookEntity> allBooks, String col1, String col2, String col3, String col4) {
    	header("LIST OF ALL BOOKS");
    	doubleLineBreak();
		System.out.printf("%-10s | %-40s | %-20s | %-10s%n", col1, col2, col3, col4);
		singleLineBreak();
		
		for (BookEntity book : allBooks) {	
			System.out.printf("%-10s | %-40s | %-20s | %-10s%n",
					book.getId(), book.getTitle(), book.getAuthor(), (book.getIsAvailable() ? "AVAILABLE" : "BORROWED"));		
		}
		
    }
    
    public void displayAvailableBooks(List<BookEntity> availableBooks, String col1, String col2, String col3) {    	
    	header("LIST OF AVAILABLE BOOKS");
		doubleLineBreak();
		System.out.printf("%-10s | %-40s | %-20s%n", col1, col2, col3);
		singleLineBreak();
				
		for (BookEntity book : availableBooks) {		
				System.out.printf("%-10d | %-40s | %-20s%n",
						book.getId(), book.getTitle(), book.getAuthor());				
		}
		
    }
    
    
	public void displayBorrowedBooks(List<LoanEntity> loans, String col1, String col2, String col3) {
    	header("LIST OF BOOKS CURRENTLY ON LOAN");
		doubleLineBreak();
	    System.out.printf("%-10s | %-40s | %-20s%n", col1, col2, col3);
		singleLineBreak();
		String borrowerIDName;
		for (LoanEntity loan : loans) {
			borrowerIDName = "[" + loan.getUser().getUserID() + "] " + loan.getUser().getName();
			System.out.printf("%-10d | %-40s | %-20s%n",
					 loan.getLoanId(), loan.getBook().getTitle(), borrowerIDName);
		}
			
	}

}
