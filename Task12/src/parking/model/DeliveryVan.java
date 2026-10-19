package parking.model;

import parking.rule.PeakHourPricingRule;
import java.time.LocalTime;

/**
 * New Category 2: Delivery Van
 * Charged double the car rate (Rs 60/hr) during 9 AM - 5 PM peak hours,
 * and standard car rate (Rs 30/hr) during off-peak hours.
 */
public class DeliveryVan extends Vehicle {
    private static final double CAR_STANDARD_RATE = 30.0;
    private static final double PEAK_MULTIPLIER = 2.0;

    private final PeakHourPricingRule pricingRule;

    public DeliveryVan(String regNo) {
        super(regNo);
        this.pricingRule = new PeakHourPricingRule(CAR_STANDARD_RATE, PEAK_MULTIPLIER);
    }

    @Override
    protected double ratePerHour() {
        return CAR_STANDARD_RATE;
    }

    /** Overloaded fee calculation accepting entry and exit times to evaluate peak intervals */
    public double calculateFee(int minutes, LocalTime entryTime, LocalTime exitTime) {
        return pricingRule.computeFee(minutes, entryTime, exitTime);
    }

    @Override
    public double calculateFee(int minutes) {
        // Defaults to current system time if not specified
        LocalTime now = LocalTime.now();
        return pricingRule.computeFee(minutes, now, now);
    }

    public PeakHourPricingRule getPricingRule() {
        return pricingRule;
    }
}
