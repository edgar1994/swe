package de.hsb.app.swe.controller;

import de.hsb.app.swe.model.User;
import de.hsb.app.swe.utils.RedirectUtils;
import de.hsb.app.swe.utils.UserUtils;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "indexController")
@SessionScoped
public class IndexController {

    /**
     * Switch to {@link RedirectUtils#PROJEKT_INDEX_XHTML};
     *
     * @return {@link RedirectUtils#PROJEKT_INDEX_XHTML}
     */
    public String switchToIndex(final User loggedUser) {
        if (!UserUtils.isAdmin(loggedUser)) {
            return RedirectUtils.PROJEKT_INDEX_XHTML;
        } else {
            return RedirectUtils.GRUPPE_TABELLE_XHTML;
        }
    }

}
