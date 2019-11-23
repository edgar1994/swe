package de.hsb.app.swe.validator;

import de.hsb.app.swe.utils.StringUtils;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;

@FacesValidator(value = "streetnameValidator")
public class StreetnameValidator extends AbstractValidator {

    /**
     * Liefert eine Error-Message zurueck wenn der Strassenname leer oder zu kurz/lang ist.
     *
     * @param context   {@link FacesContext}
     * @param component {@link UIComponent}
     * @param value     {@link Object}
     * @throws ValidatorException{@link ValidatorException}
     */
    @Override
    public void validate(final FacesContext context, final UIComponent component, final Object value)
            throws ValidatorException {
        final String streetname = String.valueOf(value);
        final FacesMessage message;
        if (StringUtils.isEmptyOrNullOrBlank(streetname)) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    this.messageService.getMessage("USER.VALIDATOR.ADRESS.STREET.SUMMARY"),
                    this.messageService.getMessage("USER.VALIDATOR.ADRESS.STREET.DETAIL.NOTNULL"));
            throw new ValidatorException(message);
        } else if (streetname.length() < 3 || streetname.length() > 30) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    this.messageService.getMessage("USER.VALIDATOR.ADRESS.STREET.SUMMARY"),
                    this.messageService.getMessage("USER.VALIDATOR.ADRESS.STREET.DETAIL.LENGTH"));
            throw new ValidatorException(message);
        }
    }

}
