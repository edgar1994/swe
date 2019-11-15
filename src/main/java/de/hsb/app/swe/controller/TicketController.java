package de.hsb.app.swe.controller;

import de.hsb.app.swe.model.Projekt;
import de.hsb.app.swe.model.Ticket;
import de.hsb.app.swe.repository.AbstractCrudRepository;
import de.hsb.app.swe.service.MessageService;
import de.hsb.app.swe.utils.RedirectUtils;

import javax.annotation.Nonnull;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.transaction.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * {@link Ticket}-Controller
 */
@ManagedBean(name = "ticketController")
@SessionScoped
public class TicketController extends AbstractCrudRepository<Ticket> {

    private List<Ticket> ticketList = new ArrayList<>();

    private final MessageService messageService = new MessageService();

    private boolean isNewTicket = false;

    /**
     * Legt ein neues {@link Ticket} an und redirected auf {@link RedirectUtils#NEUES_TICKET_XHTML}.
     *
     * @return {@link RedirectUtils#NEUES_TICKET_XHTML}
     */
    public String newTicket() {
        this.isNewTicket = true;
        this.selectedEntity = new Ticket();
        this.selectedEntity.setErstellungsdatum(Date.from(Instant.now()));
        return RedirectUtils.NEUES_TICKET_XHTML;
    }

    /**
     * Setzt das ausgewaehlte {@link Ticket} und redirected auf {@link RedirectUtils#NEUES_TICKET_XHTML}.
     * Wir kein {@link Ticket} wird auf {@link RedirectUtils#PROJEKT_TABELLE_XHTML} redirected.
     *
     * @param ticketId Id des zu suchenden {@link Ticket}s
     * @return {@link RedirectUtils#NEUES_TICKET_XHTML} || {@link RedirectUtils#PROJEKT_TABELLE_XHTML}
     */
    public String editTicket(final int ticketId) {
        this.isNewTicket = false;
        final Optional<Ticket> ticketOptional = this.findById(ticketId);
        if (ticketOptional.isPresent()) {
            this.selectedEntity = ticketOptional.get();
            return RedirectUtils.NEUES_TICKET_XHTML;
        }
        return RedirectUtils.PROJEKT_ANSICHT_XHTML;
    }

    /**
     * Bricht den aktuellen Vorgang ab und redirected auf {@link RedirectUtils#PROJEKT_ANSICHT_XHTML}.
     *
     * @return {@link RedirectUtils#PROJEKT_ANSICHT_XHTML}
     */
    public String switchToProjectView() {
        this.isNewTicket = false;
        return RedirectUtils.PROJEKT_ANSICHT_XHTML;
    }

    /**
     * Speichert und fuegt ein {@link Ticket} an ein {@link Projekt} an und redirected auf
     * {@link RedirectUtils#PROJEKT_ANSICHT_XHTML}.
     *
     * @param projekt {@link Projekt}
     * @return {@link RedirectUtils#PROJEKT_ANSICHT_XHTML}
     */
    public String saveNewTicketToProject(final Projekt projekt) {
        final FacesContext context = FacesContext.getCurrentInstance();
        if (projekt != null && this.selectedEntity != null) {
            projekt.getTicket().removeIf(ticket -> ticket.getId() == this.selectedEntity.getId());
            projekt.getTicket().add(this.selectedEntity);
            this.selectedEntity.setProjekt(projekt);
            try {
                this.utx.begin();
                this.selectedEntity = this.em.merge(this.selectedEntity);
                this.em.merge(projekt);
                this.em.persist(this.selectedEntity);
                this.utx.commit();
                if (this.isNewTicket) {
                    context.addMessage(null, new FacesMessage(
                            this.messageService.getMessage("NEUESTICKET.MESSAGE.SAVE.SUMMARY"),
                            this.messageService.getMessage("NEUESTICKET.MESSAGE.SAVE.DETAIL")));
                } else {
                    context.addMessage(null, new FacesMessage(
                            this.messageService.getMessage("NEUESTICKET.MESSAGE.CHANGE.SUMMARY"),
                            this.messageService.getMessage("NEUESTICKET.MESSAGE.CHANGE.DETAIL")));
                }
                this.isNewTicket = false;
            } catch (final NotSupportedException | SystemException | SecurityException | IllegalStateException |
                    RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        this.messageService.getMessage("NEUESTICKET.MESSAGE.ERROR.SUMMARY"),
                        this.messageService.getMessage("NEUESTICKET.MESSAGE.ERROR.DETAIL")));
                this.logger.error(this.messageService.getMessage("LOG.TICKET.SAVING.ERROR", e.getMessage()));
            }
        }
        return RedirectUtils.PROJEKT_ANSICHT_XHTML;
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
     * {@inheritDoc}
     */
    @Override
    protected List<Ticket> uncheckedSolver(@Nonnull final Object var) {
        final List<Ticket> result = new ArrayList<>();
        if (var instanceof List) {
            for (int i = 0; i < ((List<?>) var).size(); i++) {
                final Object item = ((List<?>) var).get(i);
                if (item instanceof Ticket) {
                    result.add((Ticket) item);
                }
            }
        }
        return result;
    }

    public List<Ticket> getTicketList() {
        return this.ticketList;
    }

    public void setTicketList(final List<Ticket> ticketList) {
        this.ticketList = ticketList;
    }
}
