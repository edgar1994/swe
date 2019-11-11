package de.hsb.app.swe.utils;

import de.hsb.app.swe.model.Ticket;

import javax.annotation.Nonnull;

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
    public static boolean compareUserById(@Nonnull final Ticket ticket, @Nonnull final Ticket ticketToCheck) {
        return ticket.getId() == ticketToCheck.getId();
    }

    /**
     * Prueft die Id die zweier {@link Ticket}.
     *
     * @param ticketId      UserId
     * @param ticketToCheck {@link Ticket} to check
     * @return boolean
     */
    public static boolean compareUserById(final int ticketId, @Nonnull final Ticket ticketToCheck) {
        return ticketToCheck.getId() == ticketId;
    }
}
