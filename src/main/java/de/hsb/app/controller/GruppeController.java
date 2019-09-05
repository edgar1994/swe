package de.hsb.app.controller;

import de.hsb.app.model.Gruppe;
import de.hsb.app.model.User;
import de.hsb.app.repository.AbstractCrudRepository;
import de.hsb.app.utils.RedirectUtils;
import de.hsb.app.utils.UserUtils;

import javax.annotation.Nonnull;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.persistence.Query;
import javax.transaction.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controller fuer {@link Gruppe}.
 */
@ManagedBean(name = "gruppeController")
@SessionScoped
public class GruppeController extends AbstractCrudRepository<Gruppe> {

    private List<User> zuHinzufuegendeUser;

    /**
     * Fuegt einen {@link User} der {@link List}e "zuHinzufuegendeUser" hinzu, wenn dieser User nicht bereits in der
     * {@link List} vorhanden ist.
     *
     * @param userToAdd {@link User}
     */
    public void fuegeUserHinzu(@Nonnull final User userToAdd) {
        boolean add = true;
        for (final User user : this.zuHinzufuegendeUser) {
            add &= !UserUtils.compareUserById(user, userToAdd);
        }
        if (add) {
            this.zuHinzufuegendeUser.add(userToAdd);
        }
    }

    /**
     * Entfernt einen {@link User} der {@link List}e "zuHinzufuegendeUser" hinzu, wenn dieser User nicht bereits in der
     * {@link List} vorhanden ist.
     *
     * @param userToDelete {@link User}
     */
    public void entferneUser(@Nonnull final User userToDelete) {
        this.zuHinzufuegendeUser.removeIf(user -> this.zuHinzufuegendeUser != null &&
                !this.zuHinzufuegendeUser.isEmpty() && UserUtils.compareUserById(user, userToDelete));
    }

