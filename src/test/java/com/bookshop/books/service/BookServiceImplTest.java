package com.bookshop.books.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.bookshop.books.dto.request.BookFilterRequestDto;
import com.bookshop.books.dto.request.BookUpdateRequestDto;
import com.bookshop.books.exception.BookDeletionNotAllowedException;
import com.bookshop.books.exception.BookNotFoundException;
import com.bookshop.books.exception.DuplicateIsbnException;
import com.bookshop.books.mapper.BookMapper;
import com.bookshop.books.model.BookEntity;
import com.bookshop.books.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

	@Mock
	private BookMapper bookMapper;


    @InjectMocks
    private BookServiceImpl bookService;

    private BookEntity book;

    @BeforeEach
    void setUp() {
        book = new BookEntity();
        book.setIsbn("9781234567890");
        book.setTitle("Clean Code");
        book.setAuthor("Robert Martin");
        book.setCategoryTags("programming,software");
        book.setPublisher("Prentice Hall");
        book.setPrice(BigDecimal.valueOf(3000.00));
        book.setStockQuantity(10);
    }

    /* =========================
       createBook
       ========================= */

    @Test
    void createBook_shouldSaveBook_whenIsbnDoesNotExist() {
    	//Arrange
        when(bookRepository.existsByIsbn(book.getIsbn())).thenReturn(false);
        when(bookRepository.save(book)).thenReturn(book);

        //Act
        BookEntity result = bookService.createBook(book);

        //Assert
        assertThat(result).isNotNull();
        
        verify(bookRepository).existsByIsbn(book.getIsbn());
        verify(bookRepository).save(book);
    }

    @Test
    void createBook_shouldThrowException_whenIsbnAlreadyExists() {
    	//Arrange
        when(bookRepository.existsByIsbn(book.getIsbn())).thenReturn(true);

        //Act & Assert
        assertThatThrownBy(() -> bookService.createBook(book))
                .isInstanceOf(DuplicateIsbnException.class);

        verify(bookRepository).existsByIsbn(book.getIsbn());
        verify(bookRepository, never()).save(any());
    }

    /* =========================
       getBookById
       ========================= */    
    @Test
    void getBookById_shouldReturnBook_whenFound() {
    	//Arrange
        UUID id = UUID.randomUUID();
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        //Act
        BookEntity result = bookService.getBookById(id);

        //Assert
        assertThat(result).isSameAs(book);
        
        verify(bookRepository).findById(id);
    }

    @Test
    void getBookById_shouldThrowException_whenNotFound() {
    	//Arrange
        UUID id = UUID.randomUUID();
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        //Act & Assert
        assertThatThrownBy(() -> bookService.getBookById(id))
                .isInstanceOf(BookNotFoundException.class);
        
        verify(bookRepository).findById(id);
    }

    /* =========================
       getBooks (filtering)
       ========================= */
    @Test
    void getBooks_shouldFilterbyTitleandPaginate() {
    	//Arrange
    	BookFilterRequestDto filter = new BookFilterRequestDto();
    	filter.setTitle("Code");
    	
    	Pageable pageable = PageRequest.of(0, 10);
    	
    	List<BookEntity> books = List.of(book);
        Page<BookEntity> expectedPage = new PageImpl<>(books, pageable, books.size());
        
        when(bookRepository.findAll(ArgumentMatchers.<Specification<BookEntity>>any(), any(Pageable.class)))
        .thenReturn(expectedPage);
    	
        //Act
      	Page<BookEntity> result = bookService.getBooks(filter, pageable);
    	
        //Assert
    	assertThat(result.getContent()).hasSize(1);
    	assertThat(result.getContent().get(0).getTitle()).isEqualTo("Clean Code");
    	assertThat(result.getContent().get(0).getCategoryTags()).contains("programming");
    	
    	verify(bookRepository).findAll(ArgumentMatchers.<Specification<BookEntity>>any(), any(Pageable.class));
    	verify(bookRepository, never()).findById(any());
    }
	

    /* =========================
       updateBook
       ========================= */

    @Test
    void updateBook_shouldUpdateFieldsAndSave() {
    	//Arrange
        UUID id = UUID.randomUUID();
        
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(BookEntity.class))).thenReturn(book);

        doNothing().when(bookMapper)
            .updateEntityFromDto(any(BookUpdateRequestDto.class), any(BookEntity.class));

        BookUpdateRequestDto update = new BookUpdateRequestDto();
        update.setTitle("Clean Architecture");

        //Act
        bookService.updateBook(id, update);

        //Assert
        verify(bookMapper).updateEntityFromDto(update, book);
        verify(bookRepository).save(book);

    }

    /* =========================
       deleteBook
       ========================= */

    @Test
    void deleteBook_shouldDeleteBook_whenWithinValidAge() {
    	//Arrange
        UUID id = UUID.randomUUID();
        book.setStockQuantity(1);

        // Between 7 days and 1 year old
        setCreatedAt(book, OffsetDateTime.now().minusDays(30));

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        //Act & Assert
        assertDoesNotThrow(() -> bookService.deleteBook(id));
        
        verify(bookRepository).findById(id);
        verify(bookRepository).delete(book);
    }
    
    @Test
    void deleteBook_shouldDeleteBook_whenAgeEqualsOneWeek() {
    	//Arrange
        UUID id = UUID.randomUUID();
        book.setStockQuantity(1);

        setCreatedAt(book, OffsetDateTime.now().minusDays(7));

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        //Act & Assert
        assertDoesNotThrow(() -> bookService.deleteBook(id));
        
        verify(bookRepository).findById(id);
        verify(bookRepository).delete(book);
    }
    
    @Test
    void deleteBook_shouldDeleteBook_whenAgeEqualsOneYear() {
    	//Arrange
        UUID id = UUID.randomUUID();
        book.setStockQuantity(1);

        setCreatedAt(book, OffsetDateTime.now().minusYears(1));

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        //Act & Assert
        assertDoesNotThrow(() -> bookService.deleteBook(id));
        
        verify(bookRepository).findById(id);
        verify(bookRepository).delete(book);
    }

    @Test
    void deleteBook_shouldFail_whenBookIsTooNew() {
    	//Arrange
        UUID id = UUID.randomUUID();
        setCreatedAt(book, OffsetDateTime.now().minusDays(6));

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        //Act & Assert
        assertThatThrownBy(() -> bookService.deleteBook(id))
                .isInstanceOf(BookDeletionNotAllowedException.class);
        
        verify(bookRepository).findById(id);
        verify(bookRepository, never()).delete(ArgumentMatchers.<Specification<BookEntity>>any());
    }

    @Test
    void deleteBook_shouldFail_whenBookIsTooOld() {
    	//Arrange
        UUID id = UUID.randomUUID();
        setCreatedAt(book, OffsetDateTime.now().minusYears(2));

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        //Act & Assert
        assertThatThrownBy(() -> bookService.deleteBook(id))
                .isInstanceOf(BookDeletionNotAllowedException.class);
        
        verify(bookRepository).findById(id);
        verify(bookRepository, never()).delete(ArgumentMatchers.<Specification<BookEntity>>any());
    }

    /* =========================
       Test helper
       ========================= */

    private static void setCreatedAt(BookEntity book, OffsetDateTime time) {
        try {
            var field = BookEntity.class.getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(book, time);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}