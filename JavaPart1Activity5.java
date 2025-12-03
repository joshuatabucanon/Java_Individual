public class JavaPart1Activity5 {

	public static void main(String[] args) {
	    int total = sumAllFromNum1toNum2(1, 50);
        System.out.println("Sum: " + total);
	}

	public static int sumAllFromNum1toNum2(int start, int end) {
		int sum = 0;
		for (int i = start; i <= end; i++) {
			sum += i;
		}
		return sum;
	}
}