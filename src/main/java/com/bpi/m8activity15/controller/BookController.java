package com.bpi.m8activity15.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bpi.m8activity15.dto.request.CreateBookRequestDto;
import com.bpi.m8activity15.dto.request.UpdateBookRequestDto;
import com.bpi.m8activity15.dto.response.BookResponseDto;
import com.bpi.m8activity15.service.BookService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookResponseDto> getBooks(
            @RequestParam(required = false) String title) {
        return bookService.getBooks(title);
    }

    @GetMapping("/{id}")
    public BookResponseDto getBookById(@PathVariable Integer id) {
        return bookService.getBookById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponseDto createBook(
            @Valid @RequestBody CreateBookRequestDto dto) {
        return bookService.createBook(dto);
    }

    @PatchMapping("/{id}")
    public BookResponseDto updateBook(
            @PathVariable Integer id,
            @RequestBody UpdateBookRequestDto dto) {
        return bookService.updateBook(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Integer id) {
        bookService.deleteBook(id);
    }
}