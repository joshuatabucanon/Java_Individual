package m7group6.project1.api.controllers;

import static spark.Spark.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import m7group6.project1.api.ApiError;
import m7group6.project1.app.ServiceLocator;
import m7group6.project1.exceptions.InvalidBookIdException;
import m7group6.project1.exceptions.InvalidDBInputException;
import m7group6.project1.service.LibraryService;
import m7group6.project1.service.dto.BookDto;
import m7group6.project1.service.dto.LoanDto;
import m7group6.project1.service.dto.UserDto;
import m7group6.project1.util.JsonUtil;

public final class LoanController {
    private LoanController() {}

    public static void register() {
        path("/api/loans", () -> {

            // GET /api/loans
            get("", (req, res) -> {
                List<LoanDto> loans = svc().getLoans();
                res.status(200);
                return JsonUtil.toJson(halLoans(loans));
            });

            // POST /api/loans { "bookId": 1, "userId": 5 }
            post("", (req, res) -> {
                try {
                    CreateLoan body = JsonUtil.fromJson(req.body(), CreateLoan.class);
                    if (body == null || body.bookId == null || body.userId == null) {
                        res.status(400);
                        return JsonUtil.toJson(ApiError.of(400, "BadRequest",
                                "bookId and userId are required", req.pathInfo()));
                    }

                    // Borrow using DTO, not entity
                    UserDto borrower = new UserDto(body.userId, null);
                    svc().borrowBook(body.bookId, borrower);

                    // resolve created loan id and return HAL
                    int loanId = svc().findLoanIdByBookId(body.bookId);
                    if (loanId <= 0) {
                        // fallback: 201 with collection link
                        res.status(201);
                        return JsonUtil.toJson(Map.of(
                                "message", "Loan created",
                                "_links", Map.of("collection", Map.of("href", "/api/loans"))));
                    }

                    LoanDto loan = svc().getLoanById(loanId);
                    res.status(201);
                    res.header("Location", "/api/loans/" + loanId);
                    return JsonUtil.toJson(halLoan(loan));

                } catch (InvalidBookIdException e) {
                    res.status(404);
                    return JsonUtil.toJson(ApiError.of(404, "InvalidBookId", e.getMessage(), req.pathInfo()));
                } catch (InvalidDBInputException e) {
                    // includes: book already borrowed or borrow limit reached
                    res.status(409);
                    return JsonUtil.toJson(ApiError.of(409, "Conflict", e.getMessage(), req.pathInfo()));
                } catch (Exception e) {
                    res.status(500);
                    return JsonUtil.toJson(ApiError.of(500, "ServerError",
                            "Unexpected error while creating loan", req.pathInfo()));
                }
            });

            // DELETE /api/loans/:id
            delete("/:id", (req, res) -> {
                try {
                    int id = Integer.parseInt(req.params(":id"));
                    svc().returnBook(id);
                    res.status(204);
                    return "";
                } catch (InvalidDBInputException e) {
                    // loan does not exist
                    res.status(404);
                    return JsonUtil.toJson(ApiError.of(404, "InvalidLoanId", e.getMessage(), req.pathInfo()));
                } catch (NumberFormatException nfe) {
                    res.status(400);
                    return JsonUtil.toJson(ApiError.of(400, "BadRequest", "Loan id must be an integer", req.pathInfo()));
                } catch (Exception e) {
                    res.status(500);
                    return JsonUtil.toJson(ApiError.of(500, "ServerError",
                            "Unexpected error while closing loan", req.pathInfo()));
                }
            });
        });
    }

    // --------- DTO for request body ---------
    static final class CreateLoan {
        public Integer bookId;
        public Integer userId;
    }

    // --------- Minimal HAL builders (DTO-based) ---------
    private static Map<String, Object> halLoan(LoanDto l) {
        Map<String, Object> m = new HashMap<>();
        m.put("loanId", l.loanId());

        BookDto b = l.book();
        Map<String, Object> book = new HashMap<>();
        book.put("bookId", b.bookId());
        book.put("title", b.title());
        book.put("author", b.author());
        book.put("_links", Map.of("self", Map.of("href", "/api/books/" + b.bookId())));
        m.put("book", book);

        var u = l.user();
        Map<String, Object> user = new HashMap<>();
        user.put("userId", u.userId());
        user.put("name", u.name());
        user.put("_links", Map.of("self", Map.of("href", "/api/users/" + u.userId())));
        m.put("user", user);

        m.put("_links", Map.of(
                "self", Map.of("href", "/api/loans/" + l.loanId()),
                "collection", Map.of("href", "/api/loans"),
                "return", Map.of("href", "/api/loans/" + l.loanId(), "method", "DELETE")
        ));
        return m;
    }

    private static Map<String, Object> halLoans(List<LoanDto> loans) {
        List<Map<String, Object>> arr = new ArrayList<>();
        for (LoanDto l : loans) arr.add(halLoan(l));
        return Map.of(
                "_links", Map.of("self", Map.of("href", "/api/loans")),
                "_embedded", Map.of("loans", arr),
                "total", loans.size()
        );
    }

    private static LibraryService svc() {
        return ServiceLocator.libraryService();
    }
}