package de.hsb.app.swe.utils;

import de.hsb.app.swe.model.Gruppe;
import de.hsb.app.swe.model.Projekt;
import de.hsb.app.swe.model.Ticket;
import de.hsb.app.swe.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Utils-Klasse fuer {@link List}
 */
public class ListUtils {

    /**
     * Soll die Warnung "Unchecked cast" loesen.
     *
     * @param var Object
     * @return List<Gruppe>
     */
    public static List<Gruppe> uncheckedSolverGroup(final Object var) {
        final List<Gruppe> result = new ArrayList<>();
        if (var instanceof List) {
            for (int i = 0; i < ((List<?>) var).size(); i++) {
                final Object item = ((List<?>) var).get(i);
                if (item instanceof Gruppe) {
                    result.add((Gruppe) item);
                }
            }
        }
        return result;
    }

    /**
     * Soll die Warnung "Unchecked cast" loesen.
     *
     * @param var Object
     * @return List<Projekt>
     */
    public static List<Projekt> uncheckedSolverProjekt(final Object var) {
        final List<Projekt> result = new ArrayList<>();
        if (var instanceof List) {
            for (int i = 0; i < ((List<?>) var).size(); i++) {
                final Object item = ((List<?>) var).get(i);
                if (item instanceof Projekt) {
                    result.add((Projekt) item);
                }
            }
        }
        return result;
    }

    /**
     * Soll die Warnung "Unchecked cast" loesen.
     *
     * @param var Object
     * @return List<Ticket>
     */
    public static List<Ticket> uncheckedSolverTicket(final Object var) {
        final List<Ticket> result = new ArrayList<>();
        if (var instanceof List) {
            for (int i = 0; i < ((List<?>) var).size(); i++) {
                final Object item = ((List<?>) var).get(i);
                if (item instanceof Ticket) {
                    result.add((Ticket) item);
                }
            }
        }
        return result;
    }

    /**
     * Soll die Warnung "Unchecked cast" loesen.
     *
     * @param var Object
     * @return List<Projekt>
     */
    public static List<User> uncheckedSolverUser(final Object var) {
        final List<User> result = new ArrayList<>();
        if (var instanceof List) {
            for (int i = 0; i < ((List<?>) var).size(); i++) {
                final Object item = ((List<?>) var).get(i);
                if (item instanceof User) {
                    result.add((User) item);
                }
            }
        }
        return result;
    }
}
