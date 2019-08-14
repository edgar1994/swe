package de.hsb.app.validator;

import com.google.common.collect.ImmutableList;
import de.hsb.app.utils.StringUtils;

import javax.annotation.Nonnull;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.util.Collection;

@FacesValidator(value = "usernameValidator")
public class UsernameValidator implements Validator {

    private static final String SUMMARY = "Ungültiger Username";

    // Diese Liste kann noch um vielen weiteren illegalen Charactern erweitert werden.
    private Collection<String> illegalCharacters = ImmutableList.of(" ", "+", "#", ",", "`", "´", "*", "~", ".", ";", ":",
            "-", "_", "!", "\"");

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String username = String.valueOf(value);
        FacesMessage message;
        if (StringUtils.isEmptyOrNullOrBlank(username)) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, SUMMARY,
                    "Der Username darf nicht leer sein!");
            throw new ValidatorException(message);
        } else if (username.length() < 5) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, SUMMARY,
                    "Der Username ist zu kurz!\n Der Username muss aus mindestens 5 bis 15 Zeichen bestehen");
            throw new ValidatorException(message);
        } else if (username.length() > 15) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, SUMMARY,
                    "Der Username ist zu lang!\n Der Username muss aus mindestens 5 bis 15 Zeichen bestehen");
            throw new ValidatorException(message);
        } else if (cointainsIlegalCharacters(username)) {
            String detail = String.format("Der Username darf folgende Character nicht besitzen:\n %s",
                    this.illegalCharacters);
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, SUMMARY, detail);
            throw new ValidatorException(message);
        }
    }

    /**
     * Prueft ob der String "value" verbotene Character aus illegalCharacters beinhaltet.
     *
     * @param value {@link String}
     * @return boolean
     */
    private boolean cointainsIlegalCharacters(@Nonnull String value) {
        boolean illegal = false;
        for (String illegalCharacter : this.illegalCharacters) {
            illegal |= value.contains(illegalCharacter);
        }
        return illegal;
    }

}
