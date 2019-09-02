package de.hsb.app.model;

import javax.faces.bean.ManagedBean;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NamedQuery(name = Aufgabe.NAMED_QUERY_NAME, query = Aufgabe.NAMED_QUERY_QUERY)
@Entity
@ManagedBean(name = "aufgabe")
public class Aufgabe {

    public static final String NAMED_QUERY_NAME = "SelectAufgabe";

    public static final String NAMED_QUERY_QUERY = "Select au from Aufgabe au";

    @Id
    @GeneratedValue
    private int id;

    @NotNull
    private String beschreibung;

    @OneToOne(cascade = CascadeType.ALL)
    private User bearbeiter;

    public Aufgabe() {
    }

    public Aufgabe(final String beschreibung, final User bearbeiter) {
        this.beschreibung = beschreibung;
        this.bearbeiter = bearbeiter;
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getBeschreibung() {
        return this.beschreibung;
    }

    public void setBeschreibung(final String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public User getBearbeiter() {
        return this.bearbeiter;
    }

    public void setBearbeiter(final User bearbeiter) {
        this.bearbeiter = bearbeiter;
    }
}
