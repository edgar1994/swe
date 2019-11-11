package de.hsb.app.swe.controller;

import de.hsb.app.swe.enumeration.Rolle;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 * Controller fuer {@link Rolle}.
 */
@ManagedBean(name = "rolleController")
@ApplicationScoped
public class RolleController {

    /**
     * Liefert die Rolle {@link Rolle#ADMIN} zurueck.
     *
     * @return {@link Rolle#ADMIN}
     */
    public Rolle getAdmin() {
        return Rolle.ADMIN;
    }

    /**
     * Liefert die Rolle {@link Rolle#MITARBEITER} zurueck.
     *
     * @return {@link Rolle#MITARBEITER}
     */
    public Rolle getEmployee() {
        return Rolle.MITARBEITER;
    }

    /**
     * Liefert die Rolle {@link Rolle#KUNDE} zurueck.
     *
     * @return {@link Rolle#KUNDE}
     */
    public Rolle getCustomer() {
        return Rolle.KUNDE;
    }

    /**
     * Liefert die Rolle {@link Rolle#USER} zurueck.
     *
     * @return {@link Rolle#USER}
     */
    public Rolle getUser() {
        return Rolle.USER;
    }

    /**
     * Liefert ein Array aller {@link Rolle}n zurueck.
     *
     * @return Rolle[]
     */
    public Rolle[] getRolleValues() {
        return Rolle.values();
    }

}
