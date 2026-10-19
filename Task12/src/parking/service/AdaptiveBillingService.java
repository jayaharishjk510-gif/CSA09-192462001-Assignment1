package parking.service;

import parking.model.DeliveryVan;
import parking.model.EBike;
import parking.model.ParkingTicket;
import parking.model.Vehicle;
import parking.rule.Pass;

import java.time.LocalTime;

/**
 * Adaptive Billing Service supporting:
 * - Dynamic Peak-Hour pricing for Delivery Vans
 * - Flat-Fee pricing for E-Bikes
 * - Polymorphic Pass Waivers (Monthly and One-Day Passes)
 */
public class AdaptiveBillingService {

    public static class AdaptiveInvoice {
        public final long ticketId;
        public final String regNo;
        public final String vehicleCategory;
        public final String passType;
        public final int durationMinutes;
        public final String timeSpan;
        public final double calculatedBaseFee;
        public final double passWaiver;
        public final double netPayable;

        public AdaptiveInvoice(long ticketId, String regNo, String vehicleCategory,
                               String passType, int durationMinutes, String timeSpan,
                               double calculatedBaseFee, double passWaiver, double netPayable) {
            this.ticketId = ticketId;
            this.regNo = regNo;
            this.vehicleCategory = vehicleCategory;
            this.passType = passType;
            this.durationMinutes = durationMinutes;
            this.timeSpan = timeSpan;
            this.calculatedBaseFee = calculatedBaseFee;
            this.passWaiver = passWaiver;
            this.netPayable = netPayable;
        }

        @Override
        public String toString() {
            return String.format("Invoice #%d | %s (%s, %s) | %d mins [%s] | Base: Rs %.1f | Waiver: -Rs %.1f | NET: Rs %.1f",
                    ticketId, regNo, vehicleCategory, passType, durationMinutes, timeSpan, calculatedBaseFee, passWaiver, netPayable);
        }
    }

    public static AdaptiveInvoice generateBill(ParkingTicket ticket, long exitMinutes, LocalTime exitClock) {
        if (ticket == null) throw new IllegalArgumentException("Ticket cannot be null.");
        if (exitMinutes < ticket.getEntryTimeMinutes()) {
            throw new IllegalArgumentException("Exit time cannot precede entry time.");
        }

        int duration = (int) (exitMinutes - ticket.getEntryTimeMinutes());
        Vehicle v = ticket.getVehicle();
        LocalTime entryClock = ticket.getEntryClockTime();

        // 1. Compute Base Parking Fee using Strategy / Dynamic Polymorphism
        double baseFee;
        if (v instanceof DeliveryVan van) {
            baseFee = van.calculateFee(duration, entryClock, exitClock);
        } else if (v instanceof EBike eBike) {
            baseFee = eBike.calculateFee(duration);
        } else {
            baseFee = v.calculateFee(duration);
        }

        // 2. Apply Pass Discounts & Waivers
        Pass pass = ticket.getPass();
        double waiver = (pass != null) ? pass.applyPassWaiver(baseFee) : 0.0;
        double netPayable = Math.max(0.0, baseFee - waiver);

        String passDesc = (pass != null) ? pass.getPassType() : "Standard (No Pass)";
        String timeSpanDesc = String.format("%s to %s", entryClock, exitClock);

        return new AdaptiveInvoice(ticket.getTicketId(), v.getRegNo(), v.getClass().getSimpleName(),
                passDesc, duration, timeSpanDesc, baseFee, waiver, netPayable);
    }
}
