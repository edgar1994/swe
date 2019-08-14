package de.hsb.app.view;

import javax.annotation.Nonnull;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "growlView")
@SessionScoped
public class GrowlView {

    /**
     * Erstellt eine Message mit summary als Ueberschrift und message als Nachricht.
     *
     * @param summary {@link String}
     * @param message {@link String}
     */
    public void showAction(@Nonnull final String summary, @Nonnull final String message) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(summary, message));
    }

}
