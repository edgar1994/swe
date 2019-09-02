package de.hsb.app.controller;

import de.hsb.app.model.Aufgabe;
import de.hsb.app.repository.AbstractCrudRepository;

import javax.annotation.Nonnull;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * Controller fuer {@link Aufgabe}
 */
@ManagedBean(name = "aufgabeController")
@SessionScoped
public class AufgabeController extends AbstractCrudRepository<Aufgabe> {

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected Class<Aufgabe> getRepositoryClass() {
        return Aufgabe.class;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected String getQueryCommand() {
        return Aufgabe.NAMED_QUERY_QUERY;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected String getSelect() {
        return Aufgabe.NAMED_QUERY_NAME;
    }
}
