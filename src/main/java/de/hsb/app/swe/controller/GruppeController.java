package de.hsb.app.swe.controller;

import de.hsb.app.swe.model.Gruppe;
import de.hsb.app.swe.model.Projekt;
import de.hsb.app.swe.model.User;
import de.hsb.app.swe.repository.AbstractCrudRepository;
import de.hsb.app.swe.utils.ListUtils;
import de.hsb.app.swe.utils.RedirectUtils;
import de.hsb.app.swe.utils.StringUtils;
import de.hsb.app.swe.utils.UserUtils;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.persistence.Query;
import javax.transaction.*;
import java.time.Instant;
import java.util.*;

/**
 * Controller fuer {@link Gruppe}.
 */
@ManagedBean(name = "gruppeController")
@SessionScoped
public class GruppeController extends AbstractCrudRepository<Gruppe> {

    private boolean isNewGroup = false;

    /**
     * Loescht eine {@link Gruppe} redirected auf {@link RedirectUtils#GRUPPE_TABELLE_XHTML}.
     * Ist der {@link User} dazu nicht berechtigt wird die ausgewaehlte {@link Gruppe} nicht geloescht und er bleibt auf
     * {@link RedirectUtils#GRUPPE_TABELLE_XHTML}.
     *
     * @param loggedUser Eingeloggter {@link User}
     * @param groupId    Gruppen-ID
     * @return {@link RedirectUtils#GRUPPE_TABELLE_XHTML}
     */
    public String deleteGruppe(final User loggedUser, final int groupId) {
        final FacesContext context = FacesContext.getCurrentInstance();
        if (loggedUser != null) {
            final Optional<Gruppe> group = this.findById(groupId);
            if (group.isPresent()) {
                this.selectedEntity = group.get();
                if (UserUtils.compareUserById(this.selectedEntity.getLeiterId(), loggedUser) ||
                        UserUtils.isAdmin(loggedUser)) {
                    this.deleteGruppe(loggedUser, this.selectedEntity);
                } else {
                    context.addMessage(null, new FacesMessage(
                            this.messageService.getMessage("GROUP.MESSAGE.DELETE.FAILED.SUMMARY"),
                            this.messageService.getMessage("GROUP.MESSAGE.DELETE.FAILED.DETAIL.PERMISSIONDENIED")));
                    this.logger.error("LOG.GROUP.DELETE.FAILED.PERMISSIONDENIED", groupId);
                }
            }
        }
        return RedirectUtils.GRUPPE_TABELLE_XHTML;
    }

