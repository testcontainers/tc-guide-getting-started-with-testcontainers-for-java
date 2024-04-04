package com.testcontainers.demo.entity;

import static jakarta.persistence.CascadeType.PERSIST;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SoftwareRelease {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    private String description;

    @OneToMany(cascade = PERSIST)
    private List<Application> applications = new ArrayList<>();

    /**
     * Default constructor for the Release class.
     */
    public SoftwareRelease() {}

    /**
     * Constructor for the Release class.
     *
     * @param id           the ID of the release
     * @param releaseDate  the release date
     * @param description  the description of the release
     * @param applications the list of applications associated with the release
     */
    public SoftwareRelease(Integer id, LocalDate releaseDate, String description, List<Application> applications) {
        this.id = id;
        this.releaseDate = releaseDate;
        this.description = description;
        this.applications = applications;
    }

    /**
     * Get the ID of the release.
     *
     * @return the ID of the release
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set the ID of the release.
     *
     * @param id the ID of the release
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Get the release date.
     *
     * @return the release date
     */
    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    /**
     * Set the release date.
     *
     * @param releaseDate the release date
     */
    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     * Get the description of the release.
     *
     * @return the description of the release
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the release.
     *
     * @param description the description of the release
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the list of applications associated with the release.
     *
     * @return the list of applications associated with the release
     */
    public List<Application> getApplications() {
        return applications;
    }

    /**
     * Set the list of applications associated with the release.
     *
     * @param applications the list of applications associated with the release
     */
    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }

    /**
     * Add an application to the list of applications associated with the release.
     *
     * @param application the application to be added
     */
    public void addApplication(Application application) {
        this.applications.add(application);
    }
}
