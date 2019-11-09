package de.hsb.app.swe.controller;

import de.hsb.app.swe.model.Ticket;
import de.hsb.app.swe.repository.AbstractCrudRepository;

import javax.annotation.Nonnull;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link Ticket}-Controller
 */
@ManagedBean(name = "ticketController")
@SessionScoped
public class TicketController extends AbstractCrudRepository<Ticket> {

    private List<Ticket> ticketList = new ArrayList<>();

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
