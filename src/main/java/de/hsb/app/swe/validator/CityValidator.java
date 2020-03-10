package de.hsb.app.swe.validator;

import de.hsb.app.swe.utils.StringUtils;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;

/**
 * Stadtname Validator
 */
@FacesValidator(value = "cityValidator")
public class CityValidator extends AbstractValidator {

    /**
     * Liefert eine Error-Message zurueck wenn der Stadtname leer oder zu kurz/lang ist.
     *
     * @param context   {@link FacesContext}
     * @param component {@link UIComponent}
     * @param value     {@link Object}
     * @throws ValidatorException{@link ValidatorException}
     */
    @Override
    public void validate(final FacesContext context, final UIComponent component, final Object value)
            throws ValidatorException {
        final String city = String.valueOf(value);
        final FacesMessage message;
        if (StringUtils.isEmptyOrNullOrBlank(city)) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    this.messageService.getMessage("USER.VALIDATOR.ADRESS.CITY.SUMMARY"),
                    this.messageService.getMessage("USER.VALIDATOR.ADRESS.CITY.DETAIL.NOTNULL"));
            throw new ValidatorException(message);
        } else if (city.length() < 3 || city.length() > 30) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    this.messageService.getMessage("USER.VALIDATOR.ADRESS.CITY.SUMMARY"),
                    this.messageService.getMessage("USER.VALIDATOR.ADRESS.CITY.DETAIL.LENGTH"));
            throw new ValidatorException(message);
        }
    }

}
