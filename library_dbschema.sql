-- =====================================================================
-- Library DB schema: users, books, loans, indexes, and trigger
-- Matches DBSchema.java used via DBUtil/DBConfig in the project.
-- =====================================================================

-- =====================================================================
--To execute sql file via psql-portable:
-- =====================================================================
--Go to your 'psql-portable' folder, right click the 'bin' folder. From the drop down click Open in Terminal then enter below commands in CMD:
--.\psql.exe -h localhost -p 5432 -U <user used in DBConfig (group6)> -d library_db -v ON_ERROR_STOP=1 -f "<folder path>\library_dbschema.sql"

-- Use a transaction so either everything succeeds or nothing does.
BEGIN;

-- ---------------------------------------------------------------------
-- DROP TABLES (dependency-safe order)
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS loans;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS users;

-- ---------------------------------------------------------------------
-- ROLES (STRICT ENUMERATION VIA TABLE)
-- ---------------------------------------------------------------------
CREATE TABLE roles (
    name VARCHAR(20) PRIMARY KEY
);

-- Only allowed roles
INSERT INTO roles (name) VALUES
    ('ROLE_ADMIN'),
    ('ROLE_USER');

-- ---------------------------------------------------------------------
-- USERS
-- ---------------------------------------------------------------------
CREATE TABLE users (
    user_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    CONSTRAINT chk_users_name_nonblank CHECK (btrim(name) <> '')
);

-- ---------------------------------------------------------------------
-- USER ↔ ROLE ASSOCIATION
-- ---------------------------------------------------------------------
CREATE TABLE user_roles (
    user_id INTEGER NOT NULL,
    role_name VARCHAR(20) NOT NULL,
    PRIMARY KEY (user_id, role_name),
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_name) REFERENCES roles(name) ON DELETE RESTRICT
);

-- ---------------------------------------------------------------------
-- BOOKS
-- ---------------------------------------------------------------------
CREATE TABLE books (
    book_id INTEGER PRIMARY KEY,
    title VARCHAR(300) NOT NULL,
    author VARCHAR(200) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    CHECK (book_id >= 1)
);

CREATE INDEX idx_books_available ON books (is_available);

-- ---------- CREATE/RE-CREATE: Trigger function to block edits when unavailable ----------
-- Drop the trigger if it exists (table exists now, so this won't error)
DROP TRIGGER IF EXISTS trg_books_prevent_edit_when_unavailable ON books;

-- (Re)create the function
CREATE OR REPLACE FUNCTION books_prevent_edit_when_unavailable()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    -- If current row is unavailable, block changes to title/author
    IF NOT OLD.is_available THEN
        IF (NEW.title  IS DISTINCT FROM OLD.title)
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

CREATE TRIGGER trg_books_prevent_edit_when_unavailable
BEFORE UPDATE ON books
FOR EACH ROW
EXECUTE FUNCTION books_prevent_edit_when_unavailable();

-- ---------------------------------------------------------------------
-- LOANS
-- ---------------------------------------------------------------------
CREATE TABLE loans (
    loan_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    book_id INTEGER NOT NULL UNIQUE,
    user_id INTEGER NOT NULL,
    CONSTRAINT fk_loans_book
        FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    CONSTRAINT fk_loans_user
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT
);

CREATE INDEX idx_loans_user_id ON loans (user_id);

-- ---------------------------------------------------------------------
-- SEED DATA
-- ---------------------------------------------------------------------

-- Books
INSERT INTO books (book_id, title, author, is_available) VALUES
    (1, 'Clean Code', 'Robert Martin', TRUE),
    (2, 'Effective Java', 'Joshua Bloch', TRUE),
    (3, 'Domain-Driven Design', 'Eric Evans', TRUE),
    (4, 'Spring in Action', 'Craig Walls', TRUE),
    (5, 'Java Concurrency in Practice', 'Brian Goetz', TRUE);

COMMIT;
