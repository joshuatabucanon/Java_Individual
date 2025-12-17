package java_M3_Activity3;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ProductMap {

	private static Scanner input = new Scanner(System.in);
	private static Map<String, Double> pcProducts = new HashMap<>();
	
	public static void main(String[] args) {				
		pcProducts.put("Laptop", 50000.00);
		pcProducts.put("Monitor", 7000.00);
		pcProducts.put("Mouse", 500.50);
		pcProducts.put("Keyboard", 500.50);
		pcProducts.put("Printer", 6000.00);
		
		String chosenOption = "";
		do  {
			displayMenuChoices();
			chosenOption = getChoice();
			processChoice(chosenOption);
		} while (!chosenOption.equalsIgnoreCase("5"));
		input.close();
	}
	
	private static void displayMenuChoices() {
		System.out.println("============== JT PC Store Management System ==============");
		System.out.println("Select an option from 1 to 5 :");
		System.out.println("1. Search a product");
		System.out.println("2. Add a product");
		System.out.println("3. Print all products and prices");
		System.out.println("4. Find the cheapest product");
		System.out.println("5. Exit");
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
			searchCheapestProducts(pcProducts);
		} else if (chosenOption.equalsIgnoreCase("5")) {
			System.out.println("Exiting... ");	
		} else {
			System.out.println("You have entered an invalid input.");
			System.out.println("Your only options are 1 - 5. ");
		}
		System.out.println("");
	}
	private static String getChoice() {		
		String menuChoice = "";
		System.out.println();
		System.out.print("Enter choice: ");
		
		menuChoice = input.nextLine();
		return menuChoice;
	}
	
    private static void askProductToBeSearched(Map<String, Double> pcProducts) {
    	String productName;
    	
    	System.out.print("Enter product name to search: ");
        productName = input.nextLine();
        productName = productName.trim();
        
        if (pcProducts.containsKey(productName)) {
            System.out.println("Product [" + productName + "] found. Price: " + showIn2decimals(pcProducts.get(productName)));
        } else {
            System.out.println("Product [" + productName.trim() + "] not found." );
        }
    }
    
    private static String showIn2decimals(double price) {
    	String formattedPrice = "";
    	
    	formattedPrice = String.format("%.2f", price);
    	
    	return formattedPrice;
    }
    
    private static void askProductToAdd(Map<String, Double> pcProducts) {
    	String productName;
    	double inputPrice = 0.0;
    	boolean isPriceValid = true;
    	boolean isProductExisting = false;
    	double existingPrice = 0.0;
    	
    	
    	System.out.print("Enter product name to add: ");
        productName = input.nextLine();
        productName = productName.trim();
        
        //This is to check if product to be added already exists
        if (pcProducts.containsKey(productName)) {
        	isProductExisting = true;
        	System.out.println("Product [" + productName + "] is already existing. ");
        	//Get price of existing product
        	existingPrice = pcProducts.get(productName);
        	System.out.print("Enter updated price: ");
        } else {
        	System.out.print("Enter price: ");
        }
        
        //This is for input of price
        if (input.hasNextDouble()) {
        	inputPrice = input.nextDouble();
        } else if (input.hasNextInt()) {
        	inputPrice = input.nextInt();
        } else {
        	//This are for inputs that are not numbers
        	isPriceValid  = false;
        }
        //This consumes the newline character after user enters an input
        input.nextLine();
        
        //This is for negative prices
        if (inputPrice < 0.0 ) {
        	isPriceValid = false;        	
        }
    	
    	if (isPriceValid) {
    		//If price >= 0, proceed with put() method
    		pcProducts.put(productName, inputPrice);
    		
    		if (isProductExisting) {
    			if (inputPrice == existingPrice) {
    				System.out.println("Inputted price is the same with product's current price.");
    				System.out.println("No changes were made.");
    			} else {
    				System.out.println("Price of [" + productName + "] has been changed from "
    						+ showIn2decimals(existingPrice) + " to " + showIn2decimals(inputPrice));
    			}
    		} else {
    			System.out.println("New product added: " + productName);
    		}
    		 
    	} else {
    		System.out.print("Price inputted is invalid. ");
    		if (isProductExisting) {
    			//This is for adding 
    			System.out.print("Existing price was not changed.\n");
    		} else {
    			System.out.print("Product not added.\n");
    		}
    	}
    	
    }
    
    private static void printAllProductsAndCount(Map<String, Double> pcProducts) {
    	System.out.println("Here is the list of all products and their prices: ");
    	int i = 1;
    	for (Map.Entry<String, Double> entry : pcProducts.entrySet()) {
    		System.out.println(i + ") " + entry.getKey() + " - " + String.format("%.2f", entry.getValue()));
    		i++;
    	}

    }
    
    private static void searchCheapestProducts(Map<String, Double> pcProducts) {
        Double cheapestPrice = Double.MAX_VALUE;
        for (Double price : pcProducts.values()) {
            if (price < cheapestPrice) {
            	cheapestPrice = price;
            }
        }
        System.out.println("Here is the list of the cheapest product/s:");
        for (Map.Entry<String, Double> entry : pcProducts.entrySet()) {
            if (entry.getValue().equals(cheapestPrice)) { 
            	System.out.println(entry.getKey() + " = " + showIn2decimals(entry.getValue()));
            }
        }
    }

}
