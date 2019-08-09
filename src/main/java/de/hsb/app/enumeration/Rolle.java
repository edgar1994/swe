package de.hsb.app.enumeration;

public enum Rolle {

    ADMIN("ROLLE.ADMIN"),
    MITARBEITER("ROLLE.MITARBEITER"),
    KUNDE("ROLLE.KUNDE");

    private String rolle;

    Rolle(String rolle) {
        this.rolle = rolle;
    }

    public String getLanguage() {
        return rolle;
    }


}
