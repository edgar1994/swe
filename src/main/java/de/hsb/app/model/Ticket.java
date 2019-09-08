package de.hsb.app.model;

import de.hsb.app.enumeration.Status;

import javax.faces.bean.ManagedBean;
import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@NamedQuery(name = Ticket.NAMED_QUERY_NAME, query = Ticket.NAMED_QUERY_QUERY)
@Entity
@ManagedBean(name = "ticket")
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
    private String titel;

    @ManyToMany(cascade = CascadeType.ALL)
    @NotNull
    private List<User> bearbeiter;

    @NotNull
    private Status status;

    @OneToOne(cascade = CascadeType.ALL)
    @NotNull
    private Aufgabe aufgabe;

    public Ticket() {
        this.status = Status.OFFEN;
        this.bearbeiter = Collections.emptyList();
        this.erstellungsdatum = Date.from(Instant.now());
    }

    public Ticket(final Date abschlussdatum, final String titel, final List<User> bearbeiter, final Status status,
                  final Aufgabe aufgabe) {
        this.abschlussdatum = abschlussdatum;
        this.erstellungsdatum = Date.from(Instant.now());
        this.titel = titel;
        this.bearbeiter = bearbeiter;
        this.status = status;
        this.aufgabe = aufgabe;
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

    public List<User> getBearbeiter() {
        return this.bearbeiter;
    }

    public void setBearbeiter(final List<User> bearbeiter) {
        this.bearbeiter = bearbeiter;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    public Aufgabe getAufgabe() {
        return this.aufgabe;
    }

    public void setAufgabe(final Aufgabe aufgabe) {
        this.aufgabe = aufgabe;
    }

}
