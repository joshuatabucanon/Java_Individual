package com.bpi.m8activity15.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bpi.m8activity15.model.Book;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {
    List<Book> findByTitleContainingIgnoreCase(String title);
}