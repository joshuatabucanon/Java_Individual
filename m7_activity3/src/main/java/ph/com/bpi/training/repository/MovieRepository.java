package ph.com.bpi.training.repository;

import java.util.List;

import jakarta.persistence.EntityManager;
import ph.com.bpi.training.model.Movie;

public class MovieRepository implements Repository<Movie, Long> {

    private final EntityManager em;

    public MovieRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Movie save(Movie entity) {
        if (entity.getId() == null) {
            em.persist(entity);
        } else {
            entity = em.merge(entity);
        }
        return entity;
    }

    @Override
    public void delete(Movie entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }

    @Override
    public void deleteById(Long id) {
    	Movie movie = findById(id);

        if (movie != null) {
            delete(movie);
        }
    }

    @Override
    public Movie findById(Long id) {
        return em.find(Movie.class, id);
    }

    @Override
    public List<Movie> findAll() {
        return em.createQuery("SELECT m FROM Movie m", Movie.class).getResultList();
    }

}
