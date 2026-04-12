package m7group6.project1.api.controllers;

import static spark.Spark.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import m7group6.project1.api.ApiError;
import m7group6.project1.app.ServiceLocator;
import m7group6.project1.exceptions.InvalidBookIdException;
import m7group6.project1.exceptions.InvalidDBInputException; // unchecked
import m7group6.project1.service.LibraryService;
import m7group6.project1.service.dto.BookDto;
import m7group6.project1.util.JsonUtil;

public final class BookController {

    private BookController() {
    }

    public static void register() {

        path("/api/books", () -> {

            // GET /api/books?available=true|false&page=&pageSize=
            get("", (req, res) -> {
                boolean onlyAvailable = "true".equalsIgnoreCase(req.queryParams("available"));
                int page = parseInt(req.queryParams("page"), 1);
                int size = parseInt(req.queryParams("pageSize"), 20);

                List<BookDto> items = onlyAvailable
                        ? svc().getAvailableBooks()
                        : svc().getAllBooks();

                res.status(200);
                return JsonUtil.toJson(halBooks(req.pathInfo(), req.queryString(), items, page, size));
            });

            // GET /api/books/:id
            get("/:id", (req, res) -> {
                try {
                    int id = Integer.parseInt(req.params(":id"));
                    BookDto b = svc().getBookById(id);

                    if (b == null) {
                        res.status(404);
                        return JsonUtil.toJson(ApiError.of(404, "InvalidBookId", "Book " + id + " not found", req.pathInfo()));
                    }

                    res.status(200);
                    return JsonUtil.toJson(halBook(b));

                } catch (NumberFormatException e) {
                    res.status(400);
                    return JsonUtil.toJson(ApiError.of(400, "BadRequest", "Book id must be an integer", req.pathInfo()));
                }
            });

            // POST /api/books
            post("", (req, res) -> {
                try {
                    BookCreate body = JsonUtil.fromJson(req.body(), BookCreate.class);

                    boolean ok = svc().addBook(body.bookId, body.title, body.author);
                    if (!ok) {
                        res.status(422);
                        return JsonUtil.toJson(ApiError.of(422, "ValidationError", "Book could not be added", req.pathInfo()));
                    }

                    res.status(201);
                    res.header("Location", "/api/books/" + body.bookId);

                    BookDto created = svc().getBookById(body.bookId);
                    return JsonUtil.toJson(halBook(created));

                } catch (InvalidBookIdException e) {
                    res.status(422);
                    return JsonUtil.toJson(ApiError.of(422, "InvalidBookId", e.getMessage(), req.pathInfo()));

                } catch (InvalidDBInputException e) { // unchecked
                    res.status(422);
                    return JsonUtil.toJson(ApiError.of(422, "InvalidDBInput", e.getMessage(), req.pathInfo()));

                } catch (Exception e) {
                    res.status(500);
                    return JsonUtil.toJson(ApiError.of(500, "ServerError", "Unexpected error while creating book", req.pathInfo()));
                }
            });

            // PUT /api/books/:id
            put("/:id", (req, res) -> {
                try {
                    int id = Integer.parseInt(req.params(":id"));
                    BookUpdate body = JsonUtil.fromJson(req.body(), BookUpdate.class);

                    svc().updateBook(id, body.title, body.author);
                    BookDto updated = svc().getBookById(id);

                    if (updated == null) {
                        res.status(404);
                        return JsonUtil.toJson(ApiError.of(404, "InvalidBookId", "Book " + id + " not found", req.pathInfo()));
                    }

                    res.status(200);
                    return JsonUtil.toJson(halBook(updated));

                } catch (InvalidBookIdException e) {
                    res.status(404);
                    return JsonUtil.toJson(ApiError.of(404, "InvalidBookId", e.getMessage(), req.pathInfo()));

                } catch (InvalidDBInputException e) { // e.g., "book is currently loaned"
                    res.status(409);
                    return JsonUtil.toJson(ApiError.of(409, "Conflict", e.getMessage(), req.pathInfo()));

                } catch (NumberFormatException e) {
                    res.status(400);
                    return JsonUtil.toJson(ApiError.of(400, "BadRequest", "Book id must be an integer", req.pathInfo()));

                } catch (Exception e) {
                    res.status(500);
                    return JsonUtil.toJson(ApiError.of(500, "ServerError", "Unexpected error while updating book", req.pathInfo()));
                }
            });

            // DELETE /api/books/:id
            delete("/:id", (req, res) -> {
                try {
                    int id = Integer.parseInt(req.params(":id"));
                    svc().removeBook(id);

                    res.status(204);
                    return "";

                } catch (InvalidBookIdException e) {
                    res.status(404);
                    return JsonUtil.toJson(ApiError.of(404, "InvalidBookId", e.getMessage(), req.pathInfo()));

                } catch (NumberFormatException e) {
                    res.status(400);
                    return JsonUtil.toJson(ApiError.of(400, "BadRequest", "Book id must be an integer", req.pathInfo()));

                } catch (Exception e) {
                    res.status(500);
                    return JsonUtil.toJson(ApiError.of(500, "ServerError", "Unexpected error while deleting book", req.pathInfo()));
                }
            });
        });
    }

    // ----- DTOs for request body -----
    static final class BookCreate {
        public int bookId;
        public String title;
        public String author;
    }

    static final class BookUpdate {
        public String title;  // blank or null means keep current
        public String author; // blank or null means keep current
    }

    // ----- HAL builders (DTO-based) -----
    private static Map<String, Object> halBook(BookDto b) {
        Map<String, Object> m = new HashMap<>();
        m.put("bookId", b.bookId());
        m.put("title", b.title());
        m.put("author", b.author());
        m.put("isAvailable", b.isAvailable());

        Map<String, Object> links = new HashMap<>();
        links.put("self", Map.of("href", "/api/books/" + b.bookId()));
        links.put("collection", Map.of("href", "/api/books"));

        if (b.isAvailable()) {
            links.put("loan", Map.of(
                    "href", "/api/loans",
                    "method", "POST",
                    "type", "application/json",
                    "schema", Map.of("bookId", b.bookId(), "userId", "int")
            ));
        } else {
            links.put("return", Map.of(
                    "href", "/api/loans?bookId=" + b.bookId(),
                    "method", "GET"
            ));
        }

        m.put("_links", links);
        return m;
    }

    private static Map<String, Object> halBooks(String path, String query, List<BookDto> items, int page, int size) {
        Map<String, Object> root = new HashMap<>();
        root.put("_links", Map.of("self", Map.of("href", path + (query == null ? "" : "?" + query))));
        root.put("page", page);
        root.put("pageSize", size);

        int from = Math.max(0, (page - 1) * size);
        int to = Math.min(items.size(), from + size);

        List<BookDto> slice = items.subList(Math.min(from, items.size()), Math.min(to, items.size()));
        var embedded = new java.util.ArrayList<Map<String, Object>>();

        for (BookDto b : slice) {
            embedded.add(halBook(b));
        }

        root.put("_embedded", Map.of("books", embedded));
        root.put("total", items.size());

        return root;
    }

    // ----- Helpers -----
    private static LibraryService svc() {
        return ServiceLocator.libraryService();
    }

    private static int parseInt(String s, int dflt) {
        try {
            return s == null ? dflt : Integer.parseInt(s);
        } catch (Exception e) {
            return dflt;
        }
    }
}