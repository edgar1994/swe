package de.hsb.app.model;

import de.hsb.app.enumeration.Rolle;

import javax.faces.bean.ManagedBean;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * {@link User}-Model
 */
@NamedQuery(name = User.NAMED_QUERY_NAME, query = User.NAMED_QUERY_QUERY)
@Entity
@ManagedBean(name = "user")
public class User {

    public static final String NAMED_QUERY_NAME = "SelectUser";

    public static final String NAMED_QUERY_QUERY = "Select u from User u";

    @Id
    @GeneratedValue
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

    @NotNull
    private String username;

    @NotNull
    private String passwort;

    @NotNull
    private Rolle rolle;

    public User() {
        this.rolle = Rolle.USER;
        this.adresse = new Adresse();
    }

    public User(final String vorname, final String nachname, final Adresse adresse, final String username, final String passwort, final Rolle rolle) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.adresse = adresse;
        this.username = username;
        this.passwort = passwort;
        this.rolle = rolle;
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getVorname() {
        return this.vorname;
    }

    public void setVorname(final String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return this.nachname;
    }

    public void setNachname(final String nachname) {
        this.nachname = nachname;
    }

    public Adresse getAdresse() {
        return this.adresse;
    }

    public void setAdresse(final Adresse adresse) {
        this.adresse = adresse;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPasswort() {
        return this.passwort;
    }

    public void setPasswort(final String passwort) {
        this.passwort = passwort;
    }

    public Rolle getRolle() {
        return this.rolle;
    }

    public void setRolle(final Rolle rolle) {
        this.rolle = rolle;
    }

}
