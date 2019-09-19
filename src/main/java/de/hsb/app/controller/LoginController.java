package de.hsb.app.controller;

import de.hsb.app.enumeration.Rolle;
import de.hsb.app.model.Adresse;
import de.hsb.app.model.Gruppe;
import de.hsb.app.model.User;
import de.hsb.app.repository.AbstractCrudRepository;
import de.hsb.app.utils.RedirectUtils;
import de.hsb.app.utils.UserUtils;

import javax.annotation.Nonnull;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.ArrayList;
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
        List<User> userList = this.uncheckedSolver(query.getResultList());
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
        List<User> userList = this.uncheckedSolver(query.getResultList());
        if (userList.size() == 1) {
            this.user = userList.get(0);
        }
    }

    /**
     * Prueft ob der {@link User} die Berechtigung ({@link Rolle#MITARBEITER}) hat um eine neue {@link Gruppe} anzulegen.
     * Andernfalls wird auf {@link RedirectUtils#LOGIN_INDEX_XHTML} redirected;
     *
     * @param cse {@link ComponentSystemEvent}
     */
    public void userAwareNewGroup(ComponentSystemEvent cse) {
        FacesContext context = FacesContext.getCurrentInstance();
        this.checkLoggedIn(cse);
        switch (this.user.getRolle()) {
            case MITARBEITER:
                break;
            case ADMIN:
            case KUNDE:
                context.addMessage(null, new FacesMessage("Unerlaubt",
                        String.format("Sie duerfen keine Gruppe mit der Rolle %s anlegen!", this.user.getRolle())));
                context.getApplication().getNavigationHandler().
                        handleNavigation(context, null,
                                RedirectUtils.GRUPPE_TABELLE_XHTML);
                break;
            case USER:
                // Fixme Wird nicht angezeigt
                context.addMessage(null, new FacesMessage("Unerlaubt",
                        String.format("Sie duerfen keine Gruppe mit der Rolle %s anlegen!", this.user.getRolle())));
                context.getApplication().getNavigationHandler().
                        handleNavigation(context, null,
                                RedirectUtils.LOGIN_INDEX_XHTML);
                break;
            default:
                throw new IllegalArgumentException(String.format("Rolle %s exestiert nicht", this.user.getRolle()));
        }
    }

    /**
     * Prueft ob der {@link User} die Berechtigung ({@link Rolle#MITARBEITER}, {@link Rolle#ADMIN}, {@link Rolle#KUNDE})
     * hat um die Gruppentabelle einzusehen. Andernfalls wird auf {@link RedirectUtils#LOGIN_INDEX_XHTML} redirected;
     *
     * @param cse {@link ComponentSystemEvent}
     */
    public void userAwareGroupTable(ComponentSystemEvent cse) {
        FacesContext context = FacesContext.getCurrentInstance();
        this.checkLoggedIn(cse);
        switch (this.user.getRolle()) {
            case ADMIN:
            case KUNDE:
            case MITARBEITER:
                break;
            case USER:
                // Fixme Wird nicht angezeigt
                context.addMessage(null, new FacesMessage("Unerlaubt",
                        String.format("Sie duerfen keine Gruppe mit der Rolle %s anlegen!", this.user.getRolle())));
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
            // Fixme Wird nicht angezeigt
            context.addMessage(null, new FacesMessage("Nicht eingeloggt",
                    "Sie sind nicht eingeloggt. Bitte loggen Sie sich zuerst ein"));
            context.getApplication().getNavigationHandler().
                    handleNavigation(context, null,
                            RedirectUtils.LOGIN_XHTML);
        }
    }

    /**
     * Abspeichern eines neuen Kunden. Nach Erfolg wird auf {@link RedirectUtils#USER_TABELLE_XHTML} redirected.
     *
     * @return {@link RedirectUtils#USER_TABELLE_XHTML}
     */
    public String speichern() {
        this.save(this.getSelectedEntity());
        return RedirectUtils.USER_TABELLE_XHTML;
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
     * Checked ob der angemeldete {@link User} {@link Rolle#KUNDE} oder {@link Rolle#USER} hat.
     *
     * @param loggedUser eingeloggter {@link User}
     * @return boolean
     */
    public boolean isKundeOrUser(@Nonnull User loggedUser) {
        return UserUtils.isCustomer(loggedUser) || UserUtils.isUser(loggedUser);
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<User> uncheckedSolver(Object var) {
        List<User> result = new ArrayList<>();
        if (var instanceof List) {
            for (int i = 0; i < ((List<?>) var).size(); i++) {
                Object item = ((List<?>) var).get(i);
                if (item instanceof User) {
                    result.add((User) item);
                }
            }
        }
        return result;
    }

}
