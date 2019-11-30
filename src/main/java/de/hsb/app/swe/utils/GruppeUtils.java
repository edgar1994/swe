package de.hsb.app.swe.utils;

import de.hsb.app.swe.model.Gruppe;

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
    public static boolean compareGruppeById(final Gruppe gruppe, final Gruppe gruppeToCheck) {
        return gruppe.getId() == gruppeToCheck.getId();
    }

    /**
     * Vergleicht zwei {@link Gruppe}n anhand ihrer Id.
     *
     * @param gruppeId      Id einer {@link Gruppe}
     * @param gruppeToCheck {@link Gruppe}
     * @return boolean
     */
    public static boolean compareGruppeById(final int gruppeId, final Gruppe gruppeToCheck) {
        return gruppeId == gruppeToCheck.getId();
    }

}
