package m1_group6project1;

import java.util.Scanner;
/* Group 6 Project 1 - Student Grading System
 * Processing of grades is based on DepEd K-12 grading system 
 * (i.e. valid value for subject grade is 60-100 with decimal rounded off to whole number, passing grade is 75 to 100)
 */

public class Group6Project1 {
	private static Scanner input = new Scanner(System.in);
	
	private static String title;
	private static String titleWithBanner;
	private static int titleSideBannerLength;	
	private static int programBorderLimit;
	private static String lineBreak;
	private static String borderDesign;
	
	private static String studentName = "(No input yet)";
	private static String studentID = "(No input yet)";
	private static String subjectsAndGrades = "(No input yet)";
	private static int numberOfSubjects = 0;
	private static int totalOfGrades = 0;
	private static boolean allGradesValid = true;
	private static boolean isProcessAComplete = false;
	private static boolean isStudentRecordInitialized = false;
	
	private static double averageGrade = 0;
	private static String academicStatus = "(Grade not yet computed)";
	
	public static void main(String[] args) {
		String menuChoice = "";
		
		//This adds banner to the title and sets the border limit and border string for program messages.
		title = "GROUP-6 STUDENT GRADING SYSTEM";	
		titleSideBannerLength = 30;
		String sideBannerDesign = "=";
		titleWithBanner = setProgramTitleWithBanner(titleSideBannerLength, sideBannerDesign);
		programBorderLimit = titleWithBanner.length();
		borderDesign = "==";
		
		//This updates the line break based on programBorderLimit
	    char lineBreakDesign = '=';
		lineBreak = setLineBreak(programBorderLimit, lineBreakDesign);
		
		//This displays the title with banner
		displayProgramTitle();		
		
		do  {
			/* 
			 * This loop is to repeat displaying menu, getting user's choice, and
			 * processing the choice while menu choice is not yet D (Exit)
			 */
			displayMenuChoices();
			menuChoice = getChoice();
			processChoice(menuChoice);
		} while (!menuChoice.equalsIgnoreCase("D"));
		
		input.close();
	}
	
	
	private static String setProgramTitleWithBanner(int sideBannerLength, String sideBannerDesign) {
		String banneredTitle;
		String banner = "";
		
		for (int i = 0; i < sideBannerLength; i++) {
			banner = banner + sideBannerDesign;
		}
		
		banneredTitle = banner + "   " + title.trim() + "   " + banner;
		return banneredTitle;
	}
	
	private static String setLineBreak(int borderLimit, char lineBreakDesign) {
		String newLineBreak = "";
		
		for (int i = 0; i < borderLimit; i++) {
			newLineBreak = newLineBreak + lineBreakDesign;
		}

		return newLineBreak;
	}
	
	private static void displayProgramTitle() {		
		System.out.println(titleWithBanner);		
	}
	
	private static void printLineBreak() {
		System.out.println(lineBreak);
	}
	
	private static void displayMenuChoices() {		
		printLineBreak();
		System.out.println("              Program Menu (Please choose from letters A - D)");
		System.out.println("              [ A ] - ADD STUDENT INFORMATION");
		System.out.println("              [ B ] - COMPUTE STUDENT AVERAGE");
		System.out.println("              [ C ] - DISPLAY STUDENT INFORMATION");
		System.out.println("              [ D ] - EXIT");	
	}
	

  	private static void displayProgramMessage(String message) {
  		//This appends border string to the sides of program message.
  		message = borderDesign.trim() + "  " + message;
  		while(message.length() < (programBorderLimit - borderDesign.trim().length())) {
  			message = message + " ";
  		}
  		message = message + borderDesign.trim();
  		System.out.println(message);
  	}
	
	private static String getChoice() {		
		//Requests input from user from the menu choices
		String menuChoice = "";
		System.out.println();
		System.out.print("Enter menu choice: ");
		menuChoice = input.nextLine();
		
		return menuChoice;
	}
	
	private static void processChoice (String menuChoice) {
		printLineBreak();
		//Check conditions based from user's choice then call method processChoice<letter>
		if (menuChoice.equalsIgnoreCase("A")) {			
			processChoiceA();
			
		} else if (menuChoice.equalsIgnoreCase("B")) {
			if (numberOfSubjects > 0 && isProcessAComplete) {
				processChoiceB();
			} else if (!isProcessAComplete) {
				displayProgramMessage("Student Information is not yet complete. ");
				displayProgramMessage("Choose 'A' from menu and input the student's information and grades. ");
			}
								
		} else if (menuChoice.equalsIgnoreCase("C")) {
			processChoiceC();
						
		} else if (menuChoice.equalsIgnoreCase("D")) {
			//For choice D (Exit)
			displayProgramMessage("Thank you for using the program. Goodbye \\(^_^) ");	
			printLineBreak();
		} else {
			displayProgramMessage("You have entered an invalid choice.");
		}
	}
	
