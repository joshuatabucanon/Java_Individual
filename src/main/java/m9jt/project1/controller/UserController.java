package m9jt.project1.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import m9jt.project1.dto.response.UserResponseDTO;
import m9jt.project1.service.LibraryService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final LibraryService libraryService;

    public UserController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    /* =========================
       Commands (ADMIN only)
       ========================= */

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return libraryService.getAllUsers();
    }
}