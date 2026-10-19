package parking.model;

public class ElectricCar extends Car {
    private final int batteryKWh;

    public ElectricCar(String regNo, int batteryKWh) {
        super(regNo);
        if (batteryKWh <= 0) {
            throw new IllegalArgumentException("Battery capacity must be positive: " + batteryKWh);
        }
        this.batteryKWh = batteryKWh;
    }

    public int getBatteryKWh() { return batteryKWh; }
    public double chargingFee(int kWh) {
        if (kWh < 0) throw new IllegalArgumentException("Charging units cannot be negative.");
        return kWh * 12.0;
    }
}
