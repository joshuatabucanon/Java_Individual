package m7group6.project1.service;

import java.util.List;

import m7group6.project1.exceptions.InvalidBookIdException;
import m7group6.project1.service.dto.BookDto;
import m7group6.project1.service.dto.LoanDto;
import m7group6.project1.service.dto.UserDto;

public interface LibraryService
{
    // --- Queries used by controllers (DTO-based) ---
    List<BookDto> getAllBooks();
    
    List<BookDto> getAvailableBooks();

    List<LoanDto> getLoans();

    BookDto getBookById(int id);

    LoanDto getLoanById(int id);

    int findLoanIdByBookId(int bookId);

    boolean addBook(int bookId, String title, String author) throws InvalidBookIdException;

    void updateBook(int bookId, String title, String author) throws InvalidBookIdException;

    void removeBook(int bookId) throws InvalidBookIdException;

    void borrowBook(int bookId, UserDto borrower) throws InvalidBookIdException;
    
    int addUser(UserDto user);

    // Unchecked exceptions may still be thrown, but no 'throws' clause here
    void returnBook(int loanId);

    // --- Backward-compatible defaults for legacy callers ---
    default List<BookDto> displayAllBooks() {
        return getAllBooks();
    }

    default List<BookDto> displayAvailableBooks() {
        return getAvailableBooks();
    }

    default List<LoanDto> displayBorrowedBooks() {
        return getLoans();
    }
}