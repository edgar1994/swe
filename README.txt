*******************************************************
****************     Installation     *****************
*******************************************************

!!! Die Anleitung ist für Windows-Systeme ausgelegt !!!

- Entwicklungsumgebung
  - Java
  - Maven
  - Eclipse Jee 2019-06
- Ticket System GmbH
  - Login / Userdaten
  - Registrieren
  - Usertabelle
  - Gruppentabelle
  - Projekttabelle
  - Projektansicht
  - Neues Projekt
  - Ticket
  
  

*******************************************************
**************** Entwicklungsumgebung *****************
*******************************************************

****************        Java          *****************

- Wir haben die Java jdk-8u211-windows-x64 installiert 
  Dies kann über die Oracle-Seite gedownloaded werden
  https://www.oracle.com/java/technologies/javase-jdk8
  -downloads.html
  
- Nach dem Download der Installationsanweisung folgen

- Nun muss nur noch JAVA_HOME gesetzt werden dazu:
  - Öffnen der Umgebungsvariablen "Dieser PC" öffnen>
    Rechtsklick>Eigenschaften>Erweiterte 
	Systemeinstellungen>Umgebungsvariablen
  - Die nächsten Schritten sowohl in Benutzer- als 
    auch in Systemvariablen durchführen
  - Neu klicken. Dort den Namen JAVA_HOME in der 
    ersten Schaltfläche eingeben und danach den Pfad
	zur JDK eintragen: 
	"C:\Program Files\Java\jdk1.8.0_211"
  - Den Bereits existierenden eintrag "Path" 
    bearbeiten und einen neuen Eintrag hinzufügen:
	C:\Program Files\Java\jdk1.8.0_211\bin
  - Evtl ist ein neustart erforderlich

- Zum Testen CMD öffnen und "set JAVA_HOME" eingeben
- Ausgabe: C:\Program Files\Java\jdk1.8.0_211
  

****************        Maven         *****************

- Wir haben apache-maven-3.6.1 benutzt. Dies kann über 
  die Maven-Homepage heruntergeladen werden:
  https://maven.apache.org/download.cgi
  
- Nach dem Download die Zip im Wunschverzeichnis 
  entpacken: 
  Z.B. in C:\Program Files\Maven\apache-maven-3.6.1

- Nun muss nur noch M2_HOME gesetzt werden dazu:
  - Öffnen der Umgebungsvariablen "Dieser PC" öffnen>
    Rechtsklick>Eigenschaften>Erweiterte 
	Systemeinstellungen>Umgebungsvariablen
  - Die nächsten Schritten sowohl in Benutzer- als 
    auch in Systemvariablen durchführen
  - Neu klicken. Dort den Namen M2_HOME in der 
    ersten Schaltfläche eingeben und danach den Pfad
	zu Maven eintragen: 
	"C:\Program Files\Maven\apache-maven-3.6.1"
  - Den Bereits existierenden eintrag "Path" 
    bearbeiten und einen neuen Eintrag hinzufügen:
	Maven\apache-maven-3.6.1\bin
  - Evtl ist ein neustart erforderlich

- Zum Testen CMD öffnen und "mvn -v" eingeben
- Ausgabe: 
  Maven home: C:\Program Files\apache-maven-3.6.1\bin\..
  Java version: 1.8.0_211, vendor: Oracle Corporation, 
  runtime: C:\Program Files\Java\jdk1.8.0_211\jre
  Default locale: de_DE, platform encoding: Cp1252
  OS name: "windows 10", version: "10.0", arch: "amd64", 
  family: "windows"
  
  
****************  Eclipse & Wildfly   *****************

- Installer downloaden und Java EE version installieren:
  https://www.eclipse.org/eclipseide/
  
- JBoss wildfly 10.1.0 installieren:
  - In Eclipse: Help>Install new Software...
  - Add:
    - Erste Schaltfläche wildfly
	- Zweite Schaltfläche:
	  http://download.jboss.org/jbosstools/photon/
	  development/updates/
	- Bestätigen und alles Selektieren
	- Weiter und installation abwarten/durchführen
  - Server
    - Sollte in Eclipse der Reiter "Servers" nicht 
	  zu sehen sein, kann dies über Window>Show View>
	  Servers angezeigt werden
	- Auf den Link klicken "No servers are avialable. 
	  Click this link to create a new server..."
	- Unter JBoss Community Wildfly 10.x auswählen 
	  und auf Next klicken
	- Alles so lassen wieder auf Next klicken
	- "Download and install runtime..." klicken
	  Wildfly 10.1.0 Final auswählen
	- Alternate JRE: auswählen und die Java Version 
	  jdk1.8.0_211 auswählen
	- Anschließen Finish

- Maven-Projekt importieren
  - File>Importieren>Maven>Existing Maven Projects
  - Browse und Pfad zum zu importierenden Projekt 
    auswählen
  - Prüfen ob pom erkannt wird und finish
  - Über rechtsklick auf das Projekt "Show in Local 
    Terminal" (z.B. Git bash) und "mvn clean install" 
	ausführen
 
- Projekt auf Server Deployen
  - Rechtskllick auf Projekt>Run as...>On Server:
    - Unseren wildfly-Server auswählen
  - Im Browser https:localhost:8080/swe/index.xhtml
    (Für unser Projekt)
	
*******************************************************
****************  Ticket System GmbH  *****************
*******************************************************

****************  Login / Userdaten   *****************

