package m6group6.project1.repo;

import java.util.List;

import m6group6.project1.model.BookEntity;

public interface BookRepository {
    BookEntity findById(int id);                    // null if not found
    List<BookEntity> findAll();
    List<BookEntity> findAllAvailable();
    boolean insert(BookEntity book);
    boolean updateIfAvailable(int bookId, String title, String author);
    boolean deleteById(int id);               // cascades to loans via FK
}