	private static void processChoiceA () {
		/* Check if there is already an existing Student Information. 
		 * If there is already existing, initialize variables related to choice A and B.
		 * Print that previous student record has been deleted.
		 */
		
		boolean withInvalidInput = false;
		studentName = "(No input yet)";
		studentID = "(No input yet)";
		isProcessAComplete = false;
		numberOfSubjects = 0;
		subjectsAndGrades = "(No input yet)";
		//Reset variables for process B
		averageGrade = 0;
		academicStatus = "(Grade not yet computed)";

		
		displayProgramMessage("            ADD STUDENT INFORMATION");
		printLineBreak();
		
		System.out.print("Enter Student Name (First Name MI. Last Name): ");
		studentName = input.nextLine();
		System.out.print("Enter Student ID: ");
		studentID = input.nextLine();
		System.out.print("Enter number of subjects: ");
		if (input.hasNextInt()) {
			numberOfSubjects = input.nextInt();
			input.nextLine();
			getSubjectsAndGrades(numberOfSubjects);
		} else {
			withInvalidInput = true;
			numberOfSubjects = 0;
			input.nextLine();
		}
		
		printLineBreak();
		//This is to print that one of the grades or the number of subjects inputted is not valid and may require reinput
		if (!allGradesValid) {
			displayProgramMessage("There's a grade with invalid value that is not a whole number or not within 60 - 100.  ");
			displayProgramMessage("This will result to incorrect computations. ");
			withInvalidInput = true;
		}		
		if (totalOfGrades == 0) {
			//This is to inform user if total grades is still in default value of 0, meaning no input was received for grades.
			displayProgramMessage("No grades were saved.  ");
			withInvalidInput = true;
		}
		if (numberOfSubjects < 1) {
			displayProgramMessage("Please input a whole number greater than 0 for number of subjects.  ");
			withInvalidInput = true;
		}
		if (withInvalidInput) {
			displayProgramMessage("Choose 'A' from menu to reinput the student's information and grades.");
		} else {
			isProcessAComplete = true;
			displayProgramMessage("Student info has been saved.");
			if (isStudentRecordInitialized) {
				displayProgramMessage("Previous student info has been overwritten.");
			}
		}
		isStudentRecordInitialized = true;
	}
	
	private static void getSubjectsAndGrades(int numberOfSubjects) {
		/* 
		 * This method is for requesting user to input the subjects and grades 
		 * which will loop based on the number of subjects.
		 * While also looping, the grades are added up to get the total.
		 * There is also a condition that will check if there is an invalid grade value.
		 */
		double subjectGrade = 0;		
		int roundedOffGrade = 0;
		totalOfGrades = 0;
		allGradesValid = true;
		subjectsAndGrades = "";
		String subject = "";
				
		for (int i = 1; i <= numberOfSubjects; i++) {
			System.out.println("Please input the [name of the subject] and then the [student's grade].\n");
			System.out.print("Enter name of subject " + i + ": ");
			subject = input.nextLine();
			subjectsAndGrades = subjectsAndGrades + subject + " - ";
			System.out.print("Enter grade for " + subject + ": ");
			if (input.hasNextDouble()) {
				subjectGrade = input.nextDouble();
				//Round off and parse grade to whole number for cases where user inputted grades with decimal
				roundedOffGrade = (int) Math.round(subjectGrade);
				subjectsAndGrades = subjectsAndGrades + roundedOffGrade + "\n";
				//This is to add up the grades which will be used for computing average grade.
				totalOfGrades += roundedOffGrade;
			} else {
				//If input for grade is not a number, mark that there is an invalid grade then put a default valid value.
				allGradesValid = false;
				subjectGrade = 0;
				subjectsAndGrades = subjectsAndGrades + "(invalid grade)" + "\n";
			}
			input.nextLine();
			/* This is to check if all grades inputted are valid 
			 * in which 60 is the lowest grade and 100 is the highest.
			 */
			if (subjectGrade < 60 || subjectGrade > 100) {
				allGradesValid = false;
			}
		}
	}
	
	
	private static void processChoiceB() {
		displayProgramMessage("            COMPUTE STUDENT AVERAGE");
		printLineBreak();
		
		averageGrade = totalOfGrades / numberOfSubjects;
		if (averageGrade >= 60 && averageGrade < 75) {
			academicStatus = "FAILED";
		} else if (averageGrade >= 75 && averageGrade <= 100) {
			academicStatus = "PASSED";
		} else {
			academicStatus = "(Computation error encountered)";
		}
		System.out.println("Subjects and Grades:");
		System.out.println(subjectsAndGrades);
		System.out.println("Computed Average of the " + numberOfSubjects + " subjects: " + String.format("%.2f", averageGrade));
		System.out.println("Academic Status: " + academicStatus);
	}
	
	private static void processChoiceC() {
		displayProgramMessage("            STUDENT INFORMATION SUMMARY");
		printLineBreak();
		System.out.println("STUDENT NAME: " + studentName);
		System.out.println("STUDENT ID: " + studentID);
		System.out.println("SUBJECTS AND GRADES: \n" + subjectsAndGrades);
		if (averageGrade == 0) {
			System.out.println("AVERAGE: (Grade not yet computed)");
		} else {
			System.out.println("AVERAGE: " + String.format("%.2f", averageGrade));
		}		
		System.out.println("ACADEMIC STATUS: " + academicStatus);
		if (!isStudentRecordInitialized) {
			printLineBreak();
			displayProgramMessage("There is no student information yet.");
			displayProgramMessage("Choose 'A' from menu and input student information first.");
		}
		if (isProcessAComplete && academicStatus.equals("(Grade not yet computed)")) {
			printLineBreak();
			displayProgramMessage("Grade is not yet computed and Academic status is not yet determined.");
			displayProgramMessage("Choose 'B' to compute student grades.");
		}
		if (isStudentRecordInitialized && numberOfSubjects < 1) {
			printLineBreak();
			displayProgramMessage("There is no input for subjects and grades yet.");
			displayProgramMessage("Choose 'A' from menu and input student's subjects and grades.");
		}
		if (!allGradesValid) {
			printLineBreak();
			displayProgramMessage("There is an invalid grade.");
			displayProgramMessage("Choose 'A' from menu and reinput student information.");
		}
	}
}
