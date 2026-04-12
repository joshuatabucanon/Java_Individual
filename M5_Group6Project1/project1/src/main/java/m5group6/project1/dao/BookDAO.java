package m5group6.project1.dao;

import m5group6.project1.model.Book;
import java.util.List;

public interface BookDAO {
    Book findById(int id);                    // null if not found
    List<Book> findAll();
    List<Book> findAllAvailable();
    boolean insert(Book book);
    boolean updateIfAvailable(int bookId, String title, String author);
    boolean deleteById(int id);               // cascades to loans via FK
}