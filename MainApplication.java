package java_OOP2_M2_Activity6;

public class MainApplication {

	public static void main(String[] args) {
        Car car1 = new Car("Ford", 4);
        Truck truck1 = new Truck("Isuzu", 10);
        car1.startEngine();                
        car1.refuel();             
        truck1.startEngine();               
        truck1.refuel();                    
        destroyVehicle(car1);
        destroyVehicle(truck1);
	}
	public static void destroyVehicle(Vehicle vehicle) {
		vehicle.destroy();
	}

}
