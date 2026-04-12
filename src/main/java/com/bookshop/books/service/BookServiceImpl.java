package com.bookshop.books.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookshop.books.dto.request.BookFilterRequestDto;
import com.bookshop.books.dto.request.BookUpdateRequestDto;
import com.bookshop.books.exception.BookDeletionNotAllowedException;
import com.bookshop.books.exception.BookNotFoundException;
import com.bookshop.books.exception.DuplicateIsbnException;
import com.bookshop.books.mapper.BookMapper;
import com.bookshop.books.model.BookEntity;
import com.bookshop.books.repository.BookRepository;
import com.bookshop.books.repository.BookSpecifications;

/**
 * Default implementation of {@link BookService}.
 *
 * <p>
 * Enforces domain rules such as ISBN uniqueness,
 * date-based deletion constraints, and controlled updates.
 * </p>
 */
@Service
@Transactional
public class BookServiceImpl implements BookService {

    private static final Logger log =
            LoggerFactory.getLogger(BookServiceImpl.class);

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    /**
     * Constructs a new {@code BookServiceImpl}.
     *
     * @param bookRepository repository for persistence operations
     * @param bookMapper mapper for entity and DTO transformations
     */
    public BookServiceImpl(BookRepository bookRepository,
                           BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    /* =========================
       Create Book
       ========================= */
    @Override
    public BookEntity createBook(BookEntity book) {
        log.debug("Creating book with ISBN={}", book.getIsbn());

        if (bookRepository.existsByIsbn(book.getIsbn())) {
            log.warn("Duplicate ISBN attempt: {}", book.getIsbn());
            throw new DuplicateIsbnException(book.getIsbn());
        }

        BookEntity saved = bookRepository.save(book);
        log.info("Book created successfully id={}", saved.getId());

        return saved;
    }

    /* =========================
       Read Book
       ========================= */
    @Override
    @Transactional(readOnly = true)
    public BookEntity getBookById(UUID id) {
        log.debug("Fetching book id={}", id);

        return bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Book not found id={}", id);
                    return new BookNotFoundException(id);
                });
    }

    /* =========================
       Pagination + Filtering
       ========================= */
    @Override
    @Transactional(readOnly = true)
    public Page<BookEntity> getBooks(BookFilterRequestDto filter,
                                    Pageable pageable) {
        log.debug("Fetching books filter={}, pageable={}", filter, pageable);

        return bookRepository.findAll(
                BookSpecifications.filterBooks(filter),
                pageable
        );
    }

    /* =========================
       Update Book
       ========================= */
    @Override
    public BookEntity updateBook(UUID id, BookUpdateRequestDto dto) {
        log.debug("Updating book id={}", id);

        BookEntity existing = getBookById(id);

        // ISBN uniqueness check 
        if (dto.getIsbn() != null
                && !dto.getIsbn().equals(existing.getIsbn())
                && bookRepository.existsByIsbn(dto.getIsbn())) {

            log.warn("Duplicate ISBN on update id={}, isbn={}",
                    id, dto.getIsbn());
            throw new DuplicateIsbnException(dto.getIsbn());
        }

        // MapStruct updates only non-null fields
        bookMapper.updateEntityFromDto(dto, existing);
        BookEntity updated = bookRepository.save(existing);

        log.info("Book updated id={}", updated.getId());
        return updated;
    }

    /* =========================
       Delete Book 
       with date-based rules
       ========================= */
    @Override
    public void deleteBook(UUID id) {
        log.debug("Attempting delete book id={}", id);

        BookEntity book = getBookById(id);

        // Convert timestamps to calendar dates (ignore time)
        LocalDate createdDate = book.getCreatedAt().toLocalDate();
        LocalDate today = OffsetDateTime.now().toLocalDate();

        // Rule 1: must be at least 7 days old before book can be deleted
        if (createdDate.isAfter(today.minusDays(7))) {
            log.warn("Delete denied (TOO_NEW) id={}, createdAt={}",
                    id, createdDate);
            throw new BookDeletionNotAllowedException(
                    BookDeletionNotAllowedException.TOO_NEW
            );
        }

        // Rule 2: book cannot be deleted if it is older than 1 calendar year
        LocalDate expirationDate = createdDate.plusYears(1);
        if (today.isAfter(expirationDate)) {
            log.warn("Delete denied (TOO_OLD) id={}, createdAt={}",
                    id, createdDate);
            throw new BookDeletionNotAllowedException(
                    BookDeletionNotAllowedException.TOO_OLD
            );
        }

        bookRepository.delete(book);
        log.info("Book deleted id={}", id);
    }
}