package com.bpi.m8activity15.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bpi.m8activity15.dto.request.CreateBookRequestDto;
import com.bpi.m8activity15.dto.request.UpdateBookRequestDto;
import com.bpi.m8activity15.dto.response.BookResponseDto;
import com.bpi.m8activity15.exception.BookNotFoundException;
import com.bpi.m8activity15.mapper.BookMapper;
import com.bpi.m8activity15.model.Book;
import com.bpi.m8activity15.repository.BookRepository;

@Service
public class BookService {

    private final BookRepository repository;
    private final BookMapper mapper;

    public BookService(BookRepository repository, BookMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<BookResponseDto> getBooks(String title) {
        List<Book> books = (title == null || title.isBlank())
                ? repository.findAll()
                : repository.findByTitleContainingIgnoreCase(title);

        return books.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public BookResponseDto getBookById(Integer id) {
        Book book = repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        return mapper.toResponse(book);
    }

    public BookResponseDto createBook(CreateBookRequestDto dto) {
        Book book = mapper.toEntity(dto);
        return mapper.toResponse(repository.save(book));
    }

    public BookResponseDto updateBook(Integer id, UpdateBookRequestDto dto) {
        Book book = repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        mapper.updateEntity(dto, book);
        return mapper.toResponse(repository.save(book));
    }

    public void deleteBook(Integer id) {
        if (!repository.existsById(id)) {
            throw new BookNotFoundException("Book not found");
        }
        repository.deleteById(id);
    }
}