package m8jt.project1.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import m8jt.project1.dto.request.UserRequestDTO;
import m8jt.project1.dto.response.UserResponseDTO;
import m8jt.project1.service.LibraryService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final LibraryService libraryService;

    public UserController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO addUser(
            @Valid @RequestBody UserRequestDTO request) {

        return libraryService.addUser(request.name());
    }
}