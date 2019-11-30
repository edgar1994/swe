package de.hsb.app.swe.utils;

import de.hsb.app.swe.model.Gruppe;
import de.hsb.app.swe.model.Projekt;

/**
 * Utils-Klasse fuer {@link Projekt}
 */
public class ProjectUtils {

    /**
     * Vergleicht zwei {@link Projekt} anhand {@link Projekt}-Id;
     *
     * @param projekt        {@link Projekt}
     * @param projektToCheck {@link Projekt} zu pruefen
     * @return boolean
     */
    public static boolean compareUserById(final Projekt projekt, final Projekt projektToCheck) {
        return projekt.getId() == projektToCheck.getId();
    }

    /**
     * Prueft die Id die zweier {@link Projekt}.
     *
     * @param projektId      UserId
     * @param projektToCheck {@link Projekt} to check
     * @return boolean
     */
    public static boolean compareUserById(final int projektId, final Projekt projektToCheck) {
        return projektToCheck.getId() == projektId;
    }

    /**
     * Prueft  ob die uebergebene {@link Gruppe} die Gruppe des {@link Projekt}es ist.
     *
     * @param projekt {@link Projekt}
     * @param gruppe  {@link Gruppe}
     * @return boolean
     */
    public static boolean isChoosenGroup(final Projekt projekt, final Gruppe gruppe) {
//        return projekt.getGruppenId() == gruppe.getLeiterName();
        return false;
    }

}
