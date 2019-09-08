package de.hsb.app.controller;

import de.hsb.app.enumeration.Rolle;
import de.hsb.app.model.Adresse;
import de.hsb.app.model.Gruppe;
import de.hsb.app.model.User;
import de.hsb.app.repository.AbstractCrudRepository;
import de.hsb.app.utils.GruppeUtils;
import de.hsb.app.utils.RedirectUtils;
import de.hsb.app.utils.UserUtils;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.persistence.Query;
import javax.transaction.*;
import java.util.Collections;
import java.util.List;

/**
 * Controller fuer {@link Gruppe}.
 */
@ManagedBean(name = "gruppeController")
@SessionScoped
public class GruppeController extends AbstractCrudRepository<Gruppe> {

    /**
     * Entfernt einen {@link User} aus der Mitglieder-Liste der ausgewaehlten {@link Gruppe}.
     * Redirected auf {@link RedirectUtils#NEUEGRUPPE_XHTML}
     *
     * @param zuEntfernenderUser {@link User}
     */
    @Nonnull
    public String entferneUser(@Nonnull final User zuEntfernenderUser) {
        this.selectedEntity.getMitglieder().removeIf(user -> !UserUtils.compareUserById(this.selectedEntity.getLeiter(),
                user) && UserUtils.compareUserById(user, zuEntfernenderUser));
        return RedirectUtils.NEUEGRUPPE_XHTML;
    }

    /**
     * Entfernt alle {@link User} und den {@link Gruppe}n-Leiter aus der {@link Gruppe}.
     */
    private void entferneAlleUser() {
        this.selectedEntity.getMitglieder().forEach(user -> user.getGruppen().removeIf(
                gruppe -> GruppeUtils.compareGruppeById(this.selectedEntity, gruppe)));
        this.selectedEntity.getMitglieder().removeAll(this.selectedEntity.getMitglieder());
        this.selectedEntity.setLeiter(null);
    }

    /**
     * Loescht eine {@link Gruppe} und redirected auf {@link RedirectUtils#GRUPPETABELLE_XHTML}.
     *
     * @return {@link RedirectUtils#GRUPPETABELLE_XHTML}
     */
    public String deleteGruppe() {
        try {
            this.utx.begin();
            this.selectedEntity = this.entityList.getRowData();
            for (final User user : this.selectedEntity.getMitglieder()) {
                this.selectedEntity.removeUser(user);
            }
            final User user = new User("999", "999", new Adresse("999", "999", "999"),
                    "999", "999", Rolle.USER, Collections.emptySet());
            this.selectedEntity.setLeiter(user);
            this.em.persist(user);

            this.selectedEntity = this.em.merge(this.selectedEntity);
            this.em.persist(this.selectedEntity);
            this.em.remove(this.selectedEntity);
            this.entityList.setWrappedData(this.em.createNamedQuery("SelectGruppe").getResultList());
            this.utx.commit();
        } catch (final NotSupportedException | SystemException | SecurityException | IllegalStateException |
                RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            this.logger.error("Löschen fehlgeschlagen -> ", e);
        }
        return RedirectUtils.GRUPPETABELLE_XHTML;
    }

    /**
     * Erstellt eine neue {@link Gruppe} setzt den uebergenen {@link User} loggedUser als Leiter und fuegt ihn als
     * Mitglied hinzu. Redirected auf {@link RedirectUtils#NEUEGRUPPE_XHTML}.
     *
     * @param eingeloggterUser {@link User}
     * @return {@link RedirectUtils#NEUEGRUPPE_XHTML}
     */
    @CheckForNull
    public String neu(@Nonnull final User eingeloggterUser) {
        try {
            this.utx.begin();
            this.selectedEntity = new Gruppe();
            this.selectedEntity.setLeiter(eingeloggterUser);
            this.selectedEntity.addUser(eingeloggterUser);
            this.utx.commit();
            return RedirectUtils.NEUEGRUPPE_XHTML;
        } catch (final NotSupportedException | SystemException | SecurityException | IllegalStateException |
                RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            this.logger.error("Speichern fehlgeschlagen -> ", e);
            return null;
        }
    }

    /**
     * Fuegt einen {@link User} der {@link Gruppe} hinzu. Redirected auf {@link RedirectUtils#NEUEGRUPPE_XHTML}.
     *
     * @param zuHinzuzufuegenderUser {@link User}
     * @return {@link RedirectUtils#NEUEGRUPPE_XHTML}
     */
    @Nonnull
    public String fuegeUserHinzu(@Nonnull final User zuHinzuzufuegenderUser) {
        this.selectedEntity.addUser(zuHinzuzufuegenderUser);
        return RedirectUtils.NEUEGRUPPE_XHTML;
    }

    /**
     * Speichert eine {@link Gruppe}. Redirected auf {@link RedirectUtils#GRUPPETABELLE_XHTML}
     *
     * @return {@link RedirectUtils#GRUPPETABELLE_XHTML}
     */
    @Nonnull
    public String speichern() {
        this.save(this.selectedEntity);
        return RedirectUtils.GRUPPETABELLE_XHTML;
    }

    /**
     * Findet alle {@link Gruppe}n in denen der uebergebene {@link User} Mitglied oder Gruppenleiter ist.
     *
     * @param user {@link User}
     * @return List<Gruppe>
     */
    @Nonnull
    public List<Gruppe> userAwareFindeAlleGruppen(@Nonnull final User user) {
        final Query query = this.em.createQuery("select gr from Gruppe gr join fetch gr.mitglieder m " +
                "where m.id = :userId or gr.leiter.id = :userId");
        query.setParameter("userId", user.getId());
        return query.getResultList();
    }


    /**
     * Erstellt anhand aller gefundenen {@link Gruppe}n das entsprechende {@link DataModel <Gruppe>}.
     *
     * @return DataModel<User>
     */
    public DataModel<Gruppe> entityListForGruppenerstellung(@Nonnull final User user) {
        final List<Gruppe> gruppeList = this.userAwareFindeAlleGruppen(user);
        if (!gruppeList.isEmpty()) {
            this.checkEntityList();
            this.entityList.setWrappedData(gruppeList);
        }
        return this.entityList;
    }

    /**
     * Prueft ob ein {@link User} bereits Mitglied der {@link Gruppe} ist.
     *
     * @param user {@link User}
     * @return boolean
     */
    public boolean istHinzugefuegt(@Nonnull final User user) {
        boolean hinzugefuegt = false;
        for (final User hinzugefuegteUser : this.selectedEntity.getMitglieder()) {
            hinzugefuegt |= UserUtils.compareUserById(hinzugefuegteUser, user);
        }
        return hinzugefuegt;
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
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected Class<Gruppe> getRepositoryClass() {
        return Gruppe.class;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected String getQueryCommand() {
        return Gruppe.NAMED_QUERY_QUERY;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected String getSelect() {
        return Gruppe.NAMED_QUERY_NAME;
    }

}
