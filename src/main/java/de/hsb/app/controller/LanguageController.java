package de.hsb.app.controller;

import de.hsb.app.enumeration.Language;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Locale;

@ManagedBean(name = "languageHandler")
@SessionScoped
public class LanguageController {

    private String language = Language.DEUTSCH.getLanguage();

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void actionLanguage(final String language){
        FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale(language));
        this.language = language;
    }

}
