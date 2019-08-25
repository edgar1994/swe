package de.hsb.app.model;

import javax.annotation.ManagedBean;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

@NamedQuery(name = "SelectAufgabe", query = "Select au from Aufgabe au")
@Entity
@ManagedBean("Aufgabe")
public class Aufgabe {

    @Id
    @GeneratedValue
    private int id;

}
