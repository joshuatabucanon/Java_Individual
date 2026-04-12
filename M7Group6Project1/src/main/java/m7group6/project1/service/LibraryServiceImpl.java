package m7group6.project1.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import m7group6.project1.exceptions.DataAccessException;
import m7group6.project1.exceptions.InvalidBookIdException;
import m7group6.project1.exceptions.InvalidDBInputException;
import m7group6.project1.model.BookEntity;
import m7group6.project1.model.LoanEntity;
import m7group6.project1.model.UserEntity;
import m7group6.project1.repo.BookRepository;
import m7group6.project1.repo.LoanRepository;
import m7group6.project1.repo.UserRepository;
import m7group6.project1.service.dto.BookDto;
import m7group6.project1.service.dto.LoanDto;
import m7group6.project1.service.dto.UserDto;

public class LibraryServiceImpl implements LibraryService, LoanPolicy {
    private static final Logger logger = LoggerFactory.getLogger(LibraryServiceImpl.class);

    private final BookRepository bookDAO;
    private final LoanRepository loanDAO;
    private final UserRepository userDAO;

    // Policy flags
    private boolean isBookCapacityLimited = false;
    private int bookCapacityLimit;

    private boolean isLoanLimited = false;
    private int borrowLimitPerUser;

    public LibraryServiceImpl(BookRepository bookDAO, LoanRepository loanDAO, UserRepository userDAO) {
        this.bookDAO = bookDAO;
        this.loanDAO = loanDAO;
        this.userDAO = userDAO;
    }

    // ---------------------------------------------------------------------
    // Queries (DTO)
    // ---------------------------------------------------------------------
    public List<BookDto> getAllBooks() {
        try {
            List<BookEntity> rows = bookDAO.findAll();
            List<BookDto> dtos = new ArrayList<>();
            if (rows != null) {
                for (BookEntity e : rows) {
                    dtos.add(Mappers.toDto(e));
                }
            }
            return dtos;
        } catch (Exception e) {
            logger.error("Error fetching books", e);
            return List.of();
        }
    }

    public List<BookDto> getAvailableBooks() {
        try {
            List<BookEntity> rows = bookDAO.findAllAvailable();
            List<BookDto> dtos = new ArrayList<>();
            if (rows != null) {
                for (BookEntity e : rows) {
                    dtos.add(Mappers.toDto(e));
                }
            }
            return dtos;
        } catch (Exception e) {
            logger.error("Error fetching available books", e);
            return List.of();
        }
    }

    public List<LoanDto> getLoans() {
        try {
            List<LoanEntity> rows = loanDAO.findAllActive();
            List<LoanDto> dtos = new ArrayList<>();
            if (rows != null) {
                for (LoanEntity e : rows) {
                    dtos.add(Mappers.toDto(e));
                }
            }
            return dtos;
        } catch (Exception e) {
            logger.error("Error fetching loans", e);
            return List.of();
        }
    }

    public BookDto getBookById(int id) {
        try {
            BookEntity e = bookDAO.findById(id);
            return Mappers.toDto(e);
        } catch (Exception e) {
            logger.error("Error in getBookById", e);
            return null;
        }
    }

    public LoanDto getLoanById(int id) {
        try {
            LoanEntity e = loanDAO.findById(id);
            return Mappers.toDto(e);
        } catch (Exception e) {
            logger.error("Error in getLoanById", e);
            return null;
        }
    }

    // ---------------------------------------------------------------------
    // Commands (validate + throw domain/infra exceptions)
    // Only InvalidBookIdException is declared (checked).
    // InvalidDBInputException & DataAccessException are unchecked and propagate.
    // ---------------------------------------------------------------------
    @Override
    public boolean addBook(int bookID, String bookTitle, String author) throws InvalidBookIdException {
        try {
            if (isBookCapacityLimited()) {
                if (bookDAO.findAll().size() >= getBookCapacityLimit()) {
                    throw new InvalidDBInputException("Library capacity limit reached. Cannot add more books.");
                }
            }

            // Enforce positive ID at service level (business-facing message).
            if (bookID < 1) {
                throw new InvalidBookIdException("Invalid Book ID [" + bookID + "]. ID should be >= 1.");
            }

            // Prevent duplicate IDs with a friendly error before PK violation.
            if (bookDAO.findById(bookID) != null) {
                throw new InvalidBookIdException("Duplicate Book ID [" + bookID + "] already exists.");
            }

            // Let Bean Validation + entity callbacks enforce field constraints/trim
            BookEntity toAdd = new BookEntity(bookID, bookTitle, author);

            boolean ok = bookDAO.insert(toAdd);
            if (!ok) {
                throw new DataAccessException("Insert returned false for bookId=" + bookID);
            }
            logger.info("Book added: id={}, title='{}'", bookID, bookTitle);
            return true;
        } catch (InvalidBookIdException ex) {
            throw ex;
        } catch (InvalidDBInputException ex) {
            throw ex;
        } catch (Exception e) {
            logger.error("Unexpected error in addBook()", e);
            throw new DataAccessException("Unexpected error while adding book", e);
        }
    }

    @Override
    public void updateBook(int bookId, String title, String author) throws InvalidBookIdException {
        try {
            // Business rule: cannot update while borrowed
            BookEntity existing = bookDAO.findById(bookId);
            if (existing == null) {
                throw new InvalidBookIdException("No book found with ID [" + bookId + "].");
            }
            if (Boolean.FALSE.equals(existing.getIsAvailable())) {
                throw new InvalidDBInputException("Book is currently loaned and cannot be updated.");
            }

            boolean updated = bookDAO.updateIfAvailable(bookId, title, author);
            if (!updated) {
                throw new DataAccessException("No changes applied to book id=" + bookId);
            }
            logger.info("Book updated id={}", bookId);
        } catch (InvalidBookIdException ex) {
            throw ex;
        } catch (InvalidDBInputException ex) {
            throw ex;
        } catch (Exception e) {
            logger.error("Unexpected error in updateBook()", e);
            throw new DataAccessException("Unexpected error while updating book", e);
        }
    }

