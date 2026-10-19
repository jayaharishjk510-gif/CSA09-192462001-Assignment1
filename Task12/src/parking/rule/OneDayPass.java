package parking.rule;

import java.time.LocalDate;

/**
 * One-Day Pass purchased by campus visitors.
 * Allows unlimited exits during the issued calendar date.
 */
public class OneDayPass implements Pass {
    private final String passId;
    private final LocalDate validDate;
    private final double purchasePrice;

    public OneDayPass(String passId, LocalDate validDate, double purchasePrice) {
        this.passId = passId;
        this.validDate = validDate;
        this.purchasePrice = purchasePrice;
    }

    public OneDayPass(String passId) {
        this(passId, LocalDate.now(), 100.0); // Default Rs 100 for today
    }

    @Override public String getPassId() { return passId; }
    @Override public String getPassType() { return "Visitor One-Day Pass"; }

    @Override
    public boolean isValidToday() {
        return LocalDate.now().equals(validDate);
    }

    @Override
    public double applyPassWaiver(double calculatedBaseFee) {
        return isValidToday() ? calculatedBaseFee : 0.0;
    }

    public double getPurchasePrice() { return purchasePrice; }
}
