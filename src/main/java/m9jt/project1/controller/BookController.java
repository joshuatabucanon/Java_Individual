package m9jt.project1.controller;

import java.util.List;

import jakarta.validation.Valid;
import m9jt.project1.dto.request.BookRequestDTO;
import m9jt.project1.dto.response.BookResponseDTO;
import m9jt.project1.service.LibraryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final LibraryService libraryService;

    public BookController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    /* =========================
       Queries
       ========================= */

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public List<BookResponseDTO> getAllBooks() {
        return libraryService.getAllBooks();
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/available")
    public List<BookResponseDTO> getAvailableBooks() {
        return libraryService.getAvailableBooks();
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{bookId}")
    public BookResponseDTO getBookById(@PathVariable int bookId) {
        return libraryService.getBookById(bookId);
    }

    /* =========================
       Commands (ADMIN only)
       ========================= */

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Void> addBook(
            @Valid @RequestBody BookRequestDTO request) {

        libraryService.addBook(
                request.bookId(),
                request.title(),
                request.author());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{bookId}")
    public ResponseEntity<Void> updateBook(
            @PathVariable int bookId,
            @RequestBody BookRequestDTO request) {

        libraryService.updateBook(
                bookId,
                request.title(),
                request.author());

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> removeBook(@PathVariable int bookId) {
        libraryService.removeBook(bookId);
        return ResponseEntity.noContent().build();
    }
}