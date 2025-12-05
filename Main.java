package Java_OOP1_Activity3;
import java.time.Year;
public class Main {

	public static void main(String[] args) {
		Car car1 = new Car();
		Car car2 = new Car("Ford Mustang",2021,"red","Sports Car");
		int currentYear = Year.now().getValue();
		
		car1.setModel("Honda Jazz");
		car1.setYearMade(2020);
		car1.setColor("white");
		car1.setType("Hatchback");
		
		tellCarAge(car1,currentYear);
		tellCarAge(car2,currentYear);
		
	}
	
	private static void tellCarAge(Car car,int currentYear) {
		System.out.println("This " + car.getColor() + " " + car.getModel() + " " + car.getType().toLowerCase() + " is " 
				+ (currentYear - car.getYearMade()) + " years old.");
	}
	
}
