package de.hsb.app.swe.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utils-Klasse fuer {@link Date}
 */
public class DateUtils {

    private static final String DATE_TIME_DDMMYYYY = "dd.MM.yyyy";

    private static final String DATE_TIME_HMSS = "H:m:s,S";

    /**
     * Formatiert ein {@link Date} in das Format "dd.MM.yyyy".
     *
     * @param date {@link Date}
     * @return "dd.MM.yyyy"
     */
    public static String formatedDateDDMMYYYY(final Date date) {
        return new SimpleDateFormat(DATE_TIME_DDMMYYYY, Locale.GERMAN).format(date);
    }

    /**
     * Formatiert ein {@link Date} in das Format "h:m:s,S".
     *
     * @param date {@link Date}
     * @return "h:m:s,S"
     */
    public static String formatedDateHMSS(final Date date) {
        return new SimpleDateFormat(DATE_TIME_HMSS, Locale.GERMAN).format(date);
    }
}
