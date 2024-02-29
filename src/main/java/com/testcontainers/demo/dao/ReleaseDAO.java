package com.testcontainers.demo.dao;

import com.testcontainers.demo.entity.Application;
import com.testcontainers.demo.entity.Release;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Transactional
@Repository
public class ReleaseDAO implements IReleaseDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private IApplicationDAO applicationDAO;

    @Override
    public void addRelease(Release release) {
        entityManager.persist(release);
    }

    @Override
    public void addApplication(Integer appId, Integer releaseId) {
        Release release = getReleaseById(releaseId);
        Application application = applicationDAO.getApplicationById(appId);
        release.addApplication(application);
        entityManager.flush();
    }

    @Override
    public Release getReleaseById(int releaseId) {
        return entityManager.find(Release.class, releaseId);
    }
}
