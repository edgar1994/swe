package de.hsb.app.swe.controller;

import de.hsb.app.swe.enumeration.Rolle;
import de.hsb.app.swe.enumeration.Status;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean(name = "enumController")
@ApplicationScoped
public class EnumController {

    /**
     * Liefert ein Array aller {@link Rolle}n zurueck.
     *
     * @return Rolle[]
     */
    public Rolle[] getRolleValues() {
        return Rolle.values();
    }

    /**
     * Liefert ein Array aller {@link Status}-Werte zurueck.
     *
     * @return Status[]
     */
    public Status[] getStatusValues() {
        return Status.values();
    }

    /**
     * Liefert die Rolle {@link Rolle#ADMIN} zurueck.
     *
     * @return {@link Rolle#ADMIN}
     */
    public Rolle getRolleAdmin() {
        return Rolle.ADMIN;
    }

    /**
     * Liefert die Rolle {@link Rolle#MITARBEITER} zurueck.
     *
     * @return {@link Rolle#MITARBEITER}
     */
    public Rolle getRolleEmployee() {
        return Rolle.MITARBEITER;
    }

    /**
     * Liefert die Rolle {@link Rolle#KUNDE} zurueck.
     *
     * @return {@link Rolle#KUNDE}
     */
    public Rolle getRolleCustomer() {
        return Rolle.KUNDE;
    }

    /**
     * Liefert die Rolle {@link Rolle#USER} zurueck.
     *
     * @return {@link Rolle#USER}
     */
    public Rolle getRolleUser() {
        return Rolle.USER;
    }

}
