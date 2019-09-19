package de.hsb.app.utils;

import de.hsb.app.model.Gruppe;
import de.hsb.app.model.Projekt;

import javax.annotation.Nonnull;

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
    public static boolean compareUserById(@Nonnull Projekt projekt, @Nonnull Projekt projektToCheck) {
        return projekt.getId() == projektToCheck.getId();
    }

    /**
     * Prueft die Id die zweier {@link Projekt}.
     *
     * @param projektId      UserId
     * @param projektToCheck {@link Projekt} to check
     * @return boolean
     */
    public static boolean compareUserById(int projektId, @Nonnull Projekt projektToCheck) {
        return projektToCheck.getId() == projektId;
    }

    /**
     * Prueft ob die uebergebene {@link Gruppe} die Gruppe des {@link Projekt}es ist.
     *
     * @param projekt {@link Projekt}
     * @param gruppe  {@link Gruppe}
     * @return boolean
     */
    public static boolean isChoosenGroup(@Nonnull Projekt projekt, @Nonnull Gruppe gruppe) {
        return projekt.getGruppenId() == gruppe.getLeiterId();
    }

}
