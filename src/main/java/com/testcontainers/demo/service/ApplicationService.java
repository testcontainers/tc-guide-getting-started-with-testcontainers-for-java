package com.testcontainers.demo.service;

import com.testcontainers.demo.dao.IApplicationDAO;
import com.testcontainers.demo.entity.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService implements IApplicationService {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationService.class);

    @Autowired
    private IApplicationDAO applicationDAO;

    @Override
    public synchronized Application addApplication(Application application) {
        if (applicationDAO.applicationExists(application.getName(), application.getOwner())) {
            LOG.info("Application <{}> already exists", application.getName());
        } else {
            applicationDAO.addApplication(application);
            LOG.info("Application <{}> added", application.getName());
        }
        return application;
    }

    @Override
    public Application getApplicationById(int applicationId) {
        return applicationDAO.getApplicationById(applicationId);
    }

    @Override
    public void updateApplication(Application application) {
        applicationDAO.updateApplication(application);
    }

    @Override
    public void deleteApplication(int applicationId) {
        applicationDAO.deleteApplication(applicationId);
    }

    @Override
    public List<Application> getAllApplications() {
        return applicationDAO.getAll();
    }
}
