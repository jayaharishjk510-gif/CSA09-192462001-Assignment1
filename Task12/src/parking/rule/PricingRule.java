package parking.rule;

import java.time.LocalTime;

/**
 * Strategy Interface for Pricing Rules.
 * Allows pluggable, decoupled pricing calculation without mutating Vehicle classes.
 */
public interface PricingRule {
    double computeFee(int minutes, LocalTime entryTime, LocalTime exitTime);
}
