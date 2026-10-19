package parking.rule;

public class MonthlyPass implements Pass {
    private final String passId;

    public MonthlyPass(String passId) {
        this.passId = passId;
    }

    @Override public String getPassId() { return passId; }
    @Override public String getPassType() { return "Monthly Pass (Staff/Student)"; }
    @Override public boolean isValidToday() { return true; }

    @Override
    public double applyPassWaiver(double calculatedBaseFee) {
        return calculatedBaseFee; // Waives 100% of base parking fee
    }
}
