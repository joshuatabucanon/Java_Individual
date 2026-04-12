package m8jt.project1.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import m8jt.project1.dto.response.BookResponseDTO;
import m8jt.project1.dto.response.LoanResponseDTO;
import m8jt.project1.dto.response.UserResponseDTO;
import m8jt.project1.exception.ConflictException;
import m8jt.project1.exception.ResourceNotFoundException;
import m8jt.project1.mapper.BookMapper;
import m8jt.project1.mapper.LoanMapper;
import m8jt.project1.mapper.UserMapper;
import m8jt.project1.model.BookEntity;
import m8jt.project1.model.LoanEntity;
import m8jt.project1.model.UserEntity;
import m8jt.project1.repository.BookJpaRepository;
import m8jt.project1.repository.LoanJpaRepository;
import m8jt.project1.repository.UserJpaRepository;

@Service
@Transactional
public class LibraryServiceImpl implements LibraryService {

    private final BookJpaRepository bookRepository;
    private final LoanJpaRepository loanRepository;
    private final UserJpaRepository userRepository;

    private final BookMapper bookMapper;
    private final LoanMapper loanMapper;
    private final UserMapper userMapper;

    private boolean borrowLimitEnabled = false;
    private int borrowLimitPerUser;

    private boolean bookCapacityLimitEnabled = false;
    private int bookCapacityLimit;

    public LibraryServiceImpl(
            BookJpaRepository bookRepository,
            LoanJpaRepository loanRepository,
            UserJpaRepository userRepository,
            BookMapper bookMapper,
            LoanMapper loanMapper,
            UserMapper userMapper) {

        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.bookMapper = bookMapper;
        this.loanMapper = loanMapper;
        this.userMapper = userMapper;
    }



    /* =========================
       Queries
       ========================= */

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDTO> getAvailableBooks() {
        return bookRepository.findByIsAvailableTrueOrderByTitleAsc()
                .stream()
                .map(bookMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponseDTO getBookById(int bookId) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book not found with id " + bookId));

        return bookMapper.toResponse(book);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponseDTO> getAllLoans() {
        return loanRepository.findAll()
                .stream()
                .map(loanMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LoanResponseDTO getLoanById(int loanId) {
        LoanEntity loan = loanRepository.findById(loanId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Loan not found with id " + loanId));

        return loanMapper.toResponse(loan);
    }



    /* =========================
       Commands
       ========================= */

    @Override
    public void addBook(int bookId, String title, String author) {

        if (bookId < 1) {
            throw new ConflictException("Book ID must be greater than or equal to 1");
        }

        if (bookCapacityLimitEnabled && bookRepository.count() >= bookCapacityLimit) {
            throw new ConflictException("Library book capacity limit reached");
        }

        if (bookRepository.existsById(bookId)) {
            throw new ConflictException("Duplicate book ID " + bookId);
        }

        BookEntity book = new BookEntity(bookId, title, author);
        bookRepository.save(book);
    }

    @Override
    public void updateBook(int bookId, String title, String author) {

        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book not found with id " + bookId));

        if (!book.getIsAvailable()) {
            throw new ConflictException("Book is currently borrowed and cannot be updated");
        }

        if (title != null && !title.isBlank()) {
            book.setTitle(title);
        }

        if (author != null && !author.isBlank()) {
            book.setAuthor(author);
        }
    }


	@Override
	@Transactional
	public void removeBook(int bookId) {
	
	    // 1) Fail fast if the book does not exist
	    if (!bookRepository.existsById(bookId)) {
	        throw new ResourceNotFoundException("Book not found with id " + bookId);
	    }
	
	    // 2) Look up associated loan ID WITHOUT loading LoanEntity
	    Integer loanId = loanRepository.findLoanIdByBookId(bookId).orElse(null);
	
	    // 3) Delete by ID (no managed BookEntity ever exists)
	    bookRepository.deleteById(bookId);
	    bookRepository.flush();
	
	    // 4) Defensive check 
	    if (loanId != null && loanRepository.existsById(loanId)) {
            throw new ConflictException(
                    "Loan still exists after deleting book id " + bookId
            );
        }
    }


    @Override
    public UserResponseDTO addUser(String name) {

        UserEntity user = new UserEntity(name);
        userRepository.saveAndFlush(user);

        return userMapper.toResponse(user);
    }

    @Override
    public void borrowBook(int bookId, int userId) {

        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book not found with id " + bookId));

        if (!book.getIsAvailable()) {
            throw new ConflictException("Book is already borrowed");
        }

        if (borrowLimitEnabled &&
                loanRepository.countByUser_UserId(userId) >= borrowLimitPerUser) {

            throw new ConflictException("Borrow limit reached for user " + userId);
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id " + userId));

        book.setIsAvailable(false);

        LoanEntity loan = new LoanEntity();
        loan.setBook(book);
        loan.setUser(user);

        loanRepository.save(loan);
    }

    @Override
    public void returnBook(int loanId) {

        LoanEntity loan = loanRepository.findById(loanId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Loan not found with id " + loanId));

        loan.getBook().setIsAvailable(true);
        loanRepository.delete(loan);
    }



    /* =========================
       Policy Controls
       ========================= */

    @Override
    public void setBorrowLimit(int borrowLimit) {
        this.borrowLimitPerUser = borrowLimit;
    }

    @Override
    public void setBookCapacityLimit(int capacity) {
        this.bookCapacityLimit = capacity;
    }

    @Override
    public void enableBorrowLimit(boolean enabled) {
        this.borrowLimitEnabled = enabled;
    }

    @Override
    public void enableBookCapacityLimit(boolean enabled) {
        this.bookCapacityLimitEnabled = enabled;
    }
}