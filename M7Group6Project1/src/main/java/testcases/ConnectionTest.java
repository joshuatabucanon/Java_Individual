package testcases;

import jakarta.persistence.EntityManager;
import m7group6.project1.util.EntityManagerUtil;

public class ConnectionTest {
	public static void main(String[] args) {
		testConnection();
	}
	static void testConnection() {
		EntityManager em = EntityManagerUtil.getInstance().createEntityManager();
		try {
			if(em.isOpen()) {
				System.out.println("entity manager open, ready to create transaction");
			}
		} finally {
			EntityManagerUtil.getInstance().closeEntityManager(em);
			EntityManagerUtil.getInstance().shutdownFactory();
		}
	}
}
