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
package m4_group6project1;


import java.util.Scanner;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import m4_group6project1.exception.InvalidBookIdException;


public class LibraryApplication {
	private static final Logger logger = LoggerFactory.getLogger(LibraryApplication.class);
	private User user;
	private Library library;	

	// Main Application Logic, call this in your Main.java
	public void start() {
		//This is to create a User with a name and ID.
		this.user = new User();
		Scanner sc = new Scanner(System.in);		
		setUser(user, sc);				
		
		//Creation of Library object.
		this.library = new Library();			
		this.library.setBookCapacityLimit(5);
		this.library.setBorrowLimit(5);
		
		//Set loan and book capacity to be no longer limited
		this.library.setBookCapacityLimited(false);
		this.library.setLoanLimited(false);
		
		//Initialization of Book objects and adding them to the library.
		this.library.initializeBooks();
		
		String option = "";
		DisplayFormatter df = new DisplayFormatter();
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
			logger.info("User selected option='{}'", option);
			df.doubleLineBreak();
			switch (option.trim()) {
				case "1":
					logger.info("Processing option [1] Display All Books.");
					df.displayAllBooks(this.library);
					break;
				case "2":
					logger.info("Processing option [2] Display Available Books.");
 					df.displayAvailableBooks(this.library);
					break;
				case "3":
					logger.info("Processing option [3] Display All Borrowed Books.");
 					df.displayBorrowedBooks(this.library);
					break;
				case "4":
					logger.info("Processing option [4] Borrow Book.");
					df.displayAvailableBooks(this.library);
					borrowBook(this.library, sc, this.user);
					break;
				case "5":
					logger.info("Processing option [5] Return Book.");
					df.displayBorrowedBooks(this.library);
					returnBook(this.library, sc);
					break;
				case "6":
					logger.info("Processing option [6] Add Book.");
					addBook(this.library, sc);
					break;
				case "7":
					logger.info("Processing option [7] Remove Book.");
					df.displayAllBooks(this.library);
					removeBook(this.library, sc);
					break;
				case "8":
					logger.info("Processing option [8] Update Book.");
					df.displayAllBooks(this.library);
					updateBook(this.library, sc);
					break;
				case "0":
					logger.info("Processing option [0] Exit.");
					System.out.println("Exiting...");	
					break;
				default:
					System.out.println("Please enter a valid option from the menu.");
					logger.warn("Invalid menu option entered: '{}'", option);
					break;
			}
		} while(!option.equals("0"));
		sc.close();
	}
	
	private void setUser(User user, Scanner sc){	
		String name = null;
	    try {
		    System.out.println("🤗 Welcome to Group-6 Library System 🤗");

			do {
			    System.out.print("\nPlease enter your name: ");
			    name = sc.nextLine();
			    if (name == null || name.trim().isEmpty()) {
			        System.out.println("Name cannot be blank so that I may address you properly.");
			        logger.warn("Input read for user's name was blank.");
			    }
			} while (name == null || name.trim().isEmpty());
			user.setName(name.trim());

	        System.out.println("\n👋😄 Hi there, " + user.getName() + ".");
	        System.out.println("You are now logged in.\nYour user ID is " + user.getUserID() + ".");
	        logger.info("User login initialized: name='{}', id={}", user.getName(), user.getUserID());

	    } catch (NoSuchElementException nse) {
	        // Thrown if input is closed or no line is available
	        System.out.println("\nWe couldn't read your input (no line available). Please restart and try again.");
	        logger.error("NoSuchElementException while reading user name.", nse);

	    } catch (IllegalStateException ise) {
	        // Thrown if Scanner is closed
	        System.out.println("\nInput is currently unavailable. Please restart the program.");
	        logger.error("Scanner was in an illegal state (likely closed).", ise);

	    } catch (Exception ex) {
	        // Catch-all for unexpected issues
	        System.out.println("\nAn unexpected error occurred while setting up your profile. Please try again.");
	        logger.error("Unexpected error in setUser()", ex);
	    }
	}
		
	private void borrowBook(Library lib, Scanner sc, User user) {	
		if (lib.isLoanLimited() && (lib.countUserLoan(user) >= lib.getborrowLimitPerUser())) {
			System.out.println("Library system has detected that the user [" + user.getName() + "] with ID [" + user.getUserID()
					+ "]\n has reached the limit of books that can be borrowed.");
            System.out.println("User should return a book to be able to borrow again.");
            logger.warn("User {} (id={}) reached borrow limit={}", user.getName(), user.getUserID(), lib.getborrowLimitPerUser());
			return;
		}
		
		if (lib.getBooks().size() == 0) {
			System.out.println("Library is empty. No book can be borrowed.");
			logger.info("No books in library. Borrow aborted.");
			return;
		}
		if (lib.checkForAvailableBook(lib.getBooks())) {			
			System.out.print("\nEnter Book ID to borrow: ");
			logger.info("Prompted for Book ID to borrow.");
			if (sc.hasNextInt()) {
				int bookID = Integer.parseInt(sc.nextLine().trim());
				logger.info("User entered bookID={} to borrow.", bookID);
				lib.borrowBook(bookID, user);
			} else {
				System.out.println("Input was not a valid ID number.");
				logger.warn("User entered a non-integer for Book ID for borrowBook.");
				sc.nextLine();
			}	
		} else {
			System.out.println("All books have been borrowed! Sorry :(");
			logger.info("No available books can be borrowed.");
		}
	}
	
	private void returnBook(Library lib, Scanner sc) {		
		if (lib.getLoans().size() == 0) {
			System.out.println("There is currently no borrowed book to return.");
			logger.info("No existing loans; return aborted.");
			return;
		}
		System.out.print("\nEnter Loan ID to be closed: ");
		logger.info("Prompted for Loan ID to return.");
		if (sc.hasNextInt()) {
			int loanId = Integer.parseInt(sc.nextLine().trim());
			logger.info("User entered loanId={} to close.", loanId);
			lib.returnBook(loanId);
		} else {
			System.out.println("Input was not a valid ID number.");
			logger.warn("User entered a non-integer for Loan ID for returnBook.");
			sc.nextLine();
		}	
		
	}
	
	private void addBook(Library lib, Scanner sc) {	
		try {
			if (lib.isBookCapacityLimited() && (lib.getBooks().size() >= lib.getBookCapacityLimit())) {
				System.out.println("Library's book capacity limit is reached. Books can no longer be added.");
				logger.warn("Capacity reached; cannot add book.");
				return;
			}
			
			System.out.println("Please input the details of book to be added.\n");
			System.out.print("Enter Book ID to be added: ");
			logger.info("Prompted user for Book ID to add.");
			if (!sc.hasNextInt() ) {
				System.out.println("Input was not a valid ID number.");
				logger.warn("Non-integer entered for Book ID to add.");
				sc.nextLine();			
				return;
			}
				
			int bookID = Integer.parseInt(sc.nextLine().trim());		
			logger.info("User entered bookID={} to add.", bookID);
			
			if (bookID < 1) {
				throw new InvalidBookIdException("User inputted invalid book ID: " + bookID);
			}
			
			String[] existingBookDetails = lib.getExistingBookDetails(lib.getBooks(), bookID);
			if (existingBookDetails != null) {
				System.out.println("Book ID [" + bookID + "] is already existing in the system and being used for below book.");
				System.out.println("        Title  - " + existingBookDetails[1]);
				System.out.println("        Author - " + existingBookDetails[2]);
				System.out.println("Adding of duplicate IDs is not allowed.");
				//If book ID to be added was found to be already existing in books list, 
				//this gets a number for book ID that is not yet used.
				int nextAvailableBookID = lib.checkFreeBookID(lib.getBooks());
				System.out.println("The next available book ID is " + nextAvailableBookID + ".");
				logger.warn("Duplicate book ID {}; suggested next available={}", bookID, nextAvailableBookID);
			} else {
				System.out.print("Enter Book Title: ");
				logger.info("Prompted user for Book Title.");
				String bookTitle = sc.nextLine().trim();
				
				System.out.print("Enter Author: ");
				logger.info("Prompted for Book Author.");
				String author = sc.nextLine().trim();
				//This adds to book list and returns true after successfully adding.
				if (lib.addBook(bookID, bookTitle, author)) {
					System.out.println("\nBook was successfully added.");
					logger.info("Displayed to user that book was added successfully");
				}

			}
		} catch (NumberFormatException e) {
			logger.warn("NumberFormatException while adding a book.", e);
		} catch (InvalidBookIdException e) {
			System.out.println("User should input a valid Book ID not lower than 1.");
			//This gets the next available number for book ID.
			System.out.println("Please use the next available book ID which is # " + lib.checkFreeBookID(lib.getBooks()) + ".");
			logger.warn("User tried to input an invalid bookID in UI. " + e.getMessage());
		}
		
	}
	
	private void removeBook(Library lib, Scanner sc) {	
		System.out.print("\nEnter ID of book to be removed: ");
		logger.info("Prompted user for Book ID to remove.");
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
		System.out.print("\nEnter ID of book to be updated: ");
		logger.info("Prompted user for Book ID to update.");
		if (sc.hasNextInt()) {
			int bookID = Integer.parseInt(sc.nextLine().trim());
			logger.info("User entered bookID={} to update.", bookID);
			Book bookToBeUpdated = lib.getBookRefByID(bookID);
			if (bookToBeUpdated == null) {
				logger.warn("No book found for id={}. No book was updated.", bookID);
				return;
			}
			if (bookToBeUpdated.getIsAvailable()) {
				System.out.println("Book's current title: [" + bookToBeUpdated.getTitle() + "]"); 
				System.out.print("Enter new Title (leave blank and hit enter key to keep current): ");
				logger.info("Prompted user for new Title.");
				String newTitle = sc.nextLine().trim();
				if (!newTitle.isEmpty()) {
					bookToBeUpdated.setTitle(newTitle);
					logger.info("Title updated to '{}'", newTitle);
				}
				System.out.println("Book's current author: [" + bookToBeUpdated.getAuthor() + "]"); 
				System.out.print("Enter new Author (leave blank and hit enter key to keep current): ");
				logger.info("Prompted user for new Author.");
				String newAuthor = sc.nextLine().trim();
				if (!newAuthor.isEmpty()) {
					bookToBeUpdated.setAuthor(newAuthor);
					logger.info("Author updated to '{}'", newAuthor);
				}
				if (newTitle.isEmpty() && newAuthor.isEmpty()) {
					System.out.println("\nNo changes were made to the book.");
					logger.info("No changes applied to book id={}", bookID);
				} else {
					System.out.println("\nBook has been updated.");
					System.out.println("Book ID [" + bookToBeUpdated.getId() + "] details: ");
					System.out.println("Title:  [" + bookToBeUpdated.getTitle() + "]");
					System.out.println("Author: [" + bookToBeUpdated.getAuthor() + "]");
                    logger.info("Book updated id={}, title='{}', author='{}'",
                            bookToBeUpdated.getId(), bookToBeUpdated.getTitle(), bookToBeUpdated.getAuthor());
				}				
			} else {
				System.out.println("Book ["+ bookToBeUpdated.getTitle() + "] is currently loaned and is not allowed to be updated.");
				logger.warn("Attempted to update loaned book but was not allowed. "
						+ "id={}, title='{}'", bookToBeUpdated.getId(), bookToBeUpdated.getTitle());
			}
		} else {
			System.out.println("Input was not a valid ID number.");
			logger.warn("User entered a non-integer for Book ID to update.");
			sc.nextLine();
		}	
	}
}
