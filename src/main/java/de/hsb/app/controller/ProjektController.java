package de.hsb.app.controller;

import de.hsb.app.enumeration.Status;
import de.hsb.app.model.Gruppe;
import de.hsb.app.model.Projekt;
import de.hsb.app.model.User;
import de.hsb.app.repository.AbstractCrudRepository;
import de.hsb.app.utils.DateUtils;
import de.hsb.app.utils.GruppeUtils;
import de.hsb.app.utils.ProjectUtils;
import de.hsb.app.utils.RedirectUtils;

import javax.annotation.Nonnull;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.Query;
import javax.transaction.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * {@link Projekt}-Controller
 */
@ManagedBean(name = "projektController")
@SessionScoped
public class ProjektController extends AbstractCrudRepository<Projekt> {

    private int choosenGroupId;

    /**
     * Erstellt ein neues {@link Projekt} und redirected auf {@link RedirectUtils#NEUES_PROJEKT_XHTML}.
     *
     * @return {@link RedirectUtils#NEUES_PROJEKT_XHTML}
     */
    @Nonnull
    public String newProject(@Nonnull User projektLeiter) {
        this.selectedEntity = new Projekt();
        this.selectedEntity.setLeiterId(projektLeiter.getId());
        return RedirectUtils.NEUES_PROJEKT_XHTML;
    }

    /**
     * Zaehlt wie viele Tickets {@link Status#OFFEN} sind.
     *
     * @param projekt {@link Projekt}
     * @return Anzahl der offenen Tickets
     */
    public long openTickets(@Nonnull Projekt projekt) {
        return projekt.getTicket().stream().filter(ticket -> Status.OFFEN.equals(ticket.getStatus())).count();
    }

    /**
     * Bricht den aktuellen Vorgang ab und redirected auf {@link RedirectUtils#PROJEKT_TABELLE_XHTML}
     *
     * @return {@link RedirectUtils#PROJEKT_TABELLE_XHTML}
     */
    @Nonnull
    public String abbrechen() {
        return RedirectUtils.PROJEKT_TABELLE_XHTML;
    }

    /**
     * Findet alle Projekte eines {@link User}s in der er auch Mitglied ist.
     *
     * @param loggedUser  eingeloggter {@link User}
     * @param gruppenList {@link List<Gruppe>}
     * @return List<Projekt>
     */
    @Nonnull
    public List<Projekt> userAwareFindAllByGruppe(@Nonnull User loggedUser, @Nonnull List<Gruppe> gruppenList) {
        List<Projekt> projektList = new ArrayList<>();
        for (Gruppe gruppe : gruppenList) {
            Query query = this.em.createQuery("select pr from Projekt pr fetch all properties where pr.gruppenId = :gruppenId");
            query.setParameter("gruppenId", gruppe.getId());
            projektList.addAll(this.uncheckedSolver(query.getResultList()));
        }
        return projektList;
    }


    /**
     * Prueft ob die Gruppe die bearbeitende Gruppe des Projekts ist.
     *
     * @param gruppe zu pruefende Gruppe
     * @return boolean
     */
    public boolean isChoosenGroup(@Nonnull Gruppe gruppe) {
        return GruppeUtils.compareGruppeById(this.choosenGroupId, gruppe);
    }

    /**
     * Formatirt das {@link Date} auf "dd.MM.yyyy".
     *
     * @param date {@link Date}
     * @return "dd.MM.yyyy"
     */
    public String formattedDateDDMMYYYY(Date date) {
        return DateUtils.formatedDateDDMMYYYY(date);
    }

    /**
     * Prueft ob die Gruppe die Gruppe des Projektes ist.
     *
     * @param projekt {@link Projekt}
     * @param gruppe  {@link Gruppe}
     * @return boolean
     */
    public boolean isChoosenGroup(@Nonnull Projekt projekt, @Nonnull Gruppe gruppe) {
        return ProjectUtils.isChoosenGroup(projekt, gruppe);
    }

    /**
     * Setzt die ausgewaehlte Gruppe fuer das {@link Projekt}. Redirected auf {@link RedirectUtils#NEUES_PROJEKT_XHTML}.
     *
     * @param gruppe {@link Gruppe}
     * @return {@link RedirectUtils#NEUES_PROJEKT_XHTML}
     */
    public String addGroupToProjekt(@Nonnull Gruppe gruppe) {
        this.choosenGroupId = gruppe.getId();
        return RedirectUtils.NEUES_PROJEKT_XHTML;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected Class<Projekt> getRepositoryClass() {
        return Projekt.class;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected String getQueryCommand() {
        return Projekt.NAMED_QUERY_QUERY;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected String getSelect() {
        return Projekt.NAMED_QUERY_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Projekt> uncheckedSolver(Object var) {
        List<Projekt> result = new ArrayList<Projekt>();
        if (var instanceof List) {
            for (int i = 0; i < ((List<?>) var).size(); i++) {
                Object item = ((List<?>) var).get(i);
                if (item instanceof Projekt) {
                    result.add((Projekt) item);
                }
            }
        }
        return result;
    }

    public int getChoosenGroupId() {
        return this.choosenGroupId;
    }

    public void setChoosenGroupId(int choosenGroupId) {
        this.choosenGroupId = choosenGroupId;
    }

    /**
     * Speicher das {@link Projekt} und fuehr zureuck auf {@link RedirectUtils#NEUES_PROJEKT_XHTML}
     *
     * @return {@link RedirectUtils#NEUES_PROJEKT_XHTML}
     */
    public String save() {
        this.selectedEntity.setGruppenId(this.choosenGroupId);
        this.selectedEntity.setErstellungsdatum(Date.from(Instant.now()));
        try {
            this.utx.begin();
            this.selectedEntity = this.em.merge(this.selectedEntity);
            this.em.persist(this.selectedEntity.getTicket());
            this.em.persist(this.selectedEntity);
            this.utx.commit();
            return RedirectUtils.PROJEKT_TABELLE_XHTML;
        } catch (NotSupportedException | SystemException | SecurityException | IllegalStateException |
                RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            this.logger.error("Speichern fehlgeschlagen -> ", e);
            return null;
        }
    }

}
