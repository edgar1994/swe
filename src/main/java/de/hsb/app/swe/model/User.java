package de.hsb.app.swe.model;

import de.hsb.app.swe.enumeration.Rolle;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * {@link User}-Model
 */
@NamedQuery(name = User.NAMED_QUERY_NAME, query = User.NAMED_QUERY_QUERY)
@Entity
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

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Adresse adresse;

    @NotNull
    private String username;

    @NotNull
    private String passwort;

    @NotNull
    private Rolle rolle;

    @ManyToMany(mappedBy = "mitglieder", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    private Set<Gruppe> gruppen = new HashSet<>();

    private boolean firstLogin = true;

    public User() {
        this.rolle = Rolle.USER;
        this.adresse = new Adresse();
    }

    public User(final String vorname, final String nachname, final Adresse adresse, final String username, final String passwort, final Rolle rolle, final Set<Gruppe> gruppen) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.adresse = adresse;
        this.username = username;
        this.passwort = passwort;
        this.gruppen = gruppen;
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

    public Set<Gruppe> getGruppen() {
        return this.gruppen;
    }

    public void setGruppen(final Set<Gruppe> gruppen) {
        this.gruppen = gruppen;
    }

    public boolean isFirstLogin() {
        return this.firstLogin;
    }

    public void setFirstLogin(final boolean firstLogin) {
        this.firstLogin = firstLogin;
    }

}