    /**
     * Loescht eine {@link Gruppe} und entfernt vorher dessen {@link User}. Das {@link Projekt} an dem die {@link Gruppe}
     * gearbeitet hat wird nicht gelöscht bekommt aber keine {@link Gruppe} mehr zugewissen. (Muss von einem Admin
     * geloescht werden)
     *
     * @param loggedUser Eingeloggter {@link User}
     * @param group      {@link Gruppe}
     */
    private void deleteGruppe(final User loggedUser, final Gruppe group) {
        final FacesContext context = FacesContext.getCurrentInstance();
        if (loggedUser != null) {
            if (group != null) {
                try {
                    this.utx.begin();
                    // Loesche die Gruppe aus dem User
                    for (final User user : this.selectedEntity.getMitglieder()) {
                        user.getGruppen().remove(this.selectedEntity);
                    }
                    final Query query = this.em.createQuery("select pr from Projekt pr where pr.gruppenId = :groupId");
                    query.setParameter("groupId", group.getId());
                    // Loesche die Gruppen aus dem Projekt
                    for (final Projekt projekt : ListUtils.uncheckedSolverProjekt(query.getResultList())) {
                        projekt.setGruppenId(0);
                        this.em.persist(projekt);
                    }
                    // Leere die MitgliederListe
                    this.selectedEntity.setMitglieder(new HashSet<>());
                    this.selectedEntity = this.em.merge(this.selectedEntity);
                    // Loesche die Gruppe
                    this.em.remove(this.selectedEntity);
                    this.entityList.setWrappedData(this.userAwareFindAllGruppen(loggedUser).getWrappedData());
                    context.addMessage(null, new FacesMessage(
                            this.messageService.getMessage("GROUP.MESSAGE.DELETE.SUMMARY"),
                            this.messageService.getMessage("GROUP.MESSAGE.DELETE.DETAIL",
                                    this.selectedEntity.getTitel())));
                    this.logger.info("LOG.GROUP.DELETE.SUCCSESS", this.selectedEntity.getTitel(), group.getId());
                    this.utx.commit();
                    // Zweites loeschen, da nach ersten mal nur Mitglieder entfernt.
                    this.utx.begin();
                    this.selectedEntity = this.em.merge(this.selectedEntity);
                    final Query q = this.em.createQuery("select p from Projekt p where p.gruppenId = :gruppenId");
                    q.setParameter("gruppenId", group.getId());
                    for (final Projekt p : ListUtils.uncheckedSolverProjekt(q.getResultList())) {
                        p.setGruppenId(0);
                        p.setLeiterId(0);
                        this.em.remove(p);
                    }
                    this.em.remove(this.selectedEntity);
                    this.utx.commit();
                } catch (final NotSupportedException | SystemException | SecurityException | IllegalStateException |
                        RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            this.messageService.getMessage("GROUP.MESSAGE.DELETE.FAILED.SUMMARY"),
                            this.messageService.getMessage("GROUP.MESSAGE.DELETE.FAILED.DETAIL.ERROR",
                                    group.getTitel())));
                    this.logger.error(
                            "LOG.GROUP.DELETE.FAILED", group.getId(), e.getMessage());
                }

            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        this.messageService.getMessage("GROUP.MESSAGE.DELETE.FAILED.SUMMARY"),
                        this.messageService.getMessage("GROUP.MESSAGE.DELETE.FAILED.DETAIL.NOTFOUND")));
                this.logger.error("LOG.GROUP.DELETE.FAILED.NOTFOUND");
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    this.messageService.getMessage("GROUP.MESSAGE.DELETE.FAILED.SUMMARY"),
                    this.messageService.getMessage("GROUP.MESSAGE.DELETE.FAILED.DETAIL.EMPTYUSER")));
            this.logger.error("LOG.GROUP.DELETE.FAILED.NOTFOUND");
        }
    }

    /**
     * Findet alle Gruppentitel die bereits in der Datenbank gespeichert wurden.
     *
     * @return {@link List<String>}
     */
    private List<String> findAllGrouptitles() {
        final Query query = this.em.createQuery("select gr.titel from Gruppe gr");
        return StringUtils.uncheckedSolver(query.getResultList());
    }

    /**
     * Wenn der Titel bereits existiert wird eine {@link FacesMessage} geschmissen.
     *
     * @return boolean
     */
    public boolean doesGrouptitleExists() {
        if (this.isNewGroup && this.selectedEntity != null) {
            final FacesContext context = FacesContext.getCurrentInstance();
            final List<String> titles = this.findAllGrouptitles();
            if (titles.contains(this.selectedEntity.getTitel())) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        this.messageService.getMessage("GROUP.MESSAGE.NEWGROUP.FAILED.SUMMARY"),
                        this.messageService.getMessage("GROUP.VALIDATOR.MESSAGE.SAVE.DETAIL.EXISTS")
                ));
                return true;
            }
        }
        return false;
    }

    /**
     * Erstellt eine neue {@link Gruppe} setzt den uebergenen {@link User} loggedUser als Leiter und fuegt ihn als
     * Mitglied hinzu. Redirected auf {@link RedirectUtils#NEUE_GRUPPE_XHTML}.
     *
     * @param loggedUser Eingeloggter {@link User}
     * @return {@link RedirectUtils#NEUE_GRUPPE_XHTML}
     */
    public String newGroup(final User loggedUser) {
        final FacesContext context = FacesContext.getCurrentInstance();
        if (loggedUser != null) {
            this.isNewGroup = true;
            this.selectedEntity = new Gruppe();
            this.selectedEntity.setLeiterId(loggedUser.getId());
            this.selectedEntity.setLeiterName(UserUtils.getNachnameVornameString(loggedUser));
            this.selectedEntity.addUser(loggedUser);
            this.selectedEntity.setErstellungsdatum(Date.from(Instant.now()));
            return RedirectUtils.NEUE_GRUPPE_XHTML;
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    this.messageService.getMessage("GROUP.MESSAGE.NEWGROUP.FAILED.SUMMARY"),
                    this.messageService.getMessage("GROUP.MESSAGE.NEWGROUP.FAILED.DETAIL.NOUSER")));
            this.logger.error("LOG.GROUP.ERROR.NOUSER");
            return RedirectUtils.GRUPPE_TABELLE_XHTML;
        }

    }

    /**
     * Prueft ob der User die Gruppe editieren darf und setzt die gewaehlte {@link Gruppe} und redirected auf
     * {@link RedirectUtils#NEUE_GRUPPE_XHTML} weiter. Sollte er nicht die Gruppe editieren duerfen bleibt er auf
     * {@link RedirectUtils#GRUPPE_TABELLE_XHTML}.
     *
     * @return {@link RedirectUtils#NEUE_GRUPPE_XHTML} || {@link RedirectUtils#GRUPPE_TABELLE_XHTML}
     */
    public String userAwareEditGroup(final User user) {
        this.checkEntityList();
        this.isNewGroup = false;
        final Gruppe gruppeToCheck = this.entityList.getRowData();
        if (gruppeToCheck != null && (UserUtils.compareUserById(gruppeToCheck.getLeiterId(), user) ||
                UserUtils.isAdmin(user))) {
            this.selectedEntity = gruppeToCheck;
            return RedirectUtils.NEUE_GRUPPE_XHTML;
        }
        return RedirectUtils.GRUPPE_TABELLE_XHTML;
    }

    /**
     * Setzt die Mitglieder der gewaehlten {@link Gruppe} und speichert sie. Redirected auf
     * {@link RedirectUtils#GRUPPE_TABELLE_XHTML}
     *
     * @return {@link RedirectUtils#GRUPPE_TABELLE_XHTML}
     */
    public String saveGroup(final Set<User> members) {
        final FacesContext context = FacesContext.getCurrentInstance();
        if (members != null) {
            if (this.isNewGroup && this.doesGrouptitleExists()) {
                return RedirectUtils.NEUE_GRUPPE_XHTML;
            }
            try {
                this.utx.begin();
                this.selectedEntity.setMitglieder(members);
                this.selectedEntity = this.em.merge(this.selectedEntity);
                this.em.persist(this.selectedEntity);
                this.utx.commit();
                if (this.isNewGroup) {
                    context.addMessage(null, new FacesMessage(
                            this.messageService.getMessage("GROUP.MESSAGE.SAVE.SUMMARY.NEW"),
                            this.messageService.getMessage("GROUP.MESSAGE.SAVE.DETAIL.NEW")));
                } else {
                    context.addMessage(null, new FacesMessage(
                            this.messageService.getMessage("GROUP.MESSAGE.SAVE.SUMMARY.EDIT"),
                            this.messageService.getMessage("GROUP.MESSAGE.SAVE.DETAIL.EDIT")));
                }
                this.logger.info("LOG.GROUP.SAVE.SUCCESS", this.selectedEntity.getTitel());
            } catch (final NotSupportedException | SystemException | SecurityException | IllegalStateException |
                    RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
                this.logger.error("LOG.GROUP.SAVE.FAILED", e.getMessage());
            }
            this.isNewGroup = false;
            return RedirectUtils.GRUPPE_TABELLE_XHTML;
        } else {
            return RedirectUtils.NEUE_GRUPPE_XHTML;
        }
    }

    /**
     * Findet alle {@link Gruppe}n in denen der uebergebene {@link User} Mitglied oder Gruppenleiter ist.
     *
     * @param user {@link User}
     * @return List<Gruppe>
     */
    public DataModel<Gruppe> userAwareFindAllGruppen(final User user) {
        this.checkEntityList();
        if (user != null) {
            switch (user.getRolle()) {
                case KUNDE:
                case MITARBEITER:
                    this.entityList.setWrappedData(new ArrayList<>(user.getGruppen()));
                    return this.entityList;
                case ADMIN:
                    this.entityList.setWrappedData(this.findAll());
                    return this.entityList;
                case USER:
                default:
                    throw new IllegalArgumentException(this.messageService.getMessage(
                            "EXCEPTION.ILLEGALARGUMENT.GROUP", user.getRolle()));
            }
        } else {
            this.logger.error("LOG.GROUP.ERROR.NOUSER");
            return this.entityList;
        }
    }

    /**
     * Findet alle {@link Gruppe}n in denen der uebergebene {@link User} Mitglied oder Gruppenleiter ist.
     *
     * @param user {@link User}
     * @return List<Gruppe>
     */
    public DataModel<Gruppe> userAwareFindAllGruppenByLeiterId(final User user) {
        this.checkEntityList();
        if (user != null) {
            switch (user.getRolle()) {
                case MITARBEITER:
                    final Query query = this.em.createQuery("select gr from Gruppe gr where gr.leiterId = :userId");
                    query.setParameter("userId", user.getId());
                    final List<Gruppe> gruppeList = this.uncheckedSolver(query.getResultList());
                    this.entityList.setWrappedData(gruppeList);
                    return this.entityList;
                case ADMIN:
                    this.entityList.setWrappedData(this.findAll());
                    return this.entityList;
                case USER:
                case KUNDE:
                default:
                    throw new IllegalArgumentException(this.messageService.getMessage(
                            "EXCEPTION.ILLEGALARGUMENT.GROUP", user.getRolle()));
            }
        } else {
            this.logger.error("LOG.GROUP.ERROR.NOUSER");
            return this.entityList;
        }
    }

    /**
     * Liefert zuruck ob der eingeloggte {@link User} eine neue {@link Gruppe} erstellen darf oder nicht.
     *
     * @param loggedUser eingeloggter User
     * @return boolean
     */
    public boolean userAwareNewGroup(final User loggedUser) {
        if (loggedUser != null) {
            switch (loggedUser.getRolle()) {
                case KUNDE:
                case ADMIN:
                case USER:
                    return false;
                case MITARBEITER:
                    return true;
                default:
                    throw new IllegalArgumentException(this.messageService.getMessage(
                            "EXCEPTION.ILLEGALARGUMENT.GROUP", loggedUser.getRolle()));
            }
        } else {
            this.logger.error("LOG.GROUP.ERROR.NOUSER");
            return false;
        }
    }

    /**
     * Liefert zuruck ob der eingeloggte {@link User} eine {@link Gruppe} loeschen darf oder nicht.
     *
     * @param loggedUser eingeloggter User
     * @return boolean
     */
    public boolean userAwareIsGroupLeader(final User loggedUser, final Gruppe currentGroup) {
        if (loggedUser != null) {
            switch (loggedUser.getRolle()) {
                case KUNDE:
                case USER:
                    return false;
                case MITARBEITER:
                    if (currentGroup != null) {
                        return UserUtils.compareUserById(currentGroup.getLeiterId(), loggedUser);
                    } else {
                        this.logger.error("LOG.GROUP.ERROR.NOTNULL");
                        return false;
                    }
                case ADMIN:
                    return true;
                default:
                    throw new IllegalArgumentException(this.messageService.getMessage(
                            "EXCEPTION.ILLEGALARGUMENT.GROUP", loggedUser.getRolle()));
            }
        } else {
            this.logger.error("LOG.GROUP.ERROR.NOUSER");
            return false;
        }
    }

    /**
     * Bricht den aktuellen Vorgang ab und redirected auf {@link RedirectUtils#GRUPPE_TABELLE_XHTML}.
     *
     * @return {@link RedirectUtils#GRUPPE_TABELLE_XHTML}
     */
    public String switchToGruppe() {
        return RedirectUtils.GRUPPE_TABELLE_XHTML;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<Gruppe> getRepositoryClass() {
        return Gruppe.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getQueryCommand() {
        return Gruppe.NAMED_QUERY_QUERY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getSelect() {
        return Gruppe.NAMED_QUERY_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Gruppe> uncheckedSolver(final Object var) {
        final List<Gruppe> result = new ArrayList<>();
        if (var instanceof List) {
            for (int i = 0; i < ((List<?>) var).size(); i++) {
                final Object item = ((List<?>) var).get(i);
                if (item instanceof Gruppe) {
                    result.add((Gruppe) item);
                }
            }
        }
        return result;
    }
}
