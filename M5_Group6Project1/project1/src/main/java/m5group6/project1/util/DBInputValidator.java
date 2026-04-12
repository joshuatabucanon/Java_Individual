package m5group6.project1.util;

import m5group6.project1.exceptions.InvalidDBInputException;

/**
 * Centralized, schema-aware checks for values used in DB INSERT/UPDATE.
 *
 * Only validates items not already enforced by UI/Service to avoid duplication:
 *  - books.title (required on insert, max 300)
 *  - books.author (required on insert, max 200)
 *  - users.name (length <= 200; CHECK (btrim(name) <> '')
 *
 * All methods are side-effect free and throw InvalidDBInputException on failure.
 */
public final class DBInputValidator {

    private DBInputValidator() {}

    // ---- Schema constants (mirror PostgreSQL DDL) ----
    private static final int MAX_BOOK_TITLE_LEN  = 300; // books.title VARCHAR(300)
    private static final int MAX_BOOK_AUTHOR_LEN = 200; // books.author VARCHAR(200)
    private static final int MAX_USER_NAME_LEN   = 200; // users.name  VARCHAR(200)

    // -------------------------------------------------------------------------
    // BOOK: INSERT (title/author are NOT NULL; enforce required + max length)
    // -------------------------------------------------------------------------

    /** Ensures title is non-blank and ≤ 300 characters for INSERTs. */
    public static void ensureBookTitleForInsert(String title) {
        requirePresentAndMaxLen("Title", title, MAX_BOOK_TITLE_LEN);
    }

    /** Ensures author is non-blank and ≤ 200 characters for INSERTs. */
    public static void ensureBookAuthorForInsert(String author) {
        requirePresentAndMaxLen("Author", author, MAX_BOOK_AUTHOR_LEN);
    }

    // -------------------------------------------------------------------------
    // BOOK: UPDATE (DAO treats blank as "keep current"; only cap length if set)
    // -------------------------------------------------------------------------

    /** If non-blank, title must be ≤ 300 characters for UPDATEs. */
    public static void ensureBookTitleForUpdate(String newTitle) {
        if (!isBlank(newTitle) && lengthExceeds(newTitle, MAX_BOOK_TITLE_LEN)) {
            throw new InvalidDBInputException(
                "Title must be at most " + MAX_BOOK_TITLE_LEN + " characters.");
        }
    }

    /** If non-blank, author must be ≤ 200 characters for UPDATEs. */
    public static void ensureBookAuthorForUpdate(String newAuthor) {
        if (!isBlank(newAuthor) && lengthExceeds(newAuthor, MAX_BOOK_AUTHOR_LEN)) {
            throw new InvalidDBInputException(
                "Author must be at most " + MAX_BOOK_AUTHOR_LEN + " characters.");
        }
    }

    // -------------------------------------------------------------------------
    // USER: INSERT (Enforce ≤ 200 chars and NOT NULL)
    // -------------------------------------------------------------------------
    public static void ensureUserNameForInsert(String name) {
        // Required & non-blank (aligns with CHECK (btrim(name) <> ''))
        if (isBlank(name)) {
            throw new InvalidDBInputException("User name is required and cannot be blank.");
        }
        // Length ≤ 200
        if (lengthExceeds(name, MAX_USER_NAME_LEN)) {
            throw new InvalidDBInputException(
                "User name must be at most " + MAX_USER_NAME_LEN + " characters.");
        }
    }


    // =========================================================================
    // SIMPLE GETTERS (Use in UI: LibraryApplication & Library)
    // =========================================================================

    /** Return maximum allowed DB length for user.name) */
    public static int getMaxUserNameLength() {
        return MAX_USER_NAME_LEN;
    }

    /** Return maximum allowed DB length for books.title) */
    public static int getMaxBookTitleLength() {
        return MAX_BOOK_TITLE_LEN;
    }

    /** Return maximum allowed DB length for books.author) */
    public static int getMaxBookAuthorLength() {
        return MAX_BOOK_AUTHOR_LEN;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static void requirePresentAndMaxLen(String field, String value, int maxLen) {
        if (isBlank(value)) {
            throw new InvalidDBInputException(field + " is required.");
        }
        if (lengthExceeds(value, maxLen)) {
            throw new InvalidDBInputException(field + " must be at most " + maxLen + " characters.");
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static boolean lengthExceeds(String s, int maxLen) {
        return s != null && s.trim().length() > maxLen;
    }
}