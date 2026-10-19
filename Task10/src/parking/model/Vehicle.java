package parking.model;

import java.util.Objects;

/**
 * Base abstract model in parking.model package.
 * Enforces field encapsulation via private modifiers.
 */
public abstract class Vehicle {
    private final String regNo;

    protected Vehicle(String regNo) {
        if (regNo == null || regNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration plate cannot be null or blank.");
        }
        this.regNo = regNo.toUpperCase().trim();
    }

    public final String getRegNo() {
        return regNo;
    }

    protected abstract double ratePerHour();

    public final double calculateFee(int minutes) {
        if (minutes < 0) {
            throw new IllegalArgumentException("Parking duration cannot be negative: " + minutes);
        }
        int hours = (minutes + 59) / 60;
        return hours * ratePerHour();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle other)) return false;
        return Objects.equals(this.regNo, other.regNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regNo);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + regNo + "]";
    }
}
