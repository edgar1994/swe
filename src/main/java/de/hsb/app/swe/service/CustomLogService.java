package de.hsb.app.swe.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class CustomLogService<T> {

    private final Logger logger;

    private final MessageService messageService = new MessageService();

    public CustomLogService(final Class<T> tClass) {
        this.logger = LoggerFactory.getLogger(tClass);
    }

    public void error(final String value, final Object... args) {
        this.error(Locale.getDefault(), value, args);
    }

    public void error(final Locale locale, final String value, final Object... args) {
        if (args.length > 0) {
            this.logger.error(this.messageService.getMessage(locale, value,
                    args));
        } else {
            this.logger.error(this.messageService.getMessage(locale, value));
        }
    }

    public void error(final String value) {
        this.error(Locale.getDefault(), value);
    }

    private void info(final Locale locale, final String value, final Object... args) {
        this.logger.info(this.messageService.getMessage(locale, value, args));
    }

    public void info(final String value, final Object... args) {
        this.info(Locale.getDefault(), value, args);
    }

}
