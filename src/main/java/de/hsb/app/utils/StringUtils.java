package de.hsb.app.utils;

/**
 * Utils Klasse um {@link String}n zu bearbeiten.
 */
public class StringUtils {

    /**
     * Prueft ob value entweder null, empty oder blank ist.
     *
     * @param value {@link String}
     * @return boolean
     */
    public static boolean isEmptyOrNullOrBlank(String value) {
        return value == null || value.isEmpty() || value.trim().length() == 0;
    }

}
