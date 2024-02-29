package com.testcontainers.demo.dao;

import com.testcontainers.demo.entity.Application;

public interface IApplicationDAO {
    void addApplication(Application application);
    boolean applicationExists(String name, String owner);
    Application getApplicationById(int applicationId);
    void updateApplication(Application application);
    void deleteApplication(int applicationId);
}
