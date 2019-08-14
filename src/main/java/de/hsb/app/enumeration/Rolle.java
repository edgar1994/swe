package de.hsb.app.enumeration;

/**
 * Enumeration fuer {@link Rolle}n
 */
public enum Rolle {

    ADMIN("ROLLE.ADMIN"),
    MITARBEITER("ROLLE.MITARBEITER"),
    USER("ROLLE.USER"),
    KUNDE("ROLLE.KUNDE");

    private String rolle;

    Rolle(String rolle) {
        this.rolle = rolle;
    }

    public String getLanguage() {
        return rolle;
    }


}
