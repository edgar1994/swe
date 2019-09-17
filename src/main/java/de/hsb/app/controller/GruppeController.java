package de.hsb.app.controller;

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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
     * @param userToDelete Zu loeschenden {@link User}
     */
    @Nonnull
    public String deleteUser(@Nonnull User userToDelete) {
        this.selectedEntity.getMitglieder().removeIf(user -> !UserUtils.compareUserById(this.selectedEntity.getLeiterId(),
                userToDelete) && UserUtils.compareUserById(user, userToDelete));
        return RedirectUtils.NEUEGRUPPE_XHTML;
    }

    /**
     * Finde die
     *
     * @param loggedUser Eingeloggter User
     * @param rowData    Gruppe aus der Uebersicht
     * @return Gruppe
     * @throws IllegalStateException Gruppe = null
     */
    @Nonnull
    private Gruppe userAwareFindGruppeToDeleteByLoggedUserAndRowData(@Nonnull User loggedUser, @Nonnull Gruppe rowData)
            throws IllegalStateException {
        // Fixme Workaround
        // Suche alle fuer den User sichtbaren Gruppen und pruefe sie mit der RowData
        List<Gruppe> gruppen = this.userAwareFindAllGruppen(loggedUser);
        if (!gruppen.isEmpty()) {
            for (Gruppe gruppe : gruppen) {
                // Haben die Gruppe und die RowData die gleiche ID und der eingellogte User ist der Gruppenleiter oder
                // ein Admin dann liefere die Gruppe zurueck.
                if (GruppeUtils.compareGruppeById(gruppe, rowData) &&
                        (UserUtils.compareUserById(gruppe.getLeiterId(), loggedUser) || UserUtils.isAdmin(loggedUser))) {
                    return gruppe;
                }
            }
        }
        throw new IllegalStateException("Die Ausgewaehlte Gruppe darf nicht null sein!");
    }

    /**
     * Loescht eine {@link Gruppe} und redirected auf {@link RedirectUtils#GRUPPETABELLE_XHTML}.
     *
     * @return {@link RedirectUtils#GRUPPETABELLE_XHTML}
     */
    @Nonnull
    public String deleteGruppe(@Nonnull User loggedUser) {
        try {
            this.utx.begin();
            this.selectedEntity = this.userAwareFindGruppeToDeleteByLoggedUserAndRowData(loggedUser,
                    this.entityList.getRowData());
            for (User user : this.selectedEntity.getMitglieder()) {
                this.selectedEntity.removeUser(user);
            }
            this.selectedEntity = this.em.merge(this.selectedEntity);
            this.em.remove(this.selectedEntity);
            this.em.flush();
            this.entityList.setWrappedData(this.userAwareFindAllGruppen(loggedUser));
            this.utx.commit();
        } catch (NotSupportedException | SystemException | SecurityException | IllegalStateException |
                RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            this.logger.error("Löschen fehlgeschlagen -> ", e);
        }
        return RedirectUtils.GRUPPETABELLE_XHTML;
    }

    /**
     * Erstellt eine neue {@link Gruppe} setzt den uebergenen {@link User} loggedUser als Leiter und fuegt ihn als
     * Mitglied hinzu. Redirected auf {@link RedirectUtils#NEUEGRUPPE_XHTML}.
     *
     * @param eingeloggterUser Eingeloggter {@link User}
     * @return {@link RedirectUtils#NEUEGRUPPE_XHTML}
     */
    @CheckForNull
    public String neu(@Nonnull User eingeloggterUser) {
        try {
            this.utx.begin();
            this.selectedEntity = new Gruppe();
            this.selectedEntity.setLeiterId(eingeloggterUser.getId());
            this.selectedEntity.addUser(eingeloggterUser);
            this.utx.commit();
            return RedirectUtils.NEUEGRUPPE_XHTML;
        } catch (NotSupportedException | SystemException | SecurityException | IllegalStateException |
                RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            this.logger.error("Speichern fehlgeschlagen -> ", e);
            return null;
        }
    }

    /**
     * Fuegt einen {@link User} der {@link Gruppe} hinzu. Redirected auf {@link RedirectUtils#NEUEGRUPPE_XHTML}.
     *
     * @param userToAdd hinzuzufügender {@link User}
     * @return {@link RedirectUtils#NEUEGRUPPE_XHTML}
     */
    @Nonnull
    public String fuegeUserHinzu(@Nonnull User userToAdd) {
        this.selectedEntity.addUser(userToAdd);
        return RedirectUtils.NEUEGRUPPE_XHTML;
    }

    /**
     * Speichert eine {@link Gruppe}. Redirected auf {@link RedirectUtils#GRUPPETABELLE_XHTML}
     *
     * @return {@link RedirectUtils#GRUPPETABELLE_XHTML}
     */
    @Nonnull
    public String save() {
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
    private List<Gruppe> userAwareFindAllGruppen(@Nonnull User user) {
        switch (user.getRolle()) {
            case KUNDE:
            case MITARBEITER:
                List<Gruppe> gruppen = new ArrayList<>();
                Query query = this.em.createQuery("select gr from Gruppe gr join fetch gr.mitglieder m " +
                        "where m.id = :userId");
                query.setParameter("userId", user.getId());
                Set<Integer> gruppenIds = new HashSet<>();
                // Fixme Unchecked cast
                for (Gruppe gruppe : (List<Gruppe>) query.getResultList()) {
                    gruppenIds.add(gruppe.getId());
                }
                for (int id : gruppenIds) {
                    gruppen.add(this.findById(id));
                }
                return gruppen;
            case ADMIN:
                return this.findAll();
            case USER:
            default:
                throw new IllegalArgumentException(String.format("Rolle %s darf keine Gruppen haben.", user.getRolle()));

        }
    }


    /**
     * Erstellt anhand aller gefundenen {@link Gruppe}n das entsprechende {@link DataModel <Gruppe>}.
     *
     * @return DataModel<User>
     */
    public DataModel<Gruppe> entityListForGruppenerstellung(@Nonnull User user) {
        List<Gruppe> gruppeList = this.userAwareFindAllGruppen(user);
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
    public boolean isAdded(@Nonnull User user) {
        boolean hinzugefuegt = false;
        for (User hinzugefuegteUser : this.selectedEntity.getMitglieder()) {
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
    public String cancel() {
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
