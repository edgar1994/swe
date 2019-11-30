package de.hsb.app.swe.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@NamedQuery(name = Gruppe.NAMED_QUERY_NAME, query = Gruppe.NAMED_QUERY_QUERY)
@Entity
public class Gruppe {

    public static final String NAMED_QUERY_NAME = "SelectGruppe";

    public static final String NAMED_QUERY_QUERY = "Select gr from Gruppe gr";

    @Id
    @GeneratedValue
    private int id;

    private int leiterId;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "ID", nullable = false)
    private Set<User> mitglieder = new HashSet<>();

    @Temporal(TemporalType.DATE)
    @NotNull
    private Date erstellungsdatum;

    private String titel;

    public Gruppe() {
        this.erstellungsdatum = Date.from(Instant.now());
    }

    public Gruppe(final int leiterId, final Set<User> mitglieder, final String titel) {
        this.leiterId = leiterId;
        this.mitglieder = mitglieder;
        this.erstellungsdatum = Date.from(Instant.now());
        this.titel = titel;
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getLeiterId() {
        return this.leiterId;
    }

    public void setLeiterId(final int leiterId) {
        this.leiterId = leiterId;
    }

    public Set<User> getMitglieder() {
        return this.mitglieder;
    }

    public void setMitglieder(final Set<User> mitglieder) {
        this.mitglieder = mitglieder;
    }

    public Date getErstellungsdatum() {
        return this.erstellungsdatum;
    }

    public void setErstellungsdatum(final Date erstellungsdatum) {
        this.erstellungsdatum = erstellungsdatum;
    }

    public String getTitel() {
        return this.titel;
    }

    public void setTitel(final String name) {
        this.titel = name;
    }

    /**
     * Add-Methode zum sicherstellen, dass die Beziehung zwischen {@link User} zur {@link Gruppe} und {@link Gruppe}
     * zum {@link User} gesetzt wird.
     *
     * @param user {@link User}
     */
    public void addUser(final User user) {
        this.mitglieder.add(user);
        user.getGruppen().add(this);
    }

}
