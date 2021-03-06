package de.hsb.app.swe.controller;

import de.hsb.app.swe.enumeration.Rolle;
import de.hsb.app.swe.model.Gruppe;
import de.hsb.app.swe.model.Projekt;
import de.hsb.app.swe.model.User;
import de.hsb.app.swe.repository.AbstractCrudRepository;
import de.hsb.app.swe.utils.AdressUtils;
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
import java.util.*;

/**
 * UserController Link {@User}
 */
@ManagedBean(name = "userController")
@SessionScoped
public class UserController extends AbstractCrudRepository<User> {

    private boolean existingUser = false;

    private Set<User> groupmembersSet;


    /**
     * Fuegt einen User dem Groupmember-Set hinzu.
     *
     * @param userId Id eines Users
     */
    public void addUser(final int userId) {
        Optional.of(this.em.find(User.class, userId)).ifPresent(this.groupmembersSet::add);
    }

    /**
     * Prueft ob ein {@link User} bereits Mitglied der {@link Gruppe} ist.
     *
     * @param user {@link User}
     * @return boolean
     */
    public boolean isAdded(final User user) {
        boolean hinzugefuegt = false;
        for (final User hinzugefuegterUser : this.groupmembersSet) {
            hinzugefuegt |= UserUtils.compareUserById(hinzugefuegterUser, user);
        }
        return hinzugefuegt;
    }

    /**
     * Entfernt einen Member aus dem Set, der der uebergebenen UserId eines vorhanden Users im Set entspricht.
     *
     * @param userId Id eines Users
     */
    public void removeUser(final int userId) {
        final Set<User> tmpGroupmembersSet = new HashSet<>();
        for (final User member : this.groupmembersSet) {
            if (!UserUtils.compareUserById(userId, member)) {
                tmpGroupmembersSet.add(member);
            }
        }
        this.groupmembersSet = tmpGroupmembersSet;
    }

    /**
     * Leert das Group-Member-Set und setzt den eingeloggten User als ersten Member.
     *
     * @param loggedUser Eingeloggter User
     */
    public void resetGroupmembersSet(final User loggedUser) {
        this.groupmembersSet = new HashSet<>();
        if (loggedUser != null) {
            this.groupmembersSet.add(loggedUser);
        }
    }

    /**
     * Bricht den aktuellen Vorgang ab und leitet zurueck auf {@link RedirectUtils#USER_TABELLE_XHTML}.
     *
     * @return {@link RedirectUtils#USER_TABELLE_XHTML}
     */
    public String switchToUser() {
        this.existingUser = false;
        return RedirectUtils.USER_TABELLE_XHTML;
    }

    /**
     * Bricht den aktuellen Vorgang ab und leitet zurueck auf {@link RedirectUtils#IMPRINT_XHTML}.
     *
     * @return {@link RedirectUtils#IMPRINT_XHTML}
     */
    public String switchToImprint() {
        this.existingUser = false;
        return RedirectUtils.IMPRINT_XHTML;
    }


    /**
     * Formatiert die Adresse nach folgenden Format: Straße Hausnummer, Stadt, Postleitzahl.
     *
     * @param user {@link User}
     * @return Straße Hausnummer, Stadt, Postleitzahl
     */
    public String formatedAdresse(final User user) {
        return AdressUtils.formatAdresse(user.getAdresse());
    }

    /**
     * Liefert den Vor- und Nachnamen im Format "Nachname, Vorname" zurueck. Ist der Vorname oder Nachname leer wird ein
     * {@link UserUtils#SELECT_ONE_USER} zurueckgeliefert.
     *
     * @param user  {@link User}
     * @param rolle {@link Rolle} fuer die Dummy-Option
     * @return "Nachname, Vorname"
     */
    public String formatedName(final User user, final Rolle rolle) {
        if (user != null) {
            return UserUtils.getNachnameVornameString(user);
        } else {
            return UserUtils.formatedNameDummy(rolle);
        }
    }

    /**
     * Liefert den Vor- und Nachnamen im Format "Nachname, Vorname" zurueck. Ist der Vorname oder Nachname leer wird ein
     * {@link UserUtils#SELECT_ONE_USER} zurueckgeliefert.
     *
     * @param optionalUser {@link User}
     * @param rolle        {@link Rolle} fuer die Dummy-Option
     * @return "Nachname, Vorname"
     */
    public String formatedName(final Optional<User> optionalUser, final Rolle rolle) {
        return optionalUser.map(UserUtils::getNachnameVornameString)
                .orElseGet(() -> UserUtils.formatedNameDummy(rolle));
    }

