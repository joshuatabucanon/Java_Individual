package java_M4_Activity2;

public class ATMSystem {

	private static double[] accounts = {10000, 15000, 20000};
	
	public static void main(String[] args) {
        System.out.println("=== ATM Withdrawal System ===");

        System.out.println("\n--- Test 1: Valid Withdrawal ---");
        processWithdrawal("1", "5000");

        System.out.println("\n--- Test 2: Invalid Account Index ---");
        processWithdrawal("abc", "5000");

        System.out.println("\n--- Test 3: Account Not Found ---");
        processWithdrawal("10", "5000");

        System.out.println("\n--- Test 4: Insufficient Funds ---");
        processWithdrawal("1", "20000");

        System.out.println("\n=== All tests completed! ===");
	}
	
	public static void processWithdrawal(String accountIndex, String amountInput) {
        System.out.println("Account=" + accountIndex + ", Amount=" + amountInput);
        try {        	
            int accIndex = Integer.parseInt(accountIndex);
            double balance = accounts[accIndex];
            double amount = Double.parseDouble(amountInput);
            System.out.println("Current balance: ₱" +  String.format("%.2f", balance));

            if (amount > balance) {
            	System.out.println("Withdrawal: ₱" + String.format("%.2f", amount));                
                System.out.println("Insufficient funds! Cannot withdraw ₱" + String.format("%.2f", amount));
                
            } else {
                double newBalance = balance - amount;
                System.out.println("Withdrawal: ₱" + String.format("%.2f", amount)); 
                System.out.println("New Balance: " +  String.format("%.2f", newBalance));
                System.out.println("Withdrawal successful!");                
            }

        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid input!");
            System.out.println("Please enter valid numbers.");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Error: Account not found!");
            System.out.println("Invalid account index.");
        } catch (Exception e) {
            System.out.println("Transaction failed");
        }
	}
}
