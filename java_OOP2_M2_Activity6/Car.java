package java_OOP2_M2_Activity6;

public class Car extends Vehicle implements Refuelable{
	public Car(String brandName, int numberOfWheels) {
		this.setBrand(brandName);
		this.setNumberOfWheels(numberOfWheels);
	}
	
	public Car () {
		
	}
	
    @Override
    public void startEngine() {
        System.out.println("Starting engine of the " + this.getNumberOfWheels() + " wheel car " + getBrand() + ".");
    }

    @Override
    public void refuel() {
        System.out.println("Refueling gas of " + this.getNumberOfWheels() + " wheel car " + getBrand() + ".");
    }
}
