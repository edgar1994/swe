package de.hsb.app.model;

import org.springframework.stereotype.Component;

import javax.faces.bean.ManagedBean;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * {@link Mitarbeiter}-Model
 */
@NamedQuery(name = "SelectMitarbeiter", query = "Select a from Mitarbeiter a")
@Entity
@Component
@ManagedBean(name = "mitarbeiter")
public class Mitarbeiter {

    @Id
    @GeneratedValue
    @NotNull
    int id;

    @Size(min = 3, max = 30)
    @NotNull
    private String vorname;

    @Size(min = 3, max = 30)
    @NotNull
    private String nachname;

    @OneToOne(cascade = CascadeType.ALL)
    @NotNull
    private Adresse adresse;

    public Mitarbeiter() {
    }

    public Mitarbeiter(String vorname, String nachname, Adresse adresse) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.adresse = adresse;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public Adresse getAdresse() {
        return adresse;
    }

    public void setAdresse(Adresse adresse) {
        this.adresse = adresse;
    }
}
