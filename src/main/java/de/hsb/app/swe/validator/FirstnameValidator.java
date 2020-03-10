package de.hsb.app.swe.validator;

import de.hsb.app.swe.utils.StringUtils;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;

/**
 * Vorname Validator
 */
@FacesValidator(value = "firstnameValidator")
public class FirstnameValidator extends AbstractValidator {

    /**
     * Liefert eine Error-Message zurueck wenn der Vorname leer oder zu kurz/lang ist.
     *
     * @param context   {@link FacesContext}
     * @param component {@link UIComponent}
     * @param value     {@link Object}
     * @throws ValidatorException{@link ValidatorException}
     */
    @Override
    public void validate(final FacesContext context, final UIComponent component, final Object value)
            throws ValidatorException {
        final String firstname = String.valueOf(value);
        final FacesMessage message;
        if (StringUtils.isEmptyOrNullOrBlank(firstname)) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    this.messageService.getMessage("USER.VALIDATOR.FIRSTNAME.SUMMARY"),
                    this.messageService.getMessage("USER.VALIDATOR.FIRSTNAME.DETAIL.NOTNULL"));
            throw new ValidatorException(message);
        } else if (firstname.length() < 3 || firstname.length() > 30) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    this.messageService.getMessage("USER.VALIDATOR.FIRSTNAME.SUMMARY"),
                    this.messageService.getMessage("USER.VALIDATOR.FIRSTNAME.DETAIL.LENGTH",
                            firstname.length()));
            throw new ValidatorException(message);
        }
    }

}
