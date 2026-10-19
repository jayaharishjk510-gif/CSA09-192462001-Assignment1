package parking.service;

import parking.model.Chargeable;
import parking.model.ParkingTicket;
import parking.model.UserAffiliation;
import parking.model.Vehicle;

public class BillingService {

    public static class Invoice {
        public final long ticketId;
        public final String regNo;
        public final String vehicleType;
        public final UserAffiliation affiliation;
        public final int durationMinutes;
        public final int billableHours;
        public final double hourlyRate;
        public final double baseParkingFee;
        public final double chargingFee;
        public final double passWaiver;
        public final double netPayable;

        public Invoice(long ticketId, String regNo, String vehicleType, UserAffiliation affiliation,
                       int durationMinutes, int billableHours, double hourlyRate,
                       double baseParkingFee, double chargingFee, double passWaiver, double netPayable) {
            this.ticketId = ticketId;
            this.regNo = regNo;
            this.vehicleType = vehicleType;
            this.affiliation = affiliation;
            this.durationMinutes = durationMinutes;
            this.billableHours = billableHours;
            this.hourlyRate = hourlyRate;
            this.baseParkingFee = baseParkingFee;
            this.chargingFee = chargingFee;
            this.passWaiver = passWaiver;
            this.netPayable = netPayable;
        }

        @Override
        public String toString() {
            return String.format("Invoice #%d | %s (%s, %s) | %d mins (%d hrs @ Rs %.0f) | Base: Rs %.1f | EV: Rs %.1f | Waiver: -Rs %.1f | NET: Rs %.1f",
                    ticketId, regNo, vehicleType, affiliation, durationMinutes, billableHours,
                    hourlyRate, baseParkingFee, chargingFee, passWaiver, netPayable);
        }
    }

    public static Invoice computeInvoice(ParkingTicket ticket, long exitMinutes, int kwhConsumed) {
        if (ticket == null) throw new IllegalArgumentException("Ticket cannot be null.");
        if (exitMinutes < ticket.getEntryTimeMinutes()) {
            throw new IllegalArgumentException("Exit time precedes entry time.");
        }

        int duration = (int) (exitMinutes - ticket.getEntryTimeMinutes());
        int billableHours = (duration == 0) ? 0 : (duration + 59) / 60;
        Vehicle v = ticket.getVehicle();

        double baseFee = v.calculateFee(duration);
        double hourlyRate = (billableHours > 0) ? (baseFee / billableHours) : 0.0;

        double chargingFee = 0.0;
        if (v instanceof Chargeable c && kwhConsumed > 0) {
            chargingFee = c.chargingFee(kwhConsumed);
        }

        // Pass waiver logic: Student/Staff monthly pass waives base parking, but not EV charging
        boolean hasPass = ticket.getAffiliation().hasMonthlyPass();
        double waiver = hasPass ? baseFee : 0.0;
        double net = (baseFee - waiver) + chargingFee;

        return new Invoice(ticket.getTicketId(), v.getRegNo(), v.getClass().getSimpleName(),
                ticket.getAffiliation(), duration, billableHours, hourlyRate, baseFee, chargingFee, waiver, net);
    }
}
