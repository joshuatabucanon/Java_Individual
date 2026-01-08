package java_M4_Activity4;

public class AccountValidator {

	public static void main(String[] args) {

		testCase("1234567890");
		testCase("123");
		testCase(null);

	}
	
    public static void validateAccountNumber(String accountNumber) throws Exception {
        if (accountNumber == null) {
            throw new NullPointerException("Cannot be null");
        }

        if (accountNumber.length() != 10) {
            throw new Exception("Must be 10 digits");
        }

        System.out.println("Valid account: " + accountNumber);
    }
    
    public static void testCase(String accountnumbers) {
        try {
            validateAccountNumber(accountnumbers);
        } catch (NullPointerException npe) {
            System.out.println("Error: " + npe.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
