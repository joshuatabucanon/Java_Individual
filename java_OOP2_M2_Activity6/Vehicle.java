package java_OOP2_M2_Activity6;

public abstract class Vehicle {

    private String brand;
    private int numberOfWheels;
    
	public Vehicle(String brand, int numberOfWheels) {
		this.numberOfWheels = numberOfWheels;
		this.brand = brand;
	}
	
	public Vehicle () {
		
	}

	public String getBrand() {
		return this.brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}
	
	public int getNumberOfWheels() {
		return this.numberOfWheels;
	}

	public void setNumberOfWheels(int numberOfWheels) {
		this.numberOfWheels = numberOfWheels;
	}
	public abstract void startEngine();
	public void destroy() {
        System.out.print(this.getBrand() + " with " + this.numberOfWheels + " wheels has been destroyed.");
	}

}
