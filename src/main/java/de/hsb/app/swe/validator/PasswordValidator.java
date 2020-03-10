package de.hsb.app.swe.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;
import java.util.regex.Pattern;

/**
 * Passwort Validator
 */
@FacesValidator(value = "passwordValidator")
public class PasswordValidator extends AbstractValidator {
    /**
     * Liefert eine Error-Message zurueck wenn das Passwort nicht dem Pattern entspricht.
     *
     * @param context
     * @param component
     * @param value
     * @throws ValidatorException
     */
    @Override
    public void validate(final FacesContext context, final UIComponent component, final Object value) throws ValidatorException {
        final String password = String.valueOf(value);
        final Pattern pattern = Pattern.compile("(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$");
        final FacesMessage message;
        if (!pattern.matcher(password).matches()) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    this.messageService.getMessage("USER.VALIDATOR.PASSWORD.SUMMARY"),
                    this.messageService.getMessage("USER.VALIDATOR.PASSWORD.DETAIL.REGEX"));
            throw new ValidatorException(message);
        }
    }
}
