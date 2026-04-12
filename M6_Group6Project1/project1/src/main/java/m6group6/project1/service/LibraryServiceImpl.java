package m6group6.project1.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

import m6group6.project1.exceptions.InvalidBookIdException;
import m6group6.project1.model.BookEntity;
import m6group6.project1.model.LoanEntity;
import m6group6.project1.model.UserEntity;
import m6group6.project1.repo.BookRepository;
import m6group6.project1.repo.LoanRepository;
import m6group6.project1.repo.UserRepository;
import m6group6.project1.util.DisplayFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LibraryServiceImpl implements LibraryService, LoanPolicy {
    private static final Logger logger = LoggerFactory.getLogger(LibraryServiceImpl.class);

    // DB-backed
    private final BookRepository bookDAO;
    private final LoanRepository loanDAO;
    private final UserRepository userDAO;

    private boolean isBookCapacityLimited = false;
    private int bookCapacityLimit;

    private boolean isLoanLimited = false;
    private int borrowLimitPerUser;
    
    private final DisplayFormatter df = new DisplayFormatter();


    public LibraryServiceImpl(BookRepository bookDAO, LoanRepository loanDAO, UserRepository userDAO) {
        this.bookDAO  = bookDAO;
        this.loanDAO  = loanDAO;
        this.userDAO  = userDAO;
    }
    

    
    @Override
    public void displayAllBooks() {
    	List<BookEntity> allBooks = getAllBooks();
    	if (isLibraryEmpty(allBooks)) {
    		return;
    	}	
    	
    	df.displayAllBooks(allBooks, "Book ID", "Title", "Author", "Status");
    }
    
    @Override
    public void displayAvailableBooks() {
    	if (isLibraryEmpty(getAllBooks())) {
    		return;
    	}

    	List<BookEntity> availableBooks = getAvailableBooks();
    	if (availableBooks.isEmpty()) {
    		System.out.println("No books are available. All books have been loaned.");
    		return;
    	}
    	
    	df.displayAvailableBooks(availableBooks, "Book ID", "Title", "Author");  
    }
    
    @Override
    public void displayBorrowedBooks() {
    	if (isLibraryEmpty(getAllBooks())) {
    		return;
    	}
    	
    	List<LoanEntity> loaned = getLoans();
    	if (loaned.size() == 0) {
    		System.out.println("There are no borrowed books at the moment.");
    		return;
    	}
    	df.displayBorrowedBooks(loaned,"Loan ID", "Title", "Borrower ID and Name");
    	
    	
    }
    
    public boolean isLibraryEmpty(List<BookEntity> allBooks) {    	
    	if (allBooks.size() == 0) {
    		System.out.println("There are no books to display. \nLibrary is empty.");
    		return true;
    	}    	
    	return false;
    }
    
    @Override
    public void borrowBook(int bookID, UserEntity borrower) {
        try {
            BookEntity bookToBorrow = getBookRefByID(bookID);
            if (bookToBorrow == null) return;

            if (Boolean.FALSE.equals(bookToBorrow.getIsAvailable())) {
                System.out.println("Sorry, " + bookToBorrow.getTitle() + " is already borrowed.");
                logger.info("A loaned book was requested: id={}, title='{}'", bookID, bookToBorrow.getTitle());
                return;
            }
            int newLoanId = loanDAO.createLoanAndMarkBookUnavailable(bookID, borrower.getUserID());
            if (newLoanId > 0) {
                System.out.println(bookToBorrow.getTitle() + " has been loaned. Loan ID: " + newLoanId);
                logger.info("Loan created loanId={}, userId={}, bookId={}, title={}",
                        newLoanId, borrower.getUserID(), bookToBorrow.getId(), bookToBorrow.getTitle());
            } else {
                System.out.println("Failed to create loan. The book may already be borrowed.");
                logger.warn("Loan creation failed for bookId={}, userId={}", bookID, borrower.getUserID());
            }
        } catch (Exception e) {
            logger.error("Unexpected error in borrowBook()", e);
            System.out.println("An unexpected error occurred while creating a loan.");
        }
    }

    @Override
    public void returnBook(int loanID) {
        try {
            LoanEntity loanToRemove = getLoanRefByID(loanID);
            if (loanToRemove == null) {
                System.out.println("Loan ID [" + loanID + "] is not existing.");
                logger.warn("No loan found with id={}", loanID);
                return;
            }
            BookEntity b = loanToRemove.getBook();
            if (loanDAO.deleteLoanAndMarkBookAvailable(loanID)) {
                System.out.println("Loan ID [" + loanID + "] has been closed.");
                System.out.println("[" + b.getTitle() + "] is now available for borrowing.");
                logger.info("Loan closed. loanId={}, book='{}'", loanID, b.getTitle());
            } else {
                System.out.println("An error was encountered. Book [" + b.getId() + "] [" + b.getTitle() + "] is still not made available.");
                logger.error("Failed to close loan id={} for book id={}, title='{}'", loanID, b.getId(), b.getTitle());
            }
        } catch (Exception e) {
            logger.error("Unexpected error in returnBook()", e);
        }
    }

    @Override
    public boolean addBook(int bookID, String bookTitle, String author) throws InvalidBookIdException {
        try {
            if (isBookCapacityLimited() && (getAllBooks().size() >= getBookCapacityLimit())) {            	
                System.out.println("Library's book capacity limit is reached. Books can no longer be added.");
                System.out.println("Book with title [" + bookTitle + "] is not added to the Library system.");
                logger.error("Book Capacity limit reached. Book not added: id={}, title='{}', author='{}'", bookID, bookTitle, author);
                return false;
            }
            
            //This is for invalid book IDs that are < 1.
            if (bookID < 1) {
            	logger.trace("Processing book ID that is < 1.");
                logger.error("Book with title [{}] and author [{}] is not added to the Library system.", bookTitle, author);
                throw new InvalidBookIdException("\nInvalid Book ID [" + bookID + "] in addBook(). ID should not be lower than 1.");
            }
            
            //This is for duplicate book ID checking against DB.
            logger.debug("Calling method findById(bookID) to check existing book IDs in DB and prevent duplicates");
            BookEntity existing = bookDAO.findById(bookID);
            if (existing != null) {
                logger.error("Book with title [{}] and author [{}] is not added due to duplicate ID.", bookTitle, author);
                throw new InvalidBookIdException("\nInvalid duplicate Book ID [" + bookID + "]. This ID already exists.");
            } else {
            	//This is to add a valid book to the DB
                BookEntity bookToBeAdded = new BookEntity(bookID, bookTitle, author);                
                logger.debug("Calling method insert(bookToBeAdded) to add a valid book to DB.");
                if (bookDAO.insert(bookToBeAdded)) {
                	
                    logger.info("Book added: id={}, title='{}', author='{}'", bookID, bookTitle, author);
                    return true;
                } else {
                    logger.error("DB insert failed. Book not added: id={}, title='{}', author='{}'", bookID, bookTitle, author);
                    System.out.println("Book was not successfully added to the system. Please contact support.");
                    return false;
                }
            }
        } catch (InvalidBookIdException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in addBook()", e);
            return false;
        }
    }

    public String[] getExistingBookDetails(List<BookEntity> books, int id) {
        try {
            BookEntity b = bookDAO.findById(id);
            if (b == null) return null;
            return new String[] { String.valueOf(id), b.getTitle(), b.getAuthor() };
        } catch (Exception e) {
            logger.error("Error in getExistingBookDetails", e);
            return null;
        }
    }

    // Compute next free ID by scanning DB list (ascending)
    public int checkFreeBookID(List<BookEntity> ignore) {
        try {
            List<BookEntity> all = getAllBooks();
            all.sort(Comparator.comparingInt(BookEntity::getId));
            int counter = 1;
            for (BookEntity b : all) {
                if (counter < b.getId()) return counter;
                counter++;
            }
            return counter;
        } catch (Exception e) {
            logger.error("Error computing next free Book ID", e);
            return 1;
        }
    }

    // DB-backed availability check
    public boolean checkForAvailableBook(List<BookEntity> ignore) {
        try {
            return !getAvailableBooks().isEmpty();
        } catch (Exception e) {
            logger.error("Error checking available books", e);
            return false;
        }
    }

    public BookEntity getBookRefByID(int bookID) {
        try {
            BookEntity b = bookDAO.findById(bookID);
            if (b != null) return b;
            
            //This is for no book found based on the bookID.
            System.out.println("There is no book with ID [" + bookID + "] in the system.");
            logger.warn("No book found with id={} via getBookRefByID().", bookID);
        } catch (Exception e) {
            logger.error("Error in getBookRefByID", e);
        }
        return null;
    }

    public LoanEntity getLoanRefByID(int loanID) {
        try {
            return loanDAO.findById(loanID);
        } catch (Exception e) {
            logger.error("Error in getLoanRefByID", e);
            return null;
        }
    }

    @Override
    public void removeBook(int bookID) {
        try {
            BookEntity bookToRemove = getBookRefByID(bookID);
            if (bookToRemove == null) {
                return;
            }

            // 1) Look up the loan ID associated with this book (if any)
            int associatedLoanId = -1;
            try {
                associatedLoanId = loanDAO.findLoanIdByBookId(bookID); // NEW DAO method
            } catch (Exception lookupEx) {
                logger.error("Failed to look up loan for bookId={}", bookID, lookupEx);
            }

            String title = bookToRemove.getTitle();

            // 2) Delete the book (ON DELETE CASCADE should remove the loan row)
            boolean deleted = bookDAO.deleteById(bookID);
            if (!deleted) {
                System.out.println("An error occurred. The book could not be removed.");
                logger.error("Failed to delete book id={}", bookID);
                return;
            }

            System.out.println("Book with ID [" + bookID + "] and title [" + title + "] has been removed.");
            logger.info("Book removed id={}, title='{}'", bookID, title);

            // 3) If we found a loan before, confirm that it no longer exists
            if (associatedLoanId > 0) {
                boolean stillExists = true;
                try {
                    stillExists = loanDAO.existsById(associatedLoanId); 
                } catch (Exception existsEx) {
                    logger.error("Failed to confirm removal of loanId={}", associatedLoanId, existsEx);
                }

                if (!stillExists) {
                    System.out.println("Loan with ID [" + associatedLoanId + "] is associated with the book and has also been removed.");
                } else {
                    // Defensive message if cascade did not occur (should not happen with the defined DB schema)
                    System.out.println("A loan record [ID " + associatedLoanId + "] was associated but could not be confirmed as removed.");
                    logger.error("Loan still exists after book delete. loanId={}, bookId={}", associatedLoanId, bookID);
                }
            } else {
                // This is for no associated loan for the deleted book
                System.out.println("No associated loan records were found for the removed book. No loan record was needed to be deleted.");
            }
        } catch (Exception e) {
            logger.error("Unexpected error in removeBook()", e);
            System.out.println("An unexpected error occurred while removing the book.");
        }
    }
    
    @Override
    public void updateBook(int bookId, String title, String author) {
        try {
            boolean updated = bookDAO.updateIfAvailable(bookId, title, author);
            if (updated) {
                // Re-read from DB and display values
                BookEntity refreshed = bookDAO.findById(bookId);
                System.out.println("\nBook has been updated.");
                System.out.println("Book ID [" + refreshed.getId() + "] details: ");
                System.out.println("Title: [" + refreshed.getTitle() + "]");
                System.out.println("Author: [" + refreshed.getAuthor() + "]");
                logger.info("Book updated id={}, title='{}', author='{}'",
                            refreshed.getId(), refreshed.getTitle(), refreshed.getAuthor());
            } else {
                System.out.println("\nBook ["+ title + "] is currently loaned and is not allowed to be updated.");
                logger.warn("No row updated for book id={}. Possible concurrent change or unavailable state.", bookId);
            }
        } catch (Exception e) {
            System.out.println("\nAn unexpected error occurred while updating the book. Please try again.");
            logger.error("Unexpected error occurred in UI updateBook()", e);
        }
    }
    
	public int addUser(UserEntity user) {
		return userDAO.insert(user);
	}


    public int countUserLoan(UserEntity borrower) {
        try {
            return loanDAO.countActiveLoansByUser(borrower.getUserID());
        } catch (Exception e) {
            logger.error("Error counting loans for userId={}", borrower.getUserID(), e);
            return 0;
        }
    }

    // Extracts all books from DB.
    public List<BookEntity> getAllBooks() {
        try {
            return bookDAO.findAll();
        } catch (Exception e) {
            logger.error("Error fetching books", e);
            return new ArrayList<>();
        }
    }

    public List<BookEntity> getAvailableBooks() {
        try {
            return bookDAO.findAllAvailable();
        } catch (Exception e) {
            logger.error("Error fetching available books", e);
            return new ArrayList<>();
        }
    }

    public List<LoanEntity> getLoans() {
        try {
            return loanDAO.findAllActive();
        } catch (Exception e) {
            logger.error("Error fetching loans", e);
            return new ArrayList<>();
        }
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
