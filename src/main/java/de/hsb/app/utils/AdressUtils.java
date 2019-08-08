package de.hsb.app.utils;

import de.hsb.app.model.Adresse;

/**
 * Utils Klasse um {@link Adresse}n zu bearbeiten.
 */
public class AdressUtils {

    /**
     * Formatiert die Adresse nach folgenden Format: Straße Hausnummer, Stadt, Postleitzahl.
     *
     * @param adresse {@link Adresse}
     * @return Straße Hausnummer, Stadt, Postleitzahl
     */
    public static String formatAdresse(Adresse adresse) {
        return String.format("%s, %s, %s", adresse.getStrasse(), adresse.getStadt(), adresse.getPlz());
    }

}
