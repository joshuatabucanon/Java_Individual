package m8jt.project1.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import m8jt.project1.dto.request.BookRequestDTO;
import m8jt.project1.dto.response.BookResponseDTO;
import m8jt.project1.service.LibraryService;

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

    @GetMapping
    public List<BookResponseDTO> getAllBooks() {
        return libraryService.getAllBooks();
    }

    @GetMapping("/available")
    public List<BookResponseDTO> getAvailableBooks() {
        return libraryService.getAvailableBooks();
    }

    @GetMapping("/{bookId}")
    public BookResponseDTO getBookById(@PathVariable int bookId) {
        return libraryService.getBookById(bookId);
    }

    /* =========================
       Commands
       ========================= */

    @PostMapping
    public ResponseEntity<Void> addBook(
            @Valid @RequestBody BookRequestDTO request) {

        libraryService.addBook(
                request.bookId(),
                request.title(),
                request.author());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

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

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> removeBook(@PathVariable int bookId) {
        libraryService.removeBook(bookId);
        return ResponseEntity.noContent().build();
    }
}