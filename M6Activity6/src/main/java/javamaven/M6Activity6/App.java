package javamaven.M6Activity6;

import jakarta.persistence.EntityManager;
import javamaven.M6Activity6.util.EntityManagerUtil;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Expression;

import javamaven.M6Activity6.entity.Student;
import javamaven.M6Activity6.entity.Course;


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
	    CriteriaBuilder cb = em.getCriteriaBuilder();

	    // Create a query that returns String (the name)
	    CriteriaQuery<String> cq = cb.createQuery(String.class);

	    // FROM Student s
	    Root<Student> student = cq.from(Student.class);

	    // SELECT s.name ORDER BY s.name
	    cq.select(student.get("name"))
	      .orderBy(cb.asc(student.get("id")));

	    for (String name : em.createQuery(cq).getResultList()) {
	        System.out.println(name);
	    }
	}

	public static Long countCoursesByStudentId(EntityManager em, Long id) {
	    if (id == null) return 0L;

	    CriteriaBuilder cb = em.getCriteriaBuilder();

	    // Returns a Long count
	    CriteriaQuery<Long> cq = cb.createQuery(Long.class);

	    // FROM Course c
	    Root<Course> course = cq.from(Course.class);

	    // c.student.id path
	    Expression<Long> studentIdPath = course.get("student").get("id");

	    // SELECT COUNT(c) WHERE c.student.id = :id
	    cq.select(cb.count(course))
	      .where(cb.equal(studentIdPath, id));

	    return em.createQuery(cq).getSingleResult();
	}
	
	
	public static Long countStudentsByAgeGreaterThan(EntityManager em, int age) {
	    CriteriaBuilder cb = em.getCriteriaBuilder();

	    // Returns a Long count
	    CriteriaQuery<Long> cq = cb.createQuery(Long.class);

	    // FROM Student s
	    Root<Student> student = cq.from(Student.class);

	    // WHERE s.age > :age
	    cq.select(cb.count(student))
	      .where(cb.gt(student.get("age"), age));

	    return em.createQuery(cq).getSingleResult();
	}
}
