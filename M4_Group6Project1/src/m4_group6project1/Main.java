
package m4_group6project1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        logger.info("\nM4_Group6_Project1 Library Application starting...");

        try {
            LibraryApplication libraryApplication = new LibraryApplication();
            libraryApplication.start();
            logger.info("Application exited normally.");

        } catch (Exception ex) {
            logger.error("Fatal error occurred in main()", ex);
            System.out.println("A fatal error occurred. The program will exit.");

        } finally {
            logger.info("Main method finished execution.");
        }
    }
}
