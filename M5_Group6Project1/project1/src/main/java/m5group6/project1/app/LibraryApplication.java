/*
 * 1. Upon application start, ask user to create one User
 * 2. Create one Library object
 * 3. Initialize 5 Book objects and add it to all Library slots
 * 4. Display options:
 * 
 * - [1] Display All Books
 * - [2] Display Available Books
 * - [3] Display All Borrowed Books
 * - [4] Borrow Book
 * - [5] Return Book
 * - [6] Add Book
 * - [7] Remove Book
 * - [8] Update Book
 * - [0] Exit
 * 
 * - user selects the number of the option
 * ===============================================
 * 
 *	 [1] Display All Books
 * - Display all Books (ID, Title and Author) regardless if there is a Loan existing for that Book.
 *   
 *   [2] Display Available Books
 * - Display Books that do not have a Loan slot
 * 
 *   [3] Display All Borrowed Books 
 * - Display Books that have a Loan equivalent.
 * - Display the Book title and the User name of borrower
 *   
 *	 [4] Borrow Book
 * - Displays all available books and User selects what book to borrow
 * - Create a Loan object, set Loan id set Book and set User to current user
 * 
 * 	 [5] Return Book
 * - Display all Loans, user selects the Loan and removes that from the slot.
 * 
 *   [6] Add Book
 * - User can add a new book to the List<Book> by entering ID, Title, and Author.
 * - An existing ID should not be allowed.
 *   
 *   [7] Remove Book
 * - User can remove a book from the List<Book> by ID.
 * - Any associated loan is also removed from the List<Loan>.
 * 
 *   [8] Update Book 
 * - User can update the Title and/or Author of an existing book in the List<Book>.
 * - Books that are currently borrowed/loaned should not be updatable.
 *   
 *   [0] Exit   
 * - Stops the program  
 * */
package m5group6.project1.app;


import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import m5group6.project1.dao.BookDAO;
import m5group6.project1.dao.DAOFactory;
import m5group6.project1.dao.LoanDAO;
import m5group6.project1.dao.UserDAO;
import m5group6.project1.exceptions.InvalidBookIdException;
import m5group6.project1.exceptions.InvalidDBInputException;
import m5group6.project1.model.Book;
import m5group6.project1.model.User;
import m5group6.project1.service.Library;
import m5group6.project1.util.DBInputValidator;
import m5group6.project1.util.DisplayFormatter;

public class LibraryApplication {
	private static final Logger logger = LoggerFactory.getLogger(LibraryApplication.class);
	private User user;
	private Library library;	
	private final DisplayFormatter df = new DisplayFormatter();

	// Main Application Logic, call this in your Main.java
	public void start() {
		//This is to create a User with a name and ID.
		this.user = new User();
		Scanner sc = new Scanner(System.in);		
		
		//Creation of Library object.
        BookDAO bookDAO = DAOFactory.bookDAO();
        LoanDAO loanDAO = DAOFactory.loanDAO();
        UserDAO userDAO = DAOFactory.userDAO();
        this.library = new Library(bookDAO, loanDAO, userDAO);

		this.library.setBookCapacityLimit(5);
		this.library.setBorrowLimit(5);
		
		//Set loan and book capacity to be no longer limited
		this.library.setBookCapacityLimited(false);
		this.library.setLoanLimited(false);
		
		//Set user name and ID
		setUser(this.library, user, sc);
						
		String option = "";
		
		do {
			df.doubleLineBreak();
			System.out.println("\n   ===[      LIBRARY PROGRAM MENU      ]===");
			System.out.println("         [1] Display All Books");
			System.out.println("         [2] Display Available Books");
			System.out.println("         [3] Display All Borrowed Books");
			System.out.println("         [4] Borrow Book");
			System.out.println("         [5] Return Book");
			System.out.println("         [6] Add Book");
			System.out.println("         [7] Remove Book");
			System.out.println("         [8] Update Book");
			System.out.println("         [0] Exit");
			System.out.print("\nEnter chosen option >> ");
			option = sc.nextLine();
			logger.trace("User selected option='{}'", option);
			df.doubleLineBreak();
			switch (option.trim()) {
				case "1":
					logger.trace("Processing option [1] Display All Books.");
					this.library.displayAllBooks();
					break;
				case "2":
					logger.trace("Processing option [2] Display Available Books.");
					this.library.displayAvailableBooks();
					break;
				case "3":
					logger.trace("Processing option [3] Display All Borrowed Books.");
					this.library.displayBorrowedBooks();
					break;
				case "4":
					logger.trace("Processing option [4] Borrow Book.");
					this.library.displayAvailableBooks();
					borrowBook(this.library, sc, this.user);
					break;
				case "5":
					logger.trace("Processing option [5] Return Book.");
					this.library.displayBorrowedBooks();
					returnBook(this.library, sc);
					break;
				case "6":
					logger.trace("Processing option [6] Add Book.");
					addBook(this.library, sc);
					break;
				case "7":
					logger.trace("Processing option [7] Remove Book.");
					this.library.displayAllBooks();
					removeBook(this.library, sc);
					break;
				case "8":
					logger.trace("Processing option [8] Update Book.");
					this.library.displayAllBooks();
					updateBook(this.library, sc);
					break;
				case "0":
					logger.trace("Processing option [0] Exit.");
					System.out.println("Thank you for visiting the library and using the system. Goodbye 👋😊📖");	
					break;
				default:
					System.out.println("Please enter a valid option from the menu.");
					logger.warn("Invalid menu option entered: '{}'", option);
					break;
			}
		} while(!option.equals("0"));
		sc.close();
	}
	

