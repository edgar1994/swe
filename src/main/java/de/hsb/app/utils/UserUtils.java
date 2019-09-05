package de.hsb.app.utils;

import de.hsb.app.enumeration.Rolle;
import de.hsb.app.model.User;

import javax.annotation.Nonnull;

public class UserUtils {

    public static final User DUMMY_USER_KUNDE = new User("Kunde", "Kein", AdressUtils.EMPTY_ADRESSE, "   ", "   ",
            Rolle.KUNDE);

    public static final User DUMMY_USER_MITARBEITER = new User("Mitarbeiter", "Kein", AdressUtils.EMPTY_ADRESSE, "   ",
            "   ", Rolle.MITARBEITER);


    /**
     * Liefert den Namen im Format "Nachname, Vorname" des uebergebenen Users.
     *
     * @param user {@link User}
     * @return "Nachname, Vorname"
     */
    @Nonnull
    public static String getNachnameVornameString(@Nonnull final User user) {
        return user.getNachname().concat(", ").concat(user.getVorname());
    }

    /**
     * Vergleicht zwei {@link User} anhand {@link User}-Id;
     *
     * @param user        {@link User}
     * @param userToCheck {@link User} zu pruefen
     * @return boolean
     */
    public static boolean compareUserById(@Nonnull final User user, @Nonnull final User userToCheck) {
        return user.getId() == userToCheck.getId();
    }

    /**
     * Vergleicht zwei {@link User} anhand von Rolle, Nachname, Vorname, Username und der Adresse-Id
     *
     * @param user        {@link User}
     * @param userToCheck {@link User} zu pruefen
     * @return boolean
     */
    public static boolean compareUser(@Nonnull final User user, @Nonnull final User userToCheck) {
        boolean same;
        same = user.getRolle().equals(userToCheck.getRolle());
        same &= user.getNachname().equals(userToCheck.getNachname());
        same &= user.getVorname().equals(userToCheck.getVorname());
        same &= user.getUsername().equals(userToCheck.getUsername());
        same &= AdressUtils.compareAdresseById(user.getAdresse(), userToCheck.getAdresse());
        return same;
    }

}
