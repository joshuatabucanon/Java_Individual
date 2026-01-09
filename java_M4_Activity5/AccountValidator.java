package java_M4_Activity5;

public class AccountValidator {

	public static void main(String[] args) {
		System.out.println("=== Account Number Validation Test ===\n");
        testValidation("Test 1: Valid account", "1234567890");      
        testValidation("Test 2: Too short", "123");                     
        testValidation("Test 3: Contains letters", "12345ABC90");       
        testValidation("Test 4: Contains space", "1234 567890");        
        testValidation("Test 5: Null value", null);                     

	}
	
    public static void validateAccountNumber(String accountNumber) throws InvalidAccountNumberException {
        if (accountNumber == null) {
            throw new NullPointerException("Account number cannot be null");
        }

        for (char c : accountNumber.toCharArray()) {
            if (!Character.isDigit(c)) {
                throw new InvalidAccountFormatException("Account number must contain only digits");
            }
        }

        if (accountNumber.length() != 10) {
            throw new InvalidAccountNumberException("Account number must be exactly 10 digits");
        }

        System.out.println("Valid account number: " + accountNumber);
    }

    public static void testValidation(String testName, String accountNumber) {
        try {
        	System.out.println(testName + " (" + accountNumber + ")");
            validateAccountNumber(accountNumber);
        } catch (InvalidAccountNumberException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (InvalidAccountFormatException e) {
            System.out.println("Warning: " + e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Warning: " + e.getMessage());
        }
        System.out.println(); 
    }


}
