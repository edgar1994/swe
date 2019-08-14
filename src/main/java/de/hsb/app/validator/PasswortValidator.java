package de.hsb.app.validator;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.util.Collection;

@FacesValidator(value = "passwortValidator")
public class PasswortValidator implements Validator {

    private static final String SUMMARY = "Passwort ungültig";

    // Kann erweitert werden wenn benoetigt.
    private Collection<String> requiredCharacters = ImmutableList.of("+", "-", "_", ".");

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        final String passwort = String.valueOf(value);
        FacesMessage message;
        if (passwort.contains(" ")) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, SUMMARY,
                    "Das Passwort darf kein Leerzeichen enthalten");
            throw new ValidatorException(message);
        } else if (!requiredCharacters(passwort)) {
            final String details = String.format("Das Passwort muss mindestens einen der Folgenden zeichen" +
                    " enthalten:\n%s", requiredCharacters);
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, SUMMARY, details);
            throw new ValidatorException(message);
        } else if (passwort.length() < 5) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, SUMMARY,
                    "Das Passwort ist zu kurz!\n Das Passwort muss mindestens 5 Zeichen lang sein");
            throw new ValidatorException(message);
        }
    }

    /**
     * Prueft ob der String "value" die benoetigten Character aus requiredCharacters beinhaltet.
     *
     * @param value {@link String}
     * @return boolean
     */
    private boolean requiredCharacters(@Nonnull String value) {
        for (String requiredCharacter : requiredCharacters) {
            if (value.contains(requiredCharacter)) {
                return true;
            }
        }
        return false;
    }
}
