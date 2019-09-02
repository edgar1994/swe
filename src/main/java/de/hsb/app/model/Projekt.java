package de.hsb.app.model;

import javax.faces.bean.ManagedBean;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collections;
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

    @Valid
    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    private User leiter;

    @NotNull
    @OneToMany(cascade = CascadeType.ALL)
    private List<Ticket> ticket;

    @NotNull
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Gruppe> gruppen;

    @Temporal(TemporalType.DATE)
    @NotNull
    private Date erstellungsdatum;

    @Future
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date abschlussdatum;

    public Projekt() {
        this.ticket = Collections.emptyList();
        this.gruppen = Collections.emptyList();
    }

    public Projekt(String titel, User leiter, List<Ticket> ticket, List<Gruppe> gruppen, Date erstellungsdatum, Date abschlussdatum) {
        this.titel = titel;
        this.leiter = leiter;
        this.ticket = ticket;
        this.gruppen = gruppen;
        this.erstellungsdatum = erstellungsdatum;
        this.abschlussdatum = abschlussdatum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public User getLeiter() {
        return leiter;
    }

    public void setLeiter(User leiter) {
        this.leiter = leiter;
    }

    public List<Ticket> getTicket() {
        return ticket;
    }

    public void setTicket(List<Ticket> ticket) {
        this.ticket = ticket;
    }

    public List<Gruppe> getGruppen() {
        return gruppen;
    }

    public void setGruppen(List<Gruppe> gruppen) {
        this.gruppen = gruppen;
    }

    public Date getErstellungsdatum() {
        return erstellungsdatum;
    }

    public void setErstellungsdatum(Date erstellungsdatum) {
        this.erstellungsdatum = erstellungsdatum;
    }

    public Date getAbschlussdatum() {
        return abschlussdatum;
    }

    public void setAbschlussdatum(Date abschlussdatum) {
        this.abschlussdatum = abschlussdatum;
    }

}
