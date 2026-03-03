package ph.com.bpi.training;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EntityManagerUtil {

    // Singleton instance
    private static EntityManagerUtil instance;

    // Encapsulated EntityManagerFactory
    private final EntityManagerFactory emf;

    // Private constructor to prevent instantiation
    private EntityManagerUtil() {
        this.emf = Persistence.createEntityManagerFactory("default");
    }

    // Global access method
    public static synchronized EntityManagerUtil getInstance() {
        if (instance == null) {
            instance = new EntityManagerUtil();
        }
        return instance;
    }

    /** Create a new EntityManager */
    public EntityManager createEntityManager() {
        return emf.createEntityManager();
    }

    /** Check if an EntityManager is open */
    public boolean isOpen(EntityManager em) {
        return em != null && em.isOpen();
    }

    /** Safely close an EntityManager */
    public void closeEntityManager(EntityManager em) {
        if (isOpen(em)) {
            em.close();
        }
    }

    /** Shutdown the EntityManagerFactory */
    public void shutdownFactory() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
