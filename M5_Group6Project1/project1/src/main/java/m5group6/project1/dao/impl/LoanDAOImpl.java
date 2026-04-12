package m5group6.project1.dao.impl;

import m5group6.project1.dao.LoanDAO;
import m5group6.project1.exceptions.DataAccessException;  //unchecked exception
import m5group6.project1.model.Book;
import m5group6.project1.model.Loan;
import m5group6.project1.model.User;
import m5group6.project1.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanDAOImpl implements LoanDAO {

    private static final String SQL_FIND_BY_ID =
        "SELECT l.loan_id, b.book_id, b.title, b.author, b.is_available, " +
        "       u.user_id, u.name AS user_name " +
        "FROM loans l " +
        "JOIN books b ON b.book_id = l.book_id " +
        "JOIN users u ON u.user_id = l.user_id " +
        "WHERE l.loan_id = ?";

    private static final String SQL_FIND_ALL_ACTIVE =
        "SELECT l.loan_id, b.book_id, b.title, b.author, b.is_available, " +
        "       u.user_id, u.name AS user_name " +
        "FROM loans l " +
        "JOIN books b ON b.book_id = l.book_id " +
        "JOIN users u ON u.user_id = l.user_id " +
        "ORDER BY l.loan_id";

    private static final String SQL_COUNT_ACTIVE_BY_USER =
        "SELECT COUNT(*) FROM loans WHERE user_id = ?";

    private static final String SQL_FIND_LOAN_ID_BY_BOOK_ID =
        "SELECT loan_id FROM loans WHERE book_id = ?";

    private static final String SQL_EXISTS_BY_ID =
        "SELECT 1 FROM loans WHERE loan_id = ?";

    // ---------- Single-statement CTEs (atomic operations) ----------
    // Create loan only if the book is currently available.
    private static final String SQL_LOAN_WITH_CTE = """
        WITH updated AS (
            UPDATE books
               SET is_available = FALSE
             WHERE book_id = ? AND is_available = TRUE
             RETURNING book_id
        )
        INSERT INTO loans (book_id, user_id)
        SELECT book_id, ?
          FROM updated
        RETURNING loan_id
        """;

    // Delete loan and mark the associated book available.
    private static final String SQL_DELETE_LOAN_AND_FREE_BOOK = """
        WITH del AS (
            DELETE FROM loans
             WHERE loan_id = ?
         RETURNING book_id
        ),
        upd AS (
            UPDATE books b
               SET is_available = TRUE
              FROM del
             WHERE b.book_id = del.book_id
         RETURNING b.book_id
        )
        SELECT 1 FROM upd
        """;

    @Override
    public Loan findById(int loanId) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setInt(1, loanId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find loan by id=" + loanId, e);
        }
    }

    @Override
    public List<Loan> findAllActive() {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_ACTIVE);
             ResultSet rs = ps.executeQuery()) {

            List<Loan> out = new ArrayList<>();
            while (rs.next()) out.add(map(rs));
            return out;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch active loans", e);
        }
    }

    @Override
    public int countActiveLoansByUser(int userId) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_COUNT_ACTIVE_BY_USER)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to count active loans for userId=" + userId, e);
        }
    }

    @Override
    public int createLoanAndMarkBookUnavailable(int bookId, int userId) {
        // Single-statement CTE: flip is_available->FALSE if currently TRUE, then insert loan.
        // Returns new loan_id, or -1 if the book wasn't available.
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_LOAN_WITH_CTE)) {

            ps.setInt(1, bookId);
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                "Failed to create loan and mark book unavailable (bookId=" + bookId + ", userId=" + userId + ")", e);
        }
    }

    @Override
    public boolean deleteLoanAndMarkBookAvailable(int loanId) {
        // Single-statement CTE: delete loan, then mark the book available using the deleted row's book_id.
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE_LOAN_AND_FREE_BOOK)) {

            ps.setInt(1, loanId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // true if a row flowed through upd; false if loanId not found
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                "Failed to delete loan and mark book available (loanId=" + loanId + ")", e);
        }
    }

    @Override
    public int findLoanIdByBookId(int bookId) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_LOAN_ID_BY_BOOK_ID)) {

            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find loanId by bookId=" + bookId, e);
        }
    }

    @Override
    public boolean existsById(int loanId) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTS_BY_ID)) {

            ps.setInt(1, loanId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to check existence of loanId=" + loanId, e);
        }
    }

    // ---------- Row mapper ----------
    private static Loan map(ResultSet rs) throws SQLException {
        Book b = new Book(
            rs.getInt("book_id"),
            rs.getString("title"),
            rs.getString("author")
        );
        Boolean avail = (Boolean) rs.getObject("is_available");
        if (avail != null) b.setIsAvailable(avail);

        User u = new User();
        u.setUserID(rs.getInt("user_id"));
        u.setName(rs.getString("user_name"));

        Loan loan = new Loan();
        loan.setLoanId(rs.getInt("loan_id"));
        loan.setBook(b);
        loan.setUser(u);
        return loan;
    }
}