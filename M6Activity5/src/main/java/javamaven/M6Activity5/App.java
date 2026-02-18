package javamaven.M6Activity5;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import javamaven.M6Activity5.util.EntityManagerUtil;

public class App {
	public static void main(String[] args) {
		EntityManager em = EntityManagerUtil.getInstance().createEntityManager();
		try {
			printAllStudentNames(em);
			System.out.println(countCoursesByStudentId(em, 9L));
			System.out.println(countStudentsByAgeGreaterThan(em, 26));
		} finally {
			EntityManagerUtil.getInstance().closeEntityManager(em);
			EntityManagerUtil.getInstance().shutdownFactory();
		}
	}
	
	public static void printAllStudentNames(EntityManager em) {
	    // JPQL selects the 'name' field from Student entities
	    String jpql = "SELECT s.name FROM Student s ORDER BY s.id";
	
	    TypedQuery<String> query = em.createQuery(jpql, String.class);
	
	    for (String name : query.getResultList()) {
	        System.out.println(name);
	    }
	}

	public static Long countCoursesByStudentId(EntityManager em, Long id) {
	    if (id == null) return 0L;
	    String jpql = "SELECT COUNT(c) FROM Course c WHERE c.student.id = :id";
	    TypedQuery<Long> query = em.createQuery(jpql, Long.class);
	    query.setParameter("id", id);
	    return query.getSingleResult();
	}
	
	public static Long countStudentsByAgeGreaterThan(EntityManager em, int age) {
	    String jpql = "SELECT COUNT(s) FROM Student s WHERE s.age > :age";
	    return em.createQuery(jpql, Long.class)
	             .setParameter("age", age)
	             .getSingleResult();
	}
}
