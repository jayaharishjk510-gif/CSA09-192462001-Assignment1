package parking.rule;

import java.time.LocalTime;

/**
 * Flat fee rule: Charges a fixed amount regardless of parking duration.
 * Applied to e-bikes.
 */
public class FlatFeePricingRule implements PricingRule {
    private final double flatFee;

    public FlatFeePricingRule(double flatFee) {
        this.flatFee = flatFee;
    }

    @Override
    public double computeFee(int minutes, LocalTime entryTime, LocalTime exitTime) {
        if (minutes <= 0) return 0.0;
        return flatFee;
    }

    public double getFlatFee() { return flatFee; }
}
