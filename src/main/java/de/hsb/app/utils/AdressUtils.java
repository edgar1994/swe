package de.hsb.app.utils;

import de.hsb.app.model.Adresse;

import javax.annotation.Nonnull;

/**
 * Utils Klasse um {@link Adresse}n zu bearbeiten.
 */
public class AdressUtils {

    public static final Adresse EMPTY_ADRESSE = new Adresse("   ", "   ", "   ");

    /**
     * Formatiert die Adresse nach folgenden Format: Straße Hausnummer, Stadt, Postleitzahl.
     *
     * @param adresse {@link Adresse}
     * @return Straße Hausnummer, Stadt, Postleitzahl
     */
    public static String formatAdresse(final Adresse adresse) {
        return String.format("%s, %s, %s", adresse.getStrasse(), adresse.getStadt(), adresse.getPlz());
    }

    /**
     * Vergleicht zwei {@link Adresse}n miteinander anhand von PLZ, Strasse und Stadt.
     *
     * @param adresse        {@link Adresse}
     * @param adresseToCheck {@link Adresse} die geprueft werden soll
     * @return boolean
     */
    public static boolean compareAdresse(@Nonnull final Adresse adresse, @Nonnull final Adresse adresseToCheck) {
        boolean same;
        same = adresse.getPlz().equalsIgnoreCase(adresseToCheck.getPlz());
        same &= adresse.getStadt().equalsIgnoreCase(adresseToCheck.getStadt());
        same &= adresse.getStrasse().equalsIgnoreCase(adresseToCheck.getStrasse());
        return same;
    }

    /**
     * Vergleicht zwei {@link Adresse}n miteinander anhand der ID.
     *
     * @param adresse        {@link Adresse}
     * @param adresseToCheck {@link Adresse} die geprueft werden soll
     * @return boolean
     */
    public static boolean compareAdresseById(@Nonnull final Adresse adresse, @Nonnull final Adresse adresseToCheck) {
        return adresse.getId() == adresseToCheck.getId();
    }

}