	private void setUser(Library lib, User user, Scanner sc){
	    String name = null;
	    try {
	        System.out.println("🤗 Welcome to Group-6 Library System 🤗");
	        boolean isNameLengthInvalid;
	        do {
	            System.out.print("\nPlease enter your name: ");
	            name = sc.nextLine();
	            
	            isNameLengthInvalid = name.trim().length() > DBInputValidator.getMaxUserNameLength();
				if (isNameLengthInvalid) {
				    System.out.println("Name is too long and not allowed. Maximum allowed is "
				        + DBInputValidator.getMaxUserNameLength() + " characters.");
				    continue;
				}

	            if (name == null || name.trim().isEmpty()) {
	                System.out.println("Name cannot be blank so that I may address you properly.");
	                logger.warn("User entered blank name in UI.");
	            }
	        } while (name == null || name.trim().isEmpty() || isNameLengthInvalid);
	        user.setName(name.trim());
	        
	        //This will add the user to DB and return the generated user ID.	
        	user.setUserID(lib.addUser(user));
	        System.out.println("\n👋😄 Hi there, " + user.getName() + ".");
	        System.out.println("You are now logged in.\nYour user ID is " + user.getUserID() + ".");
	        logger.info("User login initialized: name='{}', id={}", user.getName(), user.getUserID());

	    } catch (InvalidDBInputException ex) {
        	System.out.println("\nWe couldn't save your profile because name inputted is invalid.");
        	logger.error("Failed to add user due to invalid DB input.", ex);
	    } catch (RuntimeException ex) {
        	System.out.println("\nWe couldn't save your profile due to a system error. Please try again.");
        	logger.error("Failed to add user", ex);
	    } catch (Exception ex) {
	        System.out.println("\nAn unexpected error occurred while setting up your profile. Please try again.");
	        logger.error("Unexpected error occurred in setUser()", ex);
	    }
	}

		
	private void borrowBook(Library lib, Scanner sc, User user) {	
		
		// This is to check if user reached borrow limit.
		if (lib.isLoanLimited() && (lib.countUserLoan(user) >= lib.getborrowLimitPerUser())) {
			System.out.println("\nLibrary system has detected that the user [" + user.getName() + "] with ID [" + user.getUserID()
					+ "]\n has reached the limit of books that can be borrowed.");
            System.out.println("User should return a book to be able to borrow again.");
            logger.warn("User {} (id={}) reached borrow limit={}", user.getName(), user.getUserID(), lib.getborrowLimitPerUser());
			return;
		}
		
		//This is to check if there are books in the library or if it is empty.
    	if (lib.getAllBooks().size() == 0) {
			System.out.println("No book can be borrowed.");
			logger.warn("No books in library. User is unable to borrow.");
    		return;
    	}

		//This is to check if there is still an available book that can be borrowed.
		if (!lib.checkForAvailableBook(lib.getAllBooks())) {		
			System.out.println("All books have been borrowed! Sorry :(");
			logger.info("No available books can be borrowed.");
			return;
		}
			
		//Proceed if there is available book that can be borrowed and request user input for Book ID to borrow.
		System.out.print("\nEnter Book ID to borrow: ");
		logger.trace("Prompted for Book ID to borrow.");
		if (sc.hasNextInt()) {
			int bookID = Integer.parseInt(sc.nextLine().trim());
			logger.trace("User entered bookID={} to borrow.", bookID);
			
			//This is to search book by ID and borrow book for user.
			lib.borrowBook(bookID, user);
			
		} else {
			
			//This is for user input for book ID that is not an integer.
			System.out.println("Input was not a valid ID number.");
			logger.warn("User entered a non-integer for Book ID for borrowBook.");
			sc.nextLine();
		}	
		
	}
	