    /**
     * Liefert den Vor- und Nachnamen im Format "Nachname, Vorname" zurueck. Ist der Vorname oder Nachname leer wird ein
     * {@link UserUtils#SELECT_ONE_USER} zurueckgeliefert.
     *
     * @param user  {@link User}
     * @param rolle {@link Rolle} fuer die Dummy-Option
     * @return "Nachname, Vorname"
     */
    public String formatedNameForDropdown(final User user, final Rolle rolle) {
        if (user != null && (user.getId() == 0 || StringUtils.isEmptyOrNullOrBlank(user.getVorname()) ||
                StringUtils.isEmptyOrNullOrBlank(user.getNachname()))) {
            return UserUtils.SELECT_ONE_USER;
        } else {
            return this.formatedName(user, rolle);
        }
    }

    /**
     * Sucht alle {@link User} aus der {@link Gruppe} eines {@link Projekt}es, die nicht der Projektleiter aber
     * {@link Rolle#MITARBEITER} sind und liefert diese zurueck.
     *
     * @param projectToCheck zu pruefendes Projekt
     * @return List<User>
     */
    public List<User> findAllEmployeesInProjectExceptLeader(final Projekt projectToCheck) {
        final List<User> userList = new ArrayList<>();
        if (projectToCheck != null && projectToCheck.getGruppenId() != 0) {
            final Gruppe groupToCheck = this.em.find(Gruppe.class, projectToCheck.getGruppenId());
            if (groupToCheck != null) {
                for (final User user : groupToCheck.getMitglieder()) {
                    if (!UserUtils.compareUserById(projectToCheck.getLeiterId(), user) &&
                            Rolle.MITARBEITER.equals(user.getRolle())) {
                        userList.add(user);
                    }
                }
            }
        }
        if (userList.isEmpty()) {
            userList.add(UserUtils.DUMMY_USER_MITARBEITER);
        }
        return userList;
    }

    /**
     * Sucht alle {@link User} aus der {@link Gruppe} eines {@link Projekt}es, die die Rolle {@link Rolle#KUNDE} haben
     * und liefert diese zurueck.
     *
     * @param projectToCheck zu pruefendes Projekt
     * @return List<User>
     */
    public List<User> findAllCustomers(final Projekt projectToCheck) {
        final List<User> userList = new ArrayList<>();
        if (projectToCheck != null && projectToCheck.getGruppenId() != 0) {
            final Gruppe groupToCheck = this.em.find(Gruppe.class, projectToCheck.getGruppenId());
            if (groupToCheck != null) {
                for (final User user : groupToCheck.getMitglieder()) {
                    if (Rolle.KUNDE.equals(user.getRolle())) {
                        userList.add(user);
                    }
                }
            }
        }
        if (userList.isEmpty()) {
            userList.add(UserUtils.DUMMY_USER_KUNDE);
        }
        return userList;
    }

    /**
     * Sucht den User anhand der uebergebenen Id.
     * Liefert den Vor- und Nachnamen im Format "Nachname, Vorname" zurueck.
     *
     * @param userId {@link User}Id
     * @return "Nachname, Vorname"
     */
    public String formatedName(final int userId, final Rolle rolle) {
        final Optional<User> optionalUser = this.findById(userId);
        return optionalUser.map(UserUtils::getNachnameVornameString).orElseGet(() -> UserUtils.formatedNameDummy(rolle));
    }

    /**
     * Sucht den ersten {@link User} mit der {@link Rolle} "{@link Rolle#KUNDE}" und liefert diesen zurueck.
     * Gibt es keinen Kunden in der Liste wird {@link UserUtils#DUMMY_USER_KUNDE} zurueckgeliefert.
     *
     * @param userList {@link Set<User>}
     * @return User mit {@link Rolle#KUNDE} || {@link UserUtils#DUMMY_USER_KUNDE}
     */
    public User getKundeUser(final Set<User> userList) {
        for (final User user : userList) {
            if (Rolle.KUNDE.equals(user.getRolle())) {
                return user;
            }
        }
        return UserUtils.DUMMY_USER_KUNDE;
    }

