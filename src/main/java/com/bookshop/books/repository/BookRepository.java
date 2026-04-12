package com.bookshop.books.repository;

import com.bookshop.books.model.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface BookRepository
        extends JpaRepository<BookEntity, UUID>,
                JpaSpecificationExecutor<BookEntity> {
	
	boolean existsByIsbn(String isbn);
}