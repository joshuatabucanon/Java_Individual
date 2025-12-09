package java_OOP2_M2_Activity6;

public class Truck extends Vehicle implements Refuelable {
	public Truck(String brandName, int numberOfWheels) {
		this.setBrand(brandName);
		this.setNumberOfWheels(numberOfWheels);
	}
	
    @Override
    public void startEngine() {
    	System.out.println("Starting engine of the " + this.getNumberOfWheels() + " wheel truck " + getBrand() + ".");
    }

    @Override
    public void refuel() {
    	System.out.println("Refueling gas of " + this.getNumberOfWheels() + " wheel truck "  + getBrand() + ".");
    }

	
}