    /**
     * Sucht den zu bearbeitenden {@link User} raus und redirected auf {@link RedirectUtils#NEUER_USER_XHTML}.
     *
     * @return {@link RedirectUtils#NEUER_USER_XHTML}
     */
    public String edit() {
        this.selectedEntity = this.entityList.getRowData();
        this.existingUser = true;
        return RedirectUtils.NEUER_USER_XHTML;
    }

    /**
     * Setzt den eigenen {@link User} und redirectet auf {@link RedirectUtils#NEUER_USER_XHTML}.
     *
     * @param user {@link User}
     * @return {@link RedirectUtils#NEUER_USER_XHTML}
     */
    public String editOwnUser(final User user) {
        this.selectedEntity = user;
        this.existingUser = true;
        return RedirectUtils.NEUER_USER_XHTML;
    }

    /**
     * Loescht ein Element in der Liste.
     *
     * @return {@link RedirectUtils#USER_TABELLE_XHTML}.
     */
    public String deleteRow(final User loggedUser) {
        if (loggedUser != null) {
            this.delete(loggedUser);
        }
        return RedirectUtils.USER_TABELLE_XHTML;
    }

    /**
     * Loescht einen User sofern er es nicht selbst ist und ADMIN ist.
     *
     * @param loggedUser Eingeloggter {@link User}
     */
    private boolean delete(final User loggedUser) {
        final FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (loggedUser != null && UserUtils.isAdmin(loggedUser)) {
                this.selectedEntity = this.entityList.getRowData();
                this.utx.begin();
                if (this.selectedEntity != null && !UserUtils.compareUserById(loggedUser, this.selectedEntity)) {
                    for (Gruppe gruppe : this.selectedEntity.getGruppen()) {
                        gruppe.getMitglieder().removeIf(user -> UserUtils.compareUserById(user, this.selectedEntity));
                        gruppe = this.em.merge(gruppe);
                        this.em.persist(gruppe);
                    }
                    this.selectedEntity = this.em.merge(this.selectedEntity);
                    this.em.remove(this.selectedEntity);
                    this.entityList.setWrappedData(this.em.createNamedQuery(this.getSelect()).getResultList());
                    this.utx.commit();
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                            this.messageService.getMessage("GRUPPE.DELETE.MESSAGE.SUCCESS.SUMMARY"),
                            this.messageService.getMessage("GRUPPE.DELETE.MESSAGE.SUCCESS.DETAIL")
                    ));
                    return true;
                } else {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            this.messageService.getMessage("GRUPPE.DELETE.MESSAGE.FAILED.SUMMARY"),
                            this.messageService.getMessage("GRUPPE.DELETE.MESSAGE.FAILED.DETAIL.ITSELF")));
                    return false;
                }
            }
        } catch (final NotSupportedException | SystemException | SecurityException | IllegalStateException |
                RollbackException | HeuristicMixedException |
                HeuristicRollbackException e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    this.messageService.getMessage("GRUPPE.DELETE.MESSAGE.FAILED.SUMMARY"),
                    this.messageService.getMessage("GRUPPE.DELETE.MESSAGE.FAILED.DETAIL.ERROR", e.getMessage())
            ));
            this.logger.error("GRUPPE.DELETE.MESSAGE.FAILED.DETAIL.ERROR", e);
        }
        return false;
    }

    /**
     * Legt einen neuen Kunden an und leitet auf {@link RedirectUtils#USER_TABELLE_XHTML}.
     *
     * @return {@link RedirectUtils#USER_TABELLE_XHTML}
     */
    public String newUser() {
        this.setSelectedEntity(new User());
        this.existingUser = false;
        return RedirectUtils.NEUER_USER_XHTML;
    }

    /**
     * Abspeichern eines neuen {@link User}s. Nach Erfolg wird auf {@link RedirectUtils#USER_TABELLE_XHTML} redirected.
     * Ist kein Passwort gesetzt wird
     *
     * @return {@link RedirectUtils#USER_TABELLE_XHTML}
     */
    public String saveUser(final User loggedUser) {
        final FacesContext facesContext = FacesContext.getCurrentInstance();
        if (this.selectedEntity != null) {
            if (!this.doesUsernameExists()) {
                if (UserUtils.isAdmin(loggedUser) && StringUtils.isEmptyOrNullOrBlank(this.selectedEntity.getPasswort())) {
                    this.getSelectedEntity().setPasswort("passwort+");
                }
                if (!this.save(this.getSelectedEntity())) {
                    facesContext.addMessage(null, new FacesMessage("Fehlgeschlagen", "Fehlgeschlagen"));
                }
            } else {
                return RedirectUtils.NEUER_USER_XHTML;
            }
        } else {
            this.logger.error("LOG.USER.ERROR.SAVE.FAILED");
        }
        this.existingUser = false;
        return RedirectUtils.USER_TABELLE_XHTML;
    }

    /**
     * Findet alle {@link User} fuer die Gruppenerstellung.
     *
     * @return List<User> fuer die Gruppenerstellung
     */
    private List<User> findAllUserForGruppenerstellung() {
        final Query query = this.em.createQuery("select u from  User u where u.rolle = :mitarbeiter or " +
                "u.rolle = :kunde and u.rolle <> :admin and u.rolle <> :user");
        query.setParameter("mitarbeiter", Rolle.MITARBEITER);
        query.setParameter("admin", Rolle.ADMIN);
        query.setParameter("kunde", Rolle.KUNDE);
        query.setParameter("user", Rolle.USER);
        return this.uncheckedSolver(query.getResultList());
    }

    /**
     * Erstellt anhand aller gefundenen {@link User} fuer die Gruppenerstellung das entsprechende
     * {@link DataModel<User>}. Der eingeloggte {@link User} wird nicht mit Aufgeführt,
     * da dieser bereits in der {@link Gruppe} als Leiter gesetzt ist und ein {@link Rolle#ADMIN} oder ein
     * {@link Rolle#USER} werden ebenfalls ausgeschlossen, da diese keine Member einer Gruppe sein duerfen.
     * Ein Gruppenleiter kann nicht entfernt werden.
     *
     * @param loggedUser eingeloggter {@link User}
     * @return DataModel<User>
     */
    public DataModel<User> entityListForGruppenerstellung(final User loggedUser) {
        final List<User> userList = this.findAllUserForGruppenerstellung();
        userList.removeIf(user -> UserUtils.compareUserById(loggedUser, user) || Rolle.ADMIN.equals(user.getRolle())
                || Rolle.USER.equals(user.getRolle()));
        if (!userList.isEmpty()) {
            this.checkEntityList();
            this.entityList.setWrappedData(userList);
        }
        return this.entityList;
    }

    /**
     * Prueft ob der Username bereits existiert.
     *
     * @return boolean
     */
    public boolean doesUsernameExists() {
        if (!this.existingUser) {
            final Query query = this.em.createQuery("select u.username from User u");
            final List<String> usernameList = StringUtils.uncheckedSolver(query.getResultList());
            final FacesContext context = FacesContext.getCurrentInstance();
            if (this.selectedEntity.getUsername() != null && usernameList.contains(this.selectedEntity.getUsername())) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        this.messageService.getMessage("USER.VALIDATOR.USERNAME.SUMMARY"),
                        this.messageService.getMessage("USER.VALIDATOR.USERNAME.DETAIL.EXISTS")));
                return true;
            }
        }
        return false;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<User> getRepositoryClass() {
        return User.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getQueryCommand() {
        return User.NAMED_QUERY_QUERY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getSelect() {
        return User.NAMED_QUERY_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<User> uncheckedSolver(final Object var) {
        final List<User> result = new ArrayList<>();
        if (var instanceof List) {
            for (int i = 0; i < ((List<?>) var).size(); i++) {
                final Object item = ((List<?>) var).get(i);
                if (item instanceof User) {
                    result.add((User) item);
                }
            }
        }
        return result;
    }

    /**
     * Aktualisiert das groupmembersSet.
     *
     * @param groupmembersSet {@link Set<User>}
     */
    public void updateGroupMemberSet(final Set<User> groupmembersSet) {
        if (groupmembersSet != null) {
            this.groupmembersSet = groupmembersSet;
        }
    }

    /**
     * Getter fuer groupmembersSet.
     *
     * @return groupmembersSet
     */
    public Set<User> getGroupmembersSet() {
        return this.groupmembersSet;
    }

    /**
     * Setter fuer groupmembersSet.
     */
    public void setGroupmembersSet(final Set<User> groupmembersSet) {
        this.groupmembersSet = groupmembersSet;
    }

    /**
     * getter fuer existingUser.
     */
    public boolean isExistingUser() {
        return this.existingUser;
    }

    /**
     * Setter fuer existingUser.
     */
    public void setExistingUser(final boolean existingUser) {
        this.existingUser = existingUser;
    }
}
