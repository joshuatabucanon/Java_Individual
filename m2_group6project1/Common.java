package m2_group6project1;

public class Common {
	
	// Prints a double line divider
    public static void doubleLineBreak() {
        System.out.println("════════════════════════════════════════════════════════════════════════════════════════════════════════════════");
    }

    // Prints a single line divider
    public static void singleLineBreak() {
        System.out.println("────────────────────────────────────────────────────────────────────────────────────────────────────────────────");
    }

    public static void header(String title) {
        doubleLineBreak();
        System.out.printf("│ %-76s │%n", title); 
        doubleLineBreak();
    }



}
