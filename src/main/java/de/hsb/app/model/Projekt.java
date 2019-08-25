package de.hsb.app.model;

import javax.annotation.ManagedBean;
import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@NamedQuery(name = "SelectProjekt", query = "Select p from Projekt p")
@Entity
@ManagedBean("Projekt")
public class Projekt {

    @Id
    @GeneratedValue
    private int id;

    @OneToOne(cascade = CascadeType.ALL)
    private User leiter;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Aufgabe> aufgaben;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<User> mitglieder;

    public Projekt() {
        this.aufgaben = Collections.emptyList();
        this.mitglieder = Collections.emptyList();
    }

    public Projekt(User leiter, List<Aufgabe> aufgaben, List<User> mitglieder) {
        this.leiter = leiter;
        this.aufgaben = aufgaben;
        this.mitglieder = mitglieder;
    }

    public User getLeiter() {
        return leiter;
    }

    public void setLeiter(User leiter) {
        this.leiter = leiter;
    }

    public List<Aufgabe> getAufgaben() {
        return aufgaben;
    }

    public void setAufgaben(List<Aufgabe> aufgaben) {
        this.aufgaben = aufgaben;
    }

    public List<User> getMitglieder() {
        return mitglieder;
    }

    public void setMitglieder(List<User> mitglieder) {
        this.mitglieder = mitglieder;
    }
}
