
package m5.PostgresDBConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.MissingFormatArgumentException;
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.SQLException;

public class M5Activity3 {

    private static final String URL = "jdbc:postgresql://localhost:5432/training_db";
    private static final String USER = "jrptabucanon";
    private static final String PASSWORD = "postgres";

    public static void main(String[] args) {
    	Scanner sc = new Scanner(System.in);
    	String menuChoice = "";
    	
    	displayProgramTitle();
    	
		do  {
			displayMenuChoices();
			menuChoice = getChoice(sc);
			processChoice(menuChoice, sc);
		} while (!menuChoice.equalsIgnoreCase("5"));
		
        sc.close();
    }
	private static void displayProgramTitle() {		
		System.out.println("===============  STUDENT COURSE MANAGEMENT SYSTEM  ================");		
	}
	private static void displayMenuChoices() {		
		printLineBreak();
		System.out.println("   Program Menu (Please choose from numbers 1 - 5)");
		System.out.println("   [ 1 ] - ADD NEW STUDENT");
		System.out.println("   [ 2 ] - ADD COURSE GRADE");
		System.out.println("   [ 3 ] - DISPLAY ALL STUDENTS");
		System.out.println("   [ 4 ] - DISPLAY ALL COURSE GRADES");
		System.out.println("   [ 5 ] - EXIT");	
	}
	private static String getChoice(Scanner sc) {		
		//Requests input from user from the menu choices
		String menuChoice = "";
		System.out.println();
		System.out.print("Enter menu choice: ");
		menuChoice = sc.nextLine();
		
		return menuChoice;
	}
	
	private static void processChoice(String menuChoice, Scanner sc) {
		printLineBreak();
		if (menuChoice.equalsIgnoreCase("1")) {		
	        //For option 1) ADD NEW STUDENT
			System.out.println("Please enter info of new student to be added. ");
	        System.out.print("Enter name: ");
	        String name = sc.nextLine();
	        System.out.print("Enter age: ");
	        try {
	            int age = Integer.parseInt(sc.nextLine().trim());
	            if (age < 0) {
	            	System.out.println("Age must not be negative.");
	            	return;
	            }
	            System.out.print("Enter email: ");
	            String email = sc.nextLine();
	            addStudent(name, age, email);
	        } catch (NumberFormatException nfe) {
	            System.out.println("Invalid age. Please enter a whole number.");
	        }


		} else if (menuChoice.equalsIgnoreCase("2")) {
			//For option 2) ADD COURSE GRADE
	        try {
	            System.out.print("Enter student ID: ");
	            int studentId = Integer.parseInt(sc.nextLine().trim());

	            System.out.print("Enter course name: ");
	            String courseName = sc.nextLine().trim();

	            System.out.print("Enter grade: ");
	            double grade = Double.parseDouble(sc.nextLine().trim());

	            addCourseGrade(studentId, courseName, grade);
	        } catch (NumberFormatException nfe) {
	            System.out.println("Invalid number entered. Please use numbers for ID and grade.");
	        }


		} else if (menuChoice.equalsIgnoreCase("3")) {
			//For option 3) DISPLAY ALL STUDENTS
	        displayAllStudents();
		} else if (menuChoice.equalsIgnoreCase("4")) {
			//For option 4) DISPLAY ALL COURSE GRADES
			displayAllCourses();
		} else if (menuChoice.equalsIgnoreCase("5")) {
			//For option 5) EXIT
			System.out.println("Thank you for using the program. Goodbye \\(^_^) ");	
			printLineBreak();
		} else {
			System.out.println("You have entered an invalid choice.");
		}
	}
	
