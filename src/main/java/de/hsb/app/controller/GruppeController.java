package de.hsb.app.controller;

import de.hsb.app.model.Gruppe;
import de.hsb.app.model.Ticket;
import de.hsb.app.model.User;
import de.hsb.app.repository.AbstractCrudRepository;
import de.hsb.app.utils.RedirectUtils;

import javax.annotation.Nonnull;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.transaction.*;
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
    public String speichern() {
        this.save(this.getSelectedEntity());
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
     * Fuegt ein {@link Ticket} in die ausgewaehlte {@link Gruppe} hinzu und redirected auf
     * {@link RedirectUtils#NEUEGRUPPE_XHTML}
     *
     * @param ticket {@link Ticket}
     * @return {@link RedirectUtils#NEUEGRUPPE_XHTML}
     */
    @Nonnull
    public String addTicket(@Nonnull Ticket ticket) {
        this.selectedEntity = this.entityList.getRowData();
        this.selectedEntity.getTicket().add(ticket);
        try {
            this.utx.begin();
            this.selectedEntity = this.em.merge(this.selectedEntity);
            ticket = this.em.merge(ticket);
            this.em.persist(this.selectedEntity);
            this.em.persist(ticket);
            this.entityList.setWrappedData(this.em.createNamedQuery(this.getSelect()).getResultList());
            this.utx.commit();
        } catch (final NotSupportedException | SystemException | SecurityException | IllegalStateException | RollbackException
                | HeuristicMixedException | HeuristicRollbackException e) {
            this.logger.error(e.getMessage());
        }
        return RedirectUtils.NEUEGRUPPE_XHTML;
    }

    /**
     * Fuegt eine Liste an {@link Ticket}s in die ausgewaehlte {@link Gruppe} hinzu und redirected auf
     * {@link RedirectUtils#NEUEGRUPPE_XHTML}
     *
     * @param tickets {@link List<Ticket>}
     * @return {@link RedirectUtils#NEUEGRUPPE_XHTML}
     */
    @Nonnull
    public String addTickets(@Nonnull List<Ticket> tickets) {
        this.selectedEntity = this.entityList.getRowData();
        this.selectedEntity.getTicket().addAll(tickets);
        try {
            this.utx.begin();
            this.selectedEntity = this.em.merge(this.selectedEntity);
            tickets = this.em.merge(tickets);
            this.em.persist(this.selectedEntity);
            this.em.persist(tickets);
            this.entityList.setWrappedData(this.em.createNamedQuery(this.getSelect()).getResultList());
            this.utx.commit();
        } catch (final NotSupportedException | SystemException | SecurityException | IllegalStateException | RollbackException
                | HeuristicMixedException | HeuristicRollbackException e) {
            this.logger.error(e.getMessage());
        }
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
