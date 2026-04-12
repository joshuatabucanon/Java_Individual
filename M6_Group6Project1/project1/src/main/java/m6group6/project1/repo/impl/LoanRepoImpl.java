package m6group6.project1.repo.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.validation.ConstraintViolationException;

import java.util.List;

import m6group6.project1.exceptions.DataAccessException;
import m6group6.project1.exceptions.InvalidDBInputException;
import m6group6.project1.model.BookEntity;
import m6group6.project1.model.LoanEntity;
import m6group6.project1.model.UserEntity;
import m6group6.project1.repo.LoanRepository;

public class LoanRepoImpl implements LoanRepository {

    private final EntityManager em;

    public LoanRepoImpl(EntityManager em) {
        if (em == null) throw new IllegalArgumentException("EntityManager must not be null");
        this.em = em;
    }

    // ------------------------------------------------------------
    // Reads (simple queries do not require explicit transactions)
    // ------------------------------------------------------------

    @Override
    public LoanEntity findById(int loanId) {
        try {
            // ManyToOne is EAGER by default; book and user will be available
            return em.find(LoanEntity.class, loanId);
        } catch (PersistenceException pe) {
            throw new DataAccessException("Failed to find loan by id=" + loanId, pe);
        } catch (RuntimeException re) {
            throw new DataAccessException("Unexpected error while finding loan by id=" + loanId, re);
        }
    }

    @Override
    public List<LoanEntity> findAllActive() {
        try {
            // Mirrors: SELECT l ... JOIN book JOIN user ORDER BY l.loan_id
            TypedQuery<LoanEntity> q = em.createQuery(
                "SELECT l FROM LoanEntity l " +
                "JOIN l.book b " +
                "JOIN l.user u " +
                "ORDER BY l.loanId",
                LoanEntity.class);
            return q.getResultList();
        } catch (PersistenceException pe) {
            throw new DataAccessException("Failed to fetch active loans", pe);
        } catch (RuntimeException re) {
            throw new DataAccessException("Unexpected error while fetching active loans", re);
        }
    }

    @Override
    public int countActiveLoansByUser(int userId) {
        try {
            // There is no "returned" state; every row in loans is active (schema)
            Long count = em.createQuery(
                    "SELECT COUNT(l) FROM LoanEntity l WHERE l.user.userId = :uid", Long.class)
                .setParameter("uid", userId)
                .getSingleResult();
            return count.intValue();
        } catch (PersistenceException pe) {
            throw new DataAccessException("Failed to count active loans for userId=" + userId, pe);
        } catch (RuntimeException re) {
            throw new DataAccessException("Unexpected error while counting loans for userId=" + userId, re);
        }
    }

    // ------------------------------------------------------------
    // Writes (explicit transaction; guard-then-mutate)
    // ------------------------------------------------------------

    @Override
    public int createLoanAndMarkBookUnavailable(int bookId, int userId) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            // Guard: require an existing and currently-available Book
            BookEntity book = em.find(BookEntity.class, bookId);
            if (book == null || Boolean.FALSE.equals(book.getIsAvailable())) {
                // Not a failure; just "no loan created"
                tx.commit();
                return -1;
            }

            // Mutate managed Book & persist Loan
            book.setIsAvailable(false);

            LoanEntity loan = new LoanEntity();
            loan.setBook(book);
            loan.setUser(em.getReference(UserEntity.class, userId));

            em.persist(loan);
            em.flush(); // ensure INSERT so loanId is available

            tx.commit();
            return loan.getLoanId();

        } catch (ConstraintViolationException v) {
            rollbackAndClear(tx);
            throw new InvalidDBInputException("Loan validation failed: " + v.getMessage(), v);

        } catch (EntityNotFoundException enfe) {
            rollbackAndClear(tx);
            throw new DataAccessException(
                "Failed to create loan: missing bookId=" + bookId + " or userId=" + userId, enfe);

        } catch (PersistenceException pe) {
            rollbackAndClear(tx);
            throw new DataAccessException(
                "Failed to create loan and mark book unavailable (bookId=" + bookId + ", userId=" + userId + ")", pe);

        } catch (RuntimeException re) {
            rollbackAndClear(tx);
            throw new DataAccessException("Unexpected error while creating loan", re);
        }
    }

    @Override
    public boolean deleteLoanAndMarkBookAvailable(int loanId) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            // Guard: require an existing loan
            LoanEntity loan = em.find(LoanEntity.class, loanId);
            if (loan == null) {
                // Not a failure; nothing to delete
                tx.commit();
                return false;
            }

            // Mutate managed Book & remove Loan
            BookEntity book = loan.getBook();
            book.setIsAvailable(true);

            em.remove(loan);
            // no need for explicit flush before commit

            tx.commit();
            return true;

        } catch (PersistenceException pe) {
            rollbackAndClear(tx);
            throw new DataAccessException(
                "Failed to delete loan and mark book available (loanId=" + loanId + ")", pe);

        } catch (RuntimeException re) {
            rollbackAndClear(tx);
            throw new DataAccessException("Unexpected error while closing loan", re);
        }
    }

    @Override
    public int findLoanIdByBookId(int bookId) {
        try {
            List<Integer> ids = em.createQuery(
                    "SELECT l.loanId FROM LoanEntity l WHERE l.book.id = :bid ORDER BY l.loanId DESC", Integer.class)
                .setParameter("bid", bookId)
                .getResultList();
            return ids.isEmpty() ? -1 : ids.get(0);
        } catch (PersistenceException pe) {
            throw new DataAccessException("Failed to find loanId by bookId=" + bookId, pe);
        } catch (RuntimeException re) {
            throw new DataAccessException("Unexpected error while finding loanId by bookId=" + bookId, re);
        }
    }

    @Override
    public boolean existsById(int loanId) {
        try {
            Long count = em.createQuery(
                    "SELECT COUNT(l) FROM LoanEntity l WHERE l.loanId = :id", Long.class)
                .setParameter("id", loanId)
                .getSingleResult();
            return count != null && count > 0;
        } catch (PersistenceException pe) {
            throw new DataAccessException("Failed to check existence of loanId=" + loanId, pe);
        } catch (RuntimeException re) {
            throw new DataAccessException("Unexpected error while checking existence of loanId=" + loanId, re);
        }
    }

    // ------------------------------------------------------------
    // Helper: rollback and clear persistence context on failure
    // ------------------------------------------------------------

    private void rollbackAndClear(EntityTransaction tx) {
        if (tx != null && tx.isActive()) {
            try {
                tx.rollback();
            } finally {
                // Drop all managed instances to avoid stale in-memory state
                em.clear();
            }
        } else {
            // Even if no active tx, clear to avoid dangling managed changes
            em.clear();
        }
    }
}