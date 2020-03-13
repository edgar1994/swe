package de.hsb.app.swe.controller;

import de.hsb.app.swe.enumeration.Rolle;
import de.hsb.app.swe.enumeration.Status;
import de.hsb.app.swe.model.Gruppe;
import de.hsb.app.swe.model.Projekt;
import de.hsb.app.swe.model.Ticket;
import de.hsb.app.swe.model.User;
import de.hsb.app.swe.repository.AbstractCrudRepository;
import de.hsb.app.swe.utils.GruppeUtils;
import de.hsb.app.swe.utils.RedirectUtils;
import de.hsb.app.swe.utils.StringUtils;
import de.hsb.app.swe.utils.UserUtils;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
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

    private boolean isNewProject = false;

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
            this.isNewProject = false;
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
     * Aktualisiert das aktuell ausgewählte {@link Projekt}.
     *
     * @param id ID
     */
    public void updateProjekt(final int id) {
        final Optional<Projekt> projekt = this.findById(id);
        projekt.ifPresent(value -> this.selectedEntity = value);
    }

    /**
     * Findet alle Titel die existierende Projekte haben und liefert sie als {@link List<String>} zurueck.
     *
     * @return {@link List<String>}
     */
    private List<String> findAllProjectTitles() {
        final Query query = this.em.createQuery("select pr.titel from Projekt pr");
        return StringUtils.uncheckedSolver(query.getResultList());
    }

    /**
     * Prueft ob der uebergebene Titel existiert.
     *
     * @return boolean.
     */
    public boolean doesTitleExists() {
        final FacesContext context = FacesContext.getCurrentInstance();
        if (this.isNewProject) {
            final List<String> titles = this.findAllProjectTitles();
            if (titles.contains(this.selectedEntity.getTitel())) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        this.messageService.getMessage("PROJECT.VALIDATOR.MESSAGE.SAVE.SUMMARY.FAILED"),
                        this.messageService.getMessage("PROJECT.VALIDATOR.MESSAGE.SAVE.DETAIL.EXISTS")));
                return true;
            }
        }
        return false;
    }

    /**
     * Erstellt ein neues {@link Projekt} und redirected auf {@link RedirectUtils#NEUES_PROJEKT_XHTML}.
     *
     * @param projektLeiter Projektleiter ({@link User})
     * @return {@link RedirectUtils#NEUES_PROJEKT_XHTML}
     */
    public String newProject(final User projektLeiter) {
        this.isNewProject = true;
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
     * Zaehlt wie viele Tickets {@link Status#OFFEN} sind.
     *
     * @param projekt {@link Projekt}
     * @return Anzahl der offenen Tickets
     */
    public long openTickets(final Projekt projekt) {
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
    public String switchToProjekt() {
        return RedirectUtils.PROJEKT_TABELLE_XHTML;
    }

    /**
     * Findet alle Projekte eines {@link User}s in der er auch Mitglied ist.
     *
     * @param loggedUser eingeloggter {@link User}
     * @return List<Projekt>
     */
    public List<Projekt> userAwareFindAllByGruppe(final User loggedUser) {
        if (loggedUser != null) {
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
                    throw new IllegalArgumentException(this.messageService.getMessage(
                            "EXCEPTION.ILLEGALARGUMENT.PROJECT", loggedUser.getRolle()));
            }
        } else {
            this.logger.error("LOG.PROJECT.ERROR.NOUSER");
        }
        return new ArrayList<>();
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
    public boolean isChoosenGroup(final Gruppe gruppe) {
        return GruppeUtils.compareGruppeById(this.choosenGroupId, gruppe);
    }

    /**
     * Setzt die ausgewaehlte Gruppe fuer das {@link Projekt}. Redirected auf {@link RedirectUtils#NEUES_PROJEKT_XHTML}.
     *
     * @param gruppe {@link Gruppe}
     * @return {@link RedirectUtils#NEUES_PROJEKT_XHTML}
     */
    public String addGroupToProjekt(final Gruppe gruppe) {
        this.choosenGroupId = gruppe.getId();
        return RedirectUtils.NEUES_PROJEKT_XHTML;
    }

    /**
     * Redirected auf {@link RedirectUtils#PROJEKT_ANSICHT_XHTML}.
     *
     * @return {@link RedirectUtils#PROJEKT_ANSICHT_XHTML}
     */
    public String switchToProjectAnsicht() {
        return RedirectUtils.PROJEKT_ANSICHT_XHTML;
    }

    /**
     * Speichert das {@link Projekt} und fuehr zureuck auf {@link RedirectUtils#NEUES_PROJEKT_XHTML}
     *
     * @return {@link RedirectUtils#NEUES_PROJEKT_XHTML}
     */
    public String saveProject(final List<Ticket> tickets) {
        final FacesContext context = FacesContext.getCurrentInstance();
        if (tickets != null) {
            for (final Ticket ticket : tickets) {
                ticket.setProjekt(this.selectedEntity);
            }
            if (this.isNewProject && this.doesTitleExists()) {
                return RedirectUtils.NEUES_PROJEKT_XHTML;
            }
            try {
                this.utx.begin();
                this.selectedEntity.setTicket(tickets);
                this.selectedEntity.setGruppenId(this.choosenGroupId);
                this.em.persist(this.selectedEntity);
                this.utx.commit();
                if (this.isNewProject) {
                    context.addMessage(null, new FacesMessage(
                            this.messageService.getMessage("PROJECT.VALIDATOR.MESSAGE.SAVE.SUMMARY.NEW"),
                            this.messageService.getMessage(
                                    "PROJECT.VALIDATOR.MESSAGE.SAVE.DETAIL.NEW", this.selectedEntity.getTitel())));
                } else {
                    context.addMessage(null, new FacesMessage(
                            this.messageService.getMessage("PROJECT.VALIDATOR.MESSAGE.SAVE.SUMMARY.EDIT"),
                            this.messageService.getMessage(
                                    "PROJECT.VALIDATOR.MESSAGE.SAVE.DETAIL.EDIT", this.selectedEntity.getTitel())));
                }
                this.logger.info("LOG.PROJECT.INFO.SAVE.SUCCESSFUL", this.selectedEntity.getId());
                this.choosenGroupId = 0;
                return RedirectUtils.PROJEKT_TABELLE_XHTML;
            } catch (final NotSupportedException | SystemException | SecurityException | IllegalStateException |
                    RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        this.messageService.getMessage("PROJECT.VALIDATOR.MESSAGE.SAVE.SUMMARY.FAILED"),
                        this.messageService.getMessage(
                                "PROJECT.MESSAGE.SAVE.DETAIL.FAILED", this.selectedEntity.getTitel())));
                this.logger.error("LOG.PROJECT.ERROR.SAVE.FAILED", e.getMessage());
            }
        } else {
            this.logger.error("LOG.PROJECT.ERROR.SAVE.NOPROJECT");
        }
        this.choosenGroupId = 0;
        return RedirectUtils.PROJEKT_TABELLE_XHTML;
    }

    /**
     * Loescht ein {@link Projekt} und redirected auf {@link RedirectUtils#PROJEKT_TABELLE_XHTML}.
     *
     * @param loggedUser Eingeloggter {@link User}
     * @param project    {@link Projekt}
     * @return {@link RedirectUtils#PROJEKT_TABELLE_XHTML}
     */
    public String deleteProject(final User loggedUser, Projekt project) {
        final FacesContext context = FacesContext.getCurrentInstance();
        if (project != null && loggedUser != null) {
            if (UserUtils.compareUserById(project.getLeiterId(), loggedUser) || loggedUser.getRolle() == Rolle.ADMIN) {
                try {
                    this.utx.begin();
                    project = this.em.merge(project);
                    this.em.remove(project);
                    this.em.flush();
                    this.utx.commit();
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                            this.messageService.getMessage("PROJECT.VALIDATOR.MESSAGE.DELETE.SUMMARY.SUCCESS"),
                            this.messageService.getMessage("PROJECT.VALIDATOR.MESSAGE.DELETE.DETAIL.SUCCESS",
                                    project.getTitel())
                    ));
                } catch (final NotSupportedException | SystemException | SecurityException | IllegalStateException |
                        RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            this.messageService.getMessage("PROJECT.VALIDATOR.MESSAGE.DELETE.SUMMARY.FAILED"),
                            this.messageService.getMessage("PROJECT.VALIDATOR.MESSAGE.DELETE.DETAIL.FAILED")
                    ));
                    this.logger.error("PROJECT.LOG.MESSAGE.DELETE.DETAIL.FAILED");
                }
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        this.messageService.getMessage("PROJECT.VALIDATOR.MESSAGE.DELETE.SUMMARY.NOTAUTHORIZED"),
                        this.messageService.getMessage("PROJECT.VALIDATOR.MESSAGE.DELETE.DETAIL.NOTAUTHORIZED")
                ));
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    this.messageService.getMessage("PROJECT.VALIDATOR.MESSAGE.DELETE.SUMMARY.FAILED"),
                    this.messageService.getMessage("PROJECT.VALIDATOR.MESSAGE.DELETE.DETAIL.FAILED")
            ));
            this.logger.error("PROJECT.LOG.MESSAGE.DELETE.DETAIL.FAILED");
        }
        return RedirectUtils.PROJEKT_TABELLE_XHTML;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<Projekt> getRepositoryClass() {
        return Projekt.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getQueryCommand() {
        return Projekt.NAMED_QUERY_QUERY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getSelect() {
        return Projekt.NAMED_QUERY_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Projekt> uncheckedSolver(final Object var) {
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
