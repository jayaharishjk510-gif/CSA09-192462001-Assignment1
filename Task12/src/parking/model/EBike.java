package parking.model;

import parking.rule.FlatFeePricingRule;
import parking.rule.PricingRule;

/**
 * New Category 1: E-Bike
 * Charged a flat fee regardless of duration.
 * Extends Vehicle without modifying existing classes.
 */
public class EBike extends Vehicle {
    private static final double DEFAULT_FLAT_FEE = 15.0; // Rs 15 flat fee
    private final PricingRule pricingRule;

    public EBike(String regNo, double flatFee) {
        super(regNo);
        this.pricingRule = new FlatFeePricingRule(flatFee);
    }

    public EBike(String regNo) {
        this(regNo, DEFAULT_FLAT_FEE);
    }

    @Override
    protected double ratePerHour() {
        return 0.0; // Not billed hourly
    }

    @Override
    public double calculateFee(int minutes) {
        return pricingRule.computeFee(minutes, null, null);
    }

    public double getFlatFee() {
        return ((FlatFeePricingRule) pricingRule).getFlatFee();
    }
}
