package com.testcontainers.demo.dao;

import com.testcontainers.demo.entity.Application;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class ApplicationDAO implements IApplicationDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addApplication(Application application) {
        entityManager.merge(application); // Merge the detached entity before persisting
        entityManager.persist(application);
    }

    @Override
    public boolean applicationExists(String name, String owner) {
        String jpql = "FROM Application AS a WHERE a.name = :name AND a.owner = :owner";
        List<Application> resultList = entityManager
            .createQuery(jpql, Application.class)
            .setParameter("name", name)
            .setParameter("owner", owner)
            .getResultList();
        return !resultList.isEmpty();
    }

    @Override
    public Application getApplicationById(int applicationId) {
        return entityManager.find(Application.class, applicationId);
    }

    @Override
    public void updateApplication(Application application) {
        Application app = getApplicationById(application.getId());
        app.setName(application.getName());
        app.setDescription(application.getDescription());
        app.setOwner(application.getOwner());
        entityManager.flush();
    }

    @Override
    public void deleteApplication(int applicationId) {
        entityManager.remove(getApplicationById(applicationId));
    }
}
