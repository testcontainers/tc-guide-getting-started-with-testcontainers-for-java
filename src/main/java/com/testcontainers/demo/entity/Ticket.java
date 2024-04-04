package com.testcontainers.demo.entity;

import jakarta.persistence.*;

@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String title;
    private String description;

    @ManyToOne
    @JoinColumn(name = "application_id")
    private Application application;

    @ManyToOne
    @JoinTable(
        name = "ticket_release",
        joinColumns = @JoinColumn(name = "ticket_fk"),
        inverseJoinColumns = @JoinColumn(name = "release_fk")
    )
    private SoftwareRelease softwareRelease;

    private String status;

    public Ticket() {}

    public Ticket(
        Integer id,
        String title,
        String description,
        Application application,
        SoftwareRelease softwareRelease,
        String status
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.application = application;
        this.softwareRelease = softwareRelease;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public SoftwareRelease getRelease() {
        return softwareRelease;
    }

    public void setRelease(SoftwareRelease softwareRelease) {
        this.softwareRelease = softwareRelease;
    }
}
