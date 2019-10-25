package de.hsb.app.swe.controller;

import de.hsb.app.swe.enumeration.Rolle;
import de.hsb.app.swe.model.Gruppe;
import de.hsb.app.swe.model.User;
import de.hsb.app.swe.repository.AbstractCrudRepository;
import de.hsb.app.swe.utils.AdressUtils;
import de.hsb.app.swe.utils.RedirectUtils;
import de.hsb.app.swe.utils.UserUtils;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.DataModel;
import javax.persistence.Query;
import java.util.*;
import java.util.stream.Collectors;

/**
 * UserController
 */
@ManagedBean(name = "userController")
@ApplicationScoped
public class UserController extends AbstractCrudRepository<User> {

    private Set<User> groupmembersSet;

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

    public void removeUser(final int userId) {
        Optional.of(this.em.find(User.class, userId)).ifPresent(this.groupmembersSet::remove);
    }

    public void resetGroupmembersSet(@CheckForNull final User loggedUser) {
        this.groupmembersSet = new HashSet<>();
        if (loggedUser != null) {
            this.groupmembersSet.add(loggedUser);
        }
    }

    /**
     * Liefert ein Array aller {@link Rolle}n zurueck.
     *
     * @return Rolle[]
     */
    public Rolle[] getRolleValues() {
        return Rolle.values();
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
     * Liefert den Vor- und Nachnamen im Format "Nachname, Vorname" zurueck.
     *
     * @param user {@link User}
     * @return "Nachname, Vorname"
     */
    public String formatedName(@CheckForNull final User user) {
        if (user != null) {
            return UserUtils.getNachnameVornameString(user);
        } else {
            return UserUtils.getNachnameVornameString(UserUtils.DUMMY_USER_KUNDE);
        }
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
     * Abspeichern eines neuen Kunden. Nach Erfolg wird auf {@link RedirectUtils#USER_TABELLE_XHTML} redirected.
     *
     * @return {@link RedirectUtils#USER_TABELLE_XHTML}
     */
    public String save() {
        if (this.selectedEntity != null) {
            this.getSelectedEntity().setPasswort("passwort+");
            this.save(this.getSelectedEntity());
        } else {
            this.logger.error("Selected Entity is not allowed to be null -> {0}", new NullPointerException());
        }
        return RedirectUtils.USER_TABELLE_XHTML;
    }

    /**
     * Findet alle {@link User} fuer die Gruppenerstellung.
     *
     * @return List<User> fuer die Gruppenerstellung
     * @deprecated Lambda erst ab 1.8 verwendbar
     */
    @Deprecated
    @Nonnull
    private List<User> findAllUserForGruppenerstellungTest() {
        return this.findAll().stream().filter(user -> user.getRolle().equals(Rolle.KUNDE) ||
                user.getRolle().equals(Rolle.MITARBEITER) || user.getRolle().equals(Rolle.ADMIN))
                .collect(Collectors.toList());
    }

    /**
     * Findet alle {@link User} fuer die Gruppenerstellung.
     *
     * @return List<User> fuer die Gruppenerstellung
     */
    private List<User> findAllUserForGruppenerstellung() {
        final Query query = this.em.createQuery("select u from  User u where u.rolle = :mitarbeiter or " +
                "u.rolle = :admin or u.rolle = :kunde");
        query.setParameter("mitarbeiter", Rolle.MITARBEITER);
        query.setParameter("admin", Rolle.ADMIN);
        query.setParameter("kunde", Rolle.KUNDE);
        return this.uncheckedSolver(query.getResultList());
    }

    /**
     * Erstellt anhand aller gefundenen {@link User} fuer die Gruppenerstellung das entsprechende
     * {@link DataModel<User>}. Der eingeloggte {@link User} wird nicht mit Aufgeführt, da dieser bereits in der
     * {@link Gruppe} als Leiter gesetzt ist. Er kann nicht entfernt werden.
     *
     * @param loggedUser eingeloggter {@link User}
     * @return DataModel<User>
     */
    public DataModel<User> entityListForGruppenerstellung(@Nonnull final User loggedUser) {
        final List<User> userList = this.findAllUserForGruppenerstellung();
        userList.removeIf(user -> UserUtils.compareUserById(loggedUser, user));
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

    public Set<User> getGroupmembersSet() {
        return this.groupmembersSet;
    }

    public void setGroupmembersSet(final Set<User> groupmembersSet) {
        this.groupmembersSet = groupmembersSet;
    }

}
