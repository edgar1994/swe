package de.hsb.app.swe.service;

import javax.faces.context.FacesContext;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * {@link MessageService}: Zum Uebersetzen von Properties fuer das Backend.
 */
public class MessageService {

    /**
     * Uebersetzt anhand der {@link Locale} der aktuellen {@link FacesContext}-Instanz
     * und liefert anschließend den uebersetzten {@link String} zurueck.
     *
     * @param messageKey MessageKey
     * @return String
     */
    public String getMessage(final String messageKey) {
        final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        return this.getMessage(messageKey, locale);
    }

    /**
     * Liefert einen uebersetzen formatierten String zurueck.
     *
     * @param messageKey MessageKey
     * @param args       Argumente zum formatieren
     * @return String
     */
    public String getMessage(final String messageKey, final Object... args) {
        return String.format(this.getMessage(messageKey), args);
    }

    /**
     * Liefert einen uebersetzen formatierten String zurueck.
     *
     * @param messageKey MessageKey
     * @param args       Argumente zum formatieren
     * @return String
     */
    public String getMessage(final Locale locale, final String messageKey, final Object... args) {
        return String.format(this.getMessage(messageKey, locale), args);
    }

    /**
     * Holt sich das {@link ResourceBundle} und die aktuelle {@link Locale}.
     * Ist das {@link ResourceBundle} instanziert wird geprueft ob der Message-Key
     * vorhanden ist und liefert anschließend den uebersetzten {@link String} zurueck.
     *
     * @param messageKey MessageKey
     * @param locale     {@link Locale}
     * @return String
     */
    public String getMessage(final String messageKey, final Locale locale) {
        final ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n.messages", locale);
        if (resourceBundle.containsKey(messageKey)) {
            return resourceBundle.getString(messageKey);
        }

        return messageKey;
    }

}
