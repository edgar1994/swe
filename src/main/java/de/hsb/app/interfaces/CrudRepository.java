package de.hsb.app.interfaces;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
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
    @CheckForNull
    T findById(int id);

    /**
     * Finde alle {@link T}.
     *
     * @return {@link List<T>}
     */
    @Nonnull
    List<T> findAll();

    /**
     * Speichert {@link T}.
     *
     * @param entity {@link T}.
     * @return boolean
     */
    boolean save(@Nonnull T entity);

    /**
     * Löscht {@link T} aus der Datenbank.
     *
     * @return boolean
     */
    boolean delete();

}
