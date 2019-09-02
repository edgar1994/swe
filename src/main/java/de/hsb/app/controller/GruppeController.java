package de.hsb.app.controller;

import de.hsb.app.model.Gruppe;
import de.hsb.app.model.User;
import de.hsb.app.repository.AbstractCrudRepository;
import de.hsb.app.utils.RedirectUtils;

import javax.annotation.Nonnull;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller fuer {@link Gruppe}.
 */
@ManagedBean(name = "gruppeController")
@SessionScoped
public class GruppeController extends AbstractCrudRepository<Gruppe> {

    /**
     * Bricht den aktuellen Vorgang ab und redirected auf {@link RedirectUtils#GRUPPETABELLE_XHTML}.
     *
     * @return {@link RedirectUtils#GRUPPETABELLE_XHTML}
     */
    @Nonnull
    public static String abbrechen() {
        return RedirectUtils.GRUPPETABELLE_XHTML;
    }

    public DataModel<Gruppe> userAwareEntityList(@Nonnull final User loggedUser) {
        final List<Gruppe> gruppeList = this.userAwareCreateDefaultGruppenListe(loggedUser);
        if (!gruppeList.isEmpty()) {
            this.entityList.setWrappedData(this.userAwareCreateDefaultGruppenListe(loggedUser));
        }
        return this.entityList;
    }

    /**
     * Sucht alle Gruppen heraus die für den Benutzer sichbar seien duerfen.
     *
     * @param loggedUser {@link User}
     * @return List<Gruppe>
     */
    public List<Gruppe> userAwareCreateDefaultGruppenListe(@Nonnull final User loggedUser) {
        List<Gruppe> gruppeList = this.findAll();

        switch (loggedUser.getRolle()) {
            case USER:
            case MITARBEITER:
            case KUNDE:
                gruppeList = gruppeList.stream().filter(gruppe -> gruppe.getMitglieder().contains(loggedUser))
                        .collect(Collectors.toList());
                break;
            case ADMIN:
                break;
            default:
                throw new IllegalArgumentException("Rolle Exestiert nicht.");
        }

        return gruppeList;
    }

    /**
     * Sucht die zu bearbeitende {@link Gruppe} raus und redirected auf {@link RedirectUtils#NEUEGRUPPE_XHTML}
     *
     * @return {@link RedirectUtils#NEUEGRUPPE_XHTML}
     */
    @Nonnull
    public String bearbeiten() {
        this.selectedEntity = this.entityList.getRowData();
        return RedirectUtils.NEUEGRUPPE_XHTML;
    }

    /**
     * Abspeichern einer neuen {@link Gruppe}. Nach Erfolg wird auf {@link RedirectUtils#GRUPPETABELLE_XHTML} redirected.
     *
     * @return {@link RedirectUtils#GRUPPETABELLE_XHTML}
     */
    @Nonnull
    public String speichern(@Nonnull final User user) {
        this.save(this.getSelectedEntity());
        this.getSelectedEntity().setErstellungsdatum(Date.from(Instant.now()));
        switch (user.getRolle()) {
            case ADMIN:
            case MITARBEITER:
                this.getSelectedEntity().setLeiter(user);
                break;
            case KUNDE:
            case USER:
            default:
                throw new IllegalArgumentException(String.format("User mit der Rolle '%s' darf keine Gruppe erstellen.",
                        user.getRolle()));
        }
        return RedirectUtils.GRUPPETABELLE_XHTML;
    }

    /**
     * Loescht ein Element in der Liste.
     *
     * @return {@link RedirectUtils#GRUPPETABELLE_XHTML}.
     */
    @Nonnull
    public String loeschen() {
        this.delete();
        return RedirectUtils.GRUPPETABELLE_XHTML;
    }

    /**
     * Legt ein neues Projekt an und redirected auf {@link RedirectUtils#NEUEGRUPPE_XHTML}.
     *
     * @return {@link RedirectUtils#NEUEGRUPPE_XHTML}
     */
    @Nonnull
    public String neu() {
        this.setSelectedEntity(new Gruppe());
        return RedirectUtils.NEUEGRUPPE_XHTML;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected Class<Gruppe> getRepositoryClass() {
        return Gruppe.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected String getQueryCommand() {
        return Gruppe.NAMED_QUERY_QUERY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected String getSelect() {
        return Gruppe.NAMED_QUERY_NAME;
    }

}
