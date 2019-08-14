package de.hsb.app.view;

import javax.annotation.Nonnull;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "growlView")
@SessionScoped
public class GrowlView {

    private String summary;

    private String message;

    public void showAction(@Nonnull final String summary, @Nonnull final String message) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(summary, message));
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * Setzt die Message fuer die {@link GrowlView}.
     *
     * @param message {@link String}
     */
    public void textMessage(String message) {
        this.message = message;
    }

    /**
     * Setzt die Summary fuer die {@link GrowlView}.
     *
     * @param summary {@link String}
     */
    public void textSummary(String summary) {
        this.summary = summary;
    }
}
