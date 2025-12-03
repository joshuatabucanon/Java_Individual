import java.util.Scanner;
public class JavaPart1Activity3 {
	public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter first integer: ");
        int num1 = input.nextInt();

        System.out.print("Enter second integer: ");
        int num2 = input.nextInt();

        int sum = num1 + num2;
        int difference = num1 - num2;
        int product = num1 * num2;
        
        JavaPart1Activity3.display(sum, difference, product);
        
        input.close();

	}
	public static void display(int sum, int difference, int product) {
        System.out.println("Sum: " + sum);
        System.out.println("Difference: " + difference);
        System.out.println("Product: " + product);
	}
	
}