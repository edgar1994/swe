package de.hsb.app.swe.validator;

import de.hsb.app.swe.utils.StringUtils;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;
import java.util.regex.Pattern;

@FacesValidator(value = "zipValidator")
public class ZipValidator extends AbstractValidator {

    /**
     * Liefert eine Error-Message zurueck wenn die Postleitzahl leer ist oder nicht zu dem Pattern "[0-9]{5}" passt.
     *
     * @param context   {@link FacesContext}
     * @param component {@link UIComponent}
     * @param value     {@link Object}
     * @throws ValidatorException{@link ValidatorException}
     */
    @Override
    public void validate(final FacesContext context, final UIComponent component, final Object value)
            throws ValidatorException {
        final String zip = String.valueOf(value);
        final Pattern pattern = Pattern.compile("[0-9]{5}");
        final FacesMessage message;
        if (StringUtils.isEmptyOrNullOrBlank(zip)) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    this.messageService.getMessage("USER.VALIDATOR.ZIP.CITY.SUMMARY"),
                    this.messageService.getMessage("USER.VALIDATOR.ZIP.CITY.DETAIL.NOTNULL"));
            throw new ValidatorException(message);
        } else if (!pattern.matcher(zip).matches()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    this.messageService.getMessage("USER.VALIDATOR.ZIP.CITY.SUMMARY"),
                    this.messageService.getMessage("USER.VALIDATOR.ZIP.CITY.DETAIL.REGEX"));
            throw new ValidatorException(message);
        }
    }

}
