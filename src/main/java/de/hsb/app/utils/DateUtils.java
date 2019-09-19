package de.hsb.app.utils;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String DATE_TIME_DDMMYYYY = "dd.MM.yyyy";

    /**
     * Formatiert ein {@link Date} in das Format "dd.MM.yyyy".
     *
     * @param date {@link Date}
     * @return "dd.MM.yyyy"
     */
    public static String formatedDateDDMMYYYY(@Nonnull Date date) {
        return new SimpleDateFormat(DATE_TIME_DDMMYYYY, Locale.GERMAN).format(date);
    }

}
