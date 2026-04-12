package com.bookshop.books.security;

import static com.bookshop.books.security.BookPermissions.*;

public final class BookRoles {

    private BookRoles() {}

    public static final String USER  = "ROLE_USER";
    public static final String ADMIN = "ROLE_ADMIN";

    public static final String[] USER_AUTHORITIES = {
        BOOK_READ
    };

    public static final String[] ADMIN_AUTHORITIES = {
        BOOK_READ,
        BOOK_CREATE,
        BOOK_UPDATE,
        BOOK_DELETE
    };
}