package de.hsb.app.validator;

import de.hsb.app.model.User;
import de.hsb.app.utils.StringUtils;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.util.List;

@ManagedBean(name = "usernameValidator")
@FacesValidator(value = "usernameValidator")
public class UsernameValidator implements Validator {

    /**
     * Liefert eine Error-Message zurueck wenn der Username leer oder bereits vorhanden ist.
     *
     * @param context   {@link FacesContext}
     * @param component {@link UIComponent}
     * @param value     {@link Object}
     * @throws ValidatorException{@link ValidatorException}
     */
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String username = String.valueOf(value);
        FacesMessage message;
        if (StringUtils.isEmptyOrNullOrBlank(username)) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ungültiger Username",
            "Der Username darf nicht leer sein oder aus Leerzeichen bestehen!");
            throw new ValidatorException(message);
        } else if (username.length() < 5 || username.length() > 15) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ungültiger Username",
                    "Der Username ist zu kurz!");
            throw new ValidatorException(message);
        }
    }

}