    public static void addStudent(String name, int age, String email) {
        final String sql = "INSERT INTO students(name, age, email) VALUES (?, ?, ?) RETURNING id";
        
        try (
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement ps = conn.prepareStatement(sql)
            ) {
                ps.setString(1, name);
                ps.setInt(2, age);
                ps.setString(3, email);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int newId = rs.getInt("id");
                        System.out.println("\nNew student has been added. ID is " + newId + ".");
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error adding student: " + e.getMessage());
            }
    }

	
	public static void addCourseGrade(int studentId, String courseName, double grade) {
	    if (grade < 0.0 || grade > 100.0) {
	        System.out.println("Grade must be between 0 and 100.");
	        return;
	    }
	
	    final String checkSql  = "SELECT 1 FROM students WHERE id = ?";
	    final String insertSql = "INSERT INTO courses(student_id, course_name, grade) VALUES (?, ?, ?)";
	
	    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
	
	        // Validate first that student ID exists
	        try (PreparedStatement check = conn.prepareStatement(checkSql)) {
	            check.setInt(1, studentId);
	            try (ResultSet rs = check.executeQuery()) {
	                if (!rs.next()) {
	                    System.out.printf("\nCannot add course. Student ID %d does not exist. "
	                    			+ "\nPlease add the student first or use a valid student ID.%n", studentId);
	                    return; // Skip insert
	                }
	            }
	        }
	
	        // 2) Proceed to insert if valid
	        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
	            ps.setInt(1, studentId);
	            ps.setString(2, courseName);
	            ps.setDouble(3, grade);
	            int rows = ps.executeUpdate();
	            if (rows > 0) {
	                System.out.println("\nCourse grade added for student ID: " + studentId);
	            } else {
	                System.out.println("\nNo rows inserted. Please try again.");
	            }
	        }
	
	    } catch (SQLException e) {
	        System.out.println("Database error while adding course grade. Please try again.");
	        System.err.println("SQLState: " + e.getSQLState() + " Message: " + e.getMessage());
	    }
	}


	
	public static void displayAllStudents() {
	    final String sql = "SELECT id, name, age, email FROM students ORDER BY id";
	    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {
	
	        System.out.printf("%-5s | %-20s | %-5s | %-30s%n", "ID", "Student Name", "Age", "Email");
	        printLineBreak();
	
	        while (rs.next()) {
	            int id = rs.getInt("id");
	            String name = rs.getString("name");
	            int age = rs.getInt("age");
	            String email = rs.getString("email");
	            System.out.printf("%-5d | %-20s | %-5d | %-30s%n", id, name, age, email);
	        }
	        System.out.println();
	
	    } catch (SQLException e) {
	        System.out.println("Error displaying students: " + e.getMessage());
	    } catch (MissingFormatArgumentException e) {
	        System.out.println("\nError formatting for students table: " + e.getMessage());
	    }
	}


	
	public static void displayAllCourses() {
	    final String sql =
	        "SELECT s.id AS student_id, s.name AS student_name, c.course_name, c.grade " +
	        "FROM courses c LEFT JOIN students s ON c.student_id = s.id " +
	        "ORDER BY c.course_name ASC, c.grade DESC, s.id ASC";
	
	    try (
	        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
	        Statement stmt = conn.createStatement();
	        ResultSet rs = stmt.executeQuery(sql)
	    ) {
	        // Column order & spacing: Course (25) | Grade (5) | ID (5) | Student Name (20)
	        System.out.printf("%-25s | %-5s | %-5s | %-20s%n",
	                          "Course", "Grade", "ID", "Student Name");
	        printLineBreak();
	
	        while (rs.next()) {
	            String course = rs.getString("course_name");
	            double gradeVal = rs.getDouble("grade"); // numeric read aligns with ORDER BY
	            int id = rs.getInt("student_id");
	            String name = rs.getString("student_name");
	
	            System.out.printf("%-25s | %-5.2f | %-5d | %-20s%n",
	                              course, gradeVal, id, name);
	        }
	
	    } catch (SQLException e) {
	        System.out.println("Error displaying students with courses: " + e.getMessage());
	    } catch (MissingFormatArgumentException e) {
	        System.out.println("\nError formatting for students with courses table: " + e.getMessage());
	    }
	}

    
	
	public static void printLineBreak() {
		System.out.println("===================================================================");
	}

}

