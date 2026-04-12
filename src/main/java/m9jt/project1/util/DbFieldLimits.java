package m9jt.project1.util;

/**
 * Centralized, schema-aligned field limits for the Library domain.
 *  - books.title: VARCHAR(300) NOT NULL
 *  - books.author: VARCHAR(200) NOT NULL
 *  - users.name: VARCHAR(200) NOT NULL
 *
 * Mirrors existing constraints in:
 *  - @Size annotations on entities
 *  - PostgreSQL DDL (library_dbschema.sql)
 *  
 * DATABASE SCHEMA LIMITS.
 * Must stay in sync with library_dbschema.sql.
 * Do NOT make configurable.
 */
public final class DbFieldLimits {

    private DbFieldLimits() {}

    // ---- Books ----
    /** Maximum length of Book.title (VARCHAR(300)) */
    public static final int BOOK_TITLE_MAX = 300;

    /** Maximum length of Book.author (VARCHAR(200)) */
    public static final int BOOK_AUTHOR_MAX = 200;

    /** Minimum allowed Book ID as per CHECK (book_id >= 1) */
    public static final int BOOK_ID_MIN = 1;

    // ---- Users ----
    /** Maximum length of User.name (VARCHAR(200)) */
    public static final int USER_NAME_MAX = 200;
}