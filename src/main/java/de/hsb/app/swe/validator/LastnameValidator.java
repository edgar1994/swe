package de.hsb.app.swe.validator;

import de.hsb.app.swe.utils.StringUtils;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;

/**
 * Nachname Validator
 */
@FacesValidator(value = "lastnameValidator")
public class LastnameValidator extends AbstractValidator {

    /**
     * Liefert eine Error-Message zurueck wenn der nachname leer oder zu kurz/lang ist.
     *
     * @param context   {@link FacesContext}
     * @param component {@link UIComponent}
     * @param value     {@link Object}
     * @throws ValidatorException{@link ValidatorException}
     */
    @Override
    public void validate(final FacesContext context, final UIComponent component, final Object value)
            throws ValidatorException {
        final String lastname = String.valueOf(value);
        final FacesMessage message;
        if (StringUtils.isEmptyOrNullOrBlank(lastname)) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    this.messageService.getMessage("USER.VALIDATOR.LASTNAME.SUMMARY"),
                    this.messageService.getMessage("USER.VALIDATOR.LASTNAME.DETAIL.NOTNULL"));
            throw new ValidatorException(message);
        } else if (lastname.length() < 3 || lastname.length() > 30) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    this.messageService.getMessage("USER.VALIDATOR.LASTNAME.SUMMARY"),
                    this.messageService.getMessage("USER.VALIDATOR.LASTNAME.DETAIL.LENGTH",
                            lastname.length()));
            throw new ValidatorException(message);
        }
    }

}
