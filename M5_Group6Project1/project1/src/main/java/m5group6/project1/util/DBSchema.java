package m5group6.project1.util;

import java.sql.Connection;
import java.sql.PreparedStatement;

public final class DBSchema {

    private DBSchema() {}

    // ---------- DROP (FK-safe order) ----------
    private static final String SQL_DROP_LOANS = "DROP TABLE IF EXISTS loans;";
    private static final String SQL_DROP_BOOKS = "DROP TABLE IF EXISTS books;";
    private static final String SQL_DROP_USERS = "DROP TABLE IF EXISTS users;";

    // ---------- CREATE: USERS TABLE ----------
	private static final String SQL_CREATE_USERS = """
	CREATE TABLE users (
	  user_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	  name VARCHAR(200) NOT NULL,
	  CONSTRAINT chk_users_name_nonblank CHECK (btrim(name) <> '')
	);
	""";

    // ---------- CREATE: BOOKS TABLE ----------
    private static final String SQL_CREATE_BOOKS = """
        CREATE TABLE books (
          book_id      INTEGER PRIMARY KEY,
          title        VARCHAR(300) NOT NULL,
          author       VARCHAR(200) NOT NULL,
          is_available BOOLEAN NOT NULL DEFAULT TRUE,
          CHECK (book_id >= 1)
        );
        """;

    private static final String SQL_CREATE_IDX_BOOKS_AVAIL =
        "CREATE INDEX idx_books_avail ON books(is_available);";
    

    // ---------- Trigger function + trigger (BOOKS edit guard) ----------
    // Function: block title/author edits when OLD.is_available = FALSE
    private static final String SQL_DROP_TRIGGER_BOOKS_PREVENT_EDIT =
            "DROP TRIGGER IF EXISTS trg_books_prevent_edit_when_unavailable ON books;";
    
    private static final String SQL_CREATE_FN_BOOKS_PREVENT_EDIT = """
        CREATE OR REPLACE FUNCTION books_prevent_edit_when_unavailable()
        RETURNS trigger
        LANGUAGE plpgsql
        AS $$
        BEGIN
          -- If the current row is unavailable, block business-field changes
          IF NOT OLD.is_available THEN
            IF (NEW.title IS DISTINCT FROM OLD.title)
               OR (NEW.author IS DISTINCT FROM OLD.author) THEN
              RAISE EXCEPTION
                USING MESSAGE = format(
                  'Book %s cannot be updated while it is borrowed (is_available=false).',
                  OLD.book_id
                ),
                ERRCODE = '23514'; -- check_violation
            END IF;
          END IF;

          RETURN NEW;
        END;
        $$;
        """;

    private static final String SQL_CREATE_TRIGGER_BOOKS_PREVENT_EDIT = """
        CREATE TRIGGER trg_books_prevent_edit_when_unavailable
        BEFORE UPDATE ON books
        FOR EACH ROW
        EXECUTE FUNCTION books_prevent_edit_when_unavailable();
        """;


    // ---------- CREATE: LOANS TABLE ----------
    private static final String SQL_CREATE_LOANS = """
        CREATE TABLE loans (
          loan_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
          book_id INTEGER NOT NULL,
          user_id INTEGER NOT NULL,
          CONSTRAINT fk_loans_book
            FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
          CONSTRAINT fk_loans_user
            FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT,
          CONSTRAINT uq_loans_book UNIQUE (book_id)
        );
        """;

    private static final String SQL_CREATE_IDX_LOANS_USER =
        "CREATE INDEX idx_loans_user_id ON loans(user_id);";

    /**
     * Drops existing tables, then creates tables and indexes.
     */
    public static void createTables() throws Exception {
        try (Connection conn = DBUtil.getConnection()) { // uses DBConfig for Postgres
            // ---------- DROP TABLES ----------
            try (PreparedStatement ps = conn.prepareStatement(SQL_DROP_LOANS)) { ps.executeUpdate(); }
            try (PreparedStatement ps = conn.prepareStatement(SQL_DROP_BOOKS)) { ps.executeUpdate(); }
            try (PreparedStatement ps = conn.prepareStatement(SQL_DROP_USERS)) { ps.executeUpdate(); }

            // ---------- CREATE USERS TABLE ----------
            try (PreparedStatement ps = conn.prepareStatement(SQL_CREATE_USERS)) { ps.executeUpdate(); }

            // ---------- CREATE BOOKS TABLE + INDEX ----------
            try (PreparedStatement ps = conn.prepareStatement(SQL_CREATE_BOOKS)) { ps.executeUpdate(); }
            try (PreparedStatement ps = conn.prepareStatement(SQL_CREATE_IDX_BOOKS_AVAIL)) { ps.executeUpdate(); }

            // ---------- CREATE BOOKS TRIGGER ----------
            try (PreparedStatement ps = conn.prepareStatement(SQL_DROP_TRIGGER_BOOKS_PREVENT_EDIT)) { ps.executeUpdate(); }
            try (PreparedStatement ps = conn.prepareStatement(SQL_CREATE_FN_BOOKS_PREVENT_EDIT)) { ps.executeUpdate(); }
            try (PreparedStatement ps = conn.prepareStatement(SQL_CREATE_TRIGGER_BOOKS_PREVENT_EDIT)) { ps.executeUpdate(); }

            // ---------- CREATE LOANS TABLE + INDEX ----------
            try (PreparedStatement ps = conn.prepareStatement(SQL_CREATE_LOANS)) { ps.executeUpdate(); }
            try (PreparedStatement ps = conn.prepareStatement(SQL_CREATE_IDX_LOANS_USER)) { ps.executeUpdate(); }
        }
    }
}