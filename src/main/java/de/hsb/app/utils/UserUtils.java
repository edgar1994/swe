package de.hsb.app.utils;

import de.hsb.app.enumeration.Rolle;
import de.hsb.app.model.User;

import javax.annotation.Nonnull;
import java.util.HashSet;

public class UserUtils {

    public static final User DUMMY_USER_KUNDE = new User("Kunde", "Kein", AdressUtils.EMPTY_ADRESSE, "   ", "   ",
            Rolle.KUNDE, new HashSet<>());

    public static final User DUMMY_USER_MITARBEITER = new User("Mitarbeiter", "Kein", AdressUtils.EMPTY_ADRESSE, "   ",
            "   ", Rolle.MITARBEITER, new HashSet<>());


    /**
     * Liefert den Namen im Format "Nachname, Vorname" des uebergebenen Users.
     *
     * @param user {@link User}
     * @return "Nachname, Vorname"
     */
    @Nonnull
    public static String getNachnameVornameString(@Nonnull User user) {
        return user.getNachname().concat(", ").concat(user.getVorname());
    }

    /**
     * Vergleicht zwei {@link User} anhand {@link User}-Id;
     *
     * @param user        {@link User}
     * @param userToCheck {@link User} zu pruefen
     * @return boolean
     */
    public static boolean compareUserById(@Nonnull User user, @Nonnull User userToCheck) {
        return user.getId() == userToCheck.getId();
    }

    /**
     * Vergleicht zwei {@link User} anhand von Rolle, Nachname, Vorname, Username und der Adresse-Id
     *
     * @param user        {@link User}
     * @param userToCheck {@link User} zu pruefen
     * @return boolean
     */
    public static boolean compareUserByFields(@Nonnull User user, @Nonnull User userToCheck) {
        boolean same;
        same = user.getRolle().equals(userToCheck.getRolle());
        same &= user.getNachname().equals(userToCheck.getNachname());
        same &= user.getVorname().equals(userToCheck.getVorname());
        same &= user.getUsername().equals(userToCheck.getUsername());
        same &= AdressUtils.compareAdresseById(user.getAdresse(), userToCheck.getAdresse());
        return same;
    }

    /**
     * Prueft die Id die zweier {@link User}.
     *
     * @param userId      UserId
     * @param userToCheck {@link User} to check
     * @return boolean
     */
    public static boolean compareUserById(int userId, @Nonnull User userToCheck) {
        return userToCheck.getId() == userId;
    }

    /**
     * Prueft ob der uebergebene {@link User} die {@link Rolle#MITARBEITER}} hat.
     *
     * @param userToCheck {@link User} to check
     * @return boolean
     */
    public static boolean isEmployee(@Nonnull User userToCheck) {
        return Rolle.MITARBEITER.equals(userToCheck.getRolle());
    }

    /**
     * Prueft ob der uebergebene {@link User} die {@link Rolle#ADMIN}} hat.
     *
     * @param userToCheck {@link User} to check
     * @return boolean
     */
    public static boolean isAdmin(@Nonnull User userToCheck) {
        return Rolle.ADMIN.equals(userToCheck.getRolle());
    }

    /**
     * Prueft ob der uebergebene {@link User} die {@link Rolle#KUNDE}} hat.
     *
     * @param userToCheck {@link User} to check
     * @return boolean
     */
    public static boolean isCustomer(@Nonnull User userToCheck) {
        return Rolle.KUNDE.equals(userToCheck.getRolle());
    }

    /**
     * Prueft ob der uebergebene {@link User} die {@link Rolle#USER}} hat.
     *
     * @param userToCheck {@link User} to check
     * @return boolean
     */
    public static boolean isUser(@Nonnull User userToCheck) {
        return Rolle.USER.equals(userToCheck.getRolle());
    }

}
