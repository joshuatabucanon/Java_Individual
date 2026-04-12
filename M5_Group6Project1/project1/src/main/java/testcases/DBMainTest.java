package testcases;

import java.sql.Connection;
import java.sql.SQLException;

import m5group6.project1.service.Library;
import m5group6.project1.util.DBSchema;
import m5group6.project1.util.DBUtil;

public class DBMainTest {
	
    public static void main(String[] args) {
    	Library library = new Library();	
    	
        // Verify DB is reachable before starting the application.
        try (Connection conn = DBUtil.getConnection()) {
        	System.out.println("Database service and connection tested successfully.");
        } catch (SQLException se) {
            System.err.println("The application failed to start because it is unable to connect to the database. \n"
            				+ "Please check that the database server and its service is running and that connections to it are allowed. "
            				);
            return;
        } catch (Exception ex) {
            System.err.println("A fatal error occurred during startup. " + ex);
            return;
        }
	     // Recreate DB schema (drop tables then create).
	     try {
	         DBSchema.createTables();  
	         System.out.println("Database schema recreated successfully.");
	     } catch (Exception ex) {
	         System.err.println("Failed to initialize database schema. Exiting. " + ex);
	         return;
	     }
	     
        try {
            DBInsertTest.insertInitialBooks();
            System.out.println("Inserted book records successfully.");
            
            DBInsertTest.insertInitialUsers();
            System.out.println("Inserted user records successfully.");

        } catch (Exception ex) {
            System.err.println("Failed to execute insert: " + ex);
            return;
        }
        

//        try {
//            // 1) Insert negative book_id (should fail)
//            DBInsertTest.insertNegativeBookId();
//
//            // 2) Mark a seeded book as unavailable (book_id=11 exists in your seed)
//            DBUpdateTest.markBookUnavailableById(11);
//
//            // 3) Attempt to update title/author while unavailable (should fail due to trigger)
//            DBUpdateTest.updateTitleAuthorWhileUnavailable(
//                    11,
//                    "Song of Fire and Dance (Revised Edition)",
//                    "George R. R. Martin"
//            );
//        } catch (Exception ex) {
//            System.err.println("Failed during update tests: " + ex);
//        }

        
        library.displayAllBooks();
    }
}
