package de.hsb.app.swe.utils;

import de.hsb.app.swe.model.Ticket;

/**
 * Utils-Klasse fuer {@link Ticket}.
 */
public class TicketUtils {
    /**
     * Vergleicht zwei {@link Ticket} anhand {@link Ticket}-Id;
     *
     * @param ticket        {@link Ticket}
     * @param ticketToCheck {@link Ticket} zu pruefen
     * @return boolean
     */
    public static boolean compareUserById(final Ticket ticket, final Ticket ticketToCheck) {
        return ticket.getId() == ticketToCheck.getId();
    }

    /**
     * Prueft die Id die zweier {@link Ticket}.
     *
     * @param ticketId      UserId
     * @param ticketToCheck {@link Ticket} to check
     * @return boolean
     */
    public static boolean compareUserById(final int ticketId, final Ticket ticketToCheck) {
        return ticketToCheck.getId() == ticketId;
    }
}
