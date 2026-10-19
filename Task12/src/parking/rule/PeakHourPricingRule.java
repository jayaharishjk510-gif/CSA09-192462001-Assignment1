package parking.rule;

import java.time.LocalTime;

/**
 * Peak-Hour Pricing Rule:
 * Charges double rate (Rs 60/hr) during peak hours (09:00 - 17:00),
 * and standard car rate (Rs 30/hr) outside peak hours.
 */
public class PeakHourPricingRule implements PricingRule {
    private static final LocalTime PEAK_START = LocalTime.of(9, 0);
    private static final LocalTime PEAK_END = LocalTime.of(17, 0);

    private final double standardRatePerHour;
    private final double peakMultiplier;

    public PeakHourPricingRule(double standardRatePerHour, double peakMultiplier) {
        this.standardRatePerHour = standardRatePerHour;
        this.peakMultiplier = peakMultiplier;
    }

    public static boolean isPeakHour(LocalTime time) {
        if (time == null) return false;
        return !time.isBefore(PEAK_START) && time.isBefore(PEAK_END);
    }

    @Override
    public double computeFee(int minutes, LocalTime entryTime, LocalTime exitTime) {
        if (minutes <= 0) return 0.0;
        int hours = (minutes + 59) / 60;

        // If parking intersects peak hours (checked via entry or exit time)
        boolean inPeak = isPeakHour(entryTime) || isPeakHour(exitTime);
        double activeRate = inPeak ? (standardRatePerHour * peakMultiplier) : standardRatePerHour;

        return hours * activeRate;
    }

    public double getStandardRatePerHour() { return standardRatePerHour; }
    public double getPeakMultiplier() { return peakMultiplier; }
}
