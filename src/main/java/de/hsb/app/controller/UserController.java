package de.hsb.app.controller;

import de.hsb.app.enumeration.Rolle;
import de.hsb.app.model.User;
import de.hsb.app.repository.AbstractCrudRepository;
import de.hsb.app.utils.AdressUtils;
import de.hsb.app.utils.RedirectUtils;
import org.primefaces.push.annotation.Singleton;

import javax.annotation.Nonnull;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * UserController
 */
@Singleton
@ManagedBean(name = "userController")
@SessionScoped
public class UserController extends AbstractCrudRepository<User> {

    /**
     * Sucht den zu bearbeitenden {@link User} raus und redirected auf {@link RedirectUtils#NEUERUSER_XHTML}
     *
     * @return {@link RedirectUtils#NEUERUSER_XHTML}
     */
    public String bearbeiten() {
        selectedEntity = entityList.getRowData();
        return RedirectUtils.NEUERUSER_XHTML;
    }

    /**
     * Loescht ein Element in der Liste.
     *
     * @return  {@link RedirectUtils#USERTABELLE_XHTML}.
     */
    public String loeschen(){
        delete();
        return RedirectUtils.USERTABELLE_XHTML;
    }

    /**
     * Legt einen neuen Kunden an und leitet auf {@link RedirectUtils#USERTABELLE_XHTML}.
     *
     * @return {@link RedirectUtils#USERTABELLE_XHTML}
     */
    public String neu() {
        setSelectedEntity(new User());
        return RedirectUtils.NEUERUSER_XHTML;
    }

    /**
     * Liefert ein Array aller {@link Rolle}n zurueck.
     *
     * @return Rolle[]
     */
    public static Rolle[] getRolleValues() {
        return Rolle.values();
    }

    /**
     * Formatiert die Adresse nach folgenden Format: Straße Hausnummer, Stadt, Postleitzahl.
     *
     * @param user {@link User}
     * @return Straße Hausnummer, Stadt, Postleitzahl
     */
    public static String formatedAdresse(User user) {
        return AdressUtils.formatAdresse(user.getAdresse());
    }

    /**
     * Abspeichern eines neuen Kunden. Nach Erfolg wird auf {@link RedirectUtils#USERTABELLE_XHTML} redirected.
     *
     * @return {@link RedirectUtils#USERTABELLE_XHTML}
     */
    public String speichern() {
        save(getSelectedEntity());
        return RedirectUtils.USERTABELLE_XHTML;
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
    protected String getClassName() {
        return "User";
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected String getSelect() {
        return "SelectUser";
    }
}
