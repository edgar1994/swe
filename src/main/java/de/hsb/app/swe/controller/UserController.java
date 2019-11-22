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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.persistence.Query;
import java.util.*;

/**
 * UserController
 */
@ManagedBean(name = "userController")
@SessionScoped
public class UserController extends AbstractCrudRepository<User> {

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
    public boolean isAdded(@Nonnull final User user) {
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
    public void resetGroupmembersSet(@CheckForNull final User loggedUser) {
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
        return RedirectUtils.USER_TABELLE_XHTML;
    }

    /**
     * Formatiert die Adresse nach folgenden Format: Straße Hausnummer, Stadt, Postleitzahl.
     *
     * @param user {@link User}
     * @return Straße Hausnummer, Stadt, Postleitzahl
     */
    public String formatedAdresse(@Nonnull final User user) {
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
    public String formatedName(@CheckForNull final User user, final Rolle rolle) {
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
    public List<User> findAllEmployeesInProjectExceptLeader(@CheckForNull final Projekt projectToCheck) {
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
    public List<User> findAllCustomers(@CheckForNull final Projekt projectToCheck) {
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
    @Nonnull
    public User getKundeUser(@Nonnull final Set<User> userList) {
        for (final User user : userList) {
            if (Rolle.KUNDE.equals(user.getRolle())) {
                return user;
            }
        }
        return UserUtils.DUMMY_USER_KUNDE;
    }

    /**
     * Sucht den zu bearbeitenden {@link User} raus und redirected auf {@link RedirectUtils#NEUER_USER_XHTML}
     *
     * @return {@link RedirectUtils#NEUER_USER_XHTML}
     */
    public String edit() {
        this.selectedEntity = this.entityList.getRowData();
        return RedirectUtils.NEUER_USER_XHTML;
    }

    /**
     * Loescht ein Element in der Liste.
     *
     * @return {@link RedirectUtils#USER_TABELLE_XHTML}.
     */
    public String deleteRow() {
        this.delete();
        return RedirectUtils.USER_TABELLE_XHTML;
    }

    /**
     * Legt einen neuen Kunden an und leitet auf {@link RedirectUtils#USER_TABELLE_XHTML}.
     *
     * @return {@link RedirectUtils#USER_TABELLE_XHTML}
     */
    public String newUser() {
        this.setSelectedEntity(new User());
        return RedirectUtils.NEUER_USER_XHTML;
    }

    /**
     * Abspeichern eines neuen {@link User}s. Nach Erfolg wird auf {@link RedirectUtils#USER_TABELLE_XHTML} redirected.
     *
     * @return {@link RedirectUtils#USER_TABELLE_XHTML}
     */
    public String save() {
        if (this.selectedEntity != null) {
            this.getSelectedEntity().setPasswort("passwort+");
            this.save(this.getSelectedEntity());
        } else {
            this.logger.error("LOG.USER.ERROR.SAVE.FAILED");
        }
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
    public DataModel<User> entityListForGruppenerstellung(@Nonnull final User loggedUser) {
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
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected Class<User> getRepositoryClass() {
        return User.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected String getQueryCommand() {
        return User.NAMED_QUERY_QUERY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected String getSelect() {
        return User.NAMED_QUERY_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<User> uncheckedSolver(@Nonnull final Object var) {
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
    public void updateGroupMemberSet(@CheckForNull final Set<User> groupmembersSet) {
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

}
