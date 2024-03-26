package com.testcontainers.demo.dto;

import com.testcontainers.demo.entity.Application;
import com.testcontainers.demo.entity.Release;

import java.time.LocalDate;
import java.util.List;


public class ReleaseDTO {

    private Integer id;

    private LocalDate releaseDate;

    private String description;

    private List<String> gitTags;

    private List<Application> applications;

    public ReleaseDTO() {
    }

    public ReleaseDTO(Integer id, LocalDate releaseDate, String description, List<Application> applications) {
        this.id = id;
        this.releaseDate = releaseDate;
        this.description = description;
        this.applications = applications;
    }

    public ReleaseDTO(Release release) {
        this.id = release.getId();
        this.releaseDate = release.getReleaseDate();
        this.description = release.getDescription();
        this.applications = release.getApplications();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }

    public List<String> getGitTags() {
        return gitTags;
    }

    public void setGitTags(List<String> gitTags) {
        this.gitTags = gitTags;
    }
}
