package de.hsb.app.utils;


import de.hsb.app.model.Ticket;

import javax.annotation.Nonnull;

public class TicketUtils {
    /**
     * Vergleicht zwei {@link Ticket} anhand {@link Ticket}-Id;
     *
     * @param ticket        {@link Ticket}
     * @param ticketToCheck {@link Ticket} zu pruefen
     * @return boolean
     */
    public static boolean compareUserById(@Nonnull Ticket ticket, @Nonnull Ticket ticketToCheck) {
        return ticket.getId() == ticketToCheck.getId();
    }

    /**
     * Prueft die Id die zweier {@link Ticket}.
     *
     * @param ticketId      UserId
     * @param ticketToCheck {@link Ticket} to check
     * @return boolean
     */
    public static boolean compareUserById(int ticketId, @Nonnull Ticket ticketToCheck) {
        return ticketToCheck.getId() == ticketId;
    }
}
