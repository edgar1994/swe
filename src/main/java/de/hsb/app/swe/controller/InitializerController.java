package de.hsb.app.swe.controller;

import de.hsb.app.swe.enumeration.Rolle;
import de.hsb.app.swe.enumeration.Status;
import de.hsb.app.swe.model.*;
import de.hsb.app.swe.service.CustomLogService;
import de.hsb.app.swe.utils.ListUtils;
import de.hsb.app.swe.utils.StringUtils;
import de.hsb.app.swe.utils.UserUtils;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;
import org.primefaces.push.annotation.Singleton;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Diese Klasse soll alle Post-Constructs für das gesammte Projekt uebernehmen.
 * Dies mag so in der Praxis nicht durchgefuehrt werden, aber aus uebersichts Gruenden wird es hierhin ausgelagert.
 */
@ManagedBean(name = "initializerController")
@Singleton
@ApplicationScoped
public class InitializerController {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private UserTransaction utx;

    private final CustomLogService logger = new CustomLogService<>(InitializerController.class);

    private boolean constructed = false;

    /**
     * Erstellt daten beim Initialisieren.
     */
    @PostConstruct
    public void init() {
        // Init User
        if (this.em.createQuery(User.NAMED_QUERY_QUERY).getResultList().isEmpty()) {
            this.initUser();
        } else {
            this.constructed = true;
        }
        // Init Groups
        if (this.em.createQuery(Gruppe.NAMED_QUERY_QUERY).getResultList().isEmpty()) {
            this.initGroups();
        } else {
            this.constructed = true;
        }
        // Init Projects
        if (this.em.createQuery(Projekt.NAMED_QUERY_QUERY).getResultList().isEmpty()) {
            this.initProjects();
        } else {
            this.constructed = true;
        }
    }

