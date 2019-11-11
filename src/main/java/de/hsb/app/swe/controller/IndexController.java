package de.hsb.app.swe.controller;

import de.hsb.app.swe.utils.RedirectUtils;

import javax.annotation.Nonnull;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "indexController")
@SessionScoped
public class IndexController {

    /**
     * Switch to Index.xhtml
     *
     * @return
     */
    @Nonnull
    public String switchToIndex() {
        return RedirectUtils.PROJEKT_INDEX_XHTML;
    }

}
