package de.hsb.app.swe.controller;

import de.hsb.app.swe.service.MessageService;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 * Klasse um die Button-Title-Uebersetzung anzeigen zu können. Da Frontend-Seitig nicht moeglch.
 */
@ManagedBean(name = "buttonTitleController")
@ApplicationScoped
public class ButtonTitleController {

    protected final MessageService messageService = new MessageService();

    /**
     * Hole uebersetzte Message fuer
     *
     * @param message
     * @return
     */
    public String getButtonTitle(final String message) {
        return this.messageService.getMessage(message);
    }

}
