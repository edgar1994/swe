package de.hsb.app.controller;

import de.hsb.app.model.Gruppe;
import de.hsb.app.model.User;
import de.hsb.app.repository.AbstractCrudRepository;
import de.hsb.app.utils.RedirectUtils;
import de.hsb.app.utils.UserUtils;

import javax.annotation.Nonnull;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.transaction.*;

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
    public String entferneUser(@Nonnull final User zuEntfernenderUser) {
        this.selectedEntity.getMitglieder().removeIf(user -> !UserUtils.compareUserById(this.selectedEntity.getLeiter(),
                user) && UserUtils.compareUserById(user, zuEntfernenderUser));
        return RedirectUtils.NEUEGRUPPE_XHTML;
    }

    /**
     * Erstellt eine neue {@link Gruppe} setzt den uebergenen {@link User} loggedUser als Leiter und fuegt ihn als
     * Mitglied hinzu. Redirected auf {@link RedirectUtils#NEUEGRUPPE_XHTML}.
     *
     * @param eingeloggterUser {@link User}
     * @return {@link RedirectUtils#NEUEGRUPPE_XHTML}
     */
    @Nonnull
    public String neu(@Nonnull final User eingeloggterUser) {
        this.selectedEntity = new Gruppe();
        this.selectedEntity.setLeiter(eingeloggterUser);
        this.selectedEntity.addUser(eingeloggterUser);
        return RedirectUtils.NEUEGRUPPE_XHTML;
    }

    /**
     * Fuegt einen {@link User} der {@link Gruppe} hinzu. Redirected auf {@link RedirectUtils#NEUEGRUPPE_XHTML}.
     *
     * @param zuHinzuzufuegenderUser {@link User}
     * @return {@link RedirectUtils#NEUEGRUPPE_XHTML}
     */
    public String fuegeUserHinzu(@Nonnull final User zuHinzuzufuegenderUser) {
        this.selectedEntity.addUser(zuHinzuzufuegenderUser);
        return RedirectUtils.NEUEGRUPPE_XHTML;
    }

    /**
     * Speichert eine {@link Gruppe}. Redirected auf {@link RedirectUtils#GRUPPETABELLE_XHTML}
     *
     * @return {@link RedirectUtils#GRUPPETABELLE_XHTML}
     */
    public String speichern() {
        try {
            this.utx.begin();
            this.selectedEntity = this.em.merge(this.selectedEntity);
            this.em.persist(this.selectedEntity);
            this.utx.commit();
        } catch (final NotSupportedException | SystemException | SecurityException | IllegalStateException |
                RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            this.logger.error("Speichern fehlgeschlagen -> ", e);
        }
        return RedirectUtils.GRUPPETABELLE_XHTML;
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
