package de.hsb.app.swe.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

/**
 * {@link Adresse}-Model
 */
@NamedQuery(name = Adresse.NAMED_QUERY_NAME, query = Adresse.NAMED_QUERY_QUERY)
@Entity
public class Adresse {

    public static final String NAMED_QUERY_NAME = "SelectAdresse";

    public static final String NAMED_QUERY_QUERY = "Select a from Adresse a";

    @Id
    @GeneratedValue
    private int id;

    private String strasse;

    private String plz;

    private String stadt;

    public Adresse() {
    }

    public Adresse(final String strasse, final String plz, final String stadt) {
        this.strasse = strasse;
        this.plz = plz;
        this.stadt = stadt;
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getStrasse() {
        return this.strasse;
    }

    public void setStrasse(final String strasse) {
        this.strasse = strasse;
    }

    public String getPlz() {
        return this.plz;
    }

    public void setPlz(final String plz) {
        this.plz = plz;
    }

    public String getStadt() {
        return this.stadt;
    }

    public void setStadt(final String stadt) {
        this.stadt = stadt;
    }
}
