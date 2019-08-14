package de.hsb.app.controller;

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
     * Loescht ein Element in der Liste.
     *
     * @return  {@link RedirectUtils#USERTABELLE_XHTML}.
     */
    public String loeschen(){
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
     * Formatiert die Adresse nach folgenden Format: Straße Hausnummer, Stadt, Postleitzahl.
     *
     * @param user {@link User}
     * @return Straße Hausnummer, Stadt, Postleitzahl
     */
    public String formatedAdresse(User user) {
        return AdressUtils.formatAdresse(user.getAdresse());
    }

    /**
     * Abspeichern eines neuen Kunden. Nach Erfolg wird auf {@link RedirectUtils#USERTABELLE_XHTML} redirected.
     *
     * @return {@link RedirectUtils#USERTABELLE_XHTML}
     */
    public String speichern() {
        this.save(this.getSelectedEntity());
        return RedirectUtils.USERTABELLE_XHTML;
    }

    /**
     * Bricht den aktuellen Vorgang ab und leitet zurueck auf {@link RedirectUtils#USERTABELLE_XHTML}.
     *
     * @return {@link RedirectUtils#USERTABELLE_XHTML}
     */
    public String abbrechen() {
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
