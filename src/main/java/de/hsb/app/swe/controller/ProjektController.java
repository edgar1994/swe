package de.hsb.app.swe.controller;

import de.hsb.app.swe.enumeration.Rolle;
import de.hsb.app.swe.enumeration.Status;
import de.hsb.app.swe.model.Gruppe;
import de.hsb.app.swe.model.Projekt;
import de.hsb.app.swe.model.Ticket;
import de.hsb.app.swe.model.User;
import de.hsb.app.swe.repository.AbstractCrudRepository;
import de.hsb.app.swe.utils.GruppeUtils;
import de.hsb.app.swe.utils.ProjectUtils;
import de.hsb.app.swe.utils.RedirectUtils;
import de.hsb.app.swe.utils.UserUtils;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.Query;
import javax.transaction.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * {@link Projekt}-Controller
 */
@ManagedBean(name = "projektController")
@SessionScoped
public class ProjektController extends AbstractCrudRepository<Projekt> {

    private int choosenGroupId;

    /**
     * Findet anhand der uebergebenen projectId das gewaehlte {@link Projekt} und redirected auf
     * {@link RedirectUtils#PROJEKT_ANSICHT_XHTML}. Wird kein {@link Projekt} gefunden wird auf
     * {@link RedirectUtils#PROJEKT_TABELLE_XHTML}.
     *
     * @param projectId {@link Projekt}-ID
     * @return {@link RedirectUtils#PROJEKT_ANSICHT_XHTML} || {@link RedirectUtils#PROJEKT_TABELLE_XHTML}
     */
    public String chooseProject(final int projectId) {
        final Optional<Projekt> optionalProjekt = this.findById(projectId);
        if (optionalProjekt.isPresent()) {
            this.selectedEntity = optionalProjekt.get();
            return RedirectUtils.PROJEKT_ANSICHT_XHTML;
        }
        return RedirectUtils.PROJEKT_TABELLE_XHTML;
    }

    /**
     * Prueft ob die {@link Ticket}s eines Projektes leer sind.
     *
     * @return boolean
     */
    public boolean isTicketsEmpty() {
        if (this.selectedEntity != null) {
            return this.selectedEntity.getTicket().isEmpty();
        }
        return true;
    }

    /**
     * Erstellt ein neues {@link Projekt} und redirected auf {@link RedirectUtils#NEUES_PROJEKT_XHTML}.
     *
     * @return {@link RedirectUtils#NEUES_PROJEKT_XHTML}
     */
    @Nonnull
    public String newProject(@Nonnull final User projektLeiter) {
        this.selectedEntity = new Projekt();
        this.selectedEntity.setLeiterId(projektLeiter.getId());
        this.selectedEntity.setErstellungsdatum(Date.from(Instant.now()));
        return RedirectUtils.NEUES_PROJEKT_XHTML;
    }

    /**
     * Sucht alle moeglichen Bearbeiter ({@link Rolle#MITARBEITER}) fuer ein {@link Ticket}
     *
     * @return List<User>
     */
    public List<User> findAllProcessors() {
        final List<User> processors = new ArrayList<>();
        if (this.selectedEntity != null) {
            final Gruppe gruppe = this.em.find(Gruppe.class, this.selectedEntity.getGruppenId());
            if (gruppe != null) {
                for (final User user : gruppe.getMitglieder()) {
                    if (UserUtils.isEmployee(user)) {
                        processors.add(user);
                    }
                }
            }
        }
        return processors;
    }

    /**
     * Zaehlt wie viele Tickets {@link de.hsb.app.swe.enumeration.Status#OFFEN} sind.
     *
     * @param projekt {@link Projekt}
     * @return Anzahl der offenen Tickets
     */
    public long openTickets(@CheckForNull final Projekt projekt) {
        int open = 0;
        if (projekt != null) {
            for (final Ticket ticket : projekt.getTicket()) {
                if (Status.OFFEN.equals(ticket.getStatus())) {
                    ++open;
                }
            }
        }
        return open;
    }

    /**
     * Bricht den aktuellen Vorgang ab und redirected auf {@link RedirectUtils#PROJEKT_TABELLE_XHTML}
     *
     * @return {@link RedirectUtils#PROJEKT_TABELLE_XHTML}
     */
    @Nonnull
    public String switchToProjekt() {
        return RedirectUtils.PROJEKT_TABELLE_XHTML;
    }

