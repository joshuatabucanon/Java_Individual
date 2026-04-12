package testcases;

import jakarta.persistence.EntityManager;

import m6group6.project1.exceptions.InvalidBookIdException;
import m6group6.project1.model.BookEntity;
import m6group6.project1.model.UserEntity;
import m6group6.project1.repo.BookRepository;
import m6group6.project1.repo.LoanRepository;
import m6group6.project1.repo.UserRepository;
import m6group6.project1.repo.impl.BookRepoImpl;   // JPA repo with constructor-injected EM
import m6group6.project1.repo.impl.LoanRepoImpl;   // JPA repo with constructor-injected EM
import m6group6.project1.repo.impl.UserRepoImpl;   // JPA repo with constructor-injected EM
import m6group6.project1.service.LibraryServiceImpl;
import m6group6.project1.util.EntityManagerUtil;

public class AppHappyPathTest {

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            System.out.println("❌ FAIL: " + message);
            System.exit(1); // non-zero exit signals failure to scripts/CI
        } else {
            System.out.println("✅ PASS: " + message);
        }
    }

    private static void printCase(int no, String title, String expected) {
        System.out.println();
        System.out.println("======================================================================");
        System.out.println("TEST " + no + ": " + title);
        System.out.println("Expected: " + expected);
        System.out.println("======================================================================");
    }

    public static void main(String[] args) throws Exception {
        EntityManager em = null;
        try {
            // --- Single EntityManager for the entire happy-path test (like Main) ---
            em = EntityManagerUtil.getInstance().createEntityManager();

            // --- Wire JPA repositories with the same EM (no RepoFactory) ---
            BookRepository bookRepo = new BookRepoImpl(em);
            LoanRepository loanRepo = new LoanRepoImpl(em);
            UserRepository userRepo = new UserRepoImpl(em);

            // --- Use the 3-arg LibraryServiceImpl constructor ---
            LibraryServiceImpl libService = new LibraryServiceImpl(bookRepo, loanRepo, userRepo);

            // 1) Create user (happy path)
            printCase(1, "Create a user",
                    "A user is persisted and receives a generated user ID (> 0).");
            UserEntity u = new UserEntity();
            u.setName("Happy Path User");
            int newUserId = libService.addUser(u);
            u.setUserID(newUserId);
            assertTrue(newUserId > 0, "User [" + u.getName() + "] created with generated ID [" + u.getUserID() + "]");

            // 2) Add books (happy path for addBook)
            printCase(2, "Add five books",
                    "All five books are inserted; repository count becomes 5.");
            System.out.println("Adding books...");
            addBookOrFail(libService, 1, "Song of Ice and Fire", "George R.R. Martin");
            addBookOrFail(libService, 2, "Harry Potter", "J.K. Rowling");
            addBookOrFail(libService, 3, "The Fault in Our Stars", "John Green");
            addBookOrFail(libService, 4, "Noli Me Tangere", "Jose Rizal");
            addBookOrFail(libService, 5, "Clean Code", "Robert C. Martin");
            assertTrue(bookRepo.findAll().size() == 5, "Five books exist in the library");

            // 3) Display features (exercise output)
            printCase(3, "Display all books",
                    "All five books are printed with their current availability.");
            libService.displayAllBooks();

            printCase(4, "Display available books",
                    "All five books are available initially.");
            libService.displayAvailableBooks();

            printCase(5, "Display borrowed books (none yet)",
                    "No borrowed books are listed.");
            libService.displayBorrowedBooks();

            // 4) Borrow a book
            printCase(6, "Borrow book #2 for the user",
                    "A loan row is created and book #2 becomes unavailable.");
            System.out.println("Borrowing book #2 for user " + u.getUserID());
            libService.borrowBook(2, u);
            int loanId = loanRepo.findLoanIdByBookId(2);
            assertTrue(loanId > 0, "Loan created for book #2");
            BookEntity b2 = bookRepo.findById(2);
            assertTrue(b2 != null && Boolean.FALSE.equals(b2.getIsAvailable()),
                       "Book #2 marked unavailable after borrowing");

            // 5) Display borrowed list (should list 1)
            printCase(7, "Display borrowed books (should list 1)",
                    "List shows one borrowed book entry for book #2.");
            libService.displayBorrowedBooks();

            // 6) Update book – update title only, keep author
            printCase(8, "Update title of book #1 that is available",
                    "Book #1 title is updated; author remains unchanged.");
            String newTitle = "Song of Ice and Fire (updated)";
            libService.updateBook(1, newTitle, "");
            BookEntity b2updated = bookRepo.findById(1);
            assertTrue(b2updated != null && newTitle.equals(b2updated.getTitle()),
                       "Book #2 title updated");

            // 7) Return the book
            printCase(9, "Return the loan for book #2",
                    "Loan row is deleted and book #2 becomes available again.");
            System.out.println("Returning loan #" + loanId);
            libService.returnBook(loanId);
            assertTrue(!loanRepo.existsById(loanId), "Loan closed");
            BookEntity b2returned = bookRepo.findById(2);
            assertTrue(b2returned != null && Boolean.TRUE.equals(b2returned.getIsAvailable()),
                       "Book #2 is available again");

            // 8) Remove a book
            printCase(10, "Remove book #3",
                    "Book #3 is deleted (and any dependent rows cascaded by DB).");
            System.out.println("Removing book #3");
            libService.removeBook(3);
            assertTrue(bookRepo.findById(3) == null, "Book #3 removed");

            // 9) Display available at end
            printCase(11, "Display available books (end state)",
                    "4 Books currently available (including #2 again) are listed.");
            libService.displayAvailableBooks();

            System.out.println("\n🎉 All happy-path checks passed.");
        } finally {
            // Mirror Main: close the EM and shut down the factory
            EntityManagerUtil.getInstance().closeEntityManager(em);
            EntityManagerUtil.getInstance().shutdownFactory();
        }
    }

    private static void addBookOrFail(LibraryServiceImpl library, int id, String title, String author)
            throws InvalidBookIdException {
        boolean ok = library.addBook(id, title, author);
        assertTrue(ok, "Book added: id=" + id + ", title='" + title + "', author='" + author + "'");
    }
}
