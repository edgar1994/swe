package de.hsb.app.controller;

import de.hsb.app.enumeration.Rolle;
import de.hsb.app.model.User;
import de.hsb.app.repository.AbstractCrudRepository;
import de.hsb.app.utils.AdressUtils;
import de.hsb.app.utils.RedirectUtils;
import de.hsb.app.utils.UserUtils;
import org.primefaces.push.annotation.Singleton;

import javax.annotation.Nonnull;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserController
 */
@Singleton
@ManagedBean(name = "userController")
@SessionScoped
public class UserController extends AbstractCrudRepository<User> {

    /**
     * Liefert ein Array aller {@link Rolle}n zurueck.
     *
     * @return Rolle[]
     */
    public static Rolle[] getRolleValues() {
        return Rolle.values();
    }

    /**
     * Bricht den aktuellen Vorgang ab und leitet zurueck auf {@link RedirectUtils#USERTABELLE_XHTML}.
     *
     * @return {@link RedirectUtils#USERTABELLE_XHTML}
     */
    public static String abbrechen() {
        return RedirectUtils.USERTABELLE_XHTML;
    }

    /**
     * Formatiert die Adresse nach folgenden Format: Straße Hausnummer, Stadt, Postleitzahl.
     *
     * @param user {@link User}
     * @return Straße Hausnummer, Stadt, Postleitzahl
     */
    public static String formatedAdresse(@Nonnull final User user) {
        return AdressUtils.formatAdresse(user.getAdresse());
    }

    /**
     * Liefert den Vor- und Nachnamen im Format "Nachname, Vorname" zurueck.
     *
     * @param user {@link User}
     * @return "Nachname, Vorname"
     */
    public static String formatedName(@Nonnull final User user) {
        return UserUtils.getNachnameVornameString(user);
    }

    /**
     * Sucht den ersten {@link User} mit der {@link Rolle} "{@link Rolle#KUNDE}" und liefert diesen zurueck.
     * Gibt es keinen Kunden in der Liste wird {@link UserUtils#DUMMY_USER_KUNDE} zurueckgeliefert.
     *
     * @param userList {@link List<User>}
     * @return User mit {@link Rolle#KUNDE} || {@link UserUtils#DUMMY_USER_KUNDE}
     */
    public static User getKundeUser(@Nonnull final List<User> userList) {
        for (final User user : userList) {
            if (Rolle.KUNDE.equals(user.getRolle())) {
                return user;
            }
        }
        return UserUtils.DUMMY_USER_KUNDE;
    }

    /**
     * Sucht den zu bearbeitenden {@link User} raus und redirected auf {@link RedirectUtils#NEUERUSER_XHTML}
     *
     * @return {@link RedirectUtils#NEUERUSER_XHTML}
     */
    public String bearbeiten() {
        this.selectedEntity = this.entityList.getRowData();
        return RedirectUtils.NEUERUSER_XHTML;
    }

    /**
     * Loescht ein Element in der Liste.
     *
     * @return {@link RedirectUtils#USERTABELLE_XHTML}.
     */
    public String loeschen() {
        this.delete();
        return RedirectUtils.USERTABELLE_XHTML;
    }

    /**
     * Legt einen neuen Kunden an und leitet auf {@link RedirectUtils#USERTABELLE_XHTML}.
     *
     * @return {@link RedirectUtils#USERTABELLE_XHTML}
     */
    public String neu() {
        this.setSelectedEntity(new User());
        return RedirectUtils.NEUERUSER_XHTML;
    }

    /**
     * Abspeichern eines neuen Kunden. Nach Erfolg wird auf {@link RedirectUtils#USERTABELLE_XHTML} redirected.
     *
     * @return {@link RedirectUtils#USERTABELLE_XHTML}
     */
    public String speichern() {
        this.getSelectedEntity().setPasswort("passwort+");
        this.save(this.getSelectedEntity());
        return RedirectUtils.USERTABELLE_XHTML;
    }

    public List<User> findAllUserForGruppenerstellung() {
        return this.findAll().stream().filter(user -> user.getRolle().equals(Rolle.KUNDE) ||
                user.getRolle().equals(Rolle.MITARBEITER) || user.getRolle().equals(Rolle.ADMIN))
                .collect(Collectors.toList());
    }

    public DataModel<User> entityListForGruppenerstellung() {
        final List<User> userList = this.findAllUserForGruppenerstellung();
        if (!userList.isEmpty()) {
            if (this.entityList == null) {
                this.entityList = new ListDataModel<>();
            }
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
}