	private void returnBook(Library lib, Scanner sc) {		
		//This is to check if there are any borrowed books.
		if (lib.getLoans().size() == 0) {
			System.out.println("There is currently no borrowed book to return.");
			logger.info("No existing loans; return aborted.");
			return;
		}
		
		//Proceed if there is a book that has been borrowed and request user input for loan ID.
		System.out.print("\nEnter Loan ID to be closed: ");
		logger.info("Prompted for Loan ID to return.");
		if (sc.hasNextInt()) {
			int loanId = Integer.parseInt(sc.nextLine().trim());
			logger.info("User entered loanId={} to close.", loanId);
			lib.returnBook(loanId);
		} else {
			
			//This is for user input for loan ID that is not an integer.
			System.out.println("Input was not a valid ID number.");
			logger.warn("User entered a non-integer for Loan ID for returnBook.");
			sc.nextLine();
		}	
		
	}
	
	private void addBook(Library lib, Scanner sc) {	
		try {
			if (lib.isBookCapacityLimited() && (lib.getAllBooks().size() >= lib.getBookCapacityLimit())) {
				System.out.println("Library's book capacity limit is reached. Books can no longer be added.");
				logger.warn("Capacity reached; cannot add book.");
				return;
			}
			
			System.out.println("Please input the details of book to be added.\n");
			System.out.print("Enter Book ID to be added: ");
			logger.trace("Prompted user for Book ID to add.");
			if (!sc.hasNextInt() ) {
				System.out.println("Input was not a valid ID number.");
				logger.warn("User entered a non-integer for Book ID to add.");
				sc.nextLine();			
				return;
			}
				
			int bookID = Integer.parseInt(sc.nextLine().trim());		
			logger.debug("User entered bookID={} to add.", bookID);
			
			if (bookID < 1) {
				System.out.println("User should input a valid Book ID not lower than 1.");
				//This gets the next available number for book ID.
				System.out.println("Please use the next available book ID which is # " + lib.checkFreeBookID(lib.getAllBooks()) + ".");
				throw new InvalidBookIdException("\nUser inputted invalid book ID that is < 1 : " + bookID);
			}
			
			String[] existingBookDetails = lib.getExistingBookDetails(lib.getAllBooks(), bookID);
			if (existingBookDetails != null) {
				System.out.println("Book ID [" + bookID + "] is already existing in the system and being used for below book.");
				System.out.println("        Title  - " + existingBookDetails[1]);
				System.out.println("        Author - " + existingBookDetails[2]);
				System.out.println("Adding of duplicate IDs is not allowed.");
				//If book ID to be added was found to be already existing in books list, 
				//this gets a number for book ID that is not yet used.
				int nextAvailableBookID = lib.checkFreeBookID(lib.getAllBooks());
				System.out.println("The next available book ID is " + nextAvailableBookID + ".");
				logger.warn("User entered duplicate book ID {}; System suggested next available={}", bookID, nextAvailableBookID);
				throw new InvalidBookIdException("\nUser inputted invalid duplicate book ID : " + bookID);
			} else {
				System.out.print("Enter Book Title: ");
				logger.trace("Prompted user for Book Title.");
				String bookTitle = sc.nextLine().trim();
				
				//Validate book title based on max length defined in DB
				if (bookTitle.length() > DBInputValidator.getMaxBookTitleLength()) {
				    System.out.println("Title is too long. Maximum characters allowed is " 
				        + DBInputValidator.getMaxBookTitleLength() + ".");
				    return;
				}

				
				System.out.print("Enter Author: ");
				logger.trace("Prompted for Book Author.");
				String author = sc.nextLine().trim();
				
				//Validate author based on max length defined in DB
				if (author.length() > DBInputValidator.getMaxBookAuthorLength()) {
				    System.out.println("Author is too long. Maximum characters allowed is " 
				        + DBInputValidator.getMaxBookAuthorLength() + ".");
				    return;
				}

				
				//This adds to book list and returns true after successfully adding.
				if (lib.addBook(bookID, bookTitle, author)) {
					System.out.println("\nBook was successfully added.");
					logger.trace("Displayed to user that book was added successfully");
				}

			}
		} catch (NumberFormatException e) {
			logger.warn("NumberFormatException while user is adding a book. ", e);
		} catch (InvalidBookIdException e) {
			logger.warn("User tried to input an invalid bookID in UI. ", e);
		} catch (Exception ex) {
	        System.out.println("\nAn unexpected error occurred while adding a book. Please try again.");
	        logger.error("Unexpected error occurred in UI addbook()", ex);
	    }
		
	}
	
