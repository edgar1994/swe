package de.hsb.app.swe.controller;

import de.hsb.app.swe.enumeration.Rolle;
import de.hsb.app.swe.model.User;
import de.hsb.app.swe.repository.AbstractCrudRepository;
import de.hsb.app.swe.utils.RedirectUtils;
import de.hsb.app.swe.utils.StringUtils;
import de.hsb.app.swe.utils.UserUtils;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.ArrayList;
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

    private String changeFirstPassword;

    private String changeSecPassword;

    /**
     * Logged einen {@link User} aus.
     *
     * @return {@link RedirectUtils#LOGIN_XHTML}
     */
    public String logout() {
        this.user = null;
        this.passwort = "";
        this.username = "";
        FacesContext.getCurrentInstance()
                .getExternalContext().invalidateSession();
        return RedirectUtils.LOGIN_XHTML;
    }

    /**
     * Logged einen {@link User} ein.
     *
     * @return {@link RedirectUtils}
     */
    public String login() {
        final FacesContext context = FacesContext.getCurrentInstance();
        final Query query = this.em.createQuery("select u from User u " +
                "where u.username = :username and u.passwort = :passwort ");
        query.setParameter("username", this.username);
        query.setParameter("passwort", this.passwort);
        final List<User> userList = this.uncheckedSolver(query.getResultList());
        if (userList.size() == 1) {
            this.user = userList.get(0);
            if (this.user.isFirstLogin()) {
                return RedirectUtils.CHANGE_PASSWORD_XHTML;
            } else {
                return RedirectUtils.LOGIN_INDEX_XHTML;
            }
        } else {
            context.addMessage(null, new FacesMessage(
                    this.messageService.getMessage("LOGIN.MESSAGE.ERROR.SUMMARY"),
                    this.messageService.getMessage("LOGIN.MESSAGE.ERROR.DETAIL")));
            this.user = null;
            this.logger.error("LOG.LOGIN.MORETHANONE", this.username, this.passwort);
            return null;
        }
    }

    public void updateCurrentUser() {
        final Query query = this.em.createQuery("select u from User u " +
                "where u.username = :username and u.passwort = :passwort ");
        query.setParameter("username", this.username);
        query.setParameter("passwort", this.passwort);
        final List<User> userList = this.uncheckedSolver(query.getResultList());
        if (userList.size() == 1) {
            this.user = userList.get(0);
        } else {
            this.user = null;
            this.logger.error("LOG.LOGIN.MORETHANONE", this.username, this.passwort);
        }
    }

    /**
     * Prueft ob der {@link User} die Berechtigung ({@link Rolle#MITARBEITER}, {@link Rolle#ADMIN}, {@link Rolle#KUNDE})
     * hat um die Gruppentabelle einzusehen. Andernfalls wird auf {@link RedirectUtils#LOGIN_INDEX_XHTML} redirected;
     *
     * @param cse {@link ComponentSystemEvent}
     */
    public void userAwareGroupTable(final ComponentSystemEvent cse) {
        final FacesContext context = FacesContext.getCurrentInstance();
        this.checkLoggedIn(cse);
        if (this.user != null) {
            switch (this.user.getRolle()) {
                case ADMIN:
                case KUNDE:
                case MITARBEITER:
                    break;
                case USER:
                    context.getApplication().getNavigationHandler().
                            handleNavigation(context, null,
                                    RedirectUtils.LOGIN_INDEX_XHTML);
                    break;
                default:
                    throw new IllegalArgumentException(this.messageService.getMessage(
                            "EXCEPTION.ILLEGALARGUMENT.LOGIN", this.user.getRolle()));
            }
        }
    }

    /**
     * Prueft ob der {@link User} die Berechtigung ({@link Rolle#MITARBEITER}, {@link Rolle#ADMIN}, {@link Rolle#KUNDE})
     * hat um die Projekttabelle einzusehen. Andernfalls wird auf {@link RedirectUtils#LOGIN_INDEX_XHTML} redirected;
     *
     * @param cse {@link ComponentSystemEvent}
     */
    public void userAwareProjectTable(final ComponentSystemEvent cse) {
        final FacesContext context = FacesContext.getCurrentInstance();
        this.checkLoggedIn(cse);
        if (this.user != null) {
            switch (this.user.getRolle()) {
                case ADMIN:
                case KUNDE:
                case MITARBEITER:
                    break;
                case USER:
                    context.getApplication().getNavigationHandler().
                            handleNavigation(context, null,
                                    RedirectUtils.LOGIN_INDEX_XHTML);
                    break;
                default:
                    throw new IllegalArgumentException(this.messageService.getMessage(
                            "EXCEPTION.ILLEGALARGUMENT.LOGIN", this.user.getRolle()));
            }
        }
    }

    /**
     * Aendert das Passwort eines {@link User}s und setzt das "Erste Anmeldung"-Flag auf false.
     * Nach Erfolg wird auf {@link RedirectUtils#LOGIN_INDEX_XHTML}.
     *
     * @return {@link RedirectUtils#LOGIN_INDEX_XHTML}
     */
    public String changePassword() {
        if (this.passwordEqualsInSignUp(this.changeFirstPassword, this.changeSecPassword)) {
            this.user.setPasswort(this.passwort);
            this.user.setFirstLogin(false);
            this.save(this.user);
            return RedirectUtils.PROJEKT_INDEX_XHTML;
        }

        return null;
    }

    /**
     * Prueft ob zwei uebergebene Passwoerter gleich sind.
     *
     * @param first Erstes Passwort
     * @param sec   Zweites Passwort
     * @return boolean
     */
    public boolean passwordEqualsInSignUp(final String first, final String sec) {
        final FacesContext context = FacesContext.getCurrentInstance();
        if (StringUtils.isEmptyOrNullOrBlank(first)) {
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            this.messageService.getMessage("SIGNUP.VALIDATION.MESSAGES.SUMMARY"),
                            this.messageService.getMessage("SIGNUP.VALIDATION.MESSAGES.ERROR.FIRST.EMPTY")));
            return false;
        } else if (StringUtils.isEmptyOrNullOrBlank(sec)) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    this.messageService.getMessage("SIGNUP.VALIDATION.MESSAGES.SUMMARY"),
                    this.messageService.getMessage("SIGNUP.VALIDATION.MESSAGES.ERROR.SEC.EMPTY")));
            return false;
        } else if (!first.equals(sec)) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    this.messageService.getMessage("SIGNUP.VALIDATION.MESSAGES.SUMMARY"),
                    this.messageService.getMessage("SIGNUP.VALIDATION.MESSAGES.ERROR.NOT_EQUALS")));
            return false;
        }

        return true;
    }

    /**
     * Prueft ob der Username bereits existiert.
     *
     * @return boolean
     */
    public boolean doesUsernameExists() {
        final Query query = this.em.createQuery("select u.username from User u");
        final List<String> usernameList = StringUtils.uncheckedSolver(query.getResultList());
        final FacesContext context = FacesContext.getCurrentInstance();
        if (this.selectedEntity.getUsername() != null && usernameList.contains(this.selectedEntity.getUsername())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    this.messageService.getMessage("USER.VALIDATOR.USERNAME.SUMMARY"),
                    this.messageService.getMessage("USER.VALIDATOR.USERNAME.DETAIL.EXISTS")));
            return true;
        }
        return false;
    }


    /**
     * Prueft ob der {@link User} eingeloggt ist.
     *
     * @param cse {@link ComponentSystemEvent}
     */
    public void checkLoggedIn(final ComponentSystemEvent cse) {
        final FacesContext context = FacesContext.getCurrentInstance();
        if (this.user == null) {
            context.getApplication().getNavigationHandler().
                    handleNavigation(context, null,
                            RedirectUtils.LOGIN_XHTML);
        }
    }

    /**
     * Prueft ob es das erste mal des Logins ist und redirected auf {@link RedirectUtils#CHANGE_PASSWORD_XHTML}.
     *
     * @param cse {@link ComponentSystemEvent}
     */
    public void checkFirstLogin(final ComponentSystemEvent cse) {
        final FacesContext context = FacesContext.getCurrentInstance();
        if (this.user != null && this.user.isFirstLogin()) {
            context.getApplication().getNavigationHandler().
                    handleNavigation(context, null,
                            RedirectUtils.CHANGE_PASSWORD_XHTML);
        }
    }

    /**
     * Abspeichern eines neuen Kunden. Nach Erfolg wird auf {@link RedirectUtils#USER_TABELLE_XHTML} redirected.
     *
     * @return {@link RedirectUtils#USER_TABELLE_XHTML}
     */
    public String speichern() {
        if (this.passwordEqualsInSignUp(this.changeFirstPassword, this.changeSecPassword) &&
                !this.doesUsernameExists()) {
            this.selectedEntity.setPasswort(this.changeFirstPassword);
            this.save(this.getSelectedEntity());
            return RedirectUtils.USER_TABELLE_XHTML;
        }
        return RedirectUtils.REGISTRIEREN_XHTML;
    }

    /**
     * Prueft ob der angemeldete User admin ist.
     */
    public boolean adminRole() {
        if (this.user != null) {
            return Rolle.ADMIN.equals(this.user.getRolle());
        }
        return false;
    }

    /**
     * Checked ob der angemeldete {@link User} {@link Rolle#KUNDE} oder {@link Rolle#USER} hat.
     *
     * @param loggedUser eingeloggter {@link User}
     * @return boolean
     */
    public boolean isKundeOrUser(final User loggedUser) {
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

    public String getUsername() {
        return this.username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPasswort() {
        return this.passwort;
    }

    public void setPasswort(final String passwort) {
        this.passwort = passwort;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public String getChangeFirstPassword() {
        return this.changeFirstPassword;
    }

    public void setChangeFirstPassword(final String changeFirstPassword) {
        this.changeFirstPassword = changeFirstPassword;
    }

    public String getChangeSecPassword() {
        return this.changeSecPassword;
    }

    public void setChangeSecPassword(final String changeSecPassword) {
        this.changeSecPassword = changeSecPassword;
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

}
