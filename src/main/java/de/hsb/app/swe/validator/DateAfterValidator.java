package de.hsb.app.swe.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Datums Validator
 */
@FacesValidator(value = "dateAfterValidator")
public class DateAfterValidator extends AbstractValidator {
    /**
     * Liefert eine Error-Message zurueck wenn das Datum leer ist oder in der Vergangenheit liegt.
     *
     * @param context
     * @param component
     * @param value
     * @throws ValidatorException
     */
    @Override
    public void validate(final FacesContext context, final UIComponent component, final Object value) throws ValidatorException {
        final Date date = (Date) value;
        final Date now = Date.from(Instant.now().minus(1L, ChronoUnit.DAYS));
        final FacesMessage message;
        if (date == null) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    this.messageService.getMessage("DATE.VALIDATOR.MESSAGE.SUMMARY"),
                    this.messageService.getMessage("DATE.VALIDATOR.MESSAGE.DETAIL.NULL"));
            throw new ValidatorException(message);
        } else if (date.before(now)) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    this.messageService.getMessage("DATE.VALIDATOR.MESSAGE.SUMMARY"),
                    this.messageService.getMessage("DATE.VALIDATOR.MESSAGE.DETAIL.INPAST"));
            throw new ValidatorException(message);
        }
    }
}
