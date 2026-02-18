package javamaven.M6Activity4;

import jakarta.persistence.EntityManager;
import javamaven.M6Activity4.entity.Student;
import javamaven.M6Activity4.util.EntityManagerUtil;

public class App {
	public static void main(String[] args) {
		EntityManager em = EntityManagerUtil.getInstance().createEntityManager();
		try {
			m6Activity4Solution(em);
		} finally {
			EntityManagerUtil.getInstance().closeEntityManager(em);
			EntityManagerUtil.getInstance().shutdownFactory();
		}
	}

	static void m6Activity4Solution (EntityManager em) {
		em.getTransaction ().begin();
		// 1. create Student object, assign values
	    Student newStudent = new Student("Activity4 New Student", 20,"act4newstudent@example.com");

		// 2. attach transient student object to persistence context
	    em.persist(newStudent);
	    
		// 3. call flush()
	    em.flush();
	    
		// 4. detach the managed newStudent from the persistence context
	    em.detach(newStudent);
	    
		// 5. print "is newStudent inside the persistence context: " + call contains()
		System.out.println("is newStudent inside the persistence context: " + em.contains(newStudent));
		
		// 6. reattach the detached newStudent
		newStudent = em.merge(newStudent);
		
		// 7. update newStudent, change some values like age or email
	    newStudent.setAge(21);
	    newStudent.setEmail("act4_newstudent@example.com");
		
		// 8. call flush()
	    em.flush();
	    
		// 9. print "is newStudent inside the persistence context: " + call contains()
	    System.out.println("is newStudent inside the persistence context: " + em.contains(newStudent));
	    
		// 10. mark managed newStudent for deletion
	    em.remove(newStudent);
	    
		// 11. call flush()
	    em.flush();
	    
		// 12. print "is newStudent inside the persistence context: " + call contains()
	    System.out.println("is newStudent inside the persistence context: " + em.contains(newStudent));
	    
		em.getTransaction().commit();
		}
}
