package de.hsb.app.swe.controller;

import de.hsb.app.swe.model.User;
import de.hsb.app.swe.service.MessageService;
import de.hsb.app.swe.utils.RedirectUtils;
import de.hsb.app.swe.utils.UserUtils;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * Controller für die Index-Seite
 */
@ManagedBean(name = "indexController")
@SessionScoped
public class IndexController {

    protected final MessageService messageService = new MessageService();

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

    /**
     * Liefert den Header fuer die Index-Seite.
     *
     * @param loggedUser eingelggter {@link User}
     * @return Header
     */
    public String indexHeader(final User loggedUser) {
        switch (loggedUser.getRolle()) {
            case USER:
                return this.messageService.getMessage("INDEX.HEADER.USER");
            case MITARBEITER:
                return this.messageService.getMessage("INDEX.HEADER.EMPLOYEE");
            case KUNDE:
                return this.messageService.getMessage("INDEX.HEADER.CUSTOMER");
            case ADMIN:
            default:
                return "";
        }
    }

    /**
     * Liefert den Header fuer die Index-Seite.
     *
     * @param loggedUser eingelggter {@link User}
     * @return Header
     */
    public String userInformation(final User loggedUser) {
        switch (loggedUser.getRolle()) {
            case USER:
                return this.messageService.getMessage("INDEX.INFORMATION.USER");
            case MITARBEITER:
            case KUNDE:
            case ADMIN:
            default:
                return "";
        }
    }

}