    /**
     * Initialisiert die {@link Projekt}e.
     */
    private void initProjects() {
        // Finde alle moeglichen Gruppen
        this.logger.info("Find all groups.");
        final List<Gruppe> groups = ListUtils.uncheckedSolverGroup(this.em.createQuery(Gruppe.NAMED_QUERY_QUERY)
                .getResultList());
        this.logger.info("{} groups found.", groups.size());

        // Abschluss Datum. Fromat ist "Gruppe_TicketNr => 1_1".
        final Date dueDate1 = Date.from(Instant.now().plus(7, ChronoUnit.DAYS));


        // Erstelle Tickets fuer jede Gruppe. Fromat ist "Gruppe_TicketNr => 1_1".
        this.logger.info("Prepare Tickets ...");
        final List<Ticket> tickets1 = new ArrayList<>();
        final Ticket ticket1_1 = new Ticket(dueDate1, "First Researches",
                groups.get(0).getMitglieder().iterator().next().getId(), Status.ABGESCHLOSSEN,
                "Research the Topic of the Bioprocess and what the di-esters production is");
        tickets1.add(ticket1_1);
        final Ticket ticket1_2 = new Ticket(dueDate1, "Reproduce di-esters production",
                groups.get(0).getMitglieder().iterator().next().getId(), Status.IM_TEST,
                "The process includes contacting terephthalic acid with a C6-C10 alcohol in the presence \n" +
                        "of an organo-titanium catalyst and an alcohol-amine promoter under conditions \n" +
                        "effective to form a corresponding terephthalic acid di-ester.");
        tickets1.add(ticket1_2);
        final Ticket ticket1_3 = new Ticket(dueDate1, "Look for inconsistency",
                0, Status.OFFEN,
                "Look for inconsistensy");
        tickets1.add(ticket1_3);
        final Ticket ticket1_4 = new Ticket(dueDate1, "Make own assumption",
                groups.get(0).getMitglieder().iterator().next().getId(), Status.IN_BEARBEITUNG,
                "Make own assumption");
        tickets1.add(ticket1_4);
        final Ticket ticket1_5 = new Ticket(dueDate1, "Clear inconsistency",
                groups.get(0).getMitglieder().iterator().next().getId(), Status.IN_BEARBEITUNG,
                "Clear inconsistency");
        tickets1.add(ticket1_5);
        this.logger.info("Tickets are prepared.");

        // Erstelle Projekte
        try (final ProgressBar pb = new ProgressBar(StringUtils.createLogforPB(Date.from(Instant.now()), "Create Projects"),
                10, ProgressBarStyle.UNICODE_BLOCK)) {
            final Projekt projekt1 = new Projekt("Bioprocess", groups.get(0).getLeiterId(), new ArrayList<>(),
                    groups.get(0).getId(), Date.from(Instant.now()), Date.from(Instant.now()
                    .plus(35, ChronoUnit.DAYS)),
                    "This project aims at di-esters production");
            this.saveProject(projekt1, tickets1);
            pb.step();

            final Projekt projekt2 = new Projekt("Chitinous", groups.get(1).getLeiterId(), new ArrayList<>(),
                    groups.get(1).getId(), Date.from(Instant.now()), Date.from(Instant.now()
                    .plus(35, ChronoUnit.DAYS)),
                    "Erforschung des chitinous");
            this.saveProject(projekt2, new ArrayList<>());
            pb.step();

            final Projekt projekt3 = new Projekt("Pojekt Stromerzeugung", groups.get(2).getLeiterId(), new ArrayList<>(),
                    groups.get(2).getId(), Date.from(Instant.now()), Date.from(Instant.now()
                    .plus(35, ChronoUnit.DAYS)),
                    "Neueröffnung eines SWB-Werkes");
            this.saveProject(projekt3, new ArrayList<>());
            pb.step();

            final Projekt projekt4 = new Projekt("Ace Pojekt", groups.get(3).getLeiterId(), new ArrayList<>(),
                    groups.get(3).getId(), Date.from(Instant.now()), Date.from(Instant.now()
                    .plus(35, ChronoUnit.DAYS)),
                    "Gründung des Ace-Unternehmens");
            this.saveProject(projekt4, new ArrayList<>());
            pb.step();

            final Projekt projekt5 = new Projekt("Carousel Pojekt", groups.get(4).getLeiterId(), new ArrayList<>(),
                    groups.get(4).getId(), Date.from(Instant.now()), Date.from(Instant.now()
                    .plus(35, ChronoUnit.DAYS)),
                    "Freimarkt-Aufbau in Bremen");
            this.saveProject(projekt5, new ArrayList<>());
            pb.step();

            final Projekt projekt6 = new Projekt("Superb Pojekt", groups.get(5).getLeiterId(), new ArrayList<>(),
                    groups.get(5).getId(), Date.from(Instant.now()), Date.from(Instant.now()
                    .plus(35, ChronoUnit.DAYS)),
                    "Bachelor Projekt");
            this.saveProject(projekt6, new ArrayList<>());
            pb.step();

            final Projekt projekt7 = new Projekt("Wind Pojekt", groups.get(6).getLeiterId(), new ArrayList<>(),
                    groups.get(6).getId(), Date.from(Instant.now()), Date.from(Instant.now()
                    .plus(35, ChronoUnit.DAYS)),
                    "Aufbau eines windparks in Bemerhaven");
            this.saveProject(projekt7, new ArrayList<>());
            pb.step();

            final Projekt projekt8 = new Projekt("Creek Pojekt", groups.get(7).getLeiterId(), new ArrayList<>(),
                    groups.get(7).getId(), Date.from(Instant.now()), Date.from(Instant.now()
                    .plus(35, ChronoUnit.DAYS)),
                    "Anlegen eines Bachs an die Weser");
            this.saveProject(projekt8, new ArrayList<>());
            pb.step();

            final Projekt projekt9 = new Projekt("Planet Pojekt", groups.get(8).getLeiterId(), new ArrayList<>(),
                    groups.get(8).getId(), Date.from(Instant.now()), Date.from(Instant.now()
                    .plus(35, ChronoUnit.DAYS)),
                    "Wie schütze ich besser die Umwelt");
            this.saveProject(projekt9, new ArrayList<>());
            pb.step();

            final Projekt projekt10 = new Projekt("Ticket Pojekt", groups.get(9).getLeiterId(), new ArrayList<>(),
                    groups.get(9).getId(), Date.from(Instant.now()), Date.from(Instant.now()
                    .plus(35, ChronoUnit.DAYS)),
                    "Abgabe fuer SWE III");
            this.saveProject(projekt10, new ArrayList<>());
            pb.step();
        }
    }

