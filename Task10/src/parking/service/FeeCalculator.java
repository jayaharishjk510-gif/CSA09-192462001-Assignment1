package parking.service;

import parking.model.ElectricCar;
import parking.model.ParkingTicket;
import parking.model.Vehicle;

/**
 * Encapsulated FeeCalculator service.
 * Enforces business logic validations and protects invariant pricing rules.
 */
public class FeeCalculator {

    public static class BillSummary {
        public final long ticketId;
        public final String regNo;
        public final int durationMinutes;
        public final double baseParkingFee;
        public final double chargingFee;
        public final double discount;
        public final double netPayable;

        public BillSummary(long ticketId, String regNo, int durationMinutes,
                           double baseParkingFee, double chargingFee, double discount, double netPayable) {
            this.ticketId = ticketId;
            this.regNo = regNo;
            this.durationMinutes = durationMinutes;
            this.baseParkingFee = baseParkingFee;
            this.chargingFee = chargingFee;
            this.discount = discount;
            this.netPayable = netPayable;
        }

        @Override
        public String toString() {
            return String.format("Invoice #%d [%s]: %d mins | Base: Rs %.2f | EV Charge: Rs %.2f | Discount: -Rs %.2f | Net: Rs %.2f",
                    ticketId, regNo, durationMinutes, baseParkingFee, chargingFee, discount, netPayable);
        }
    }

    public static BillSummary generateBill(ParkingTicket ticket, long exitTimeMinutes, int kwhConsumed, boolean hasPass) {
        if (ticket == null) {
            throw new IllegalArgumentException("Cannot compute bill for a null ticket.");
        }
        if (exitTimeMinutes < ticket.getEntryTimeMinutes()) {
            throw new IllegalArgumentException("Exit time (" + exitTimeMinutes
                    + ") cannot precede entry time (" + ticket.getEntryTimeMinutes() + ")");
        }

        int durationMinutes = (int) (exitTimeMinutes - ticket.getEntryTimeMinutes());
        Vehicle v = ticket.getVehicle();
        double baseFee = v.calculateFee(durationMinutes);

        double chargingFee = 0.0;
        if (v instanceof ElectricCar ec && kwhConsumed > 0) {
            chargingFee = ec.chargingFee(kwhConsumed);
        }

        // Pass waiver logic: Base parking is free for pass holders, but EV charging is billed
        double discount = hasPass ? baseFee : 0.0;
        double net = (baseFee - discount) + chargingFee;

        return new BillSummary(ticket.getTicketId(), v.getRegNo(), durationMinutes, baseFee, chargingFee, discount, net);
    }
}
