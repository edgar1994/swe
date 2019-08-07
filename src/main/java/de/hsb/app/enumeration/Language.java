package de.hsb.app.enumeration;

public enum Language {
    DEUTSCH("de"),
    ENGLISCH("en");

    private String language;

    Language(String language){
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }
}
