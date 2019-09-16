package de.hsb.app.model;

import javax.annotation.Nonnull;
import javax.faces.bean.ManagedBean;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@NamedQuery(name = Gruppe.NAMED_QUERY_NAME, query = Gruppe.NAMED_QUERY_QUERY)
@Entity
@ManagedBean(name = "gruppe")
public class Gruppe {

    public static final String NAMED_QUERY_NAME = "SelectGruppe";

    public static final String NAMED_QUERY_QUERY = "Select gr from Gruppe gr";

    @Id
    @GeneratedValue
    private int id;

    private int leiterId;

    @NotNull
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "ID", nullable = false)
    private Set<User> mitglieder;

    @Temporal(TemporalType.DATE)
    @NotNull
    private Date erstellungsdatum;

    @Size(min = 3, max = 30)
    private String titel;

    public Gruppe() {
        this.mitglieder = new HashSet<>();
        this.erstellungsdatum = Date.from(Instant.now());
    }

    public Gruppe(int leiterId, Set<User> mitglieder, String titel) {
        this.leiterId = leiterId;
        this.mitglieder = mitglieder;
        this.erstellungsdatum = Date.from(Instant.now());
        this.titel = titel;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLeiterId() {
        return this.leiterId;
    }

    public void setLeiterId(int leiterId) {
        this.leiterId = leiterId;
    }

    public Set<User> getMitglieder() {
        return this.mitglieder;
    }

    public void setMitglieder(Set<User> mitglieder) {
        this.mitglieder = mitglieder;
    }

    public Date getErstellungsdatum() {
        return this.erstellungsdatum;
    }

    public void setErstellungsdatum(Date erstellungsdatum) {
        this.erstellungsdatum = erstellungsdatum;
    }

    public String getTitel() {
        return this.titel;
    }

    public void setTitel(String name) {
        this.titel = name;
    }

    /**
     * Add-Methode zum sicherstellen, dass die Beziehung zwischen {@link User} zur {@link Gruppe} und {@link Gruppe}
     * zum {@link User} gesetzt wird.
     *
     * @param user {@link User}
     */
    public void addUser(@Nonnull User user) {
        this.mitglieder.add(user);
        user.getGruppen().add(this);
    }

    /**
     * Remove-Methode zum sicherstellen, dass die Beziehung zwischen {@link User} zur {@link Gruppe} und {@link Gruppe}
     * zum {@link User} entfernt wird.
     *
     * @param user {@link User}
     */
    public void removeUser(@Nonnull User user) {
        this.mitglieder.remove(user);
        user.getGruppen().remove(this);
    }

}
