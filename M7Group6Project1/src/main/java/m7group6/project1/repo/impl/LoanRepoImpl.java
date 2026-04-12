package m7group6.project1.repo.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.validation.ConstraintViolationException;

import java.util.List;

import m7group6.project1.exceptions.DataAccessException;
import m7group6.project1.exceptions.InvalidDBInputException;
import m7group6.project1.model.BookEntity;
import m7group6.project1.model.LoanEntity;
import m7group6.project1.model.UserEntity;
import m7group6.project1.repo.LoanRepository;

public class LoanRepoImpl implements LoanRepository {

  private final EntityManagerFactory emf;

  public LoanRepoImpl(EntityManagerFactory emf) {
    if (emf == null) throw new IllegalArgumentException("EntityManagerFactory must not be null");
    this.emf = emf;
  }

  // ------------------------------------------------------------
  // Reads
  // ------------------------------------------------------------
  @Override
  public LoanEntity findById(int loanId) {
    EntityManager em = emf.createEntityManager();
    try {
      return em.find(LoanEntity.class, loanId);
    } catch (PersistenceException pe) {
      throw new DataAccessException("Failed to find loan by id=" + loanId, pe);
    } catch (RuntimeException re) {
      throw new DataAccessException("Unexpected error while finding loan by id=" + loanId, re);
    } finally {
      em.close();
    }
  }

  @Override
  public List<LoanEntity> findAllActive() {
    EntityManager em = emf.createEntityManager();
    try {
      TypedQuery<LoanEntity> q = em.createQuery(
          "SELECT l FROM LoanEntity l JOIN l.book b JOIN l.user u ORDER BY l.loanId",
          LoanEntity.class);
      return q.getResultList();
    } catch (PersistenceException pe) {
      throw new DataAccessException("Failed to fetch active loans", pe);
    } catch (RuntimeException re) {
      throw new DataAccessException("Unexpected error while fetching active loans", re);
    } finally {
      em.close();
    }
  }

  @Override
  public int countActiveLoansByUser(int userId) {
    EntityManager em = emf.createEntityManager();
    try {
      Long count = em.createQuery(
              "SELECT COUNT(l) FROM LoanEntity l WHERE l.user.userId = :uid", Long.class)
          .setParameter("uid", userId)
          .getSingleResult();
      return count.intValue();
    } catch (PersistenceException pe) {
      throw new DataAccessException("Failed to count active loans for userId=" + userId, pe);
    } catch (RuntimeException re) {
      throw new DataAccessException("Unexpected error while counting loans for userId=" + userId, re);
    } finally {
      em.close();
    }
  }

  // ------------------------------------------------------------
  // Writes
  // ------------------------------------------------------------
  @Override
  public int createLoanAndMarkBookUnavailable(int bookId, int userId) {
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = null;
    try {
      tx = em.getTransaction();
      tx.begin();

      // Guard: require an existing and currently available Book
      BookEntity book = em.find(BookEntity.class, bookId);
      if (book == null || Boolean.FALSE.equals(book.getIsAvailable())) {
        tx.commit();
        return -1;
      }

      // Mutate Book & persist Loan
      book.setIsAvailable(false);
      LoanEntity loan = new LoanEntity();
      loan.setBook(book);
      loan.setUser(em.getReference(UserEntity.class, userId));
      em.persist(loan);
      em.flush(); // ensure INSERT so loanId is available

      tx.commit();
      return loan.getLoanId();
    } catch (ConstraintViolationException v) {
      if (tx != null && tx.isActive()) tx.rollback();
      throw new InvalidDBInputException("Loan validation failed: " + v.getMessage(), v);
    } catch (EntityNotFoundException enfe) {
      if (tx != null && tx.isActive()) tx.rollback();
      throw new DataAccessException(
          "Failed to create loan: missing bookId=" + bookId + " or userId=" + userId, enfe);
    } catch (PersistenceException pe) {
      if (tx != null && tx.isActive()) tx.rollback();
      throw new DataAccessException(
          "Failed to create loan and mark book unavailable (bookId=" + bookId + ", userId=" + userId + ")", pe);
    } catch (RuntimeException re) {
      if (tx != null && tx.isActive()) tx.rollback();
      throw new DataAccessException("Unexpected error while creating loan", re);
    } finally {
      em.close();
    }
  }

  @Override
  public boolean deleteLoanAndMarkBookAvailable(int loanId) {
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = null;
    try {
      tx = em.getTransaction();
      tx.begin();

      LoanEntity loan = em.find(LoanEntity.class, loanId);
      if (loan == null) {
        tx.commit();
        return false;
      }

      BookEntity book = loan.getBook();
      book.setIsAvailable(true);
      em.remove(loan);

      tx.commit();
      return true;
    } catch (PersistenceException pe) {
      if (tx != null && tx.isActive()) tx.rollback();
      throw new DataAccessException(
          "Failed to delete loan and mark book available (loanId=" + loanId + ")", pe);
    } catch (RuntimeException re) {
      if (tx != null && tx.isActive()) tx.rollback();
      throw new DataAccessException("Unexpected error while closing loan", re);
    } finally {
      em.close();
    }
  }

  @Override
  public int findLoanIdByBookId(int bookId) {
    EntityManager em = emf.createEntityManager();
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
    } finally {
      em.close();
    }
  }

  @Override
  public boolean existsById(int loanId) {
    EntityManager em = emf.createEntityManager();
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
    } finally {
      em.close();
    }
  }
}
