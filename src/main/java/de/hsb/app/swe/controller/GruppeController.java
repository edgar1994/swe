package de.hsb.app.swe.controller;

import de.hsb.app.swe.model.Gruppe;
import de.hsb.app.swe.model.User;
import de.hsb.app.swe.repository.AbstractCrudRepository;
import de.hsb.app.swe.utils.DateUtils;
import de.hsb.app.swe.utils.RedirectUtils;
import de.hsb.app.swe.utils.UserUtils;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.DataModel;
import javax.transaction.*;
import java.sql.Date;
import java.time.Instant;
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
    public String deleteGruppe(@CheckForNull final User loggedUser) {
        if (loggedUser != null) {
            this.checkEntityList();
            this.entityList.setWrappedData(this.em.createNamedQuery(this.getSelect()).getResultList());
            final Gruppe group = this.entityList.getRowData();
            if (group != null) {
                final int groupId = group.getId();
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
                    this.logger.info("Group with Id '{}' deleted.", groupId);
                    this.utx.commit();
                } catch (final NotSupportedException | SystemException | SecurityException | IllegalStateException |
                        RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
                    this.logger.error("Could not delete Group with Id '{}' -> Reason {}", groupId, e.getMessage());
                }
            } else {
                this.logger.error("Could not delete Group. No Group found.");
            }
        } else {
            this.logger.error("User is not allowed to be null.");
        }
        return RedirectUtils.GRUPPE_TABELLE_XHTML;
    }

    /**
     * Liefert das Datim im Format "dd.MM.yyyy" zurueck. Wenn das Datum <code>null</code> ist dann wird ein leerer
     * String zurueckgeliefert.
     *
     * @param date Date
     * @return dd.MM.yyyy
     */
    @Nonnull
    public String getFormatedDate(@CheckForNull final Date date) {
        if (date != null) {
            return DateUtils.formatedDateDDMMYYYY(date);
        } else {
            return "";
        }
    }

    /**
     * Erstellt eine neue {@link Gruppe} setzt den uebergenen {@link User} loggedUser als Leiter und fuegt ihn als
     * Mitglied hinzu. Redirected auf {@link RedirectUtils#NEUE_GRUPPE_XHTML}.
     *
     * @param loggedUser Eingeloggter {@link User}
     * @return {@link RedirectUtils#NEUE_GRUPPE_XHTML}
     */
    @CheckForNull
    public String newGroup(@CheckForNull final User loggedUser) {
        if (loggedUser != null) {
            this.selectedEntity = new Gruppe();
            this.selectedEntity.setLeiterId(loggedUser.getId());
            this.selectedEntity.addUser(loggedUser);
            this.selectedEntity.setErstellungsdatum(Date.from(Instant.now()));
            return RedirectUtils.NEUE_GRUPPE_XHTML;
        } else {
            this.logger.error("There is no user logged in. Cancel command. ");
            return RedirectUtils.GRUPPE_TABELLE_XHTML;
        }

    }

    /**
     * Prueft ob der User die Gruppe editieren darf und setzt die gewaehlte {@link Gruppe} und redirected auf
     * {@link RedirectUtils#NEUE_GRUPPE_XHTML} weiter. Sollte er nicht die Gruppe editieren duerfen bleibt er auf
     * {@link RedirectUtils#GRUPPE_TABELLE_XHTML}.
     *
     * @return {@link RedirectUtils#NEUE_GRUPPE_XHTML} || {@link RedirectUtils#GRUPPE_TABELLE_XHTML}
     */
    public String userAwareEditGroup(@Nonnull final User user) {
        this.checkEntityList();
        final Gruppe gruppeToCheck = this.entityList.getRowData();
        if (gruppeToCheck != null && UserUtils.compareUserById(gruppeToCheck.getLeiterId(), user)) {
            this.selectedEntity = gruppeToCheck;
            return RedirectUtils.NEUE_GRUPPE_XHTML;
        }
        return RedirectUtils.GRUPPE_TABELLE_XHTML;
    }

    /**
     * Setzt die Mitglieder der gewaehlten {@link Gruppe} und speichert sie. Redirected auf {@link RedirectUtils#GRUPPE_TABELLE_XHTML}
     *
     * @return {@link RedirectUtils#GRUPPE_TABELLE_XHTML}
     */
    @Nonnull
    public String saveGroup(@CheckForNull final Set<User> members) {
        if (members != null) {
            try {
                this.utx.begin();
                this.selectedEntity.setMitglieder(members);
                this.selectedEntity = this.em.merge(this.selectedEntity);
                this.em.persist(this.selectedEntity);
                this.utx.commit();
            } catch (final NotSupportedException | SystemException | SecurityException | IllegalStateException |
                    RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
                this.logger.error("Saving operation failed -> ", e);
            }
            return RedirectUtils.GRUPPE_TABELLE_XHTML;
        } else {
            return RedirectUtils.NEUE_GRUPPE_XHTML;
        }
    }

    /**
     * Findet alle {@link Gruppe}n in denen der uebergebene {@link User} Mitglied oder Gruppenleiter ist.
     *
     * @param user {@link User}
     * @return List<Gruppe>
     */
    @Nonnull
    public DataModel<Gruppe> userAwareFindAllGruppen(@CheckForNull final User user) {
        this.checkEntityList();
        if (user != null) {
            switch (user.getRolle()) {
                case KUNDE:
                case MITARBEITER:
                    this.entityList.setWrappedData(new ArrayList<>(user.getGruppen()));
                    return this.entityList;
                case ADMIN:
                    this.entityList.setWrappedData(this.findAll());
                    return this.entityList;
                case USER:
                default:
                    throw new IllegalArgumentException(String.format("Role %s has now permission to be in a Group.", user.getRolle()));
            }
        } else {
            this.logger.error("User is not allowed to be null.");
            return this.entityList;
        }
    }

    /**
     * Liefert zuruck ob der eingeloggte {@link User} eine neue {@link Gruppe} erstellen darf oder nicht.
     *
     * @param loggedUser eingeloggter User
     * @return boolean
     */
    public boolean userAwareNewGroup(@CheckForNull final User loggedUser) {
        if (loggedUser != null) {
            switch (loggedUser.getRolle()) {
                case KUNDE:
                case ADMIN:
                case USER:
                    return false;
                case MITARBEITER:
                    return true;
                default:
                    throw new IllegalArgumentException(String.format("User with Role %s does not exist.",
                            loggedUser.getRolle()));
            }
        } else {
            this.logger.error("User is not allowed to be null.");
            return false;
        }
    }

    /**
     * Liefert zuruck ob der eingeloggte {@link User} eine {@link Gruppe} loeschen darf oder nicht.
     *
     * @param loggedUser eingeloggter User
     * @return boolean
     */
    public boolean userAwareIsGroupLeader(@CheckForNull final User loggedUser, @CheckForNull final Gruppe currentGroup) {
        if (loggedUser != null) {
            switch (loggedUser.getRolle()) {
                case KUNDE:
                case USER:
                    return false;
                case MITARBEITER:
                    if (currentGroup != null) {
                        return UserUtils.compareUserById(currentGroup.getLeiterId(), loggedUser);
                    } else {
                        this.logger.error("Group is not allowed to be null.");
                        return false;
                    }
                case ADMIN:
                    return true;
                default:
                    throw new IllegalArgumentException(String.format("User with Role %s does not exist.",
                            loggedUser.getRolle()));
            }
        } else {
            this.logger.error("User is not allowed to be null.");
            return false;
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
    @Override
    protected List<Gruppe> uncheckedSolver(@Nonnull final Object var) {
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
