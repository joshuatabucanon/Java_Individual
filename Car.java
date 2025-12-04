package Java_OOP1_Activity3;

public class Car {
	private String model;
	private int yearMade;
	private String color;
	private String type;
	
	public Car() {
		
	}	
	public Car(String model, int yearMade, String color, String type) {
		this.model = model;
		this.yearMade = yearMade;
		this.color = color;
		this.type = type;
	}
	
	public String getModel() {
		return model;
	}
	
	public void setModel(String model) {
		this.model = model;
	}
	
	public int getYearMade() {
		return yearMade;
	}

	public void setYearMade(int yearMade) {
		this.yearMade = yearMade;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
		
}
