package m3_group6project1;

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
        doubleLineBreak();
        System.out.printf("│ %-76s │%n", title); 
        doubleLineBreak();
    }

    public void displayAllBooks(Library lib) {
    	if (lib.getBooks().size() == 0) {
    		System.out.println("Library is empty! Sorry :(");
    		return;
    	}
    	
    	System.out.println("\t\t\tLIST OF ALL BOOKS");
    	doubleLineBreak();
    	System.out.printf("%-10s | %-30s | %-20s | %-10s%n", "Book ID", "Title", "Author", "Status");
    	singleLineBreak();
    	for (Book book : lib.getBooks()) {
			System.out.printf("%-10d | %-30s | %-20s | %-10s%n",
					book.getId(), book.getTitle(), book.getAuthor(), (book.getIsAvailable() ? "AVAILABLE" : "BORROWED"));
    	}
    	
    }
    
    public void displayAvailableBooks(Library lib) {
    	if (lib.getBooks().size() == 0) {
    		System.out.println("Library is empty! Sorry :(");
    		return;
    	}    	
    	if (!lib.checkForAvailableBook(lib.getBooks())) {
    		System.out.println("No books available! Sorry :(");
    		return;
    	}
    	
		System.out.println("\t\t\tLIST OF AVAILABLE BOOKS");
		doubleLineBreak();
		System.out.printf("%-10s | %-30s | %-20s%n", "Book ID", "Title", "Author");
		singleLineBreak();
		
		for (Book book : lib.getBooks()) {				
			if (book.getIsAvailable()) {
				System.out.printf("%-10d | %-30s | %-20s%n",
						book.getId(), book.getTitle(), book.getAuthor());					
			}				
		}
		
    }
    
    
	public void displayBorrowedBooks(Library lib) {
    	if (lib.getBooks().size() == 0) {
    		System.out.println("Library is empty! Sorry :(");
    		return;
    	}    	
    	if (lib.getLoans().size() == 0) {
    		System.out.println("There are no borrowed books at the moment.");
    		return;
    	}
    	
		System.out.println("\t\t\tLIST OF BOOKS CURRENTLY ON LOAN");
		doubleLineBreak();
	    System.out.printf("%-10s | %-30s | %-20s%n", "Loan ID", "Title", "Borrower ID and Name");
		singleLineBreak();
		String borrowerIDName;
		for (Loan loan : lib.getLoans()) {
			borrowerIDName = "[" + loan.getUser().getUserID() + "] " + loan.getUser().getName();
			System.out.printf("%-10d | %-30s | %-20s%n",
					 loan.getLoanId(), loan.getBook().getTitle(), borrowerIDName);
		}
			
	}

}
