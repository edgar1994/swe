package de.hsb.app.swe.utils;

import java.util.Date;

/**
 * Utils-Klasse fuer {@link String}
 */
public class StringUtils {

    private static final String INITIALIZER_CONSOLE_PB_TMPL =
            "%s INFO  [de.hsb.app.swe.controller.InitializerController] (default task-2) %s";

    /**
     * Prueft ob value entweder null, empty oder blank ist.
     *
     * @param value {@link String}
     * @return boolean
     */
    public static boolean isEmptyOrNullOrBlank(final String value) {
        return value == null || value.isEmpty() || value.trim().length() == 0;
    }

    /**
     * Erstellt eine Info für die Consolenausgabe fuer die Progressbar.
     * Nur im der {@link de.hsb.app.swe.controller.InitializerController} verwenden.
     *
     * @param date    {@link Date}
     * @param pbLabel Label fuer die Progressbar
     * @return Consolen Ausgabe String
     */
    public static String createLogforPB(final Date date, final String pbLabel) {
        return String.format(INITIALIZER_CONSOLE_PB_TMPL, DateUtils.formatedDateHMSS(date), pbLabel);
    }

}