!!!!!!!!!!!!!!!!      Achtung        !!!!!!!!!!!!!!!!!
Nach dem Login wird man zum Ändern des Passworts 
gezwungen. Es Empfiehlt sich Passwort+ wieder 
einzugeben.
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

Vorinitialisierte Rollen

Loggen Sie sich zuerst mit ihrem Account ein (mmarlitz)

Username        Passwort   Rolle
mmarlitz        Passwort+  Mitarbeiter
mschmeyers      Passwort+  Kunde
egrischenko     Passwort+  Kunde
admin           Passwort+  Admin
mitarbeiter1	Passwort+  Mitarbeiter
mitarbeiter2	Passwort+  Mitarbeiter
mitarbeiter3	Passwort+  Mitarbeiter
mitarbeiter4	Passwort+  Mitarbeiter
mitarbeiter5	Passwort+  Mitarbeiter
mitarbeiter6	Passwort+  Mitarbeiter
kunde1			Passwort+  Kunde
kunde2			Passwort+  Kunde
user1			Passwort+  User


****************    Registrieren     *****************

!!!!!!!!!!!!!!!!      Achtung        !!!!!!!!!!!!!!!!!
Nach der Registrierung ist man User und hat keinerlei 
Bereechtigungen und muss von einem Admin in die
entsprechende Rolle promoted werden.
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

Username: Darf nicht existieren
Passwort: Muss aus mindestens 8 Zeichen bestehen, 
          einen Großbuchstaben und einem Sonderzeichen
          oder Zahl bestehen.
Passwort Wiederholen: Muss dem Passwort gleichen
Vorname:  Muss aus mindestens 3 bis 30 Zeichen bestehen
Nachname: Muss aus mindestens 3 bis 30 Zeichen bestehen	  
Straße:   Muss aus mindestens 3 bis 30 Zeichen bestehen
PLZ: Muss nach deutschem Standard gestaltet werden 
     (z.B. 12345)
Ort: Muss aus mindestens 3 bis 30 Zeichen bestehen


****************    Usertabelle     *****************

Zeigt eine Übersicht alle Mitarbeiter. Diese Ansicht 
kann nur von einem Admin eingesehen werden. Hier können
Admin/Mitarbeiter/Kunden/User angelegt, bearbeitet und 
gelöscht werden.

Neuer User/Bearbeiten:

- Schickt auf ein Fomular

Rolle:    Rolle des Users
Username: Darf nicht existieren 
          (Kann nicht bearbeitet werden)
Passwort: Muss aus mindestens 8 Zeichen bestehen, 
          einen Großbuchstaben und einem Sonderzeichen
          oder Zahl bestehen.
Passwort Wiederholen: Muss dem Passwort gleichen
Vorname:  Muss aus mindestens 3 bis 30 Zeichen bestehen
Nachname: Muss aus mindestens 3 bis 30 Zeichen bestehen	  
Straße:   Muss aus mindestens 3 bis 30 Zeichen bestehen
PLZ: Muss nach deutschem Standard gestaltet werden 
     (z.B. 12345)
Ort: Muss aus mindestens 3 bis 30 Zeichen bestehen

Löschen: Löscht einen User, der er nicht selbst ist.
         Admin-Rechte benötigt.
		 
		 
**************** Gruppentabelle   *****************

Übersicht aller Gruppen (Admin) oder in denen der 
User (Kunde/Mitarbeiter) Mitglied ist.

Neue Gruppe:       Nur von User mit Rolle 
                   Mitarbeiter
				   Anlegender User automatisch 
				   Gruppenleiter und in der 
				   Mitgliederliste
Gruppe bearbeiten: Nur von User mit Rolle 
                   Mitarbeiter oder Admin

Felder:			   
Gruppenname:       Darf nicht leer oder vorhanden
                   sein 
Gruppenmitglieder: Auswahl in Tabelle (Nur Rollen
                   Mitarbeiter und Kunde angezeigt)
				   
Löschen:           Nur vom Gruppenleiter und Admin

**************** Projekttabelle  *****************

Übersicht aller Projekte (Admin) oder in denen der 
User (Kunde/Mitarbeiter) Mitglied in der 
bearbeitenden Gruppe ist.

Ansicht: Von allen Usern(Kunde/Mitarbeiter) die Mitglied in der 
		 bearbeitenden Gruppe sind. Oder Admins
Löschen: Vom Projektleiter oder Admins

**************** Projektansicht  *****************

Übersicht aller Tickets und Grundlegende infos

Bearbeiten:   Alle User (außer Rolle User)
Neues Ticket: Nur von Projektmitglied 
              (Rolle Mitarbeiter)
			  
			  
**************** Neues Projekt   *****************

Kann nur angelegt werden von Mitarbeiter der Leiter
einer Gruppe ist.

Neues Projekt: Projekttitel 3 bis 30 Zeiche. Darf 
               nicht vorhanden sein.
Abschlussdatum: Nicht in der Vergangenheit
Projektbeschreibung: Freitext
Tabelle: Auswahl Bearbeitender Gruppe
			  
			  
****************      Ticket      *****************

Anlegen: Mitarbeiter der im Projekt ist.
Bearbeiten: User(Kunde/Mitarbeiter/Admin)

Titel:          Darf nicht leer oder vorhanden 
                sein
Abschlussdatum: Darf nicht in der Vergangenheit 
                liegen 
Aufgabenbeschreibung: Freitext
Bearbeitender Mitarbeiter: Auswahl in dropdown
Status:         Auswahl in dropdown
				
