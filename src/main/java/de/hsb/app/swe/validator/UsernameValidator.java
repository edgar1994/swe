package de.hsb.app.swe.validator;

import de.hsb.app.swe.utils.StringUtils;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

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
    public void validate(final FacesContext context, final UIComponent component, final Object value) throws ValidatorException {
        final String username = String.valueOf(value);
        final FacesMessage message;
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
