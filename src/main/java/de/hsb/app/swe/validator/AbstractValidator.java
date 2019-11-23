package de.hsb.app.swe.validator;

import de.hsb.app.swe.service.MessageService;

import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;

@FacesValidator
public abstract class AbstractValidator implements Validator {

    protected final MessageService messageService = new MessageService();

}
