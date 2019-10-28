package de.hsb.app.swe.model;

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

    private String leiterName;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "ID", nullable = false)
    private Set<User> mitglieder = new HashSet<>();

    @Temporal(TemporalType.DATE)
    @NotNull
    private Date erstellungsdatum;

    @Size(min = 3, max = 30)
    private String titel;

    public Gruppe() {
        this.erstellungsdatum = Date.from(Instant.now());
    }

    public Gruppe(final String leiterName, final Set<User> mitglieder, final String titel) {
        this.leiterName = leiterName;
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

    public String getLeiterName() {
        return this.leiterName;
    }

    public void setLeiterName(@Nonnull final String leiterId) {
        this.leiterName = leiterId;
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
    public void addUser(@Nonnull final User user) {
        this.mitglieder.add(user);
        user.getGruppen().add(this);
    }

    /**
     * Remove-Methode zum sicherstellen, dass die Beziehung zwischen {@link User} zur {@link Gruppe} und {@link Gruppe}
     * zum {@link User} entfernt wird.
     *
     * @param user {@link User}
     */
    public void removeUser(@Nonnull final User user) {
        this.mitglieder.remove(user);
        user.getGruppen().remove(this);
    }

}
