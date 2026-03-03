package ph.com.bpi.training.controller;

import static spark.Spark.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ph.com.bpi.training.dto.MovieRequestDTO;
import ph.com.bpi.training.model.Movie;
import ph.com.bpi.training.service.MovieService;
import ph.com.bpi.training.util.JsonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieController {
    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    // Register all routes here
    public void registerRoutes() {
        after((req, res) -> res.type("application/json"));

        get("/movies", (req, res) -> {
            Map<String, Object> response = new HashMap<>();
            try {
                List<Movie> movies = movieService.findAll();
                response.put("data", movies);
                response.put("status", "Successful");
                res.status(200);
                logger.info("GET /movies - Returned {} movie(s)", movies.size());
            } catch (Exception e) {
                response.put("status", "Failed");
                response.put("error", e.getMessage());
                res.status(500);
                logger.error("GET /movies - Error fetching movie list: {}", e.getMessage(), e);
            }
            return JsonUtil.toJson(response);
        });

        post("/movies", (req, res) -> {
            Map<String, Object> response = new HashMap<>();
            try {
                MovieRequestDTO incoming = JsonUtil.fromJson(req.body(), MovieRequestDTO.class);
                Movie saved = movieService.save(incoming);

                response.put("status", "Successful");
                response.put("data", saved);

                res.status(201);
                logger.info("POST /movies - Movie saved with id={}", saved.getId());
            } catch (Exception e) {
                response.put("status", "Failed");
                response.put("error", e.getMessage());
                res.status(400); 
                logger.error("POST /movies - Failed to save movie: {}", e.getMessage(), e);
            }
            return JsonUtil.toJson(response);
        });
    }
}