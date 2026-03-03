package ph.com.bpi.training.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import ph.com.bpi.training.dto.MovieRequestDTO;
import ph.com.bpi.training.model.Movie;
import ph.com.bpi.training.repository.MovieRepository;

import java.util.List;

public class MovieService {
    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    private final EntityManager em;
    private final MovieRepository movieRepository;

    public MovieService(EntityManager em) {
        this.em = em;
        this.movieRepository = new MovieRepository(em);
    }

    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    public Movie save(MovieRequestDTO dto) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Movie entity = mapToEntity(dto);
            Movie saved = movieRepository.save(entity);

            tx.commit();
            logger.info("Movie saved successfully with id={}", saved.getId());
            return saved;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            logger.error("Error saving movie: {}", e.getMessage(), e);
            throw e;
        }
    }

    private Movie mapToEntity(MovieRequestDTO dto) {
        Movie m = new Movie();
        m.setTitle(dto.getTitle());
        m.setDirector(dto.getDirector());
        m.setShowingDate(dto.getShowingDate());
        return m;
    }
}