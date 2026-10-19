package parking.rule;

/**
 * Pass Abstraction.
 * Extensible interface supporting Monthly and Daily passes.
 */
public interface Pass {
    String getPassId();
    String getPassType();
    double applyPassWaiver(double calculatedBaseFee);
    boolean isValidToday();
}
