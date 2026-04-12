package m5group6.project1.dao.impl;

import m5group6.project1.dao.BookDAO;
import m5group6.project1.exceptions.DataAccessException;   
import m5group6.project1.model.Book;
import m5group6.project1.util.DBUtil;
import m5group6.project1.util.DBInputValidator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAOImpl implements BookDAO {

    private static final String SQL_SELECT_BY_ID =
        "SELECT book_id, title, author, is_available FROM books WHERE book_id = ?";
    private static final String SQL_SELECT_ALL =
        "SELECT book_id, title, author, is_available FROM books ORDER BY book_id";
    private static final String SQL_SELECT_AVAILABLE =
        "SELECT book_id, title, author, is_available FROM books WHERE is_available = TRUE ORDER BY book_id";
    private static final String SQL_INSERT =
        "INSERT INTO books (book_id, title, author, is_available) VALUES (?, ?, ?, COALESCE(?, TRUE))";
    private static final String SQL_UPDATE_IF_AVAILABLE =
        "UPDATE books SET title = COALESCE(?, title), author = COALESCE(?, author) " +
        "WHERE book_id = ? AND is_available = TRUE";
    private static final String SQL_DELETE_BY_ID =
        "DELETE FROM books WHERE book_id = ?";

    @Override
    public Book findById(int id) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find book by id=" + id, e);
        }
    }

    @Override
    public List<Book> findAll() {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            List<Book> out = new ArrayList<>();
            while (rs.next()) out.add(map(rs));
            return out;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch all books", e);
        }
    }

    @Override
    public List<Book> findAllAvailable() {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_AVAILABLE);
             ResultSet rs = ps.executeQuery()) {

            List<Book> out = new ArrayList<>();
            while (rs.next()) out.add(map(rs));
            return out;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch available books", e);
        }
    }

    @Override
    public boolean insert(Book book) {

        // Validates DB inputs (fail fast before SQL): required + max length
        DBInputValidator.ensureBookTitleForInsert(book.getTitle());
        DBInputValidator.ensureBookAuthorForInsert(book.getAuthor());

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setInt(1, book.getId());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            if (book.getIsAvailable() == null) ps.setNull(4, Types.BOOLEAN);
            else ps.setBoolean(4, book.getIsAvailable());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert book id=" + book.getId(), e);
        }
    }

    @Override
    public boolean updateIfAvailable(int bookId, String title, String author) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_IF_AVAILABLE)) {

            ps.setString(1, nullIfBlank(title));
            ps.setString(2, nullIfBlank(author));
            ps.setInt(3, bookId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update (must be available) book id=" + bookId, e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE_BY_ID)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete book id=" + id, e);
        }
    }

    private static Book map(ResultSet rs) throws SQLException {
        Book b = new Book(rs.getInt("book_id"),
                          rs.getString("title"),
                          rs.getString("author"));
        Boolean available = (Boolean) rs.getObject("is_available");
        if (available != null) b.setIsAvailable(available);
        return b;
    }

    private static String nullIfBlank(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s;
    }
}