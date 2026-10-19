package parking.model;

public class ElectricCar extends Car {
    public ElectricCar(String regNo) { super(regNo); }
    public double chargingFee(int kWh) { return kWh * 12.0; }
}
