package java_M3_Activity2;

import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class ProductHashSet {
	private static Scanner input = new Scanner(System.in);
	private static Set<String> pcProducts = new HashSet<>(Arrays.asList("Laptop", "Monitor", "Mouse", "Keyboard", "Printer"));
	
	public static void main(String[] args) {				
		String chosenOption = "";
		do  {
			displayMenuChoices();
			chosenOption = getChoice();
			processChoice(chosenOption);
		} while (!chosenOption.equalsIgnoreCase("4"));
				
		input.close();
	}
	
	private static void displayMenuChoices() {
		System.out.println("============== JT PC Store Management System ==============");
		System.out.println("Select an option from 1 to 4 :");
		System.out.println("1. Search a product");
		System.out.println("2. Add a product");
		System.out.println("3. Print all products and total count of unique products");
		System.out.println("4. Exit");
	}
	
	private static void processChoice (String chosenOption) {
		System.out.println("");
		if (chosenOption.equalsIgnoreCase("1")) {
			askProductToBeSearched(pcProducts);
		} else if (chosenOption.equalsIgnoreCase("2")) {
			askProductToAdd(pcProducts);			
		} else if (chosenOption.equalsIgnoreCase("3")) {			
			printAllProductsAndCount(pcProducts);
		} else if (chosenOption.equalsIgnoreCase("4")) {
			System.out.println("Exiting... ");	
		} else {
			System.out.println("You have entered an invalid input.");
			System.out.println("Your only options are 1 - 4. ");
		}
		System.out.println("");
	}
	private static String getChoice() {		
		String menuChoice = "";
		System.out.println();
		System.out.print("Enter choice: ");
		
		menuChoice = "" + input.nextLine();
		return menuChoice;
	}
	
    private static void askProductToBeSearched(Set<String> pcProducts) {
    	String productName;
    	
    	System.out.print("Enter product name to search: ");
        productName = input.nextLine();
        
        if (pcProducts.contains(productName)) {
            System.out.println("Product found: " + productName);
        } else {
            System.out.println("Product [" + productName.trim() + "] not found!" );
        }
    }
    
    private static void askProductToAdd(Set<String> pcProducts) {
    	String productName;
    	
    	System.out.print("Enter product name to add: ");
        productName = input.nextLine();
       
        if (pcProducts.add(productName)) {
            System.out.println("Product added: " + productName);
        } else {
            System.out.println("Product [" + productName.trim() + "] already exists!" );
        }
    }
    
    private static void printAllProductsAndCount(Set<String> pcProducts) {
    	System.out.println("Here is the list of all the products: ");
        for (String pcProduct : pcProducts) {
            System.out.println(pcProduct);
        }
        System.out.println("\nTotal of unique products: " + pcProducts.size());
    }

}
