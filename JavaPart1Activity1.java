/**
 * 
 */
package java_M1_Activity1;


import java.util.Scanner;

public class JavaPart1Activity1 {
	
	public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("What is your name? ");
        String name = input.nextLine();

        System.out.println("Hello, " + name + "!");

        input.close();
	}
}
