package m7group6.project1.repo;

import java.util.List;

import m7group6.project1.model.LoanEntity;

public interface LoanRepository {
    LoanEntity findById(int loanId);           // null if not found
    List<LoanEntity> findAllActive();
    int countActiveLoansByUser(int userId);

    // Returns new loan_id, or -1 if the book was not available
    int createLoanAndMarkBookUnavailable(int bookId, int userId);

    boolean deleteLoanAndMarkBookAvailable(int loanId);
    
    /** Returns the loan_id if a loan exists for the given book_id, else -1. */
    int findLoanIdByBookId(int bookId);

    /** Returns true if a loan row with the given loan_id still exists. */
    boolean existsById(int loanId);

}
