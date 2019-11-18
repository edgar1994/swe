package de.hsb.app.swe.validator;

import de.hsb.app.swe.model.User;
import de.hsb.app.swe.utils.StringUtils;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean(name = "usernameValidator")
@FacesValidator(value = "usernameValidator")
public class UsernameValidator implements Validator {

    @PersistenceContext
    protected EntityManager em;

    @Resource
    protected UserTransaction utx;

    /**
     * Liefert eine Error-Message zurueck wenn der Username leer oder bereits vorhanden ist.
     *
     * @param context   {@link FacesContext}
     * @param component {@link UIComponent}
     * @param value     {@link Object}
     * @throws ValidatorException{@link ValidatorException}
     */
    @Override
    public void validate(final FacesContext context, final UIComponent component, final Object value) throws ValidatorException {
        final List<User> userList = this.uncheckedSolver(this.em.createQuery(User.NAMED_QUERY_QUERY).getResultList());
        final List<String> usernames = userList.stream().map(User::getUsername).collect(Collectors.toList());
        final String username = String.valueOf(value);
        final String summary = "VALIDATOR.USERNAME.SUMMARY";
        final FacesMessage message;
        if (StringUtils.isEmptyOrNullOrBlank(username)) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary,
                    "VALIDATOR.USERNAME.DETAIL.EMPTY");
            throw new ValidatorException(message);
        } else if (username.length() < 5 || username.length() > 15) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary,
                    "VALIDATOR.USERNAME.DETAIL.TOSHORT");
            throw new ValidatorException(message);
        } else if (usernames.contains(username)) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary,
                    "VALIDATOR.USERNAME.DETAIL.EXISTS");
            throw new ValidatorException(message);
        }
    }

    /**
     * Soll die Warnung "Unchecked cast" loesen.
     *
     * @param var Object
     * @return List<T>
     */
    private List<User> uncheckedSolver(final Object var) {
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
