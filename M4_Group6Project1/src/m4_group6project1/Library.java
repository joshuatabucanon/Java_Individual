package m4_group6project1;

import java.util.ArrayList;
import java.util.List;

import m4_group6project1.exception.InvalidBookIdException;

import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Library implements LoanPolicy {
	private static final Logger logger = LoggerFactory.getLogger(Library.class);
	
    private List<Book> books = new ArrayList<>();
    private boolean isBookCapacityLimited = false;
    private int bookCapacityLimit;
    
	private List<Loan> loans = new ArrayList<>();
    private boolean isLoanLimited = false;
    private int borrowLimitPerUser;
    	
    public void initializeBooks() {
    	//Initialization of 5 books.
        Object[][] initialBooks = new Object[][] {
            {-1, "The Testing of Custom Exception", "Tester"}, //For testing custom exception and error log
            {1, "The Lord of the Rings", "JRR Tolkien"},
            {4, "Demon Slayer Volume 21", "Koyoharu Gotouge"},
            {2, "One Piece Volume 101", "Eichiro Oda"},
            {3, "Gachiakuta Volume 1", "Kei Urana"},
            {5, "Look Back", "Tatsuki Fujimoto"}
        };
        for (Object[] initialBook : initialBooks) {
	        int bookID = (int) initialBook[0];
	        String bookTitle = (String) initialBook[1];
	        String author = (String) initialBook[2];
	
	        try {
	        	logger.info("Adding initial book: id={}, title='{}', author='{}'", bookID, bookTitle, author);
	            addBook(bookID, bookTitle, author); 
	        } catch (InvalidBookIdException e) {
	            logger.error("Error encountered in adding initial book due to invalid book ID. " + e.getMessage());
	            logger.error("Skipped adding initial book: id={}, title='{}', author='{}'", bookID, bookTitle, author);
	        }
	    }
	
    }
	
	public boolean addBook(int bookID, String bookTitle, String author) throws InvalidBookIdException {
		if (isBookCapacityLimited() && (getBooks().size() >= getBookCapacityLimit())) {
			System.out.println("Library's book capacity limit is reached. Books can no longer be added.");
			System.out.println("Book with title [" + bookTitle + "] is not added to the Library system.");
			logger.error("Book Capacity limit reached. "
					+ "This book is not added to the system: id={}, title='{}', author='{}'", bookID, bookTitle, author);
			return false;
		}
		
		if (bookID < 1) {
			logger.error("Book with title [{}] and author [{}] is not added to the Library system.", bookTitle, author);
			throw new InvalidBookIdException("Invalid Book ID input [" + bookID + "]. Book ID number should not be lower than 1.");
		}
		
		//This checks if book ID that will be added is already existing in books list.
		if (getExistingBookDetails(this.books, bookID) != null) {
			System.out.println("Book ID [" + bookID + "] is already existing in the system. Duplicate book ID is not allowed.");
			System.out.println("Book with title [" + bookTitle + "] is not added to the Library system.");
			return false;
		} else {					
			//lastBookID will be used to check if books list needs to be sorted after adding new element.
			int lastBookID = 0;
			if (this.books.size() > 0) {
				//Get index of the current last element in books list then get its bookID
				lastBookID = this.books.get(this.books.size() - 1).getId();
			}
			Book bookToBeAdded = new Book(bookID, bookTitle, author);
			//Add to books list if book ID is not yet existing. Returns true if book was successfully added to books list.
		    if (this.books.add(bookToBeAdded)) {
			    /*
			     * New elements are added at the end of the list, so this will 
			     * only sort the list if the book ID that was added at the end of the list is less than the book ID before it
			     * (i.e. compare ID of last element vs ID of 2nd to the last element).
			     */
			    if (bookToBeAdded.getId() < lastBookID) {
			    	//This sorts books list by ascending book ID.
			        this.books.sort(Comparator.comparingInt(Book::getId));
			    }
			    logger.info("Book added: id={}, title='{}', author='{}'", bookID, bookTitle, author);
			    return true;
		    } else {
		    	logger.error("Error encountered in Collection.add(). This book was not added: id={}, title='{}', author='{}'", bookID, bookTitle, author);
		    	System.out.println("Book with ID [" + bookToBeAdded.getId() + "] "
		    						+ "and title [" + bookToBeAdded.getTitle() + "] was not successfully added to the system."
		    								+ "Please contact system support for assistance and checking of logs.");
		    }  			
		}
		return false;
	}
	
	public String[] getExistingBookDetails(List<Book> books, int id) {
		String[] bookDetails = null;
		//Loop books list to check if book ID is already existing.	
		for (Book book : books) {
			if (book.getId() == id) {
				bookDetails = new String[3];
				bookDetails[0] = id + "";
				bookDetails[1] = book.getTitle();
				bookDetails[2] = book.getAuthor();
				return bookDetails;
			} 			
		}
		return bookDetails;
	}
	
	public int checkFreeBookID(List<Book> books) {
		//This is for getting the next lowest available book ID starting from 1 onwards until last element of books list.
		int counter = 1;
		
		for (Book book : books) {
			if (counter < book.getId()) {
				return counter;
			} else {
				counter++;
			}			
		}
		return counter;
	}
	
	public boolean checkForAvailableBook(List<Book> books) {
		//This checks if there is 1 book available.
		for (Book book : books) {
			if (book.getIsAvailable()) {
				return true;
			}
		}
		return false;
	}
	
    public Book getBookRefByID(int bookID) {
    	//Search books list by book ID to get object reference if ID is found. This prints a message if no match found.
        for (int i = 0; i < this.books.size(); i++) {
            if (this.books.get(i).getId() == bookID) {
            	Book b = this.books.get(i);
                return b;
            }
        }
        //This is for no ID match found in books list.
        System.out.println("There is no book with ID [" + bookID + "] in the system.");
        logger.warn("No book found with id={} via getBookRefByID().", bookID);
        return null;
    }
    
    public Loan getLoanRefByID(int loanID) {
    	//Search loans list by loan ID to get object reference if ID is found
        for (int i = 0; i < this.loans.size(); i++) {
            if (this.loans.get(i).getLoanId() == loanID) {
            	Loan l = this.loans.get(i);
                return l;
            }
        }
        return null;
    }
    		
	public void borrowBook(int bookID, User borrower) {
		Book bookToBorrow = getBookRefByID(bookID);		
		if (bookToBorrow == null) {
			return;
		}		
        if (!bookToBorrow.getIsAvailable()) {
        	System.out.println("Sorry, " + bookToBorrow.getTitle() + " is already borrowed.");
        	logger.info("A loaned book was being requested to be borrowed:  id={}, title='{}', author='{}'", bookID, bookToBorrow.getTitle() , bookToBorrow.getAuthor());
            return;
        }
        
        //This is for adding loan if bookToBorrow is found in the system and is available.       
        Loan newLoan = new Loan(bookToBorrow, borrower);       
        //Adds newLoan to the loans list and returns true after successfully adding.
        if (this.loans.add(newLoan)) {        	
        	System.out.println(bookToBorrow.getTitle() + " has been loaned. Loan ID: " + newLoan.getLoanId());
        	bookToBorrow.setIsAvailable(false);
        	logger.info("Loan with Loan ID={} was created for userId={}. Book with ID={} and title={} was set to unavailable.",
                    newLoan.getLoanId(), borrower.getUserID(), bookToBorrow.getId(), bookToBorrow.getTitle());
		} else {
			System.out.println("Loan ID [" + newLoan.getLoanId() + "] was not successfully added to the system.");
			logger.error("Failed to close loan id={} for book id={}, title='{}'", newLoan.getLoanId(), bookToBorrow.getId(), bookToBorrow.getTitle());
		}
        
	}
	
	public void returnBook(int loanID) {
		Loan loanToRemove = getLoanRefByID(loanID);
		if (loanToRemove == null) {
			System.out.println("Loan ID [" + loanID + "] is not existing.");
			logger.warn("No loan found with id={}", loanID);
			return;
		}
		
		Book b = loanToRemove.getBook();
		if (removeLoan(loanToRemove)) {
			b.setIsAvailable(true);	
			System.out.println("[" + b.getTitle() + "] is now available for borrowing.");
			logger.info("Loan closed. Loan id={}, book='{}'", loanID, b.getTitle());
		} else {
			System.out.println("Error encountered. Book [" + b.getId() + "] [" + b.getTitle() + "] is still not made available for borrowing.");
			logger.error("Failed to close loan id={} for book id={}, title='{}'", loanID, b.getId(), b.getTitle());
		} 
	}
	
	public boolean removeLoan(Loan loanToRemove) {		
		if (loanToRemove != null) {	
			int loanID = loanToRemove.getLoanId();
			if (this.loans.remove(loanToRemove)) {
				System.out.println("Loan ID [" + loanID + "] has been closed.");
				logger.info("Loan removed: Loan id={}, Borrower's User id={}, Borrower's name={}", loanID, loanToRemove.getUser().getUserID(),loanToRemove.getUser().getName());
				return true;
			}			
		}
		return false;
	}
		
	public void removeBook(int bookID) {
		Book bookToRemove = getBookRefByID(bookID);
		
		if (bookToRemove == null) {
			return;
		}		
		
		//If book is found based on the book ID, this removes it in books list.
		String title = bookToRemove.getTitle();
		if (this.books.remove(bookToRemove)) {
			System.out.println("Book with ID [" + bookID + "] and title [" + title + "] has been removed.");
			logger.info("Book removed id={}, title='{}'", bookID, title);
		}
		
		//This is to remove in loans list any loan that is associated to the bookID.
        for (int listIndex = 0; listIndex < this.loans.size(); listIndex++) {
            if (this.loans.get(listIndex).getBook().getId() == bookID) {
            	int loanID = this.loans.get(listIndex).getLoanId();
                this.loans.remove(listIndex);
                System.out.println("Loan with ID [" + loanID + "] is associated with the book and has also been removed.");
            }
        }
	}
	
	public int countUserLoan(User borrower) {
		int borrowedBooks = 0;
		int userID = borrower.getUserID();
		
        for (int listIndex = 0; listIndex < this.loans.size(); listIndex++) {
            if (this.loans.get(listIndex).getUser().getUserID() == userID) {
            	borrowedBooks++;
            }
        }
		return borrowedBooks;
	}
		
	public List<Book> getBooks() {
		return this.books;
	}

	public List<Loan> getLoans() {
		return this.loans;
	}
	
	
    public boolean isBookCapacityLimited() {
		return this.isBookCapacityLimited;
	}

	public void setBookCapacityLimited(boolean isBookCapacityLimited) {
		this.isBookCapacityLimited = isBookCapacityLimited;
	}

	public boolean isLoanLimited() {
		return this.isLoanLimited;
	}

	public void setLoanLimited(boolean isLoanLimited) {
		this.isLoanLimited = isLoanLimited;
	}

	public int getborrowLimitPerUser() {
		return this.borrowLimitPerUser;
	}

	@Override
	public void setBorrowLimit(int borrowLimit) {
		this.borrowLimitPerUser = borrowLimit;
	}	
        	
    public int getBookCapacityLimit() {
		return this.bookCapacityLimit;
	}

	public void setBookCapacityLimit(int bookCapacityLimit) {
		this.bookCapacityLimit = bookCapacityLimit;
	}
}
