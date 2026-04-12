package m7group6.project1.app;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.stop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import m7group6.project1.api.controllers.BookController;
import m7group6.project1.api.controllers.LoanController;
import m7group6.project1.api.controllers.UserController;
import m7group6.project1.config.DBConfig;

public class MainApiApplication {
    private static final Logger log = LoggerFactory.getLogger(MainApiApplication.class);
    private static EntityManagerFactory emf;

    public static void main(String[] args) {

        // --- Override JDBC settings with DBConfig ---
        Map<String, Object> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.url", DBConfig.getUrl());
        props.put("jakarta.persistence.jdbc.user", DBConfig.getUser());
        props.put("jakarta.persistence.jdbc.password", DBConfig.getPass());

        // ---- Fast, explicit JDBC probe BEFORE JPA (short timeout) ----
        try {
            DriverManager.setLoginTimeout(5); // seconds
            log.info("Probing database connectivity to {} as {}", DBConfig.getUrl(), DBConfig.getUser());
            try (Connection ignored =
                     DriverManager.getConnection(DBConfig.getUrl(), DBConfig.getUser(), DBConfig.getPass())) {
                log.info("Database connectivity probe succeeded");
            }
        } catch (Exception e) {
            String msg = String.format("Database is unreachable at startup. url=%s, user=%s",
                                       DBConfig.getUrl(), DBConfig.getUser());
            log.error(msg);
            System.err.println(msg);
            log.debug("Connectivity probe exception", e);
            try { stop(); } catch (Exception ignore) {}
            System.exit(1);
            return; // just in case
        } finally {
            DriverManager.setLoginTimeout(0);
        }

        // ---- Build EMF (now that JDBC probe succeeded) ----
        try {
            log.info("Initializing JPA EntityManagerFactory");
            emf = Persistence.createEntityManagerFactory("default", props);
            try (EntityManager em = emf.createEntityManager()) {
                // no-op connectivity test via JPA
            }
            log.info("JPA initialized");
        } catch (PersistenceException pe) {
            String msg = String.format(
                "Failed to initialize JPA. Check database availability and DBConfig settings. url=%s, user=%s",
                DBConfig.getUrl(), DBConfig.getUser()
            );
            log.error(msg);
            System.err.println(msg);
            log.debug("JPA bootstrap exception", pe);
            try {
                if (emf != null && emf.isOpen()) {
                    emf.close();
                }
            } catch (Exception ignore) {}
            try { stop(); } catch (Exception ignore) {}
            System.exit(1);
            return;
        }

        // ---- Wire service locator and start Spark ----
        ServiceLocator.init(emf);
        int httpPort = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        port(httpPort);
        before((req, res) -> res.type("application/json"));
        get("/health", (req, res) -> "{\"status\":\"up\"}");

        BookController.register();
        UserController.register();
        LoanController.register();

        log.info("API started on port {}. Health: GET /health", httpPort);

        // ---- Clean shutdown hook ----
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down API");
            try { stop(); } catch (Exception ignore) {}
            try {
                if (emf != null && emf.isOpen()) {
                    emf.close();
                    log.info("EntityManagerFactory closed");
                }
            } catch (Exception ex) {
                log.warn("Error while closing EntityManagerFactory", ex);
            }
        }));
    }
}