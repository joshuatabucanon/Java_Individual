-- =====================================================================
-- PSQL commands for Bookshop DB schema: books, indexes, and trigger
-- =====================================================================

CREATE DATABASE bookshopdb;

\connect bookshopdb;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS books;

CREATE TABLE books (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    isbn VARCHAR(17) UNIQUE NOT NULL
        CHECK (char_length(isbn) >= 13),
    title TEXT NOT NULL,
    category_tags TEXT,

    author TEXT NOT NULL,
    publisher TEXT,

    price NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    stock_quantity INTEGER NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE INDEX idx_books_title ON books (title);
CREATE INDEX idx_books_author ON books (author);
CREATE INDEX idx_books_created_at ON books (created_at);

CREATE OR REPLACE FUNCTION enforce_book_delete_rules()
RETURNS TRIGGER AS $$
BEGIN
    IF DATE(OLD.created_at) > CURRENT_DATE - INTERVAL '7 days' THEN
        RAISE EXCEPTION
            'Book can only be deleted after it is at least 7 days old.';
    END IF;

    IF DATE(OLD.created_at) < CURRENT_DATE - INTERVAL '1 year' THEN
        RAISE EXCEPTION
            'Book older than 1 year cannot be deleted.';
    END IF;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER book_delete_trigger
BEFORE DELETE ON books
FOR EACH ROW
EXECUTE FUNCTION enforce_book_delete_rules();



-- Below is for seeding 30 books
BEGIN;

INSERT INTO books (
    isbn,
    title,
    author,
    publisher,
    category_tags,
    price,
    stock_quantity,
    created_at
)
SELECT
    -- ISBN: 13 digits with hyphen
    '978-' ||
	(n % 10) || '-' ||
	LPAD((n * 3)::text, 2, '0') || '-' ||
	LPAD((100000 + n)::text, 6, '0') || '-' ||
	((n * 7) % 10)
	AS isbn,

    'Book ' || n           AS title,
    'Author ' || n         AS author,
    'Publisher ' || n      AS publisher,
    'category-' || ((n - (n % 10)) / 10) || ',category-' || (n % 10) AS category_tags,

    (10 + n)::numeric(10,2) AS price,

    (n % 20 + 1) AS stock_quantity,

    CURRENT_TIMESTAMP - ((n * 6) || ' days')::interval AS created_at
FROM generate_series(1, 100) AS n;

COMMIT;
