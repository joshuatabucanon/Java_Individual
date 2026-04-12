
package com.bpi.m8activity13.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.bpi.m8activity13.dto.BookDTO;

@RestController
@RequestMapping("/api/books")
public class BookController {

    // Static list of 3 books
	private static List<BookDTO> books = new ArrayList<>(Arrays.asList(
            new BookDTO(1, "Avatar The Last Airbender: Book 1", "Nick O. Lodiyan"),
            new BookDTO(2, "Avatar The Last Airbender: Book 2", "Nick O. Lodiyan"),
            new BookDTO(3, "Avatar The Last Airbender: Book 3", "Nick O. Lodiyan")
    ));

	@GetMapping
	public List<BookDTO> getBooks(@RequestParam(required = false) String title) {
	    if (title == null || title.trim().isEmpty()) {
	        return books;
	    }

	    // Prepare a list to hold matches
	    List<BookDTO> results = new ArrayList<>();
	    String search = title.toLowerCase();

	    // Manual loop search
	    for (BookDTO book : books) {
	        if (book.getTitle() != null &&
	            book.getTitle().toLowerCase().contains(search)) {
	            results.add(book);
	        }
	    }

	    return results;   // Return empty list if no matches
	}

    @GetMapping("/{id}")
    public BookDTO getBookById(@PathVariable Integer id) {
        for (BookDTO book : books) {
            if (book.getId().equals(id)) {
                return book;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
    }
    

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO addBook(@RequestBody BookDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required");
        }
        if (dto.getAuthor() == null || dto.getAuthor().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Author is required");
        }

        for (BookDTO existing : books) {
            if (existing.getId() != null && existing.getId().equals(dto.getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "A book with this id already exists");
            }
        }

        BookDTO newBook = new BookDTO(dto.getId(), dto.getTitle(), dto.getAuthor());
        books.add(newBook);
        return newBook;
    }


}
