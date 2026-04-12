package m9jt.project1.controller;

import java.util.List;

import jakarta.validation.Valid;
import m9jt.project1.dto.request.LoanRequestDTO;
import m9jt.project1.dto.response.LoanResponseDTO;
import m9jt.project1.service.LibraryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public List<LoanResponseDTO> getAllLoans() {
        return libraryService.getAllLoans();
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{loanId}")
    public LoanResponseDTO getLoanById(@PathVariable int loanId) {
        return libraryService.getLoanById(loanId);
    }

    /* =========================
       Commands (USER only)
       ========================= */

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<Void> borrowBook(
            @Valid @RequestBody LoanRequestDTO request) {

        libraryService.borrowBook(request.bookId());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{loanId}")
    public ResponseEntity<Void> returnBook(@PathVariable int loanId) {
        libraryService.returnBook(loanId);
        return ResponseEntity.noContent().build();
    }
}