    /**
     * Erstellt die {@link Gruppe}n die beim Start der Anwendung generiert werden sollen.
     */
    private void initGroups() {
        this.logger.info("Load all Emplyoees.");
        final Query queryEmployee = this.em.createQuery("select u from User u where u.rolle = :mitarbeiter", User.class);
        queryEmployee.setParameter("mitarbeiter", Rolle.MITARBEITER);
        final List<User> employeeResultList = ListUtils.uncheckedSolverUser(queryEmployee.getResultList());
        this.logger.info(String.format("%s employees are loaded.", employeeResultList.size()));
        this.logger.info("Load all customer.");
        final Query queryCustomer = this.em.createQuery("select u from User u where u.rolle = :kunde", User.class);
        queryCustomer.setParameter("kunde", Rolle.KUNDE);
        final List<User> customerResultList = ListUtils.uncheckedSolverUser(queryCustomer.getResultList());
        this.logger.info(String.format("%s customer are loaded.", employeeResultList.size()));

        this.logger.info("Prepare Groupmembers ...");
        final Set<User> groupMemberSet1 = new HashSet<>();
        groupMemberSet1.add(employeeResultList.get(0));
        groupMemberSet1.add(employeeResultList.get(2));
        groupMemberSet1.add(employeeResultList.get(3));
        groupMemberSet1.add(employeeResultList.get(4));
        groupMemberSet1.add(employeeResultList.get(5));
        groupMemberSet1.add(customerResultList.get(0));

        final Set<User> groupMemberSet2 = new HashSet<>();
        groupMemberSet2.add(employeeResultList.get(0));
        groupMemberSet2.add(employeeResultList.get(1));
        groupMemberSet2.add(employeeResultList.get(3));
        groupMemberSet2.add(employeeResultList.get(4));
        groupMemberSet2.add(customerResultList.get(0));

        final Set<User> groupMemberSet3 = new HashSet<>();
        groupMemberSet3.add(employeeResultList.get(0));
        groupMemberSet3.add(employeeResultList.get(1));
        groupMemberSet3.add(employeeResultList.get(5));
        groupMemberSet3.add(customerResultList.get(1));

        final Set<User> groupMemberSet4 = new HashSet<>();
        groupMemberSet4.add(employeeResultList.get(0));
        groupMemberSet4.add(employeeResultList.get(2));
        groupMemberSet4.add(employeeResultList.get(4));
        groupMemberSet4.add(customerResultList.get(1));
        this.logger.info("Groupmembers are prepared.");

        try (final ProgressBar pb = new ProgressBar(StringUtils.createLogforPB(Date.from(Instant.now()), "Create Groups"),
                20, ProgressBarStyle.UNICODE_BLOCK)) {
            final Gruppe group1 = new Gruppe(employeeResultList.get(0).getId(), new HashSet<>(), "Big Group AG.",
                    UserUtils.getNachnameVornameString(employeeResultList.get(0)));
            this.saveGroup(group1, groupMemberSet1);
            pb.step();
            final Gruppe group2 = new Gruppe(employeeResultList.get(1).getId(), new HashSet<>(), "The Order",
                    UserUtils.getNachnameVornameString(employeeResultList.get(1)));
            this.saveGroup(group2, groupMemberSet1);
            pb.step();
            final Gruppe group3 = new Gruppe(employeeResultList.get(2).getId(), new HashSet<>(), "Great Nerds",
                    UserUtils.getNachnameVornameString(employeeResultList.get(2)));
            this.saveGroup(group3, groupMemberSet1);
            pb.step();
            final Gruppe group4 = new Gruppe(employeeResultList.get(3).getId(), new HashSet<>(), "Irrational Rush",
                    UserUtils.getNachnameVornameString(employeeResultList.get(3)));
            this.saveGroup(group4, groupMemberSet1);
            pb.step();
            final Gruppe group5 = new Gruppe(employeeResultList.get(4).getId(), new HashSet<>(), "Blue Snakes",
                    UserUtils.getNachnameVornameString(employeeResultList.get(4)));
            this.saveGroup(group5, groupMemberSet1);
            pb.step();
            final Gruppe group6 = new Gruppe(employeeResultList.get(5).getId(), new HashSet<>(), "Bitter Finish",
                    UserUtils.getNachnameVornameString(employeeResultList.get(5)));
            this.saveGroup(group6, groupMemberSet2);
            pb.step();
            final Gruppe group7 = new Gruppe(employeeResultList.get(0).getId(), new HashSet<>(), "New Flashers",
                    UserUtils.getNachnameVornameString(employeeResultList.get(0)));
            this.saveGroup(group7, groupMemberSet2);
            pb.step();
            final Gruppe group8 = new Gruppe(employeeResultList.get(1).getId(), new HashSet<>(), "The Hillbillies",
                    UserUtils.getNachnameVornameString(employeeResultList.get(1)));
            this.saveGroup(group8, groupMemberSet2);
            pb.step();
            final Gruppe group9 = new Gruppe(employeeResultList.get(2).getId(), new HashSet<>(), "Inner Spree",
                    UserUtils.getNachnameVornameString(employeeResultList.get(2)));
            this.saveGroup(group9, groupMemberSet2);
            pb.step();
            final Gruppe group10 = new Gruppe(employeeResultList.get(3).getId(), new HashSet<>(), "Buzzing",
                    UserUtils.getNachnameVornameString(employeeResultList.get(3)));
            this.saveGroup(group10, groupMemberSet2);
            pb.step();
            final Gruppe group11 = new Gruppe(employeeResultList.get(4).getId(), new HashSet<>(), "Long Hatters",
                    UserUtils.getNachnameVornameString(employeeResultList.get(4)));
            this.saveGroup(group11, groupMemberSet2);
            pb.step();
            final Gruppe group12 = new Gruppe(employeeResultList.get(5).getId(), new HashSet<>(), "The Crawlers",
                    UserUtils.getNachnameVornameString(employeeResultList.get(5)));
            this.saveGroup(group12, groupMemberSet2);
            pb.step();
            final Gruppe group13 = new Gruppe(employeeResultList.get(0).getId(), new HashSet<>(), "Western Wranglers",
                    UserUtils.getNachnameVornameString(employeeResultList.get(0)));
            this.saveGroup(group13, groupMemberSet3);
            pb.step();
            final Gruppe group14 = new Gruppe(employeeResultList.get(1).getId(), new HashSet<>(), "Stiff Deadheads",
                    UserUtils.getNachnameVornameString(employeeResultList.get(1)));
            this.saveGroup(group14, groupMemberSet3);
            pb.step();
            final Gruppe group15 = new Gruppe(employeeResultList.get(2).getId(), new HashSet<>(), "Noble Grinders",
                    UserUtils.getNachnameVornameString(employeeResultList.get(2)));
            this.saveGroup(group15, groupMemberSet1);
            pb.step();
            final Gruppe group16 = new Gruppe(employeeResultList.get(0).getId(), new HashSet<>(), "Breakaway Cacophony",
                    UserUtils.getNachnameVornameString(employeeResultList.get(0)));
            this.saveGroup(group16, groupMemberSet3);
            pb.step();
            final Gruppe group17 = new Gruppe(employeeResultList.get(0).getId(), new HashSet<>(), "Atomic Dogs",
                    UserUtils.getNachnameVornameString(employeeResultList.get(0)));
            this.saveGroup(group17, groupMemberSet4);
            pb.step();
            final Gruppe group18 = new Gruppe(employeeResultList.get(0).getId(), new HashSet<>(), "Dancing Blitz",
                    UserUtils.getNachnameVornameString(employeeResultList.get(0)));
            this.saveGroup(group18, groupMemberSet2);
            pb.step();
            final Gruppe group19 = new Gruppe(employeeResultList.get(0).getId(), new HashSet<>(), "Skinny Hurricanes",
                    UserUtils.getNachnameVornameString(employeeResultList.get(0)));
            this.saveGroup(group19, groupMemberSet1);
            pb.step();
            final Gruppe group20 = new Gruppe(employeeResultList.get(0).getId(), new HashSet<>(), "The Apocalypse",
                    UserUtils.getNachnameVornameString(employeeResultList.get(0)));
            this.saveGroup(group20, groupMemberSet2);
            pb.step();
        }
    }

