package de.hsb.app.controller;

import de.hsb.app.model.Adresse;
import de.hsb.app.model.Mitarbeiter;
import de.hsb.app.repository.AbstractCrudRepository;
import de.hsb.app.utils.AdressUtils;
import org.primefaces.push.annotation.Singleton;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * MitarbeiterController
 */
@Singleton
@ManagedBean(name = "mitarbeiterController")
@SessionScoped
public class MitarbeiterController extends AbstractCrudRepository<Mitarbeiter> {

    /**
     * Setzt eine Standardliste fuer die MitarbeiterListe.
     */
    @PostConstruct
    public void postingFirst() {
            Adresse adresse = new Adresse("Straße HNr", "PLZ", "Stadt");
            this.save(new Mitarbeiter("Vorname", "Nachname", adresse));
    }

    /**
     * Loescht ein Element in der Liste.
     *
     * @return
     */
    public String loeschen(){
        this.delete();
        return null;
    }

    /**
     * Formatiert die Adresse nach folgenden Format: Straße Hausnummer, Stadt, Postleitzahl.
     *
     * @param mitarbeiter {@link Mitarbeiter}
     * @return Straße Hausnummer, Stadt, Postleitzahl
     */
    public String formatedAdresse(Mitarbeiter mitarbeiter) {
        return AdressUtils.formatAdresse(mitarbeiter.getAdresse());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected Class<Mitarbeiter> getRepositoryClass() {
        return Mitarbeiter.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected String getClassName() {
        return "Mitarbeiter";
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected String getSelect() {
        return "SelectMitarbeiter";
    }
}
