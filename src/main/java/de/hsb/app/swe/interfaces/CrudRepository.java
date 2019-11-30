package de.hsb.app.swe.interfaces;

import java.util.List;
import java.util.Optional;

/**
 * CrudRepository
 */
public interface CrudRepository<T> {

    /**
     * Suche ein Element mit seiner ID.
     *
     * @param id int
     * @return {@link Optional<T>}
     */
    Optional<T> findById(final int id);

    /**
     * Finde alle {@link T}.
     *
     * @return {@link List<T>}
     */
    List<T> findAll();

    /**
     * Speichert {@link T}.
     *
     * @param entity {@link T}.
     * @return boolean
     */
    boolean save(final T entity);

    /**
     * Löscht {@link T} aus der Datenbank.
     *
     * @return boolean
     */
    boolean delete();

}
