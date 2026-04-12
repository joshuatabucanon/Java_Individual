package m6group6.project1.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;

import m6group6.project1.exceptions.DataAccessException;
import m6group6.project1.exceptions.InvalidDBInputException;
import m6group6.project1.util.EntityManagerUtil;
import m6group6.project1.config.DBConfig;

import org.hibernate.exception.JDBCConnectionException;
import org.postgresql.util.PSQLException;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("\n M6_Group6_Project1 Library Application starting... ");
        EntityManagerFactory emf;
        EntityManager em = null;

        try {
        	// These three properties override persistence.xml:
        	Map<String, Object> dbValues = new HashMap<>();
        	dbValues.put("jakarta.persistence.jdbc.url", DBConfig.getUrl());
        	dbValues.put("jakarta.persistence.jdbc.user", DBConfig.getUser());
        	dbValues.put("jakarta.persistence.jdbc.password", DBConfig.getPass());

        	try {
        		emf = Persistence.createEntityManagerFactory("default", dbValues);
        		em = emf.createEntityManager();
            } catch (PersistenceException pe) {
                if (isConnectivityFailure(pe)) {
                    System.err.println(
                            "The application failed to start because it is unable to connect to the database.\n" +
                            "Please check that the database server and its service is running and that connections are correct and allowed."
                        );
                    logger.error(
                    		"Database connection failed (PostgreSQL not running or unreachable). "+
                            "Check if database server and its service is running and that connections to it are allowed. " +
                            "Ensure also that DB configurations are correct", 
                            pe);
                    return; // stop startup cleanly
                }
                // Other persistence error during EM init
                logger.error("Persistence error while initializing EntityManager.", pe);
                System.err.println("A fatal persistence error occurred during startup.");
                return;
            }

            // ---- Normal application startup ----
            logger.info("Database connected successfully.");
            LibraryApplication libraryApplication = new LibraryApplication(em);
            libraryApplication.start();

        } catch (InvalidDBInputException e) {
            // From Repo validation/Bean Validation surfaced upward
            logger.warn("Invalid database input: {}", e.getMessage());
            System.out.println("Invalid input: " + safeMessage(e));

        } catch (DataAccessException e) {
            // From Repo-level DB/infra failures (constraints, timeouts, etc.)
            logger.error("Database operation failed.", e);
            System.out.println("A database operation failed. Please try again, or contact support if the issue persists.");

        } catch (IllegalArgumentException e) {
            // Defensive programming errors (null EM, null entity/argument)
            logger.error("Application configuration or usage error: {}", e.getMessage(), e);
            System.out.println("An internal configuration error occurred. Please contact support.");

        } catch (PersistenceException e) {
            // Safety net for any remaining JPA/Hibernate issues during runtime
            logger.error("Unexpected persistence error.", e);
            System.out.println("A persistence error occurred. Please try again.");

        } catch (Exception e) {
            // Last resort guard
            logger.error("Unexpected error occurred in main().", e);
            System.out.println("A fatal error occurred. Please try again or contact support.");

        } finally {
            logger.trace("Closing Entity Manager.");
            try {
                if (em != null) {
                    EntityManagerUtil.getInstance().closeEntityManager(em);
                    EntityManagerUtil.getInstance().shutdownFactory();
                }
            } catch (Exception closeErr) {
                logger.warn("Cleanup failed in finally: {}", closeErr.getMessage(), closeErr);
            }
            logger.info("Main method finished execution.");
        }
    }

    // --- Helper: Detects "DB is down" class of failures during EM creation ---
    private static boolean isConnectivityFailure(Throwable t) {
        Throwable cur = t;
        while (cur != null) {
            if (cur instanceof JDBCConnectionException) return true;
            if (cur instanceof PSQLException) return true;
            if (cur instanceof SQLException) return true;
            if (cur instanceof ConnectException) return true;
            cur = cur.getCause();
        }
        return false;
    }

    // --- Avoid dumping stack traces or DB internals to end users ---
    private static String safeMessage(Throwable t) {
        String msg = t.getMessage();
        return (msg == null || msg.isBlank()) ? "Please verify your inputs and try again." : msg;
    }
}