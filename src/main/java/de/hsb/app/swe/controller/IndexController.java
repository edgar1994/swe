package de.hsb.app.swe.controller;

import de.hsb.app.swe.utils.RedirectUtils;

import javax.annotation.Nonnull;
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
    @Nonnull
    public String switchToIndex() {
        return RedirectUtils.PROJEKT_INDEX_XHTML;
    }

}
