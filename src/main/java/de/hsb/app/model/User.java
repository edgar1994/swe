package de.hsb.app.model;

import de.hsb.app.enumeration.Rolle;

import javax.annotation.Nonnull;
import javax.faces.bean.ManagedBean;
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

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Adresse adresse;

    @NotNull
    private String username;

    @NotNull
    private String passwort;

    @NotNull
    private Rolle rolle;

    // Fixme CascadeType
    @ManyToMany(mappedBy = "mitglieder", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Gruppe> gruppen;

    public User() {
        this.rolle = Rolle.USER;
        this.adresse = new Adresse();
        this.gruppen = new HashSet<>();
    }

    public User(String vorname, String nachname, Adresse adresse, String username, String passwort, Rolle rolle, Set<Gruppe> gruppen) {
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

    public void setId(int id) {
        this.id = id;
    }

    public String getVorname() {
        return this.vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return this.nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public Adresse getAdresse() {
        return this.adresse;
    }

    public void setAdresse(Adresse adresse) {
        this.adresse = adresse;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswort() {
        return this.passwort;
    }

    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

    public Rolle getRolle() {
        return this.rolle;
    }

    public void setRolle(Rolle rolle) {
        this.rolle = rolle;
    }

    public Set<Gruppe> getGruppen() {
        return this.gruppen;
    }

    public void setGruppen(Set<Gruppe> gruppen) {
        this.gruppen = gruppen;
    }

    /**
     * Add-Methode zum sicherstellen, dass die Beziehung zwischen {@link User} zur {@link Gruppe} und {@link Gruppe}
     * zum {@link User} gesetzt wird.
     *
     * @param gruppe {@link Gruppe}
     */
    public void addGruppe(@Nonnull Gruppe gruppe) {
        this.gruppen.add(gruppe);
        gruppe.getMitglieder().add(this);
    }

    /**
     * Remove-Methode zum sicherstellen, dass die Beziehung zwischen {@link User} zur {@link Gruppe} und {@link Gruppe}
     * zum {@link User} entfernt wird.
     *
     * @param gruppe {@link User}
     */
    public void removeGruppe(@Nonnull Gruppe gruppe) {
        this.gruppen.remove(gruppe);
        gruppe.getMitglieder().remove(this);
    }

}
