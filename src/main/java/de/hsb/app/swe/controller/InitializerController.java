package de.hsb.app.swe.controller;

import de.hsb.app.swe.enumeration.Rolle;
import de.hsb.app.swe.model.Adresse;
import de.hsb.app.swe.model.Gruppe;
import de.hsb.app.swe.model.User;
import de.hsb.app.swe.repository.AbstractCrudRepository;
import org.primefaces.push.annotation.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private final Logger logger = LoggerFactory.getLogger(AbstractCrudRepository.class);

    private boolean constructed = false;

    /**
     * Erstellt daten beim Initialisieren.
     */
    @PostConstruct
    public void init() {
        // Init User
        this.initUser();
        // Init Groups
        this.initGroups();
    }

    /**
     * Erstellt die {@link Gruppe}n die beim Start der Anwendung generiert werden sollen.
     */
    private void initGroups() {
        final Query queryEmployee = this.em.createQuery("select u from User u where u.rolle = :mitarbeiter", User.class);
        queryEmployee.setParameter("mitarbeiter", Rolle.MITARBEITER);
        final List<User> employeeResultList = queryEmployee.getResultList();
        final Query queryCustomer = this.em.createQuery("select u from User u where u.rolle = :kunde", User.class);
        queryCustomer.setParameter("kunde", Rolle.KUNDE);
        final List<User> customerResultList = queryCustomer.getResultList();

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

        final Gruppe group1 = new Gruppe(employeeResultList.get(0).getId(), new HashSet<>(), "Big Group AG.");
        group1.setId(1);
        this.saveGroup(group1, groupMemberSet1);
        final Gruppe group2 = new Gruppe(employeeResultList.get(1).getId(), new HashSet<>(), "The Order");
        group2.setId(2);
        this.saveGroup(group2, groupMemberSet1);
        final Gruppe group3 = new Gruppe(employeeResultList.get(2).getId(), new HashSet<>(), "Great Nerds");
        group3.setId(3);
        this.saveGroup(group3, groupMemberSet1);
        final Gruppe group4 = new Gruppe(employeeResultList.get(3).getId(), new HashSet<>(), "Irrational Rush");
        group4.setId(4);
        this.saveGroup(group4, groupMemberSet1);
        final Gruppe group5 = new Gruppe(employeeResultList.get(4).getId(), new HashSet<>(), "Blue Snakes");
        group5.setId(5);
        this.saveGroup(group5, groupMemberSet1);
        final Gruppe group6 = new Gruppe(employeeResultList.get(5).getId(), new HashSet<>(), "Bitter Finish");
        group6.setId(6);
        this.saveGroup(group6, groupMemberSet2);
        final Gruppe group7 = new Gruppe(employeeResultList.get(0).getId(), new HashSet<>(), "New Flashers");
        group7.setId(7);
        this.saveGroup(group7, groupMemberSet2);
        final Gruppe group8 = new Gruppe(employeeResultList.get(1).getId(), new HashSet<>(), "The Hillbillies");
        group8.setId(8);
        this.saveGroup(group8, groupMemberSet2);
        final Gruppe group9 = new Gruppe(employeeResultList.get(2).getId(), new HashSet<>(), "Inner Spree");
        group9.setId(9);
        this.saveGroup(group9, groupMemberSet2);
        final Gruppe group10 = new Gruppe(employeeResultList.get(3).getId(), new HashSet<>(), "Buzzing");
        group10.setId(10);
        this.saveGroup(group10, groupMemberSet2);
        final Gruppe group11 = new Gruppe(employeeResultList.get(4).getId(), new HashSet<>(), "Long Hatters");
        group11.setId(11);
        this.saveGroup(group11, groupMemberSet2);
        final Gruppe group12 = new Gruppe(employeeResultList.get(5).getId(), new HashSet<>(), "The Crawlers");
        group12.setId(12);
        this.saveGroup(group12, groupMemberSet2);
        final Gruppe group13 = new Gruppe(employeeResultList.get(0).getId(), new HashSet<>(), "Western Wranglers");
        group13.setId(13);
        this.saveGroup(group13, groupMemberSet3);
        final Gruppe group14 = new Gruppe(employeeResultList.get(1).getId(), new HashSet<>(), "Stiff Deadheads");
        group14.setId(14);
        this.saveGroup(group14, groupMemberSet3);
        final Gruppe group15 = new Gruppe(employeeResultList.get(2).getId(), new HashSet<>(), "Noble Grinders");
        group15.setId(15);
        this.saveGroup(group15, groupMemberSet1);
        final Gruppe group16 = new Gruppe(employeeResultList.get(0).getId(), new HashSet<>(), "Breakaway Cacophony");
        group16.setId(16);
        this.saveGroup(group16, groupMemberSet3);
        final Gruppe group17 = new Gruppe(employeeResultList.get(0).getId(), new HashSet<>(), "Atomic Dogs");
        group17.setId(17);
        this.saveGroup(group17, groupMemberSet4);
        final Gruppe group18 = new Gruppe(employeeResultList.get(0).getId(), new HashSet<>(), "Dancing Blitz");
        group18.setId(18);
        this.saveGroup(group18, groupMemberSet2);
        final Gruppe group19 = new Gruppe(employeeResultList.get(0).getId(), new HashSet<>(), "Skinny Hurricanes");
        group19.setId(19);
        this.saveGroup(group19, groupMemberSet1);
        final Gruppe group20 = new Gruppe(employeeResultList.get(0).getId(), new HashSet<>(), "The Apocalypse");
        group20.setId(20);
        this.saveGroup(group20, groupMemberSet2);
    }

    /**
     * Erstellt die {@link User} die beim Start der Anwendung generiert werden sollen.
     */
    private void initUser() {
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
        user1.setId(1);
        users.add(user1);
        final User user2 = new User("Aron", "O'Connor", adresse2, "mitarbeiter1",
                "passwort+", Rolle.MITARBEITER, new HashSet<>());
        user1.setId(2);
        users.add(user2);
        final User user3 = new User("Maximilian", "Sankt", adresse3, "mitarbeiter2",
                "passwort+", Rolle.MITARBEITER, new HashSet<>());
        user1.setId(3);
        users.add(user3);
        final User user4 = new User("Malon", "Lonlon", adresse4, "mitarbeiter3",
                "passwort+", Rolle.MITARBEITER, new HashSet<>());
        user1.setId(4);
        users.add(user4);
        final User user5 = new User("Lea", "Schroeder", adresse5, "mitarbeiter4",
                "passwort+", Rolle.MITARBEITER, new HashSet<>());
        user1.setId(5);
        users.add(user5);
        final User user6 = new User("Leon", "Blau", adresse6, "mitarbeiter5",
                "passwort+", Rolle.MITARBEITER, new HashSet<>());
        user1.setId(6);
        users.add(user6);
        final User user7 = new User("Sebastian", "Abend", adresse7, "mitarbeiter6",
                "passwort+", Rolle.MITARBEITER, new HashSet<>());
        user1.setId(7);
        users.add(user7);
        final User user8 = new User("Max", "Kundenmann", adresse8, "kunde1",
                "passwort+", Rolle.KUNDE, new HashSet<>());
        user1.setId(8);
        users.add(user8);
        final User user9 = new User("Johanna", "Saenger", adresse9, "kunde2",
                "passwort+", Rolle.KUNDE, new HashSet<>());
        user1.setId(9);
        users.add(user9);
        final User user10 = new User("Stephan", "Beike", adresse10, "user1",
                "passwort+", Rolle.USER, new HashSet<>());
        user1.setId(10);
        users.add(user10);
        for (final User user : users) {
            this.saveUser(user);
        }
    }

    /**
     * Speichert einen {@link User}.
     *
     * @param entity {@link User}
     */
    private void saveUser(@Nonnull User entity) {
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
    private void saveGroup(@Nonnull Gruppe entity, @CheckForNull final Set<User> members) {
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
     * Da zum initialisieren der Managed Bean eine Methode aufgerufen werden soll wird hier eine Empty-Methode erstellt,
     * die nur Logs ueber den erfolg geben soll.
     */
    public void emptyMethod() {
        if (!this.constructed) {
            this.constructed = true;
            this.logger.info("Will be constructed!");
        } else {
            this.logger.info("Already constructed!");
        }
    }

}
