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
 * - [6] Exit
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
 * - Display all Loans, user selects the Loan and removes that from the slot
 * 
 *   [6] Exit
 * - Stops the program  
 * */
package m2_group6project1;

import java.util.Scanner;

public class LibraryApplication {
	
	private User user;
	private Library library;
	private Loan loan;
	
	private static Scanner sc = new Scanner(System.in);
	// Main Application Logic, call this in your Main.java
	public void start() {
		// initial user creation
		
		
		String option = "";
		
		this.user = new User();
		
		setUser(user);				
		
		// initial library creation
		this.library = new Library(5);
		
		library.addBook(new Book(1, "The Lord of the Rings", "JRR Tolkien"));
		library.addBook(new Book(2, "Demon Slayer Volume 21", "Koyoharu Gotouge"));
		library.addBook(new Book(3, "One Piece Volume 101", "Eichiro Oda"));
		library.addBook(new Book(4, "Gachiakuta Volume 1", "Kei Urana"));
		library.addBook(new Book(5, "Look Back", "Tatsuki Fujimoto"));
		//library.addBook(new Book(6, "A Song of Ice and Fire", "GRRM"));
		
		this.loan = new Loan();
		loan.setBorrowLimit(5);

		// add code here
				
		do {
			Common.doubleLineBreak();
			System.out.println("   ===[      LIBRARY PROGRAM MENU      ]===");
			System.out.println("         [1] Display All Books");
			System.out.println("         [2] Display Available Books");
			System.out.println("         [3] Display All Borrowed Books");
			System.out.println("         [4] Borrow Book");
			System.out.println("         [5] Return Book");
			System.out.println("         [6] Exit");
			System.out.print("\nEnter chosen option >> ");
			option = sc.nextLine();
			//option = Integer.parseInt(strOption);
			//System.out.println("===============================================");
	
				switch (option.trim()) {
				case "1":
					displayAllBooks(library);
					break;
				case "2":
					displayAvailableBooks(library);
					break;
				case "3":
					displayBorrowedBooks(library);
					break;
				case "4":
					borrowBook(library);
					break;
				case "5":
					returnBook(library);
					break;
				case "6":
					System.out.println("Exiting...");	
					break;
				default:
					System.out.println("Please enter a valid option.");
					break;
			}
		} while(!option.equals("6"));
		sc.close();
	}
	
	private void setUser(User user){
		
		System.out.print("Please enter username: ");
		user.setName(sc.nextLine().trim());
		System.out.println("Welcome to the library, " + user.getName() + ".");
		
	}
	
	private void displayAllBooks(Library library) {
		
		library.getAllBooks();		
	}
	private void displayAvailableBooks(Library library) {
		
		library.getAvailableBooks();	
	}
	private void displayBorrowedBooks(Library library) {
		
		library.getBorrowedBooks(user.getName());	
	}
	
	private void returnBook(Library library) {		
		int count = library.getBorrowedBooks(user.getName());	
		if (count > 0) {
			Common.doubleLineBreak();
			System.out.print("Enter Loan ID to return: ");
			if (sc.hasNextInt()) {
				int loanId = Integer.parseInt(sc.nextLine().trim());
				library.returnBook(loanId);
			} else {
				System.out.println("Input was not a valid ID number.");
				sc.nextLine();
			}	
		}
	}
	private void borrowBook(Library library) {		
		int count = library.getAvailableBooks();
		if (count > 0) {
			Common.doubleLineBreak();
			System.out.print("Enter Book ID to borrow: ");
			if (sc.hasNextInt()) {
				int bookId = Integer.parseInt(sc.nextLine().trim());
				library.borrowBook(bookId, user.getName());
			} else {
				System.out.println("Input was not a valid ID number.");
				sc.nextLine();
			}	
		}
	}

		

}
