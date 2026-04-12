package m5group6.project1.dao;

import m5group6.project1.model.Loan;
import java.util.List;

public interface LoanDAO {
    Loan findById(int loanId);           // null if not found
    List<Loan> findAllActive();
    int countActiveLoansByUser(int userId);

    // Returns new loan_id, or -1 if the book was not available
    int createLoanAndMarkBookUnavailable(int bookId, int userId);

    boolean deleteLoanAndMarkBookAvailable(int loanId);
    
    /** Returns the loan_id if a loan exists for the given book_id, else -1. */
    int findLoanIdByBookId(int bookId);

    /** Returns true if a loan row with the given loan_id still exists. */
    boolean existsById(int loanId);

}
