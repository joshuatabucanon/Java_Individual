package Java_OOP1_Seatwork1;

public class Book {
	int pageNumber;
	static String[] contents = {"Title","Table of Contents","Chapter 1","Chapter 2"};
		
	void showContent(int pageNum) {
		System.out.println("Here is the content of page " + pageNum);
		System.out.println(contents[pageNum]);
	}
}
