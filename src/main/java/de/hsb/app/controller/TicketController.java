package de.hsb.app.controller;

import de.hsb.app.model.Aufgabe;
import de.hsb.app.model.Ticket;
import de.hsb.app.repository.AbstractCrudRepository;
import de.hsb.app.utils.RedirectUtils;

import javax.annotation.Nonnull;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.transaction.*;

@ManagedBean(name = "ticketController")
@SessionScoped
public class TicketController extends AbstractCrudRepository<Ticket> {

    /**
     * Sucht das zu bearbeitende {@link Ticket} raus und redirected auf {@link RedirectUtils#NEUESTICKET_XHTML}
     *
     * @return {@link RedirectUtils#NEUESTICKET_XHTML}
     */
    @Nonnull
    public String bearbeiten() {
        this.selectedEntity = this.entityList.getRowData();
        return RedirectUtils.NEUESTICKET_XHTML;
    }

    /**
     * Legt ein neues Projekt an und redirected auf {@link RedirectUtils#NEUESTICKET_XHTML}.
     *
     * @return {@link RedirectUtils#NEUESTICKET_XHTML}
     */
    @Nonnull
    public String neu() {
        this.setSelectedEntity(new Ticket());
        return RedirectUtils.NEUESTICKET_XHTML;
    }


    /**
     * Bricht den aktuellen Vorgang ab und redirected auf {@link RedirectUtils#TICKETTABELLE_XHTML}.
     *
     * @return {@link RedirectUtils#TICKETTABELLE_XHTML}
     */
    @Nonnull
    public String abbrechen() {
        return RedirectUtils.TICKETTABELLE_XHTML;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected Class<Ticket> getRepositoryClass() {
        return Ticket.class;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected String getQueryCommand() {
        return Ticket.NAMED_QUERY_QUERY;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected String getSelect() {
        return Ticket.NAMED_QUERY_NAME;
    }

    /**
     * Fuegt eine {@link Aufgabe} einem Ticket hinzu. Redirected auf {@link RedirectUtils#NEUESTICKET_XHTML}
     *
     * @param aufgabe {@link Aufgabe}
     * @return {@link RedirectUtils#NEUESTICKET_XHTML}
     */
    public String addAufgabe(@Nonnull Aufgabe aufgabe) {
        this.selectedEntity = this.entityList.getRowData();
        this.selectedEntity.setAufgabe(aufgabe);
        try {
            this.utx.begin();
            this.selectedEntity = this.em.merge(this.selectedEntity);
            aufgabe = this.em.merge(aufgabe);
            this.em.persist(this.selectedEntity);
            this.em.persist(aufgabe);
            this.entityList.setWrappedData(this.em.createNamedQuery(this.getSelect()).getResultList());
            this.utx.commit();
        } catch (final NotSupportedException | SystemException | SecurityException | IllegalStateException | RollbackException
                | HeuristicMixedException | HeuristicRollbackException e) {
            this.logger.error(e.getMessage());
        }
        return RedirectUtils.NEUESTICKET_XHTML;
    }
}
