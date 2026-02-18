package javamaven.M6Activity2;

import jakarta.persistence.EntityManager;
import javamaven.M6Activity2.entity.Student;
import javamaven.M6Activity2.util.EntityManagerUtil;

public class App {
	public static void main(String[] args) {
		EntityManager em = EntityManagerUtil .getInstance().createEntityManager();
		try {
			runM6Activity2(em);
		} finally {
			EntityManagerUtil .getInstance().closeEntityManager (em);
			EntityManagerUtil .getInstance().shutdownFactory ();
		}
	}

	static void runM6Activity2 (EntityManager em) {
		try {
			em.getTransaction ().begin();
			Student newStudent = new Student();
			newStudent.setName("Rendon Labrador" );
			newStudent.setAge(50);
			newStudent.setEmail ("rendonlabrador@gmail.com ");
			em.persist(newStudent);
			em.getTransaction ().commit();
		} catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex; 
        }
	}
}
