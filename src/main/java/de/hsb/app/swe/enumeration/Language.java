package de.hsb.app.swe.enumeration;

/**
 * Enumeration fuer Sprachen.
 */
public enum Language {
    DEUTSCH("de"),
    ENGLISCH("en");

    private final String language;

    Language(final String language) {
        this.language = language;
    }

    public String getLanguage() {
        return this.language;
    }
}
