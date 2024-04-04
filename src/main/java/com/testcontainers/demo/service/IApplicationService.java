package com.testcontainers.demo.service;

import com.testcontainers.demo.entity.Application;

import java.util.List;

public interface IApplicationService {
    Application addApplication(Application application);
    Application getApplicationById(int applicationId);
    void updateApplication(Application application);
    void deleteApplication(int applicationId);

    List<Application> getAllApplications();
}
