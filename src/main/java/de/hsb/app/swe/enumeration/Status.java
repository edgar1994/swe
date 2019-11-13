package de.hsb.app.swe.enumeration;

/**
 * Enumeration fuer {@link Status}.
 */
public enum Status {
    IN_BEARBEITUNG("STATUS.IN_BEARBEITUNG"),
    IM_TEST("STATUS.IM_TEST"),
    KLAERUNG("STATUS.KLAERUNG"),
    REVIEW("STATUS.REVIEW"),
    OFFEN("STATUS.OFFEN"),
    ABGESCHLOSSEN("STATUS.ABGESCHLOSSEN");

    private final String STATUS;

    Status(final String rolle) {
        this.STATUS = rolle;
    }

    public String getStatus() {
        return this.STATUS;
    }
}
