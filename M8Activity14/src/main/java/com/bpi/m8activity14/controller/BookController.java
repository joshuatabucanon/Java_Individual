package com.bpi.m8activity14.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.bpi.m8activity14.dto.BookDTO;
import com.bpi.m8activity14.service.BookService;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookDTO> getBooks(
            @RequestParam(required = false) String title) {
        return bookService.getBooks(title);
    }

    @GetMapping("/{id}")
    public BookDTO getBookById(@PathVariable Integer id) {
        return bookService.getBookById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO addBook(@RequestBody BookDTO dto) {
        return bookService.addBook(dto);
    }

    @PutMapping("/{id}")
    public BookDTO updateBook(
            @PathVariable Integer id,
            @RequestBody BookDTO dto) {
        return bookService.updateBook(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Integer id) {
        bookService.deleteBook(id);
    }
}