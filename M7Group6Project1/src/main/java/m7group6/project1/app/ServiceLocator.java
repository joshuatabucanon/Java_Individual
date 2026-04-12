package m7group6.project1.app;

import jakarta.persistence.EntityManagerFactory;

import m7group6.project1.repo.BookRepository;
import m7group6.project1.repo.LoanRepository;
import m7group6.project1.repo.UserRepository;
import m7group6.project1.repo.impl.BookRepoImpl;
import m7group6.project1.repo.impl.LoanRepoImpl;
import m7group6.project1.repo.impl.UserRepoImpl;
import m7group6.project1.service.LibraryServiceImpl;

public final class ServiceLocator {

  private static EntityManagerFactory emf;
  private static LibraryServiceImpl libraryService;

  private ServiceLocator() {}


  public static void init(EntityManagerFactory factory) {
    if (factory == null || !factory.isOpen()) {
        throw new IllegalArgumentException("EntityManagerFactory must be non-null and open");
    }

    emf = factory;

    // Repositories now accept EMF and open/close an EM per method call
    BookRepository bookRepo = new BookRepoImpl(emf);
    LoanRepository loanRepo = new LoanRepoImpl(emf);
    UserRepository userRepo = new UserRepoImpl(emf);

    // Build the application service (business logic stays here)
    libraryService = new LibraryServiceImpl(bookRepo, loanRepo, userRepo);
  }

  public static EntityManagerFactory emf() {
    return emf;
  }

  public static LibraryServiceImpl libraryService() {
    return libraryService;
  }
}