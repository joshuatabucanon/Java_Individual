import java.util.Scanner;

public class JavaPart1Activity2 {
	
	public static void main(String[] args) {

	Scanner input = new Scanner(System.in);

        System.out.print("Enter your age: ");
        String ageInput = input.nextLine();

        int ageAsInt = Integer.parseInt(ageInput);

        double ageAsDouble = (double) ageAsInt;

        System.out.println("Your age as int: " + ageAsInt);
        System.out.println("Your age as double: " + ageAsDouble);

        input.close();
	}

}