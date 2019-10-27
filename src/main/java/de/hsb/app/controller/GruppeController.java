package de.hsb.app.controller;

import de.hsb.app.model.Gruppe;
import de.hsb.app.model.User;
import de.hsb.app.repository.AbstractCrudRepository;
import de.hsb.app.utils.RedirectUtils;
import de.hsb.app.utils.UserUtils;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.transaction.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controller fuer {@link Gruppe}.
 */
@ManagedBean(name = "gruppeController")
@ApplicationScoped
public class GruppeController extends AbstractCrudRepository<Gruppe> {

    /**
     * Loescht eine {@link Gruppe} und redirected auf {@link RedirectUtils#GRUPPE_TABELLE_XHTML}.
     * Ist der {@link User} dazu nicht berechtigt wird die ausgewaehlte {@link Gruppe} nicht geloescht und er bleibt auf
     * der gleichen Seite.
     *
     * @return {@link RedirectUtils#GRUPPE_TABELLE_XHTML}
     */
    @Nonnull
    public String deleteGruppe(@Nonnull final User loggedUser) {
        this.checkEntityList();
        this.entityList.setWrappedData(this.em.createNamedQuery(this.getSelect()).getResultList());
        final Gruppe gruppe = this.entityList.getRowData();
        if (gruppe != null) {
            final int groupId = gruppe.getId();
            try {
                this.utx.begin();
                this.selectedEntity = this.em.find(Gruppe.class, groupId);
                for (final User user : this.selectedEntity.getMitglieder()) {
                    user.getGruppen().remove(this.selectedEntity);
                }
                this.selectedEntity.setMitglieder(new HashSet<>());
                this.selectedEntity = this.em.merge(this.selectedEntity);
                this.em.remove(this.selectedEntity);
                this.em.flush();
                this.entityList.setWrappedData(this.userAwareFindAllGruppen(loggedUser));
                this.utx.commit();
            } catch (final NotSupportedException | SystemException | SecurityException | IllegalStateException |
                    RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
                this.logger.error("Could not delete Group with Id '{}' -> Reason {}", groupId, e.getMessage());
            }
        } else {
            this.logger.error("Could not delete Group. No Group found.");
        }
        return RedirectUtils.GRUPPE_TABELLE_XHTML;
    }

    /**
     * Erstellt eine neue {@link Gruppe} setzt den uebergenen {@link User} loggedUser als Leiter und fuegt ihn als
     * Mitglied hinzu. Redirected auf {@link RedirectUtils#NEUE_GRUPPE_XHTML}.
     *
     * @param eingeloggterUser Eingeloggter {@link User}
     * @return {@link RedirectUtils#NEUE_GRUPPE_XHTML}
     */
    @CheckForNull
    public String newGroup(@Nonnull final User eingeloggterUser) {
        this.selectedEntity = new Gruppe();
        this.selectedEntity.setLeiterName(UserUtils.getNachnameVornameString(eingeloggterUser));
        this.selectedEntity.addUser(eingeloggterUser);
        return RedirectUtils.NEUE_GRUPPE_XHTML;
    }

    /**
     * Setzt die Mitglieder der gewaehlten {@link Gruppe} und speichert sie. Redirected auf {@link RedirectUtils#GRUPPE_TABELLE_XHTML}
     *
     * @return {@link RedirectUtils#GRUPPE_TABELLE_XHTML}
     */
    @Nonnull
    public String saveGroup(@Nonnull final Set<User> members) {
        try {
            this.utx.begin();
            this.selectedEntity.setMitglieder(members);
            this.selectedEntity = this.em.merge(this.selectedEntity);
            this.em.persist(this.selectedEntity);
            this.utx.commit();
        } catch (final NotSupportedException | SystemException | SecurityException | IllegalStateException |
                RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            this.logger.error("Speichern fehlgeschlagen -> ", e);
        }
        return RedirectUtils.GRUPPE_TABELLE_XHTML;
    }

    /**
     * Findet alle {@link Gruppe}n in denen der uebergebene {@link User} Mitglied oder Gruppenleiter ist.
     *
     * @param user {@link User}
     * @return List<Gruppe>
     */
    @Nonnull
    public List<Gruppe> userAwareFindAllGruppen(@Nonnull final User user) {
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

    /**
     * Liefert zuruck ob der eingeloggte {@link User} eine neue {@link Gruppe} erstellen darf oder nicht.
     *
     * @param loggedUser eingeloggter User
     * @return boolean
     */
    public boolean userAwareNewGroup(@Nonnull final User loggedUser) {
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
    public boolean userAwareIsGroupLeader(@Nonnull final User loggedUser, @Nonnull final Gruppe currentGroup) {
        switch (loggedUser.getRolle()) {
            case KUNDE:
            case USER:
                return false;
            case MITARBEITER:
                return currentGroup.getLeiterName().equalsIgnoreCase(UserUtils.getNachnameVornameString(loggedUser));
            case ADMIN:
                return true;
            default:
                throw new IllegalArgumentException(String.format("User mit der Rolle %s gibt es nicht",
                        loggedUser.getRolle()));
        }
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
    // Fixme in abstract einbinden ueber getRepoClass
    @Override
    protected List<Gruppe> uncheckedSolver(final Object var) {
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
}
