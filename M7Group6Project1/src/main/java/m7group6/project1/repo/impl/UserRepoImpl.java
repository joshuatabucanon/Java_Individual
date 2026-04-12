package m7group6.project1.repo.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;

import m7group6.project1.exceptions.DataAccessException;
import m7group6.project1.exceptions.InvalidDBInputException;
import m7group6.project1.model.UserEntity;
import m7group6.project1.repo.UserRepository;

public class UserRepoImpl implements UserRepository {
    private final EntityManagerFactory emf;

    public UserRepoImpl(EntityManagerFactory emf) {
        if (emf == null) throw new IllegalArgumentException("EntityManagerFactory must not be null");
        this.emf = emf;
    }

    @Override
    public int insert(UserEntity user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.persist(user);  // INSERT INTO users(name)
            em.flush();        // ensure IDENTITY is assigned and validation runs
            tx.commit();

            Integer id = user.getUserID();
            if (id == null) {
                throw new DataAccessException("Insert succeeded but no user_id returned", null);
            }
            return id;
        } catch (ConstraintViolationException v) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new InvalidDBInputException("User validation failed: " + v.getMessage(), v);
        } catch (PersistenceException pe) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new DataAccessException("Failed to insert user", pe);
        } catch (RuntimeException re) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new DataAccessException("Unexpected error while inserting user", re);
        } finally {
            em.close();
        }
    }
}