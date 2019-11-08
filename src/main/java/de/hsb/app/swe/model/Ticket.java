package de.hsb.app.swe.model;

import de.hsb.app.swe.enumeration.Status;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Date;

@NamedQuery(name = Ticket.NAMED_QUERY_NAME, query = Ticket.NAMED_QUERY_QUERY)
@Entity
public class Ticket {

    public static final String NAMED_QUERY_NAME = "SelectTicket";

    public static final String NAMED_QUERY_QUERY = "Select t from Ticket t";

    @Id
    @GeneratedValue
    private int id;

    @Future
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date abschlussdatum;

    @Temporal(TemporalType.DATE)
    @NotNull
    private Date erstellungsdatum;

    @Size(min = 3, max = 30)
    @NotNull
    private String titel;

    private int bearbeiterId;

    @NotNull
    private Status status;

    @NotNull
    private String beschreibung;

    public Ticket() {
        this.status = Status.OFFEN;
        this.erstellungsdatum = Date.from(Instant.now());
    }

    public Ticket(final Date abschlussdatum, final String titel, final int bearbeiterId, final Status status,
                  final String beschreibung) {
        this.abschlussdatum = abschlussdatum;
        this.erstellungsdatum = Date.from(Instant.now());
        this.titel = titel;
        this.bearbeiterId = bearbeiterId;
        this.status = status;
        this.beschreibung = beschreibung;
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public Date getAbschlussdatum() {
        return this.abschlussdatum;
    }

    public void setAbschlussdatum(final Date abschlussdatum) {
        this.abschlussdatum = abschlussdatum;
    }

    public Date getErstellungsdatum() {
        return this.erstellungsdatum;
    }

    public void setErstellungsdatum(final Date erstellungsdatum) {
        this.erstellungsdatum = erstellungsdatum;
    }

    public String getTitel() {
        return this.titel;
    }

    public void setTitel(final String titel) {
        this.titel = titel;
    }

    public int getBearbeiter() {
        return this.bearbeiterId;
    }

    public void setBearbeiter(final int bearbeiterId) {
        this.bearbeiterId = bearbeiterId;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    public String getAufgabe() {
        return this.beschreibung;
    }

    public void setAufgabe(final String beschreibung) {
        this.beschreibung = beschreibung;
    }

}
