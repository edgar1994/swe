package de.hsb.app.model;

import javax.faces.bean.ManagedBean;
import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NamedQuery(name = Projekt.NAMED_QUERY_NAME, query = Projekt.NAMED_QUERY_QUERY)
@Entity
@ManagedBean(name = "projekt")
public class Projekt {

    public static final String NAMED_QUERY_NAME = "SelectProjekt";

    public static final String NAMED_QUERY_QUERY = "Select pr from Projekt pr";

    @Id
    @GeneratedValue
    private int id;

    @Size(min = 3, max = 30)
    private String titel;

    @NotNull
    private int leiterId;

    @NotNull
    @OneToMany(cascade = CascadeType.ALL)
    private List<Ticket> ticket;

    @NotNull
    private int gruppenId;

    @Temporal(TemporalType.DATE)
    @NotNull
    private Date erstellungsdatum;

    @Future
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date abschlussdatum;

    public Projekt() {
        this.ticket = new ArrayList<>();
    }

    public Projekt(String titel, int leiter, List<Ticket> ticket, int gruppenId, Date erstellungsdatum, Date abschlussdatum) {
        this.titel = titel;
        this.leiterId = leiter;
        this.ticket = ticket;
        this.gruppenId = gruppenId;
        this.erstellungsdatum = erstellungsdatum;
        this.abschlussdatum = abschlussdatum;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitel() {
        return this.titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public int getLeiterId() {
        return this.leiterId;
    }

    public void setLeiter(int leiterId) {
        this.leiterId = leiterId;
    }

    public List<Ticket> getTicket() {
        return this.ticket;
    }

    public void setTicket(List<Ticket> ticket) {
        this.ticket = ticket;
    }

    public int getGruppen() {
        return this.gruppenId;
    }

    public void setGruppen(int gruppenId) {
        this.gruppenId = gruppenId;
    }

    public Date getErstellungsdatum() {
        return this.erstellungsdatum;
    }

    public void setErstellungsdatum(Date erstellungsdatum) {
        this.erstellungsdatum = erstellungsdatum;
    }

    public Date getAbschlussdatum() {
        return this.abschlussdatum;
    }

    public void setAbschlussdatum(Date abschlussdatum) {
        this.abschlussdatum = abschlussdatum;
    }

}
