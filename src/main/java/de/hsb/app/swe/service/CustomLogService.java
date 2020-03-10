package de.hsb.app.swe.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * Service für die Logs
 *
 * @param <T>
 */
public class CustomLogService<T> {

    private final Logger logger;

    private final MessageService messageService = new MessageService();

    public CustomLogService(final Class<T> tClass) {
        this.logger = LoggerFactory.getLogger(tClass);
    }

    /**
     * Ausgabe für Error-Log-Einträge
     *
     * @param value Nachricht
     * @param args  weitere Argumente
     */
    public void error(final String value, final Object... args) {
        this.error(Locale.getDefault(), value, args);
    }

    /**
     * Ausgabe für Error-Log-Einträge
     *
     * @param locale Sprache der Message
     * @param value  Nachricht
     * @param args   weitere Argumente
     */
    public void error(final Locale locale, final String value, final Object... args) {
        if (args.length > 0) {
            this.logger.error(this.messageService.getMessage(locale, value,
                    args));
        } else {
            this.logger.error(this.messageService.getMessage(locale, value));
        }
    }

    /**
     * Ausgabe für Error-Log-Einträge
     *
     * @param value Nachricht
     */
    public void error(final String value) {
        this.error(Locale.getDefault(), value);
    }

    /**
     * Ausgabe für Info-Log-Einträge
     *
     * @param value Nachricht
     */
    private void info(final Locale locale, final String value, final Object... args) {
        this.logger.info(this.messageService.getMessage(locale, value, args));
    }

    /**
     * Ausgabe für Info-Log-Einträge
     *
     * @param value Nachricht
     * @param args  weiter Argumente
     */
    public void info(final String value, final Object... args) {
        this.info(Locale.getDefault(), value, args);
    }

}
