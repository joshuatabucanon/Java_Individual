package com.bookshop.books.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bookshop.books.dto.request.BookFilterRequestDto;
import com.bookshop.books.dto.request.BookUpdateRequestDto;
import com.bookshop.books.model.BookEntity;

/**
 * Service interface for managing books.
 *
 * <p>
 * Defines business operations related to book management,
 * including creation, retrieval, updating, deletion,
 * and filtered searching.
 * </p>
 */
public interface BookService {

    /**
     * Creates and persists a new book.
     *
     * @param book the book entity to create
     * @return the persisted book entity
     */
    BookEntity createBook(BookEntity book);

    /**
     * Retrieves a book by its unique identifier.
     *
     * @param id unique identifier of the book
     * @return the found book entity
     */
    BookEntity getBookById(UUID id);

    /**
     * Retrieves a paginated list of books matching filter criteria.
     *
     * @param filter filtering criteria
     * @param pageable pagination configuration
     * @return a page of matching book entities
     */
    Page<BookEntity> getBooks(BookFilterRequestDto filter, Pageable pageable);

    /**
     * Updates an existing book using partial update data.
     *
     * @param id unique identifier of the book to update
     * @param dto data containing updated field values
     * @return the updated book entity
     */
    BookEntity updateBook(UUID id, BookUpdateRequestDto dto);

    /**
     * Deletes a book by its unique identifier.
     *
     * <p>
     * Deletion is subject to business rules and constraints.
     * </p>
     *
     * @param id unique identifier of the book to delete
     */
    void deleteBook(UUID id);
}