    @Override
    public void removeBook(int bookID) throws InvalidBookIdException {
        try {
            BookEntity existing = bookDAO.findById(bookID);
            if (existing == null) {
                throw new InvalidBookIdException("No book found with ID [" + bookID + "].");
            }
            boolean deleted = bookDAO.deleteById(bookID);
            if (!deleted) {
                throw new DataAccessException("Book not deleted id=" + bookID);
            }
            int associatedLoanId = loanDAO.findLoanIdByBookId(bookID);
            if (associatedLoanId > 0 && loanDAO.existsById(associatedLoanId)) {
                logger.warn("Loan {} still exists after deleting book {}", associatedLoanId, bookID);
            }
            logger.info("Book removed id={} title='{}'", bookID, existing.getTitle());
        } catch (InvalidBookIdException ex) {
            throw ex;
        } catch (Exception e) {
            logger.error("Unexpected error in removeBook()", e);
            throw new DataAccessException("Unexpected error while removing book", e);
        }
    }

    public int addUser(UserDto user) {
        if (user == null) {
            throw new InvalidDBInputException("Name is required");
        }
        // Keep a user-friendly message here; entity Bean Validation is the backstop.
        if (user.name() == null || user.name().isBlank()) {
            throw new InvalidDBInputException("Name is required");
        }

        UserEntity u = new UserEntity();
        u.setName(user.name()); // entity will trim in @PrePersist
        return userDAO.insert(u);
    }

    @Override
    public void borrowBook(int bookID, UserDto borrower) throws InvalidBookIdException {
        try {
            if (borrower == null) {
                throw new InvalidDBInputException("Borrower is required");
            }
            if (isLoanLimited()) {
                if (countUserLoan(borrower) >= getborrowLimitPerUser()) {
                    throw new InvalidDBInputException("Borrow limit reached for user.");
                }
            }

            BookEntity book = bookDAO.findById(bookID);
            if (book == null) {
                throw new InvalidBookIdException("No book found with ID [" + bookID + "].");
            }
            if (Boolean.FALSE.equals(book.getIsAvailable())) {
                throw new InvalidDBInputException("Book is already borrowed.");
            }

            int loanId = loanDAO.createLoanAndMarkBookUnavailable(bookID, borrower.userId());
            if (loanId <= 0) {
                throw new DataAccessException("Failed to create loan for bookId=" + bookID);
            }
            logger.info("Loan created id={} for bookId={} userId={}", loanId, bookID, borrower.userId());
        } catch (InvalidBookIdException ex) {
            throw ex;
        } catch (InvalidDBInputException ex) {
            throw ex;
        } catch (Exception e) {
            logger.error("Unexpected error in borrowBook()", e);
            throw new DataAccessException("Unexpected error while creating loan", e);
        }
    }

    @Override
    public void returnBook(int loanID) {
        try {
            LoanEntity loan = loanDAO.findById(loanID);
            if (loan == null) {
                throw new InvalidDBInputException("Loan ID [" + loanID + "] does not exist.");
            }
            boolean ok = loanDAO.deleteLoanAndMarkBookAvailable(loanID);
            if (!ok) {
                throw new DataAccessException("Failed to close loanId=" + loanID);
            }
            logger.info("Loan closed id={}", loanID);
        } catch (InvalidDBInputException ex) {
            throw ex;
        } catch (Exception e) {
            logger.error("Unexpected error in returnBook()", e);
            throw new DataAccessException("Unexpected error while closing loan", e);
        }
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------
    public int findLoanIdByBookId(int bookId) {
        try {
            return loanDAO.findLoanIdByBookId(bookId);
        } catch (Exception e) {
            logger.error("Error in findLoanIdByBookId", e);
            return -1;
        }
    }

    public int countUserLoan(UserDto borrower) {
        try {
            return loanDAO.countActiveLoansByUser(borrower.userId());
        } catch (Exception e) {
            logger.error("Error counting loans for userId={}", borrower.userId(), e);
            return 0;
        }
    }

    public boolean isBookCapacityLimited() {
        return this.isBookCapacityLimited;
    }

    public void setBookCapacityLimited(boolean value) {
        this.isBookCapacityLimited = value;
    }

    public boolean isLoanLimited() {
        return this.isLoanLimited;
    }

    public void setLoanLimited(boolean value) {
        this.isLoanLimited = value;
    }

    public int getborrowLimitPerUser() {
        return this.borrowLimitPerUser;
    }

    @Override
    public void setBorrowLimit(int borrowLimit) {
        this.borrowLimitPerUser = borrowLimit;
    }

    public int getBookCapacityLimit() {
        return this.bookCapacityLimit;
    }

    public void setBookCapacityLimit(int value) {
        this.bookCapacityLimit = value;
    }

    public int checkFreeBookID(List<?> ignore) {
        try {
            List<BookEntity> all = bookDAO.findAll();
            all.sort(Comparator.comparingInt(BookEntity::getId));
            int counter = 1;
            for (BookEntity b : all) {
                if (counter < b.getId()) {
                    return counter;
                }
                counter++;
            }
            return counter;
        } catch (Exception e) {
            logger.error("Error computing next free Book ID", e);
            return 1;
        }
    }
}