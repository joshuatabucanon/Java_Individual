package m6group6.project1.repo.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;

import m6group6.project1.exceptions.DataAccessException;
import m6group6.project1.exceptions.InvalidDBInputException;
import m6group6.project1.model.UserEntity;
import m6group6.project1.repo.UserRepository;
import m6group6.project1.util.DBInputValidator;

public class UserRepoImpl implements UserRepository {

    private final EntityManager em;

    public UserRepoImpl(EntityManager em) {
        if (em == null) throw new IllegalArgumentException("EntityManager must not be null");
        this.em = em;
    }

    @Override
    public int insert(UserEntity user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        // Guard: DB-aligned rules (non-blank, <= 200 chars)
        DBInputValidator.ensureUserNameForInsert(user.getName());

        // Normalize whitespace (parity with your JDBC path)
        if (user.getName() != null) {
            user.setName(user.getName().trim());
        }

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            em.persist(user); // INSERT INTO users(name)
            em.flush();       // force INSERT so IDENTITY is available

            tx.commit();

            Integer id = user.getUserID(); // your existing getter name
            if (id == null) {
                // Very defensive: should not happen with IDENTITY & flush()
                throw new DataAccessException("Insert succeeded but no user_id returned", null);
            }
            return id;

        } catch (ConstraintViolationException v) {
            // Bean Validation (e.g., blank or >200 chars)
            rollbackAndClear(tx);
            throw new InvalidDBInputException("User validation failed: ", v);

        } catch (PersistenceException pe) {
            // DB/persistence-layer failure (e.g., connectivity, constraint)
            rollbackAndClear(tx);
            throw new DataAccessException("Failed to insert user", pe);

        } catch (RuntimeException re) {
            // Defensive catch-all with consistent data-layer mapping
            rollbackAndClear(tx);
            throw new DataAccessException("Unexpected error while inserting user", re);
        }
    }

    /**
     * Roll back the active transaction and clear the persistence context
     * so a single-EntityManager app doesn't retain stale managed entities
     * after a failed write.
     */
    private void rollbackAndClear(EntityTransaction tx) {
        if (tx != null && tx.isActive()) {
            try {
                tx.rollback();
            } finally {
                // Drop in-memory state to force fresh DB reads next time
                em.clear();
            }
        } else {
            // Even if no active tx, clear to avoid dangling managed changes
            em.clear();
        }
    }
}