    /**
     * Findet alle Projekte eines {@link User}s in der er auch Mitglied ist.
     *
     * @param loggedUser eingeloggter {@link User}
     * @return List<Projekt>
     */
    @Nonnull
    public List<Projekt> userAwareFindAllByGruppe(@Nonnull final User loggedUser) {
        switch (loggedUser.getRolle()) {
            case KUNDE:
            case MITARBEITER:
                final List<Projekt> projektList = new ArrayList<>();
                for (final Gruppe gruppe : loggedUser.getGruppen()) {
                    final Query query = this.em.createQuery("select pr from Projekt pr where pr.gruppenId = :gruppenId");
                    query.setParameter("gruppenId", gruppe.getId());
                    projektList.addAll(this.uncheckedSolver(query.getResultList()));
                }
                return projektList;
            case ADMIN:
                return this.findAll();
            case USER:
            default:
                throw new IllegalArgumentException("Role {} can not have any projects!");
        }

    }

    /**
     * Prueft ob ein der eingeloggte {@link User} leiter des {@link Projekt}s ist.
     *
     * @param loggedUser Eingeloggter {@link User}
     * @param projekt    {@link Projekt}
     * @return boolean
     */
    public boolean isLeader(final User loggedUser, final Projekt projekt) {
        return UserUtils.compareUserById(projekt.getLeiterId(), loggedUser);
    }


    /**
     * Prueft ob die Gruppe die bearbeitende Gruppe des Projekts ist.
     *
     * @param gruppe zu pruefende Gruppe
     * @return boolean
     */
    public boolean isChoosenGroup(@Nonnull final Gruppe gruppe) {
        return GruppeUtils.compareGruppeById(this.choosenGroupId, gruppe);
    }

    /**
     * Prueft ob die Gruppe die Gruppe des Projektes ist.
     *
     * @param projekt {@link Projekt}
     * @param gruppe  {@link Gruppe}
     * @return boolean
     */
    public boolean isChoosenGroup(@Nonnull final Projekt projekt, @Nonnull final Gruppe gruppe) {
        return ProjectUtils.isChoosenGroup(projekt, gruppe);
    }

    /**
     * Setzt die ausgewaehlte Gruppe fuer das {@link Projekt}. Redirected auf {@link RedirectUtils#NEUES_PROJEKT_XHTML}.
     *
     * @param gruppe {@link Gruppe}
     * @return {@link RedirectUtils#NEUES_PROJEKT_XHTML}
     */
    public String addGroupToProjekt(@Nonnull final Gruppe gruppe) {
        this.choosenGroupId = gruppe.getId();
        return RedirectUtils.NEUES_PROJEKT_XHTML;
    }

    /**
     * Speicher das {@link Projekt} und fuehr zureuck auf {@link RedirectUtils#NEUES_PROJEKT_XHTML}
     *
     * @return {@link RedirectUtils#NEUES_PROJEKT_XHTML}
     */
    public String saveProject(@CheckForNull final List<Ticket> tickets) {
        if (tickets != null) {
            for (final Ticket ticket : tickets) {
                ticket.setProjekt(this.selectedEntity);
            }
            try {
                this.utx.begin();
                this.selectedEntity.setTicket(tickets);
                this.selectedEntity.setGruppenId(this.choosenGroupId);
                this.em.persist(this.selectedEntity);
                this.utx.commit();
                return RedirectUtils.PROJEKT_TABELLE_XHTML;
            } catch (final NotSupportedException | SystemException | SecurityException | IllegalStateException |
                    RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
                this.logger.error("Saving operation failed -> ", e);
            }
        } else {
            this.logger.error("Saving operation failed. Entity is null.");
        }
        return RedirectUtils.PROJEKT_TABELLE_XHTML;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected Class<Projekt> getRepositoryClass() {
        return Projekt.class;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected String getQueryCommand() {
        return Projekt.NAMED_QUERY_QUERY;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected String getSelect() {
        return Projekt.NAMED_QUERY_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Projekt> uncheckedSolver(@Nonnull final Object var) {
        final List<Projekt> result = new ArrayList<>();
        if (var instanceof List) {
            for (int i = 0; i < ((List<?>) var).size(); i++) {
                final Object item = ((List<?>) var).get(i);
                if (item instanceof Projekt) {
                    result.add((Projekt) item);
                }
            }
        }
        return result;
    }

    public int getChoosenGroupId() {
        return this.choosenGroupId;
    }

    public void setChoosenGroupId(final int choosenGroupId) {
        this.choosenGroupId = choosenGroupId;
    }
}
