package com.testcontainers.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "application_id")
    private Integer id;

    @Column(name = "app_name", nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    private String owner;

    /**
     * Default constructor for the Application class.
     */
    public Application() {}

    /**
     * Constructor for the Application class.
     *
     * @param id          the ID of the application
     * @param name        the name of the application
     * @param owner       the owner of the application
     * @param description the description of the application
     */
    public Application(Integer id, String name, String owner, String description) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.description = description;
    }

    /**
     * Get the ID of the application.
     *
     * @return the ID of the application
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set the ID of the application.
     *
     * @param id the ID of the application
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Get the name of the application.
     *
     * @return the name of the application
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the application.
     *
     * @param name the name of the application
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the owner of the application.
     *
     * @return the owner of the application
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Set the owner of the application.
     *
     * @param owner the owner of the application
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Get the description of the application.
     *
     * @return the description of the application
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the application.
     *
     * @param description the description of the application
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get a string representation of the Application object.
     *
     * @return a string representation of the Application object
     */
    @Override
    public String toString() {
        return (
            "Application{" +
            "id=" +
            id +
            ", name='" +
            name +
            '\'' +
            ", owner=" +
            owner +
            ", description='" +
            description +
            '\'' +
            '}'
        );
    }
}
