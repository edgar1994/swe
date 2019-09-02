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
    public static String getNachnameVornameString(@Nonnull final User user) {
        return user.getNachname().concat(", ").concat(user.getVorname());
    }

}
