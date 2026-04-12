package com.bookshop.books.controller;

import com.bookshop.books.dto.request.BookCreateRequestDto;
import com.bookshop.books.dto.request.BookFilterRequestDto;
import com.bookshop.books.dto.request.BookUpdateRequestDto;
import com.bookshop.books.dto.response.BookResponseDto;
import com.bookshop.books.mapper.BookMapper;
import com.bookshop.books.model.BookEntity;
import com.bookshop.books.service.BookService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for managing books in the Book Shop system.
 *
 * <p>
 * Exposes CRUD operations and supports filtering and pagination.
 * All endpoints return JSON representations of book resources.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;
    private final BookMapper bookMapper;

    /**
     * Constructs a new {@code BookController}.
     *
     * @param bookService service layer for book operations
     * @param bookMapper mapper for converting entities and DTOs
     */
    public BookController(BookService bookService, BookMapper bookMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    /**
     * Creates a new book.
     *
     * @param request validated request payload containing book details
     * @return the created book representation
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponseDto createBook(
            @Valid @RequestBody BookCreateRequestDto request) {

        BookEntity saved =
                bookService.createBook(bookMapper.toEntity(request));
        return bookMapper.toResponseDto(saved);
    }

    /**
     * Retrieves a book by its unique identifier.
     *
     * <p>
     * If the book does not exist, a {@code 404 NOT FOUND} response
     * is returned by the global exception handler.
     * </p>
     *
     * @param id UUID of the book
     * @return book data if found
     */
    @GetMapping("/{id}")
    public BookResponseDto getBookById(@PathVariable UUID id) {
        return bookMapper.toResponseDto(
                bookService.getBookById(id)
        );
    }

    /**
     * Retrieves a paginated list of books matching filter criteria.
     *
     * @param filter filtering options provided as query parameters
     * @param pageable paging and sorting configuration
     * @return page of books matching the filters
     */
    @GetMapping
    public Page<BookResponseDto> getBooks(
            @Valid BookFilterRequestDto filter,
            @PageableDefault(size = 20) Pageable pageable) {

        return bookService.getBooks(filter, pageable)
                .map(bookMapper::toResponseDto);
    }

    /**
     * Partially updates an existing book.
     *
     * @param id UUID of the book to update
     * @param request request payload containing fields to update
     * @return updated book representation
     */
    @PatchMapping("/{id}")
    public BookResponseDto patchBook(
            @PathVariable UUID id,
            @Valid @RequestBody BookUpdateRequestDto request) {

        BookEntity updated = bookService.updateBook(id, request);
        return bookMapper.toResponseDto(updated);
    }

    /**
     * Deletes a book by its ID.
     *
     * <p>
     * Deletion is subject to business rules (age constraints).
     * </p>
     *
     * @param id UUID of the book to delete
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable UUID id) {
        bookService.deleteBook(id);
    }
}