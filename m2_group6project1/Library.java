package m2_group6project1;


public class Library{
   // can contain up to 5 books	
	
	private Book[] books;
	private int bookCount = 0;
	private Loan[] loans;
	private int loanCount = 0;
	
	public Library (int counter) {
		
		this.books = new Book[counter];
		this.loans = new Loan[counter];
		
	}
	public void addBook(Book newBook) {
		
		if (bookCount < books.length) {
			books[bookCount] = newBook;
			bookCount++;
			
			//System.out.println(newBook.getTitle() + " by " + newBook.getAuthor()+ " added to library.");
		}else {
			
			//System.out.println("Library is full! Sorry :(");
		}
	}

	public void getAllBooks() {
		
		if (bookCount > 0) {
			Common.doubleLineBreak();
			System.out.println("\tLISTING ALL BOOKS:");
			Common.doubleLineBreak();
			System.out.printf("%-10s | %-30s | %-20s | %-10s%n", "Book ID", "Title", "Author", "Status");//pangformat ala excel owo
			Common.singleLineBreak();
			
			for (int x = 0; x < books.length; x++) {

				System.out.printf("%-10d | %-30s | %-20s | %-10s%n",
						books[x].getId(),
						books[x].getTitle(),
						books[x].getAuthor(),
		                (books[x].getIsAvailable() ? "AVAILABLE" : "BORROWED"));
			}
		}
		else {
			Common.doubleLineBreak();
			System.out.println("Library is empty! Sorry :(");

		}
	};
	public int getAvailableBooks() {
		
		int availableBooks = 0;
		if (bookCount > 0) {
			
			Common.doubleLineBreak();
			System.out.println("\tLISTING AVAILABLE BOOKS:");
			Common.doubleLineBreak();
			System.out.printf("%-10s | %-30s | %-20s%n", "Book ID", "Title", "Author");
			Common.singleLineBreak();
			
			for (int x = 0; x < books.length; x++) {
				
				if (books[x].getIsAvailable()) {
					System.out.printf("%-10d | %-30s | %-20s%n",
			                  books[x].getId(),
			                  books[x].getTitle(),
			                  books[x].getAuthor());
					
					availableBooks++;
				}
				
			}
			if (availableBooks == 0){
				Common.doubleLineBreak();
				System.out.println("No books available! Sorry :(");
			}
		}else {
			Common.doubleLineBreak();
			System.out.println("Library is empty! Sorry :(");

		}
		
		return availableBooks;
		
	};
	public int getBorrowedBooks(String borrower) {
	    int borrowedBooks = 0;
	    if (loanCount > 0) {
	    	Common.doubleLineBreak();
			System.out.println("\tLISTING BORROWED BOOKS:");
			Common.doubleLineBreak();
	        System.out.printf("%-10s | %-30s | %-20s%n", "Loan ID", "Title", "Borrower");
			Common.singleLineBreak();
			
	        for (int i = 0; i < loanCount; i++) {
	            Loan loan = loans[i];
	            if (loan.getUser().equalsIgnoreCase(borrower)) {
	                System.out.printf("%-10d | %-30s | %-20s%n",
	                        loan.getLoanId(),
	                        loan.getBook().getTitle(),
	                        loan.getUser());
	                borrowedBooks++;
	            }
	        }

	        if (borrowedBooks == 0) {
	        	Common.doubleLineBreak();
	        	System.out.println("No borrowed books for " + borrower + ".");
	        }
	    } else {
	    	Common.doubleLineBreak();
	    	System.out.println("No loans. Library is empty or all books are available.");
	    }
			
	    return borrowedBooks;
	};
	public void borrowBook(Integer bookId, String borrower) {		
		for (int i = 0; i < bookCount; i++) {
	        if (books[i].getId() == bookId) {
	            if (books[i].getIsAvailable()) {	              
	                if (loanCount < Loan.getBorrowLimit()) {	  
	                	Loan loan = new Loan(books[i], borrower);
	                    loans[loanCount++] = loan;
	                    System.out.println(books[i].getTitle() + " has been loaned. Loan ID: " + loan.getLoanId());
	                } else {
	                    System.out.println("You have reached the limit of maximum books that a person can borrow.");
	                    System.out.println("Please return a book to be able to borrow again.");
	                }
	            } else {
	                System.out.println("Sorry, " + books[i].getTitle() + " is already borrowed.");
	            }
	            return;
	        }
	    }
	    System.out.println("Book with ID " + bookId + " not found.");
	};
	public void returnBook(int loanId) {
	
		 for (int i = 0; i < loanCount; i++) {
		        if (loans[i].getLoanId() == loanId) {
		            loans[i].getBook().setIsAvailable(true);

		            for (int j = i; j < loanCount - 1; j++) {
		                loans[j] = loans[j + 1];
		            }
		            loans[loanCount - 1] = null;
		            loanCount--;

		            System.out.println("Loan ID " + loanId + " has been closed.");
		            return;
		        }
		    }
		    System.out.println("Loan with ID " + loanId + " not found.");
  
	};
}
