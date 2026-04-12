package testcases;

import jakarta.persistence.EntityManager;

import m6group6.project1.exceptions.InvalidBookIdException;
import m6group6.project1.exceptions.InvalidDBInputException;
import m6group6.project1.model.BookEntity;
import m6group6.project1.model.UserEntity;
import m6group6.project1.repo.BookRepository;
import m6group6.project1.repo.LoanRepository;
import m6group6.project1.repo.UserRepository;
import m6group6.project1.repo.impl.BookRepoImpl;   // JPA repos with constructor-injected EM
import m6group6.project1.repo.impl.LoanRepoImpl;
import m6group6.project1.repo.impl.UserRepoImpl;
import m6group6.project1.service.LibraryServiceImpl;
import m6group6.project1.util.DBInputValidator;
import m6group6.project1.util.EntityManagerUtil;

public class AppNegativeTest {

    // -------------- Pretty printing helpers (same style as HappyPath) --------------
    private static void printCase(int no, String title, String expected) {
        System.out.println();
        System.out.println("======================================================================");
        System.out.println("TEST " + no + ": " + title);
        System.out.println("Expected: " + expected);
        System.out.println("======================================================================");
    }
    private static void pass(String scenario) {
        System.out.println("✅ PASS: " + scenario);
    }
    private static void fail(String scenario, Throwable t) {
        System.out.println("❌ FAIL: " + scenario);
        if (t != null) {
            System.out.println(" → " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
        System.exit(1); // keep behavior deterministic for CI
    }

    public static void main(String[] args) throws Exception {
        EntityManager em = null;
        try {
            // --- Single EntityManager for the entire negative-path test (like Main/HappyPath) ---
            em = EntityManagerUtil.getInstance().createEntityManager();

            // --- Wire JPA repositories with the same EM (no RepoFactory) ---
            BookRepository bookRepo = new BookRepoImpl(em);
            LoanRepository loanRepo = new LoanRepoImpl(em);
            UserRepository userRepo = new UserRepoImpl(em);

            // --- Use the 3-arg LibraryServiceImpl constructor ---
            LibraryServiceImpl libService = new LibraryServiceImpl(bookRepo, loanRepo, userRepo);

            // ========= UNHAPPY PATH TESTS =========

            // 1) Add Book with ID < 1
            printCase(1, "Add Book with ID < 1",
                    "Inserting a book with ID < 1 is rejected with InvalidBookIdException.");
            try {
                libService.addBook(0, "Invalid ID Book", "Nobody");
                fail("Adding a book with ID < 1 should NOT succeed", null);
            } catch (InvalidBookIdException e) {
                pass("Rejected book with ID < 1");
            } catch (Exception e) {
                fail("Unexpected error adding book with invalid ID", e);
            }

            // 2) Add Book with Duplicate ID
            printCase(2, "Add Book with Duplicate ID",
                    "Second insert using an existing book ID is rejected with InvalidBookIdException.");
            try {
                // First add works
                libService.addBook(1, "Song of Ice and Fire", "George R.R. Martin");
                // Second add should fail due to duplicate ID
                libService.addBook(1, "Duplicate Title", "Someone");
                fail("Adding duplicate book ID should NOT succeed", null);
            } catch (InvalidBookIdException e) {
                pass("Duplicate book ID correctly rejected");
            } catch (Exception e) {
                fail("Unexpected error adding duplicate book", e);
            }

            // 3) Borrow Already Borrowed Book
            printCase(3, "Borrow Already Borrowed Book",
                    "Second borrow attempt for the same book is blocked; only one loan remains and book is unavailable.");
            try {
                // Create user
                UserEntity u = new UserEntity();
                u.setName("Borrow Tester");
                u.setUserID(libService.addUser(u));

                // Add book & borrow once
                libService.addBook(2, "Harry Potter", "J.K. Rowling");
                libService.borrowBook(2, u);

                // Second borrow attempt should be blocked by service/DAO logic
                libService.borrowBook(2, u);

                // Validate state: one active loan exists for book #2 and it is unavailable
                int loanId = loanRepo.findLoanIdByBookId(2);
                BookEntity b = bookRepo.findById(2);
                if (loanId > 0 && b != null && Boolean.FALSE.equals(b.getIsAvailable())) {
                    pass("Borrowing an already borrowed book was blocked");
                } else {
                    fail("Borrowing borrowed book should not succeed", null);
                }
            } catch (Exception e) {
                fail("Unexpected error during borrow-borrow test", e);
            }

            // 4) Update Borrowed Book
            printCase(4, "Update Borrowed Book",
                    "Title/Author updates are blocked while book is borrowed (guard + DB trigger).");
            try {
                // Setup: Create user, add book, borrow book
                UserEntity u = new UserEntity();
                u.setName("Update Tester");
                u.setUserID(libService.addUser(u));

                libService.addBook(3, "Noli Me Tangere", "Jose Rizal");
                libService.borrowBook(3, u);

                // Attempt update; this should NOT change the DB
                String forbiddenTitle = "Forbidden Update While Borrowed";
                libService.updateBook(3, forbiddenTitle, "");

                // Re-read to ensure DB state (repos already clear on rollback; still safe to re-read)
                BookEntity b = bookRepo.findById(3);
                if (b != null && !forbiddenTitle.equals(b.getTitle())) {
                    pass("Update on borrowed book blocked successfully");
                } else {
                    fail("Borrowed book was incorrectly updated", null);
                }

                // Cleanup: return the book
                int loanId = loanRepo.findLoanIdByBookId(3);
                if (loanId > 0) libService.returnBook(loanId);

            } catch (Exception e) {
                fail("Unexpected error during update-borrowed test", e);
            }

            // 5) Insert User with Blank Name
            printCase(5, "Insert User with Blank Name",
                    "Blank name is rejected with InvalidDBInputException (Validator + DB CHECK).");
            try {
                UserEntity blank = new UserEntity();
                blank.setName(" ");
                userRepo.insert(blank); // Should throw InvalidDBInputException
                fail("Blank name should NOT succeed", null);
            } catch (InvalidDBInputException e) {
                pass("Blank user name correctly rejected");
            } catch (Exception e) {
                fail("Unexpected error inserting blank-name user", e);
            }

            // 6) Insert User with Over-Length Name (> 200)
            printCase(6, "Insert User with Over-Length Name",
                    "Name exceeding 200 chars is rejected with InvalidDBInputException.");
            try {
                int max = DBInputValidator.getMaxUserNameLength();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < max + 1; i++) sb.append('x'); // produce 201-char name

                UserEntity longNameUser = new UserEntity();
                longNameUser.setName(sb.toString());

                userRepo.insert(longNameUser); // Should fail with InvalidDBInputException
                fail("Over-length user name should NOT succeed", null);
            } catch (InvalidDBInputException e) {
                pass("Over-length name correctly rejected");
            } catch (Exception e) {
                fail("Unexpected error inserting over-length user", e);
            }

            System.out.println("\n🎯 All negative-path checks passed.");

        } finally {
            // Mirror Main/HappyPath: close the EM and shut down the factory
            EntityManagerUtil.getInstance().closeEntityManager(em);
            EntityManagerUtil.getInstance().shutdownFactory();
        }
    }
}