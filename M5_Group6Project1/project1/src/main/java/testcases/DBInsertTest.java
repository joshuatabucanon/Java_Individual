package testcases;

import m5group6.project1.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class DBInsertTest {
    private DBInsertTest() {}

    private static final String SQL_INSERT_BOOK = """
        INSERT INTO books (book_id, title, author, is_available)
        VALUES (?, ?, ?, TRUE)
        ON CONFLICT (book_id) DO NOTHING
        """;

    private static final String SQL_INSERT_USER = """
        INSERT INTO users (name) VALUES (?)
        """;

    public static void insertInitialBooks() throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT_BOOK)) {

            ps.setInt(1, 11); ps.setString(2, "Song of Fire and Dance"); ps.setString(3, "George RR Martin"); ps.executeUpdate();
            ps.setInt(1, 14); ps.setString(2, "Harry Potter"); ps.setString(3, "JK Rowling"); ps.executeUpdate();
            ps.setInt(1, 12); ps.setString(2, "The Fault in our Stars"); ps.setString(3, "John Green"); ps.executeUpdate();
            ps.setInt(1, 13); ps.setString(2, "Noli Me Tangere"); ps.setString(3, "Jose Rizal"); ps.executeUpdate();
            ps.setInt(1, 15); ps.setString(2, "Ang Probinsyano: The Ampatuan Saga"); ps.setString(3, "Choco Martin"); ps.executeUpdate();
        }
    }

    public static void insertInitialUsers() throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT_USER)) {

            ps.setString(1, "Zack");
            ps.executeUpdate();

            ps.setString(1, "James");
            ps.executeUpdate();

            ps.setString(1, "Elias");
            ps.executeUpdate();
        }
    }
    

    public static void insertNegativeBookId() throws SQLException {
        System.out.println("\n== Insert: negative book_id test ==");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT_BOOK)) {

            ps.setInt(1, -99);
            ps.setString(2, "Should Not Insert");
            ps.setString(3, "Constraint Tester");
            ps.executeUpdate();

            System.out.println("UNEXPECTED: negative book_id insert succeeded!");
        }
        catch (SQLException se) {
            System.out.printf("Expected failure. SQLState=%s, Message=%s%n",
                    se.getSQLState(), se.getMessage());
        }
    }


    
}