    /**
     * Liefert einen {@link String} zureuck der "Hinzufuegen" oder "Enternen" entsprechnd der message.properties.
     *
     * @return NEUEGRUPPE.REMOVE || NEUEGRUPPE.ADD
     */
    public boolean istHinzugefuegt(@Nonnull final User userToCheck) {
        for (final User user : this.zuHinzufuegendeUser) {
            if (UserUtils.compareUserById(user, userToCheck)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Bricht den aktuellen Vorgang ab und redirected auf {@link RedirectUtils#GRUPPETABELLE_XHTML}.
     *
     * @return {@link RedirectUtils#GRUPPETABELLE_XHTML}
     */
    @Nonnull
    public String abbrechen() {
        return RedirectUtils.GRUPPETABELLE_XHTML;
    }

    /**
     * Setzt die {@link Gruppe}n-Liste nach Sichtbarkeit, des uebergeben {@link User}s.
     * Sollte bevorzugterweise der eingeloggte User sein.
     *
     * @param loggedUser {@link User}
     * @return DataModel<Gruppe>
     */
    @Nonnull
    public DataModel<Gruppe> userAwareEntityList(@Nonnull final User loggedUser) {
        final List<Gruppe> gruppeList = this.findAll();
        if (!gruppeList.isEmpty()) {
            this.entityList.setWrappedData(gruppeList);
        }
        return this.entityList;
    }

    /**
     * Sucht alle Gruppen heraus die für den Benutzer sichbar seien duerfen.
     *
     * @param loggedUser {@link User}
     * @return List<Gruppe>
     */
    private List<Gruppe> userAwareCreateDefaultGruppenListe(@Nonnull final User loggedUser) {
        final List<Gruppe> gruppeList;

        switch (loggedUser.getRolle()) {
            case USER:
            case MITARBEITER:
            case KUNDE:
                final Query query = this.em.createQuery("select gr from  Gruppe gr join gr.mitglieder m where m.id = :userId");
                query.setParameter("userId", loggedUser.getId());
                gruppeList = query.getResultList();
                break;
            case ADMIN:
                gruppeList = this.findAll();
                break;
            default:
                throw new IllegalArgumentException("Rolle Exestiert nicht.");
        }

        return gruppeList;
    }

    /**
     * Sucht die zu bearbeitende {@link Gruppe} raus und redirected auf {@link RedirectUtils#NEUEGRUPPE_XHTML}
     *
     * @return {@link RedirectUtils#NEUEGRUPPE_XHTML}
     */
    @Nonnull
    public String bearbeiten() {
        this.selectedEntity = this.entityList.getRowData();
        return RedirectUtils.NEUEGRUPPE_XHTML;
    }

    /**
     * Abspeichern einer neuen {@link Gruppe}. Nach Erfolg wird auf {@link RedirectUtils#GRUPPETABELLE_XHTML} redirected.
     *
     * @return {@link RedirectUtils#GRUPPETABELLE_XHTML}
     */
    @Nonnull
    public String speichern(@Nonnull User user) {
        this.getSelectedEntity().setMitglieder(this.zuHinzufuegendeUser);
        this.getSelectedEntity().setErstellungsdatum(Date.from(Instant.now()));
        switch (user.getRolle()) {
            case ADMIN:
            case MITARBEITER:
                this.getSelectedEntity().setLeiter(user);
                break;
            case KUNDE:
            case USER:
            default:
                throw new IllegalArgumentException(String.format("User mit der Rolle '%s' darf keine Gruppe erstellen.",
                        user.getRolle()));
        }
        try {
            this.utx.begin();
//            this.selectedEntity = this.em.merge(this.selectedEntity);
//            this.zuHinzufuegendeUser = this.em.merge(this.zuHinzufuegendeUser.toArray());
            user = this.em.merge(user);
//            this.em.persist(this.selectedEntity);
//            this.em.persist(this.zuHinzufuegendeUser);
            this.em.persist(user);
//            this.entityList.setWrappedData(this.em.createNamedQuery(this.getQueryCommand()).getResultList());
            this.utx.commit();
        } catch (final NotSupportedException | SystemException | SecurityException | IllegalStateException | RollbackException
                | HeuristicMixedException | HeuristicRollbackException e) {
            e.printStackTrace();
        }
        return RedirectUtils.GRUPPETABELLE_XHTML;
    }

    /**
     * Loescht ein Element in der Liste.
     *
     * @return {@link RedirectUtils#GRUPPETABELLE_XHTML}.
     */
    @Nonnull
    public String loeschen() {
        try {
            this.selectedEntity = this.entityList.getRowData();
            this.utx.begin();
            this.selectedEntity = this.em.merge(this.selectedEntity);
            this.em.detach(this.selectedEntity.getMitglieder());
            this.em.detach(this.selectedEntity.getLeiter());
            this.em.remove(this.selectedEntity);
            this.entityList.setWrappedData(this.em.createNamedQuery(this.getSelect()).getResultList());
            this.utx.commit();
        } catch (final NotSupportedException | SystemException | SecurityException | IllegalStateException |
                RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            this.logger.error("Löschen fehlgeschlagen -> ", e);
        }
        return RedirectUtils.GRUPPETABELLE_XHTML;
    }

    /**
     * Legt ein neues Projekt an und redirected auf {@link RedirectUtils#NEUEGRUPPE_XHTML}.
     *
     * @param user {@link User}
     * @return {@link RedirectUtils#NEUEGRUPPE_XHTML}
     */
    @Nonnull
    public String neu(@Nonnull final User user) {
        this.setSelectedEntity(new Gruppe());
        this.zuHinzufuegendeUser = new ArrayList<>();
        this.zuHinzufuegendeUser.add(user);
        return RedirectUtils.NEUEGRUPPE_XHTML;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected Class<Gruppe> getRepositoryClass() {
        return Gruppe.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected String getQueryCommand() {
        return Gruppe.NAMED_QUERY_QUERY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected String getSelect() {
        return Gruppe.NAMED_QUERY_NAME;
    }

    public List<User> getZuHinzufuegendeUser() {
        return this.zuHinzufuegendeUser;
    }

    public void setZuHinzufuegendeUser(final List<User> zuHinzufuegendeUser) {
        this.zuHinzufuegendeUser = zuHinzufuegendeUser;
    }

}
