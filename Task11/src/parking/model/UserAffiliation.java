package parking.model;

public enum UserAffiliation {
    STUDENT(true),
    STAFF(true),
    VISITOR(false);

    private final boolean monthlyPassFreeParking;

    UserAffiliation(boolean monthlyPassFreeParking) {
        this.monthlyPassFreeParking = monthlyPassFreeParking;
    }

    public boolean hasMonthlyPass() {
        return monthlyPassFreeParking;
    }
}
