package de.hsb.app.repository;

import de.hsb.app.interfaces.CrudRepository;
import org.slf4j.Logger;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.*;
import java.util.List;

@SessionScoped
public abstract class AbstractCrudRepository<T> implements CrudRepository<T> {

    protected DataModel<T> entityList;

    protected T selectedEntity;

    @PersistenceContext
    protected EntityManager em;

    @Resource
    protected UserTransaction utx;

    @Resource
    protected Logger logger;

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public T findById(int id) {
        return em.find(this.getRepositoryClass(), id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public List<T> findAll() {
        return em.createQuery("SELECT a FROM ".concat(this.getClassName()).concat(" a "),
                this.getRepositoryClass()).getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean save(@Nonnull T entity) {
        try {
            utx.begin();
            entity = em.merge(entity);
            em.persist(entity);
            utx.commit();
            return true;
        } catch (NotSupportedException | SystemException | SecurityException | IllegalStateException |
                RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            logger.error("Speichern fehlgeschlagen -> ", e);
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete() {
        try {
            selectedEntity = entityList.getRowData();
            utx.begin();
            selectedEntity = em.merge(selectedEntity);
            em.remove(selectedEntity);
            entityList.setWrappedData(em.createNamedQuery(this.getSelect()).getResultList());
            utx.commit();
            return true;
        } catch (NotSupportedException | SystemException | SecurityException | IllegalStateException |
                RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            logger.error("Löschen fehlgeschlagen -> ", e);
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    protected abstract Class<T> getRepositoryClass();

    /**
     * {@inheritDoc}
     */
    @Nonnull
    protected abstract String getClassName();

    /**
     * Liefert ein {@link String} zurueck. Der {@link String} sollte die Form Select{@link T} haben.
     *
     * @return Select{@link T}
     */
    @Nonnull
    protected abstract String getSelect();

    /**
     * Getter fuer {@link DataModel<T>}.
     *
     * @return {@link DataModel<T>}
     */
    public DataModel<T> getEntityList() {
        this.entityList = new ListDataModel<>(this.findAll());
        return this.entityList;
    }

}
