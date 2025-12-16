package java_M3_Activity1;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class ProductList {
	private static Scanner input = new Scanner(System.in);
	
	public static void main(String[] args) {

        List<String> products = new ArrayList<>(Arrays.asList("Laptop", "Mouse", "Keyboard", "Monitor","Printer" ));

        System.out.println("All products:");
        printProducts(products);
        System.out.println("");
        
        products.add("Webcam");
        products.remove("Mouse");
        
        System.out.println("After adding and removing products: ");
        printProducts(products);
        System.out.println("");
               
        askProductToBeSearched(products);
        askProductToBeSearched(products);
	}

    private static void printProducts(List<String> products) {
        for (int i = 0; i < products.size(); i++) {
            System.out.println((i + 1) + ". " + products.get(i));
        }
    }
    
    private static void askProductToBeSearched(List<String> products) {
    	String productName;
    	
    	System.out.print("Enter product name to search: ");
        productName = input.nextLine();
        
        if (products.contains(productName)) {
            System.out.println("Product found: " + productName);
        } else {
            System.out.println("Product [" + productName.trim() + "] not found." );
        }
        System.out.println("");
    }

}
