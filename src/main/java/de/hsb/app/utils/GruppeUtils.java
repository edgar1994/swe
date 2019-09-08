package de.hsb.app.utils;

import de.hsb.app.model.Gruppe;

import javax.annotation.Nonnull;

/**
 * Utils-Klasse fuer {@link Gruppe}
 */
public class GruppeUtils {

    /**
     * Vergleicht zwei {@link Gruppe}n anhand ihrer Id.
     *
     * @param gruppe        {@link Gruppe}
     * @param gruppeToCheck {@link Gruppe}
     * @return boolean
     */
    public static boolean compareGruppeById(@Nonnull final Gruppe gruppe, @Nonnull final Gruppe gruppeToCheck) {
        return gruppe.getId() == gruppeToCheck.getId();
    }

}