    /**
     * Erstellt die {@link User} die beim Start der Anwendung generiert werden sollen.
     */
    private void initUser() {
        try (final ProgressBar pb = new ProgressBar(StringUtils.createLogforPB(Date.from(Instant.now()), "Create User"),
                10, ProgressBarStyle.UNICODE_BLOCK)) {
            final Adresse adresse1 = new Adresse("Schillerstrasse 3", "86850", "Fischach");
            final Adresse adresse2 = new Adresse("Gruenauer Strasse 68", "21702", "Ahlerstedt");
            final Adresse adresse3 = new Adresse("Flotowstr. 85", "06528", "Beyernaumburg");
            final Adresse adresse4 = new Adresse("Genterstrasse 22", "24103", "Kiel");
            final Adresse adresse5 = new Adresse("Joachimstaler Str. 44", "55471", "Keidelheim");
            final Adresse adresse6 = new Adresse("Hardenbergstraße 68", "67744", "Lohnweiler");
            final Adresse adresse7 = new Adresse("Mollstrasse 71", "33189", "Schlangen");
            final Adresse adresse8 = new Adresse("Rudolstaedter Strasse 84", "26897", "Esterwegen");
            final Adresse adresse9 = new Adresse("Gotzkowskystrasse 79", "57638", "Neitersen");
            final Adresse adresse10 = new Adresse("Fasanenstrasse 19", "22145", "Hamburg Rahlstedt");

            final List<User> users = new ArrayList<>();
            final User user1 = new User("Dan", "Evan", adresse1, "admin",
                    "passwort+", Rolle.ADMIN, new HashSet<>());
            users.add(user1);
            pb.step();

            final User user2 = new User("Aron", "O'Connor", adresse2, "mitarbeiter1",
                    "passwort+", Rolle.MITARBEITER, new HashSet<>());
            users.add(user2);
            pb.step();

            final User user3 = new User("Maximilian", "Sankt", adresse3, "mitarbeiter2",
                    "passwort+", Rolle.MITARBEITER, new HashSet<>());
            users.add(user3);
            pb.step();

            final User user4 = new User("Malon", "Lonlon", adresse4, "mitarbeiter3",
                    "passwort+", Rolle.MITARBEITER, new HashSet<>());
            users.add(user4);
            pb.step();

            final User user5 = new User("Lea", "Schroeder", adresse5, "mitarbeiter4",
                    "passwort+", Rolle.MITARBEITER, new HashSet<>());
            users.add(user5);
            pb.step();

            final User user6 = new User("Leon", "Blau", adresse6, "mitarbeiter5",
                    "passwort+", Rolle.MITARBEITER, new HashSet<>());
            users.add(user6);
            pb.step();

            final User user7 = new User("Sebastian", "Abend", adresse7, "mitarbeiter6",
                    "passwort+", Rolle.MITARBEITER, new HashSet<>());
            users.add(user7);
            pb.step();

            final User user8 = new User("Max", "Kundenmann", adresse8, "kunde1",
                    "passwort+", Rolle.KUNDE, new HashSet<>());
            users.add(user8);
            pb.step();

            final User user9 = new User("Johanna", "Saenger", adresse9, "kunde2",
                    "passwort+", Rolle.KUNDE, new HashSet<>());
            users.add(user9);
            pb.step();

            final User user10 = new User("Stephan", "Beike", adresse10, "user1",
                    "passwort+", Rolle.USER, new HashSet<>());
            users.add(user10);
            pb.step();

            this.logger.info("Saving User ...");
            for (final User user : users) {
                this.saveUser(user);
            }
            this.logger.info("Saving User successfull.");
        }
    }

