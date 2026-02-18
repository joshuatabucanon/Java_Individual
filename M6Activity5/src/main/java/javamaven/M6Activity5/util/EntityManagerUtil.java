package javamaven.M6Activity5.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EntityManagerUtil {

	   private static EntityManagerUtil instance;

	   private final EntityManagerFactory emf;

	   private EntityManagerUtil() {
	       this.emf = Persistence.createEntityManagerFactory("default");
	   }

	   public static synchronized EntityManagerUtil getInstance() {
	       if (instance == null) {
	           instance = new EntityManagerUtil();
	       }
	       return instance;
	   }

	   public EntityManager createEntityManager() {
	       return emf.createEntityManager();
	   }

	   public boolean isOpen(EntityManager em) {
	       return em != null && em.isOpen();
	   }

	   public void closeEntityManager(EntityManager em) {
	       if (isOpen(em)) {
	           em.close();
	       }
	   }

	   public void shutdownFactory() {
	       if (emf.isOpen()) {
	           emf.close();
	       }
	   }
	}

