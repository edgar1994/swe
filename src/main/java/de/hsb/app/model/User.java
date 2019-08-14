package de.hsb.app.model;

import de.hsb.app.enumeration.Rolle;

import javax.faces.bean.ManagedBean;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * {@link User}-Model
 */
@NamedQuery(name = "SelectUser", query = "Select u from User u")
@Entity
@ManagedBean(name = "user")
public class User {

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

    @Size(min = 5, max = 15)
    @NotNull
    private String username;

    @Size(min = 5)
    @NotNull
    private String passwort;

    @NotNull
    private Rolle rolle;

    public User() {
        this.rolle = Rolle.USER;
        this.adresse = new Adresse();
    }

    public User(String vorname, String nachname, Adresse adresse, String username, String passwort, Rolle rolle) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.adresse = adresse;
        this.username = username;
        this.passwort = passwort;
        this.rolle = rolle;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswort() {
        return passwort;
    }

    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

    public Rolle getRolle() {
        return rolle;
    }

    public void setRolle(Rolle rolle) {
        this.rolle = rolle;
    }

}
