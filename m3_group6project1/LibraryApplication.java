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
package m3_group6project1;

import java.util.Scanner;

public class LibraryApplication {
	
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
			df.doubleLineBreak();
			switch (option.trim()) {
				case "1":
					df.displayAllBooks(this.library);
					break;
				case "2":
 					df.displayAvailableBooks(this.library);
					break;
				case "3":
 					df.displayBorrowedBooks(this.library);
					break;
				case "4":
					df.displayAvailableBooks(this.library);
					borrowBook(this.library, sc, this.user);
					break;
				case "5":
					df.displayBorrowedBooks(this.library);
					returnBook(this.library, sc);
					break;
				case "6":
					addBook(this.library, sc);
					break;
				case "7":
					df.displayAllBooks(this.library);
					removeBook(this.library, sc);
					break;
				case "8":
					df.displayAllBooks(this.library);
					updateBook(this.library, sc);
					break;
				case "0":
					System.out.println("Exiting...");	
					break;
				default:
					System.out.println("Please enter a valid option from the menu.");
					break;
			}
		} while(!option.equals("0"));
		sc.close();
	}
	
	private void setUser(User user, Scanner sc){	
		System.out.print("Please enter your name: ");
		user.setName(sc.nextLine()); 
		
		System.out.println("Welcome to the library, " + user.getName() + ".");
		System.out.println("Your User ID is " + user.getUserID() + ".");		
	}
		
	private void borrowBook(Library lib, Scanner sc, User user) {	
		if (lib.isLoanLimited() && (lib.countUserLoan(user) >= lib.getborrowLimitPerUser())) {
			System.out.println("Library system has detected that the user [" + user.getName() + "] with ID [" + user.getUserID()
					+ "]\n has reached the limit of books that can be borrowed.");
            System.out.println("User should return a book to be able to borrow again.");
			return;
		}
		
		if (lib.getBooks().size() == 0) {
			System.out.println("Library is empty. No book can be borrowed.");
			return;
		}
		if (lib.checkForAvailableBook(lib.getBooks())) {			
			System.out.print("\nEnter Book ID to borrow: ");
			if (sc.hasNextInt()) {
				int bookID = Integer.parseInt(sc.nextLine().trim());
				lib.borrowBook(bookID, user);
			} else {
				System.out.println("Input was not a valid ID number.");
				sc.nextLine();
			}	
		} else {
			System.out.println("All books have been borrowed! Sorry :(");
		}
	}
	
	private void returnBook(Library lib, Scanner sc) {		
		if (lib.getLoans().size() == 0) {
			System.out.println("There is currently no borrowed book to return.");
			return;
		}
		System.out.print("\nEnter Loan ID to be closed: ");
		if (sc.hasNextInt()) {
			int loanId = Integer.parseInt(sc.nextLine().trim());
			lib.returnBook(loanId);
		} else {
			System.out.println("Input was not a valid ID number.");
			sc.nextLine();
		}	
		
	}
	
	private void addBook(Library lib, Scanner sc) {	
		if (lib.isBookCapacityLimited() && (lib.getBooks().size() >= lib.getBookCapacityLimit())) {
			System.out.println("Library's book capacity limit is reached. Books can no longer be added.");
			return;
		}
		
		System.out.println("Please input the details of book to be added.\n");
		System.out.print("Enter Book ID to be added: ");
		if (sc.hasNextInt()) {
			int bookID = Integer.parseInt(sc.nextLine().trim());
			String[] existingBookDetails = lib.getExistingBookDetails(lib.getBooks(), bookID);
			if (existingBookDetails != null) {
				System.out.println("Book ID [" + bookID + "] is already existing in the system and being used for below book.");
				System.out.println("        Title  - " + existingBookDetails[1]);
				System.out.println("        Author - " + existingBookDetails[2]);
				System.out.println("Adding of duplicate IDs is not allowed.");
				//If book ID to be added was found to be already existing in books list, 
				//this gets a number for book ID that is not yet used.
				System.out.println("The next available book ID is " + lib.checkFreeBookID(lib.getBooks()) + ".");
			} else {
				System.out.print("Enter Book Title: ");
				String bookTitle = sc.nextLine().trim();
				System.out.print("Enter Author: ");
				String author = sc.nextLine().trim();
				//This adds to book list and returns true after successfully adding.
				if (lib.addBook(bookID, bookTitle, author)) {
					System.out.println("\nBook was successfully added.");
				}
			}
		} else {
			System.out.println("Input was not a valid ID number.");
			sc.nextLine();
		}	
	}
	
	private void removeBook(Library lib, Scanner sc) {	
		System.out.print("\nEnter ID of book to be removed: ");
		if (sc.hasNextInt()) {
			int bookID = Integer.parseInt(sc.nextLine().trim());
			lib.removeBook(bookID);
		} else {
			System.out.println("Input was not a valid ID number.");
			sc.nextLine();
		}	
	}
	
	private void updateBook(Library lib, Scanner sc) {
		System.out.print("\nEnter ID of book to be updated: ");
		if (sc.hasNextInt()) {
			int bookID = Integer.parseInt(sc.nextLine().trim());
			Book bookToBeUpdated = lib.getBookRefByID(bookID);
			if (bookToBeUpdated == null) {
				return;
			}
			if (bookToBeUpdated.getIsAvailable()) {
				System.out.println("Book's current title: [" + bookToBeUpdated.getTitle() + "]"); 
				System.out.print("Enter new Title (leave blank and hit enter key to keep current): ");
				String newTitle = sc.nextLine().trim();
				if (!newTitle.isEmpty()) {
					bookToBeUpdated.setTitle(newTitle);
				}
				System.out.println("Book's current author: [" + bookToBeUpdated.getAuthor() + "]"); 
				System.out.print("Enter new Author (leave blank and hit enter key to keep current): ");
				String newAuthor = sc.nextLine().trim();
				if (!newAuthor.isEmpty()) {
					bookToBeUpdated.setAuthor(newAuthor);
				}
				if (newTitle.isEmpty() && newAuthor.isEmpty()) {
					System.out.println("\nNo changes were made to the book.");
				} else {
					System.out.println("\nBook has been updated.");
					System.out.println("Book ID [" + bookToBeUpdated.getId() + "] details: ");
					System.out.println("Title:  [" + bookToBeUpdated.getTitle() + "]");
					System.out.println("Author: [" + bookToBeUpdated.getAuthor() + "]");
				}				
			} else {
				System.out.println("Book ["+ bookToBeUpdated.getTitle() + "] is currently loaned and is not allowed to be updated.");
			}
		} else {
			System.out.println("Input was not a valid ID number.");
			sc.nextLine();
		}	
	}
}
