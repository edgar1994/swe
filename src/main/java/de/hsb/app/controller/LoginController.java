package de.hsb.app.controller;

import de.hsb.app.enumeration.Rolle;
import de.hsb.app.model.Adresse;
import de.hsb.app.model.Gruppe;
import de.hsb.app.model.User;
import de.hsb.app.repository.AbstractCrudRepository;
import de.hsb.app.utils.RedirectUtils;

import javax.annotation.Nonnull;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;

/**
 * Login-Controller
 */
@ManagedBean(name = "loginController")
@SessionScoped
public class LoginController extends AbstractCrudRepository<User> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;

    private String passwort;

    private User user;

    /**
     * Logged einen {@link User} aus.
     *
     * @return {@link RedirectUtils#LOGIN_XHTML}
     */
    public String logout() {
        FacesContext.getCurrentInstance()
                .getExternalContext().invalidateSession();
        return RedirectUtils.LOGIN_XHTML;
    }

    /**
     * Erstellt daten beim Initialisieren.
     */
    // Fixme deaktiviert bis Postconstruct benötigt wird. (Feuert zwei mal)
    // @PostConstruct
    public void init() {
        Adresse adresse = new Adresse("Strasse 21", "99999", "Stadt");
        this.save(new User("Aron", "O'Connor", adresse, "test",
                "passwort+", Rolle.KUNDE, new HashSet<>()));
        this.save(new User("Edgar", "Grischenko", adresse, "edgar",
                "passwort+", Rolle.ADMIN, new HashSet<>()));
        this.save(new User("Mitarbeiter1", "Nachname1", adresse, "mitarbeiter1",
                "passwort+", Rolle.MITARBEITER, new HashSet<>()));
    }

    /**
     * Logged einen {@link User} ein.
     *
     * @return {@link RedirectUtils}
     */
    public String login() {
        Query query = this.em.createQuery("select u from User u " +
                "where u.username = :username and u.passwort = :passwort ");
        query.setParameter("username", this.username);
        query.setParameter("passwort", this.passwort);
        List<User> userList = query.getResultList();
        if (userList.size() == 1) {
            this.user = userList.get(0);
            return RedirectUtils.LOGIN_INDEX_XHTML;
        } else {
            return null;
        }
    }

    public void updateCurrentUser() {
        Query query = this.em.createQuery("select u from User u " +
                "where u.username = :username and u.passwort = :passwort ");
        query.setParameter("username", this.username);
        query.setParameter("passwort", this.passwort);
        List<User> userList = query.getResultList();
        if (userList.size() == 1) {
            this.user = userList.get(0);
        }
    }

    /**
     * Prueft ob der {@link User} die Berechtigung ({@link Rolle#MITARBEITER}) hat um eine neue {@link Gruppe}.
     *
     * @param cse {@link ComponentSystemEvent}
     */
    public void userAwareNewGroup(ComponentSystemEvent cse) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (this.user == null) {
            context.getApplication().getNavigationHandler().
                    handleNavigation(context, null,
                            RedirectUtils.LOGIN_INDEX_XHTML);
        }
        switch (this.user.getRolle()) {
            case MITARBEITER:
                break;
            case ADMIN:
            case KUNDE:
            case USER:
                context.getApplication().getNavigationHandler().
                        handleNavigation(context, null,
                                RedirectUtils.LOGIN_INDEX_XHTML);
                break;
            default:
                throw new IllegalArgumentException(String.format("Rolle %s exestiert nicht", this.user.getRolle()));
        }

    }

    /**
     * Prueft ob der {@link User} eingeloggt ist.
     *
     * @param cse {@link ComponentSystemEvent}
     */
    public void checkLoggedIn(ComponentSystemEvent cse) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (this.user == null) {
            context.getApplication().getNavigationHandler().
                    handleNavigation(context, null,
                            RedirectUtils.LOGIN_XHTML);
        }
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
     * Prueft ob der angemeldete User admin ist.
     */
    public boolean adminRole() {
        switch (this.user.getRolle()) {
            case USER:
            case KUNDE:
            case MITARBEITER:
                return false;
            case ADMIN:
                return true;
            default:
                throw new IllegalArgumentException("Rolle Exestiert nicht.");
        }
    }

    /**
     * Legt einen Neuen {@link User} an und redirected auf {@link RedirectUtils#REGISTRIEREN_XHTML}
     *
     * @return {@link RedirectUtils#REGISTRIEREN_XHTML}
     */
    public String registrieren() {
        this.setSelectedEntity(new User());
        if (this.findAll().isEmpty()) {
            this.selectedEntity.setRolle(Rolle.ADMIN);
        }
        return RedirectUtils.REGISTRIEREN_XHTML;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected Class<User> getRepositoryClass() {
        return User.class;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected String getQueryCommand() {
        return User.NAMED_QUERY_QUERY;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected String getSelect() {
        return User.NAMED_QUERY_NAME;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswort() {
        return this.passwort;
    }

    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
