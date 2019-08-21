package de.hsb.app.enumeration;

public enum Rolle {

    ADMIN("ROLLE.ADMIN"),
    MITARBEITER("ROLLE.MITARBEITER"),
    USER("ROLLE.USER"),
    KUNDE("ROLLE.KUNDE");

    private final String ROLLE;

    Rolle(final String rolle) {
        this.ROLLE = rolle;
    }

    public String getRolle() {
        return this.ROLLE;
    }


}
