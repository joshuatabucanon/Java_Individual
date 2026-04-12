package testcases;

import m5group6.project1.dao.BookDAO;
import m5group6.project1.dao.DAOFactory;
import m5group6.project1.dao.LoanDAO;
import m5group6.project1.dao.UserDAO;
//import m5group6.project1.exceptions.DataAccessException;
import m5group6.project1.exceptions.InvalidBookIdException;
import m5group6.project1.exceptions.InvalidDBInputException;
import m5group6.project1.model.Book;
import m5group6.project1.model.User;
import m5group6.project1.service.Library;
import m5group6.project1.util.DBSchema;
import m5group6.project1.util.DBInputValidator;

public class AppNegativeTest {

    private Library library = new Library();
    private BookDAO bookDAO = DAOFactory.bookDAO();
    private LoanDAO loanDAO = DAOFactory.loanDAO();
    private UserDAO userDAO = DAOFactory.userDAO();

    public static void main(String[] args) throws Exception {
    	AppNegativeTest test = new AppNegativeTest();
        test.run();
    }

    public void run() throws Exception {
        System.out.println("Resetting schema for Unhappy Path Test...");
        DBSchema.createTables();
        System.out.println("Schema ready.\n");

        testAddBookInvalidId();
        testAddBookDuplicateId();
        testBorrowAlreadyBorrowedBook();
        testUpdateBorrowedBook();
        testInsertUserBlankName();
        testInsertUserOverMaxLength();

        System.out.println("\n========== Unhappy Path Test Completed ==========");
    }

    // Helper PASS/FAIL printer
    private void pass(String scenario) {
        System.out.println("✅ PASS: " + scenario);
    }

    private void fail(String scenario, Throwable t) {
        System.out.println("❌ FAIL: " + scenario);
        if (t != null) {
            System.out.println("   → " + t.getClass().getSimpleName() + ": " + t.getMessage());
        }
    }

    // ============================================================
    // UNHAPPY PATH TESTS
    // ============================================================

    public void testAddBookInvalidId() {
        System.out.println("\n[TEST] Add Book with ID < 1");
        try {
            library.addBook(0, "Invalid ID Book", "Nobody");  // should fail (service check)
            fail("Adding a book with ID < 1 should NOT succeed", null);
        } catch (InvalidBookIdException e) {
            pass("Rejected book with ID < 1");
        } catch (Exception e) {
            fail("Unexpected error adding book with invalid ID", e);
        }
    }

    public void testAddBookDuplicateId() {
        System.out.println("\n[TEST] Add Book with Duplicate ID");
        try {
            // First add works
            library.addBook(1, "Clean Code", "Robert C. Martin");

            // Second add should fail due to duplicate ID (checked in Library.addBook)
            library.addBook(1, "Duplicate Clean Code", "Someone");
            fail("Adding duplicate book ID should NOT succeed", null);
        } catch (InvalidBookIdException e) {
            pass("Duplicate book ID correctly rejected");
        } catch (Exception e) {
            fail("Unexpected error adding duplicate book", e);
        }
    }

    public void testBorrowAlreadyBorrowedBook() {
        System.out.println("\n[TEST] Borrow Already Borrowed Book");
        try {
            // Create user
            User u = new User();
            u.setName("Borrow Tester");
            u.setUserID(library.addUser(u));

            // Add book & borrow once
            library.addBook(2, "Harry Potter", "J.K. Rowling");
            library.borrowBook(2, u);

            // Second borrow attempt should fail logically
            library.borrowBook(2, u);

            // Now check DAO state to confirm borrow did NOT occur
            int loanId = loanDAO.findLoanIdByBookId(2);
            Book b = bookDAO.findById(2);

            if (loanId > 0 && Boolean.FALSE.equals(b.getIsAvailable())) {
                pass("Borrowing an already borrowed book was blocked");
            } else {
                fail("Borrowing borrowed book should not succeed", null);
            }
        } catch (Exception e) {
            fail("Unexpected error during borrow-borrow test", e);
        }
    }

    public void testUpdateBorrowedBook() {
        System.out.println("\n[TEST] Update Borrowed Book");

        try {
            // Setup: Create user, add book, borrow book
            User u = new User();
            u.setName("Update Tester");
            u.setUserID(library.addUser(u));

            library.addBook(3, "Noli Me Tangere", "Jose Rizal");
            library.borrowBook(3, u);

            // Attempt update; this should NOT change the DB (trigger + DAO guard)
            String forbiddenTitle = "Forbidden Update While Borrowed";
            library.updateBook(3, forbiddenTitle, "");

            // Check in DB if the title changed
            Book b = bookDAO.findById(3);
            if (!forbiddenTitle.equals(b.getTitle())) {
                pass("Update on borrowed book blocked successfully");
            } else {
                fail("Borrowed book was incorrectly updated", null);
            }
        } catch (Exception e) {
            fail("Unexpected error during update-borrowed test", e);
        }
        finally {
            // cleanup: return the book, if loan exists
            int loanId = loanDAO.findLoanIdByBookId(3);
            if (loanId > 0) library.returnBook(loanId);
        }
    }

    public void testInsertUserBlankName() {
        System.out.println("\n[TEST] Insert User with Blank Name");
        try {
            User blank = new User();
            blank.setName("   "); // UI-level invalid, also DB CHECK(btrim(name) <> '')
            userDAO.insert(blank); // Should throw InvalidDBInputException
            fail("Blank name should NOT succeed", null);
        } catch (InvalidDBInputException e) {
            pass("Blank user name correctly rejected");
        } catch (Exception e) {
            fail("Unexpected error inserting blank-name user", e);
        }
    }

    public void testInsertUserOverMaxLength() {
        System.out.println("\n[TEST] Insert User with Over-Length Name");

        try {
            int max = DBInputValidator.getMaxUserNameLength();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < max + 1; i++) sb.append('x'); // produce 201-char name

            User longNameUser = new User();
            longNameUser.setName(sb.toString());

            userDAO.insert(longNameUser);  // Should fail
            fail("Over-length user name should NOT succeed", null);

        } catch (InvalidDBInputException e) {
            pass("Over-length name correctly rejected");
        } catch (Exception e) {
            fail("Unexpected error inserting over-length user", e);
        }
    }
}