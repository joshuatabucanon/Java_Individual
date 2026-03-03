package ph.com.bpi.training;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;
import java.util.List;


public class Main {
	
	private static final Logger logger =  LoggerFactory.getLogger(Main.class);
	private static final ObjectMapper mapper = new ObjectMapper();
	 
    public static void main(String[] args) {
    	// intialize entityManager;
        EntityManager em = EntityManagerUtil.getInstance().createEntityManager();
    	
        // initialize movieRepository
    	MovieRepository movieRepository = new MovieRepository(em);
    	
    	 // Start server on port 4567 (default)
        port(4567);
        
     // Force JSON output
        after((req, res) -> res.type("application/json"));

        // GET /movies -> return all movies

		get("/movies", (req, res) -> {
		    List<Movie> movies = movieRepository.findAll();
		    return mapper.writeValueAsString(movies);
		});


        // POST /movies -> accept a Movie JSON and save it
        post("/movies", (req, res) -> {
            Movie incoming = mapper.readValue(req.body(), Movie.class);

            EntityTransaction tx = em.getTransaction();
            tx.begin();
            Movie saved = movieRepository.save(incoming);  // uses save() from Repository
            tx.commit();

            res.status(201);
            return mapper.writeValueAsString(saved);
        });
        
        
        
        
    }
    
    

}