    /**
     * Speichert einen {@link User}.
     *
     * @param entity {@link User}
     */
    private void saveUser(User entity) {
        try {
            this.utx.begin();
            entity = this.em.merge(entity);
            this.em.merge(entity);
            this.utx.commit();
        } catch (final NotSupportedException | SystemException | SecurityException | IllegalStateException |
                RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            this.logger.error("Saving operation failed -> ", e);
        }
    }

    /**
     * Speichert eine {@link Gruppe}.
     *
     * @param entity  {@link Gruppe}
     * @param members MemberSet {@link Set<User>}
     */
    private void saveGroup(Gruppe entity, final Set<User> members) {
        if (members != null) {
            try {
                this.utx.begin();
                entity.setMitglieder(members);
                entity = this.em.merge(entity);
                this.em.persist(entity);
                this.utx.commit();
            } catch (final NotSupportedException | SystemException | SecurityException | IllegalStateException |
                    RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
                this.logger.error("Saving operation failed -> ", e);
            }
        } else {
            this.logger.error("Saving operation failed. Entity is null.");
        }
    }

    /**
     * Speichert eine {@link Projekt}.
     *
     * @param entity  {@link Projekt}
     * @param tickets MemberSet {@link Set<Ticket>}
     */
    private void saveProject(final Projekt entity, final List<Ticket> tickets) {
        if (tickets != null) {
            for (final Ticket ticket : tickets) {
                ticket.setProjekt(entity);
            }
            entity.setTicket(tickets);
            try {
                this.utx.begin();
                this.em.persist(entity);
                this.utx.commit();
            } catch (final NotSupportedException | SystemException | SecurityException | IllegalStateException |
                    RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
                this.logger.error("Saving operation failed -> ", e);
            }
        } else {
            this.logger.error("Saving operation failed. Entity is null.");
        }
    }

    /**
     * Da zum initialisieren der Managed Bean eine Methode aufgerufen werden soll wird hier eine Empty-Methode erstellt,
     * die nur Logs ueber den erfolg geben soll.
     */
    public void emptyMethod() {
        if (!this.constructed) {
            this.logger.info("Will be constructed!");
        } else {
            this.logger.info("Already constructed!");
        }
    }

}
