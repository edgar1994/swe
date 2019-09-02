package de.hsb.app.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.faces.bean.ManagedBean;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@NamedQuery(name = Gruppe.NAMED_QUERY_NAME, query = Gruppe.NAMED_QUERY_QUERY)
@Entity
@ManagedBean(name = "gruppe")
public class Gruppe {

    public static final String NAMED_QUERY_NAME = "SelectGruppe";

    public static final String NAMED_QUERY_QUERY = "Select gr from Gruppe gr";

    @Id
    @GeneratedValue
    private int id;

    @Valid
    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    private User leiter;

    @NotNull
    @NotEmpty
    @ManyToMany(cascade = CascadeType.ALL)
    private List<User> mitglieder;

    @Temporal(TemporalType.DATE)
    @NotNull
    private Date erstellungsdatum;

    @Size(min = 3, max = 30)
    private String titel;

    public Gruppe() {
        this.mitglieder = Collections.emptyList();
        this.erstellungsdatum = Date.from(Instant.now());
    }

    public Gruppe(final User leiter, final List<Ticket> ticket, final List<User> mitglieder, final Date abschlussdatum,
                  final String titel) {
        this.leiter = leiter;
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

    public User getLeiter() {
        return this.leiter;
    }

    public void setLeiter(final User leiter) {
        this.leiter = leiter;
    }

    public List<User> getMitglieder() {
        return this.mitglieder;
    }

    public void setMitglieder(final List<User> mitglieder) {
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
}
