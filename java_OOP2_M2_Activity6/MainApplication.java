package java_OOP2_M2_Activity6;

public class MainApplication {

	public static void main(String[] args) {
        Vehicle car = new Car("Ford", 4);
        Vehicle truck = new Truck("Isuzu", 10);
        car.startEngine();                
        car.refuel();                      
        truck.startEngine();               
        truck.refuel();                    
        destroyVehicle(car);
	}
	public static void destroyVehicle(Vehicle vehicle) {
		vehicle.destroy();
	}

}
