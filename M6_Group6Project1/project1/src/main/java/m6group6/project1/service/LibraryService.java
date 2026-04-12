package m6group6.project1.service;

import m6group6.project1.exceptions.InvalidBookIdException;
import m6group6.project1.model.UserEntity;

public interface LibraryService {
	
	void displayAllBooks();
	
	void displayAvailableBooks();
	
	void displayBorrowedBooks();
	
	void borrowBook(int bookID, UserEntity borrower);
	
	void returnBook(int loanID);
	
	boolean addBook(int bookID, String bookTitle, String author) throws InvalidBookIdException;
	
	void removeBook(int bookID);
	
	void updateBook(int bookId, String title, String author);
	
	
}
