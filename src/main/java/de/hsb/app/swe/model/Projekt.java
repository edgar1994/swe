package de.hsb.app.swe.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NamedQuery(name = Projekt.NAMED_QUERY_NAME, query = Projekt.NAMED_QUERY_QUERY)
@Entity
public class Projekt {

    public static final String NAMED_QUERY_NAME = "SelectProjekt";

    public static final String NAMED_QUERY_QUERY = "Select pr from Projekt pr";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String titel;

    @NotNull
    private int leiterId;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL,
            mappedBy = "projekt", orphanRemoval = true)
    private List<Ticket> ticket;

    @NotNull
    private int gruppenId;

    @Temporal(TemporalType.DATE)
    @NotNull
    private Date erstellungsdatum;

    @Temporal(TemporalType.DATE)
    private Date abschlussdatum;

    private String beschreibung;

    public Projekt() {
        this.ticket = new ArrayList<>();
    }

    public Projekt(final String titel, final int leiter, final List<Ticket> ticket, final int gruppenId, final Date erstellungsdatum, final Date abschlussdatum, final String beschreibung) {
        this.titel = titel;
        this.leiterId = leiter;
        this.ticket = ticket;
        this.gruppenId = gruppenId;
        this.erstellungsdatum = erstellungsdatum;
        this.abschlussdatum = abschlussdatum;
        this.beschreibung = beschreibung;
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getTitel() {
        return this.titel;
    }

    public void setTitel(final String titel) {
        this.titel = titel;
    }

    public int getLeiterId() {
        return this.leiterId;
    }

    public void setLeiterId(final int leiterId) {
        this.leiterId = leiterId;
    }

    public List<Ticket> getTicket() {
        return this.ticket;
    }

    public void setTicket(final List<Ticket> ticket) {
        this.ticket = ticket;
    }

    public int getGruppenId() {
        return this.gruppenId;
    }

    public void setGruppenId(final int gruppenId) {
        this.gruppenId = gruppenId;
    }

    public Date getErstellungsdatum() {
        return this.erstellungsdatum;
    }

    public void setErstellungsdatum(final Date erstellungsdatum) {
        this.erstellungsdatum = erstellungsdatum;
    }

    public Date getAbschlussdatum() {
        return this.abschlussdatum;
    }

    public void setAbschlussdatum(final Date abschlussdatum) {
        this.abschlussdatum = abschlussdatum;
    }

    public String getBeschreibung() {
        return this.beschreibung;
    }

    public void setBeschreibung(final String beschreibung) {
        this.beschreibung = beschreibung;
    }
}
