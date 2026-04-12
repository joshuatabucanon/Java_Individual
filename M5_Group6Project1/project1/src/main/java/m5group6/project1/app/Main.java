package m5group6.project1.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import m5group6.project1.exceptions.DataAccessException;
import m5group6.project1.exceptions.InvalidDBInputException;
import m5group6.project1.util.DBUtil;

import java.sql.Connection;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("\n           M5_Group6_Project1 Library Application starting... ");

        // Verify DB is reachable before starting the application.
        try (Connection conn = DBUtil.getConnection()) {
            logger.info("Database connected successfully. ");
        } catch (DataAccessException e) {
            logger.error("Database connection failed at startup. "
            		+ "Check if database server and its service is running and that connections to it are allowed. "
            		+ "Ensure also that DB configurations are correct", 
            		e);
            System.err.println("The application failed to start because it is unable to connect to the database. \n"
            				+ "Please check that the database server and its service is running and that connections are correct and allowed. "
            				);
            return;
        } catch (Exception ex) {
            logger.error("Unexpected error during startup. ", ex);
            System.err.println("A fatal error occurred during startup. ");
            return;
        }
        
        // Start the application only if DB connectivity is OK.
        try {
            LibraryApplication libraryApplication = new LibraryApplication();
            libraryApplication.start();
            logger.info("Application exited normally.");
        } catch (InvalidDBInputException e) {
        	logger.error("Invalid DB Input error occured. ", e);
        	System.out.println("\nAn error occured due to invalid input to DB. ");        	
	    } catch (DataAccessException e) {
            logger.error("Data Access error occurred in main(). ", e);
            System.out.println("A data access error has occurred. ");
        } catch (Exception e) {
            logger.error("Fatal error occurred in main(). ", e);
            System.out.println("A fatal error occurred. ");
        } finally {
            logger.info("Main method finished execution. ");
        }
    }
}
