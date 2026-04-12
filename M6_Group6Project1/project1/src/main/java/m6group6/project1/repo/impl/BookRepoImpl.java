package m6group6.project1.repo.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.validation.ConstraintViolationException;

import java.util.List;

import m6group6.project1.exceptions.DataAccessException;
import m6group6.project1.exceptions.InvalidDBInputException;
import m6group6.project1.model.BookEntity;
import m6group6.project1.repo.BookRepository;
import m6group6.project1.util.DBInputValidator;

public class BookRepoImpl implements BookRepository {

    private final EntityManager em;

    public BookRepoImpl(EntityManager em) {
        if (em == null) throw new IllegalArgumentException("EntityManager must not be null");
        this.em = em;
    }

    // ------------------------------------------------------------
    // READS
    // ------------------------------------------------------------

    @Override
    public BookEntity findById(int id) {
        try {
            return em.find(BookEntity.class, id);
        } catch (PersistenceException pe) {
            throw new DataAccessException("Failed to find book by id=" + id, pe);
        } catch (RuntimeException re) {
            throw new DataAccessException("Unexpected error while finding book by id=" + id, re);
        }
    }

    @Override
    public List<BookEntity> findAll() {
        try {
            TypedQuery<BookEntity> q = em.createQuery(
                "SELECT b FROM BookEntity b ORDER BY b.title ASC", BookEntity.class);
            return q.getResultList();
        } catch (PersistenceException pe) {
            throw new DataAccessException("Failed to fetch all books", pe);
        } catch (RuntimeException re) {
            throw new DataAccessException("Unexpected error while fetching all books", re);
        }
    }

    @Override
    public List<BookEntity> findAllAvailable() {
        try {
            TypedQuery<BookEntity> q = em.createQuery(
                "SELECT b FROM BookEntity b WHERE b.isAvailable = TRUE ORDER BY b.title ASC",
                BookEntity.class);
            return q.getResultList();
        } catch (PersistenceException pe) {
            throw new DataAccessException("Failed to fetch available books", pe);
        } catch (RuntimeException re) {
            throw new DataAccessException("Unexpected error while fetching available books", re);
        }
    }

    // ------------------------------------------------------------
    // WRITES
    // ------------------------------------------------------------

    @Override
    public boolean insert(BookEntity book) {
        if (book == null) throw new IllegalArgumentException("Book cannot be null");

        // Fail fast against schema limits
        DBInputValidator.ensureBookTitleForInsert(book.getTitle());
        DBInputValidator.ensureBookAuthorForInsert(book.getAuthor());  // schema-aware checks
        if (book.getId() < 1) {
            throw new InvalidDBInputException("Book ID must be at least 1.");
        }
        if (book.getTitle() != null) book.setTitle(book.getTitle().trim());
        if (book.getAuthor() != null) book.setAuthor(book.getAuthor().trim());
        if (book.getIsAvailable() == null) book.setIsAvailable(Boolean.TRUE);

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            em.persist(book);
            em.flush(); // ensure INSERT now (catch constraint issues early)

            tx.commit();
            return true;

        } catch (ConstraintViolationException v) {
            rollbackAndClear(tx); // <-- clears EM after rollback
            throw new InvalidDBInputException("Book validation failed: " + v.getMessage(), v);

        } catch (PersistenceException pe) {
            rollbackAndClear(tx); // <-- clears EM after rollback
            throw new DataAccessException("Failed to insert book id=" + book.getId(), pe);

        } catch (RuntimeException re) {
            rollbackAndClear(tx); // <-- clears EM after rollback
            throw new DataAccessException("Unexpected error while inserting book id=" + book.getId(), re);
        }
    }

    /**
     * Load the managed entity, verify it's available,then mutate only when allowed. 
     * On any failure, we rollback and clear the persistence context so stale in-memory state cannot linger.
     */
    @Override
    public boolean updateIfAvailable(int bookId, String title, String author) {
        // Lightweight guards that match your UI/service semantics
        DBInputValidator.ensureBookTitleForUpdate(title);
        DBInputValidator.ensureBookAuthorForUpdate(author);

        final String newTitle  = (title  == null || title.trim().isEmpty())  ? null : title.trim();
        final String newAuthor = (author == null || author.trim().isEmpty()) ? null : author.trim();

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            BookEntity b = em.find(BookEntity.class, bookId);
            if (b == null) {                 // no such book
                tx.commit();
                return false;
            }
            if (Boolean.FALSE.equals(b.getIsAvailable())) {
                // Borrowed: do not mutate in-memory state
                tx.commit();
                return false;
            }

            boolean changed = false;
            if (newTitle != null)  { 
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
            // e.g., Bean Validation limits; clear EM so mutated state doesn't survive
            rollbackAndClear(tx); // <-- new centralized helper
            throw new InvalidDBInputException("Book validation failed: " + v.getMessage(), v);

        } catch (PersistenceException pe) {
            // e.g., trigger/constraint rejection at DB side
            rollbackAndClear(tx); // <-- ensures first-level cache is cleared
            throw new DataAccessException("Failed to update (must be available) book id=" + bookId, pe);

        } catch (RuntimeException re) {
            rollbackAndClear(tx); // <-- defensive
            throw new DataAccessException("Unexpected error while updating book id=" + bookId, re);
        }
    }

    @Override
    public boolean deleteById(int id) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            BookEntity b = em.find(BookEntity.class, id);
            if (b == null) { tx.commit(); return false; }

            em.remove(b);
            tx.commit();
            return true;

        } catch (PersistenceException pe) {
            rollbackAndClear(tx); // <-- clears EM after rollback
            throw new DataAccessException("Failed to delete book id=" + id, pe);

        } catch (RuntimeException re) {
            rollbackAndClear(tx); // <-- clears EM after rollback
            throw new DataAccessException("Unexpected error while deleting book id=" + id, re);
        }
    }

    // ------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------

    private void rollbackAndClear(EntityTransaction tx) {
        if (tx != null && tx.isActive()) {
            try {
                tx.rollback();
            } finally {
                // IMPORTANT for single-EM apps:
                // Drop all managed instances to avoid stale in-memory state
                em.clear();
            }
        } else {
            // Even if no active tx, clearing can still protect against stale managed entities
            em.clear();
        }
    }
}