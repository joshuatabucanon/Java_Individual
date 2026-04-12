package m7group6.project1.repo.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.validation.ConstraintViolationException;

import java.util.List;

import m7group6.project1.exceptions.DataAccessException;
import m7group6.project1.exceptions.InvalidDBInputException;
import m7group6.project1.model.BookEntity;
import m7group6.project1.repo.BookRepository;

public class BookRepoImpl implements BookRepository {
    private final EntityManagerFactory emf;

    public BookRepoImpl(EntityManagerFactory emf) {
        if (emf == null) throw new IllegalArgumentException("EntityManagerFactory must not be null");
        this.emf = emf;
    }

    // ------------------------------------------------------------
    // READS
    // ------------------------------------------------------------
    @Override
    public BookEntity findById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(BookEntity.class, id);
        } catch (PersistenceException pe) {
            throw new DataAccessException("Failed to find book by id=" + id, pe);
        } catch (RuntimeException re) {
            throw new DataAccessException("Unexpected error while finding book by id=" + id, re);
        } finally {
            em.close();
        }
    }

    @Override
    public List<BookEntity> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<BookEntity> q = em.createQuery(
                "SELECT b FROM BookEntity b ORDER BY b.title ASC", BookEntity.class);
            return q.getResultList();
        } catch (PersistenceException pe) {
            throw new DataAccessException("Failed to fetch all books", pe);
        } catch (RuntimeException re) {
            throw new DataAccessException("Unexpected error while fetching all books", re);
        } finally {
            em.close();
        }
    }

    @Override
    public List<BookEntity> findAllAvailable() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<BookEntity> q = em.createQuery(
                "SELECT b FROM BookEntity b WHERE b.isAvailable = TRUE ORDER BY b.title ASC",
                BookEntity.class);
            return q.getResultList();
        } catch (PersistenceException pe) {
            throw new DataAccessException("Failed to fetch available books", pe);
        } catch (RuntimeException re) {
            throw new DataAccessException("Unexpected error while fetching available books", re);
        } finally {
            em.close();
        }
    }

    // ------------------------------------------------------------
    // WRITES
    // ------------------------------------------------------------
    @Override
    public boolean insert(BookEntity book) {
        if (book == null) throw new IllegalArgumentException("Book cannot be null");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.persist(book);     // Bean Validation runs here on flush
            em.flush();           // ensure INSERT and catch constraint issues early
            tx.commit();
            return true;
        } catch (ConstraintViolationException v) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new InvalidDBInputException("Book validation failed: " + v.getMessage(), v);
        } catch (PersistenceException pe) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new DataAccessException("Failed to insert book id=" + book.getId(), pe);
        } catch (RuntimeException re) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new DataAccessException("Unexpected error while inserting book id=" + book.getId(), re);
        } finally {
            em.close();
        }
    }

    @Override
    public boolean updateIfAvailable(int bookId, String title, String author) {
        final String newTitle = (title == null || title.trim().isEmpty()) ? null : title.trim();
        final String newAuthor = (author == null || author.trim().isEmpty()) ? null : author.trim();

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            BookEntity b = em.find(BookEntity.class, bookId);
            if (b == null) {
                tx.commit();
                return false;
            }
            if (Boolean.FALSE.equals(b.getIsAvailable())) {
                // respect the domain rule: cannot update while loaned
                tx.commit();
                return false;
            }
            boolean changed = false;
            if (newTitle != null) {
                b.setTitle(newTitle);
                changed = true;
            }
            if (newAuthor != null) {
                b.setAuthor(newAuthor);
                changed = true;
            }
            tx.commit();
            return changed;
        } catch (ConstraintViolationException v) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new InvalidDBInputException("Book validation failed: " + v.getMessage(), v);
        } catch (PersistenceException pe) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new DataAccessException("Failed to update (must be available) book id=" + bookId, pe);
        } catch (RuntimeException re) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new DataAccessException("Unexpected error while updating book id=" + bookId, re);
        } finally {
            em.close();
        }
    }

    @Override
    public boolean deleteById(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            BookEntity b = em.find(BookEntity.class, id);
            if (b == null) {
                tx.commit();
                return false;
            }
            em.remove(b);
            tx.commit();
            return true;
        } catch (PersistenceException pe) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new DataAccessException("Failed to delete book id=" + id, pe);
        } catch (RuntimeException re) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new DataAccessException("Unexpected error while deleting book id=" + id, re);
        } finally {
            em.close();
        }
    }
}