package com.testcontainers.demo.dao;

import com.testcontainers.demo.entity.Application;
import com.testcontainers.demo.entity.SoftwareRelease;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Transactional
@Repository
public class SoftwareReleaseDAO implements ISoftwareReleaseDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private IApplicationDAO applicationDAO;

    @Override
    public void addRelease(SoftwareRelease softwareRelease) {
        entityManager.persist(softwareRelease);
    }

    @Override
    public void addApplication(Integer appId, Integer releaseId) {
        SoftwareRelease softwareRelease = getReleaseById(releaseId);
        Application application = applicationDAO.getApplicationById(appId);
        softwareRelease.addApplication(application);
        entityManager.flush();
    }

    @Override
    public SoftwareRelease getReleaseById(int releaseId) {
        return entityManager.find(SoftwareRelease.class, releaseId);
    }
}
