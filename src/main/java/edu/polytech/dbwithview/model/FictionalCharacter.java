package edu.polytech.dbwithview.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


/**
 * Here is our Model (from MVC)
 * We consider fictional characters
 * We annotate with @Entity (from jakarta persistence)
 * Jakarta Persistence defines a standard for
 * management of persistence and object/relational mapping
 * in Java(R) environments.
 * see https://jakarta.ee/specifications/persistence/
 */
@Entity
public class FictionalCharacter {

    /**
     * The following annotation allow to identify
     * the variable as the ID for the database
     * and the strategy to generate ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstname;
    private String lastname;

    public FictionalCharacter() {
    }

    public FictionalCharacter(Long id, String firstname, String lastname) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public Long getId() {
        return id;
    }
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

}