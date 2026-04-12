package m8jt.project1.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import m8jt.project1.dto.request.LoanRequestDTO;
import m8jt.project1.dto.response.LoanResponseDTO;
import m8jt.project1.service.LibraryService;

@RestController
@RequestMapping("/api/v1/loans")
public class LoanController {

    private final LibraryService libraryService;

    public LoanController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    /* =========================
       Queries
       ========================= */

    @GetMapping
    public List<LoanResponseDTO> getAllLoans() {
        return libraryService.getAllLoans();
    }

    @GetMapping("/{loanId}")
    public LoanResponseDTO getLoanById(@PathVariable int loanId) {
        return libraryService.getLoanById(loanId);
    }

    /* =========================
       Commands
       ========================= */

    @PostMapping
    public ResponseEntity<Void> borrowBook(
            @Valid @RequestBody LoanRequestDTO request) {

        libraryService.borrowBook(
                request.bookId(),
                request.userId());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{loanId}")
    public ResponseEntity<Void> returnBook(@PathVariable int loanId) {
        libraryService.returnBook(loanId);
        return ResponseEntity.noContent().build();
    }
}