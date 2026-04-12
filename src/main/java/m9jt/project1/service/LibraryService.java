package m9jt.project1.service;

import java.util.List;

import m9jt.project1.dto.response.BookResponseDTO;
import m9jt.project1.dto.response.LoanResponseDTO;
import m9jt.project1.dto.response.UserResponseDTO;

public interface LibraryService {

    /* =========================
       Queries
       ========================= */

    List<BookResponseDTO> getAllBooks();

    List<BookResponseDTO> getAvailableBooks();

    BookResponseDTO getBookById(int bookId);

    List<LoanResponseDTO> getAllLoans();

    LoanResponseDTO getLoanById(int loanId);

    /* =========================
       Commands
       ========================= */

    void addBook(int bookId, String title, String author);

    void updateBook(int bookId, String title, String author);

    void removeBook(int bookId);

    List<UserResponseDTO> getAllUsers();

    void borrowBook(int bookId);

    void returnBook(int loanId);



    /* =========================
       Policy Controls
       ========================= */

    void setBorrowLimit(int borrowLimit);

    void setBookCapacityLimit(int capacity);

    void enableBorrowLimit(boolean enabled);

    void enableBookCapacityLimit(boolean enabled);
}