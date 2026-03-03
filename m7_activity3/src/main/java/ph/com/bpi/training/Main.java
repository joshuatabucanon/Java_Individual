package ph.com.bpi.training;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ph.com.bpi.training.controller.MovieController;
import ph.com.bpi.training.service.MovieService;
import ph.com.bpi.training.util.EntityManagerUtil;

import jakarta.persistence.EntityManager;

import static spark.Spark.*;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        port(4567);  

        // Create EntityManager in Main
        EntityManager em = EntityManagerUtil.getInstance().createEntityManager();

        // Pass EM to the service
        MovieService movieService = new MovieService(em);
        MovieController movieController = new MovieController(movieService);

        // Register all routes
        movieController.registerRoutes();

        logger.info("Server started on port {}", 4567);
    }
}