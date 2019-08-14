package de.hsb.app.vallidator;


import de.hsb.app.utils.StringUtils;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

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
        // Hier koennte ein Validator fuer ExistingUsername eingesetzt werden, allerdings ist uns nicht bekannt wie.
    }

}
