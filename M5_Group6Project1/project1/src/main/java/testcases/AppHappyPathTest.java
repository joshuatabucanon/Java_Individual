package testcases;

import m5group6.project1.dao.BookDAO;
import m5group6.project1.dao.DAOFactory;
import m5group6.project1.dao.LoanDAO;
//import m5group6.project1.dao.UserDAO;
import m5group6.project1.exceptions.InvalidBookIdException;
import m5group6.project1.model.Book;
import m5group6.project1.model.User;
import m5group6.project1.service.Library;
import m5group6.project1.util.DBSchema;

public class AppHappyPathTest {

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            System.out.println("❌ FAIL: " + message);
            System.exit(1); // non-zero exit signals failure to scripts/CI
        } else {
            System.out.println("✅ PASS: " + message);
        }
    }

    public static void main(String[] args) throws Exception {
        // 0) Reset DB to a known state (drops & recreates tables, triggers, indexes)
        System.out.println("Recreating schema...");
        DBSchema.createTables(); // uses DBUtil/DBConfig for connection details
        System.out.println("Schema ready.\n");

        // 1) Wire service & DAOs
        Library library = new Library();            // façade for features
        BookDAO bookDAO = DAOFactory.bookDAO();     // for state checks
        LoanDAO loanDAO = DAOFactory.loanDAO();     // for state checks
//        UserDAO userDAO = DAOFactory.userDAO();     // available if needed

        // 2) Create user (happy path)
        User u = new User();
        u.setName("Happy Path User");
        int newUserId = library.addUser(u);
        u.setUserID(newUserId);
        assertTrue(newUserId > 0, "User created with generated ID");

        // 3) Add books (happy path for addBook)
        System.out.println("\nAdding books...");
        addBookOrFail(library, 1, "Song of Ice and Fire", "George R.R. Martin");
        addBookOrFail(library, 2, "Harry Potter", "J.K. Rowling");
        addBookOrFail(library, 3, "The Fault in Our Stars", "John Green");
        addBookOrFail(library, 4, "Noli Me Tangere", "Jose Rizal");
        addBookOrFail(library, 5, "Clean Code", "Robert C. Martin");

        assertTrue(bookDAO.findAll().size() == 5, "Five books exist in the library");

        // 4) Display features (no assertions on console output—just exercise)
        System.out.println("\n-- Display All Books --");
        library.displayAllBooks();

        System.out.println("\n-- Display Available Books --");
        library.displayAvailableBooks();

        System.out.println("\n-- Display Borrowed Books (should be none) --");
        library.displayBorrowedBooks();

        // 5) Borrow a book (feature: borrowBook)
        System.out.println("\nBorrowing book #2 for user " + u.getUserID());
        library.borrowBook(2, u);
        int loanId = loanDAO.findLoanIdByBookId(2);
        assertTrue(loanId > 0, "Loan created for book #2");
        Book b2 = bookDAO.findById(2);
        assertTrue(b2 != null && Boolean.FALSE.equals(b2.getIsAvailable()), "Book #2 marked unavailable after borrowing");

        // 6) Display borrowed list (feature: displayBorrowedBooks)
        System.out.println("\n-- Display Borrowed Books (should list 1) --");
        library.displayBorrowedBooks();

        // 7) Update book (feature: updateBook) – update title only, keep author
        String newTitle = "Song of Ice and Fire (updated)";
        library.updateBook(1, newTitle, "");
        Book b2updated = bookDAO.findById(1);
        assertTrue(b2updated != null && newTitle.equals(b2updated.getTitle()), "Book #2 title updated");

        // 8) Return the book (feature: returnBook)
        System.out.println("\nReturning loan #" + loanId);
        library.returnBook(loanId);
        assertTrue(!loanDAO.existsById(loanId), "Loan closed");
        Book b2returned = bookDAO.findById(2);
        assertTrue(b2returned != null && Boolean.TRUE.equals(b2returned.getIsAvailable()), "Book #2 is available again");

        // 9) Remove a book (feature: removeBook)
        System.out.println("\nRemoving book #3");
        library.removeBook(3);
        assertTrue(bookDAO.findById(3) == null, "Book #3 removed");

        // 10) Display available at end (exercise display again)
        System.out.println("\n-- Display Available Books (end state) --");
        library.displayAvailableBooks();

        System.out.println("\n🎉 All happy-path checks passed.");
    }

    private static void addBookOrFail(Library library, int id, String title, String author) throws InvalidBookIdException {
        boolean ok = library.addBook(id, title, author);
        assertTrue(ok, "Book added: id=" + id + ", title='" + title + "', author='" + author + "'");
    }
}