	private void removeBook(Library lib, Scanner sc) {	
		//This is to check if there are books in the library or if it is empty.
    	if (lib.getAllBooks().size() == 0) {
			System.out.println("No book to remove.");
			logger.warn("No books in library. User is unable to borrow.");
    		return;
    	}
    	
		System.out.print("\nEnter ID of book to be removed: ");
		logger.trace("Prompted user for Book ID to remove.");
		if (sc.hasNextInt()) {
			int bookID = Integer.parseInt(sc.nextLine().trim());
			logger.info("User entered bookID={} to remove.", bookID);
			lib.removeBook(bookID);
		} else {
			System.out.println("Input was not a valid ID number.");
			logger.warn("User entered a non-integer for Book ID to remove.");
			sc.nextLine();
		}	
	}
	
	private void updateBook(Library lib, Scanner sc) {
		if(this.library.getAllBooks().size() == 0) {
			System.out.println("No book to update.");
			return;
		}
		
	    System.out.print("\nEnter ID of book to be updated: ");
	    logger.trace("Prompted user for Book ID to update.");
	    if (sc.hasNextInt()) {
	        int bookID = Integer.parseInt(sc.nextLine().trim());
	        logger.info("User entered bookID={} to update.", bookID);

	        // Resolve the book via service (null => not found)
	        Book bookToBeUpdated = lib.getBookRefByID(bookID);
	        if (bookToBeUpdated == null) {
	            logger.warn("No book found for id={}. No book was updated.", bookID);
	            return;
	        }

	        // Must be available; DB trigger also guards this
	        if (!Boolean.TRUE.equals(bookToBeUpdated.getIsAvailable())) {
	            System.out.println("Book [" + bookToBeUpdated.getTitle() + "] is currently loaned and is not allowed to be updated.");
	            logger.warn("Attempted to update loaned book but was not allowed. id={}, title='{}'",
	                        bookToBeUpdated.getId(), bookToBeUpdated.getTitle());
	            return;
	        }

	        // Gather new values (blank => keep current)
	        System.out.println("Book's current title: [" + bookToBeUpdated.getTitle() + "]");
	        System.out.print("Enter new Title (leave blank and hit enter key to keep current): ");
	        logger.trace("Prompted user for new Title.");
	        String newTitle = sc.nextLine().trim();

	        //Validate new title based on max length defined in DB
			if (!newTitle.isBlank() &&
			    newTitle.length() > DBInputValidator.getMaxBookTitleLength()) {
			
			    System.out.println("New title is too long. Maximum characters allows is "
			        + DBInputValidator.getMaxBookTitleLength() + ".");
			    return;
			}

	        System.out.println("Book's current author: [" + bookToBeUpdated.getAuthor() + "]");
	        System.out.print("Enter new Author (leave blank and hit enter key to keep current): ");
	        logger.trace("Prompted user for new Author.");
	        String newAuthor = sc.nextLine().trim();
	        
	        //Validate new author based on max length defined in DB
			if (!newAuthor.isBlank() &&
				newAuthor.length() > DBInputValidator.getMaxBookAuthorLength()) {
			
			    System.out.println("New author is too long. Maximum characters allows is "
			        + DBInputValidator.getMaxBookTitleLength() + ".");
			    return;
			}

	        if (newTitle.isEmpty() && newAuthor.isEmpty()) {
	            System.out.println("\nNo changes were made to the book.");
	            logger.info("No changes applied to book id={}", bookID);
	            return;
	        }
	        
	        lib.updateBook(bookID, newTitle, newAuthor);
	        
	    } else {
	        System.out.println("Input was not a valid ID number.");
	        logger.warn("User entered a non-integer for Book ID to update.");
	        sc.nextLine();
	    }
	}
}
