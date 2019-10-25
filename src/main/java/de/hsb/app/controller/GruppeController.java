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
     * Redirected auf {@link RedirectUtils#NEUE_GRUPPE_XHTML}
     *
     * @param userToRmove Zu entfernenden {@link User}
     */
    @Nonnull
    public String removeUser(@Nonnull User userToRmove) {
        this.selectedEntity.getMitglieder().removeIf(user -> !UserUtils.compareUserById(this.selectedEntity.getLeiterId(),
                userToRmove) && UserUtils.compareUserById(user, userToRmove));
        return RedirectUtils.NEUE_GRUPPE_XHTML;
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
     * Loescht eine {@link Gruppe} und redirected auf {@link RedirectUtils#GRUPPE_TABELLE_XHTML}.
     * Ist der {@link User} dazu nicht berechtigt wird die ausgewaehlte {@link Gruppe} nicht geloescht und er bleibt auf
     * der gleichen Seite.
     *
     * @return {@link RedirectUtils#GRUPPE_TABELLE_XHTML}
     */
    @Nonnull
    public String deleteGruppe(@Nonnull User loggedUser) {
        this.selectedEntity = this.userAwareFindGruppeToDeleteByLoggedUserAndRowData(loggedUser,
                this.entityList.getRowData());
        if (this.userAwareDeleteGroup(loggedUser, this.selectedEntity)) {
            try {
                this.utx.begin();
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
            return RedirectUtils.GRUPPE_TABELLE_XHTML;
        }
        return RedirectUtils.NEUE_GRUPPE_XHTML;
    }

    /**
     * Erstellt eine neue {@link Gruppe} setzt den uebergenen {@link User} loggedUser als Leiter und fuegt ihn als
     * Mitglied hinzu. Redirected auf {@link RedirectUtils#NEUE_GRUPPE_XHTML}.
     *
     * @param eingeloggterUser Eingeloggter {@link User}
     * @return {@link RedirectUtils#NEUE_GRUPPE_XHTML}
     */
    @CheckForNull
    public String newGroup(@Nonnull User eingeloggterUser) {
        try {
            this.utx.begin();
            this.selectedEntity = new Gruppe();
            this.selectedEntity.setLeiterId(eingeloggterUser.getId());
            this.selectedEntity.addUser(eingeloggterUser);
            this.utx.commit();
            return RedirectUtils.NEUE_GRUPPE_XHTML;
        } catch (NotSupportedException | SystemException | SecurityException | IllegalStateException |
                RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            this.logger.error("Speichern fehlgeschlagen -> ", e);
            return null;
        }
    }

    /**
     * Fuegt einen {@link User} der {@link Gruppe} hinzu. Redirected auf {@link RedirectUtils#NEUE_GRUPPE_XHTML}.
     *
     * @param userToAdd hinzuzufügender {@link User}
     * @return {@link RedirectUtils#NEUE_GRUPPE_XHTML}
     */
    @Nonnull
    public String addUser(@Nonnull User userToAdd) {
        this.selectedEntity.addUser(userToAdd);
        return RedirectUtils.NEUE_GRUPPE_XHTML;
    }

    /**
     * Speichert eine {@link Gruppe}. Redirected auf {@link RedirectUtils#GRUPPE_TABELLE_XHTML}
     *
     * @return {@link RedirectUtils#GRUPPE_TABELLE_XHTML}
     */
    @Nonnull
    public String save() {
        this.save(this.selectedEntity);
        return RedirectUtils.GRUPPE_TABELLE_XHTML;
    }

    /**
     * Findet alle {@link Gruppe}n in denen der uebergebene {@link User} Mitglied oder Gruppenleiter ist.
     *
     * @param user {@link User}
     * @return List<Gruppe>
     */
    @Nonnull
    public List<Gruppe> userAwareFindAllGruppen(@Nonnull User user) {
        switch (user.getRolle()) {
            case KUNDE:
            case MITARBEITER:
                return new ArrayList<>(user.getGruppen());
            case ADMIN:
                return this.findAll();
            case USER:
            default:
                throw new IllegalArgumentException(String.format("Rolle %s darf keine Gruppen haben.", user.getRolle()));

        }
    }

    public List<Gruppe> userAwareFindAllGruppenByLeiterId(@Nonnull User user) {
        switch (user.getRolle()) {
            case MITARBEITER:
                List<Gruppe> gruppen = new ArrayList<>();
                Query query = this.em.createQuery("select gr from Gruppe gr join fetch gr.mitglieder m " +
                        "where gr.leiterId = :leiterId");
                query.setParameter("leiterId", user.getId());
                Set<Integer> gruppenIds = new HashSet<>();
                for (Gruppe gruppe : this.uncheckedSolver(query.getResultList())) {
                    gruppenIds.add(gruppe.getId());
                }
                for (int id : gruppenIds) {
                    gruppen.add(this.findById(id));
                }
                return gruppen;
            case ADMIN:
                return this.findAll();
            case KUNDE:
            case USER:
            default:
                throw new IllegalArgumentException(String.format("Rolle %s kann kein Gruppen-Leiter sein.", user.getRolle()));

        }
    }

    /**
     * Liefert zuruck ob der eingeloggte {@link User} eine neue {@link Gruppe} erstellen darf oder nicht.
     *
     * @param loggedUser eingeloggter User
     * @return boolean
     */
    public boolean userAwareNewGroup(@Nonnull User loggedUser) {
        switch (loggedUser.getRolle()) {
            case KUNDE:
            case ADMIN:
            case USER:
                return false;
            case MITARBEITER:
                return true;
            default:
                throw new IllegalArgumentException(String.format("User mit der Rolle %s gibt es nicht",
                        loggedUser.getRolle()));
        }
    }

    /**
     * Liefert zuruck ob der eingeloggte {@link User} eine {@link Gruppe} loeschen darf oder nicht.
     *
     * @param loggedUser eingeloggter User
     * @return boolean
     */
    public boolean userAwareDeleteGroup(@Nonnull User loggedUser, @Nonnull Gruppe currentGroup) {
        switch (loggedUser.getRolle()) {
            case KUNDE:
            case USER:
                return false;
            case MITARBEITER:
                return UserUtils.compareUserById(currentGroup.getLeiterId(), loggedUser);
            case ADMIN:
                return true;
            default:
                throw new IllegalArgumentException(String.format("User mit der Rolle %s gibt es nicht",
                        loggedUser.getRolle()));
        }
    }


    /**
     * Erstellt anhand aller gefundenen {@link Gruppe}n das entsprechende {@link DataModel <Gruppe>}.
     *
     * @return DataModel<User>
     */
    public DataModel<Gruppe> entityListForGruppenerstellung(@Nonnull User user) {
        List<Gruppe> gruppeList = this.userAwareFindAllGruppen(user);
        this.checkEntityList();
        if (!gruppeList.isEmpty()) {
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
     * Bricht den aktuellen Vorgang ab und redirected auf {@link RedirectUtils#GRUPPE_TABELLE_XHTML}.
     *
     * @return {@link RedirectUtils#GRUPPE_TABELLE_XHTML}
     */
    @Nonnull
    public String cancel() {
        return RedirectUtils.GRUPPE_TABELLE_XHTML;
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Gruppe> uncheckedSolver(Object var) {
        List<Gruppe> result = new ArrayList<Gruppe>();
        if (var instanceof List) {
            for (int i = 0; i < ((List<?>) var).size(); i++) {
                Object item = ((List<?>) var).get(i);
                if (item instanceof Gruppe) {
                    result.add((Gruppe) item);
                }
            }
        }
        return result;
    }

}
