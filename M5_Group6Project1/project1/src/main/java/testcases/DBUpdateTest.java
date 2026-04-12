package testcases;

import m5group6.project1.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class DBUpdateTest {
    private DBUpdateTest() {}

    private static final String SQL_MARK_UNAVAILABLE =
            "UPDATE books SET is_available = FALSE WHERE book_id = ?";

    private static final String SQL_UPDATE_TITLE_AUTHOR =
            "UPDATE books SET title = ?, author = ? WHERE book_id = ?";

    /**
     * Marks a book as unavailable (is_available = FALSE).
     */
    public static void markBookUnavailableById(int bookId) throws Exception {
        System.out.println("\n== Update: mark book unavailable ==");
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_MARK_UNAVAILABLE)) {
            ps.setInt(1, bookId);
            int updated = ps.executeUpdate();
            System.out.printf("Rows updated: %d (book_id=%d set is_available=FALSE)%n", updated, bookId);
        }
    }

    /**
     * Attempts to change title/author while the book is unavailable.
     * Expected: blocked by trigger (SQLState '23514' set in DBSchema).
     */
    public static void updateTitleAuthorWhileUnavailable(int bookId, String newTitle, String newAuthor) throws Exception {
        System.out.println("\n== Update: try changing title/author while unavailable ==");
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_TITLE_AUTHOR)) {
            ps.setString(1, newTitle);
            ps.setString(2, newAuthor);
            ps.setInt(3, bookId);
            int updated = ps.executeUpdate();
            System.out.printf("UNEXPECTED: updated %d row(s). Trigger should have blocked this.%n", updated);
        } catch (SQLException se) {
            // Your trigger raises ERRCODE '23514' (check_violation)
            System.out.printf("Expected failure due to trigger. SQLState=%s, Message=%s%n",
                    se.getSQLState(), se.getMessage());
        }
    }
}