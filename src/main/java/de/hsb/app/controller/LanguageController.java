package de.hsb.app.controller;

import de.hsb.app.enumeration.Language;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.util.Locale;

@ManagedBean(name = "languageHandler")
@SessionScoped
public class LanguageController {

    private String language = Language.DEUTSCH.getLanguage();

    /**
     * Getter für language
     *
     * @return String
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Setter für language
     *
     * @param language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Setzt die Sprache mit der eine Seite übersetzt werden soll.
     *
     * @param language {@link Language}
     * @return null
     */
    public String actionLanguage(final String language) {
        FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale(language));
        this.language = language;
        return null;
    }

}
