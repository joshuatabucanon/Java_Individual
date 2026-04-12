package com.bpi.m8activity14.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.bpi.m8activity14.dto.BookDTO;

@Service
public class BookService {

    private final List<BookDTO> books = new ArrayList<>(Arrays.asList(
        new BookDTO(1, "Avatar The Last Airbender: Book 1", "Nick O. Lodiyan"),
        new BookDTO(2, "Avatar The Last Airbender: Book 2", "Nick O. Lodiyan"),
        new BookDTO(3, "Avatar The Last Airbender: Book 3", "Nick O. Lodiyan")
    ));

    public List<BookDTO> getBooks(String title) {
        if (title == null || title.trim().isEmpty()) {
            return books;
        }

        List<BookDTO> results = new ArrayList<>();
        String search = title.toLowerCase();

        for (BookDTO book : books) {
            if (book.getTitle() != null &&
                book.getTitle().toLowerCase().contains(search)) {
                results.add(book);
            }
        }
        return results;
    }

    public BookDTO getBookById(Integer id) {
        for (BookDTO book : books) {
            if (book.getId().equals(id)) {
                return book;
            }
        }
        throw new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Book not found"
        );
    }

    public BookDTO addBook(BookDTO dto) {
        validateBook(dto);

        for (BookDTO book : books) {
            if (book.getId().equals(dto.getId())) {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A book with this id already exists"
                );
            }
        }

        BookDTO newBook = new BookDTO(
            dto.getId(),
            dto.getTitle(),
            dto.getAuthor()
        );
        books.add(newBook);
        return newBook;
    }

    public BookDTO updateBook(Integer id, BookDTO dto) {
        validateBook(dto);

        BookDTO existing = null;
        for (BookDTO book : books) {
            if (book.getId().equals(id)) {
                existing = book;
                break;
            }
        }

        if (existing == null) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Book not found"
            );
        }

        existing.setTitle(dto.getTitle());
        existing.setAuthor(dto.getAuthor());
        return existing;
    }

    public void deleteBook(Integer id) {
        BookDTO toRemove = null;

        for (BookDTO book : books) {
            if (book.getId().equals(id)) {
                toRemove = book;
                break;
            }
        }

        if (toRemove == null) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Book not found"
            );
        }

        books.remove(toRemove);
    }

    private void validateBook(BookDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Title is required"
            );
        }

        if (dto.getAuthor() == null || dto.getAuthor().trim().isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Author is required"
            );
        }
    }
}