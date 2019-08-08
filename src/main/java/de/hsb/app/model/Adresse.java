package de.hsb.app.model;

import javax.annotation.ManagedBean;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * {@link Adresse}-Model
 */
@NamedQuery(name = "SelectAdresse", query = "Select a from Adresse a")
@Entity
@ManagedBean("adresse")
public class Adresse {

    @Id
    @GeneratedValue
    @NotNull
    private int id;

    @NotNull
    @Size(min = 3, max = 30)
    private String strasse;

    @NotNull
    @Size(min = 3, max = 30)
    private String plz;

    @NotNull
    @Size(min = 3, max = 30)
    private String stadt;

    public Adresse() {
    }

    public Adresse(String strasse, String plz, String stadt) {
        this.strasse = strasse;
        this.plz = plz;
        this.stadt = stadt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public String getStadt() {
        return stadt;
    }

    public void setStadt(String stadt) {
        this.stadt = stadt;
    }
}
