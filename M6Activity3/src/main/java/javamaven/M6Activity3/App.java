package javamaven.M6Activity3;

import jakarta.persistence.EntityManager;
import javamaven.M6Activity3.entity.Course;
import javamaven.M6Activity3.entity.Student;
import javamaven.M6Activity3.util.EntityManagerUtil;

public class App {
	public static void main(String[] args) {
		EntityManager em = EntityManagerUtil.getInstance().createEntityManager();
		try {
			persistOneToMany(em, 9, "Civil Engineering", 80.5);
			persistOneToMany(em, 9, "Information Technology", 80);
		} finally {
			EntityManagerUtil.getInstance().closeEntityManager(em);
			EntityManagerUtil.getInstance().shutdownFactory();
		}
	}

	static void persistOneToMany(EntityManager em, int studentId, String courseName, double grade) {
		em.getTransaction().begin();
		
		Student student1 = em.find(Student.class, studentId);
		
		Course newCourse = new Course();
		newCourse.setCourseName(courseName);
		newCourse.setGrade(grade);
		newCourse.setStudent(student1);
		
		em.persist(newCourse);
		
		em.getTransaction().commit();
	}
}
