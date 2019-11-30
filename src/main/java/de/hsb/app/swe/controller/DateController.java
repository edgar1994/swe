package de.hsb.app.swe.controller;

import de.hsb.app.swe.utils.DateUtils;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.util.Date;

/**
 * Controller fuer {@link Date}.
 */
@ManagedBean(name = "dateController")
@ApplicationScoped
public class DateController {

    /**
     * Liefert das Datim im Format "dd.MM.yyyy" zurueck. Wenn das Datum <code>null</code> ist dann wird ein leerer
     * String zurueckgeliefert.
     *
     * @param date Date
     * @return dd.MM.yyyy
     */
    public String getFormatedDateDDMMYYYY(final Date date) {
        if (date != null) {
            return DateUtils.formatedDateDDMMYYYY(date);
        } else {
            return "";
        }
    }

}
