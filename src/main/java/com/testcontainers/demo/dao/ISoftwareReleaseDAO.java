package com.testcontainers.demo.dao;

import com.testcontainers.demo.entity.SoftwareRelease;

public interface ISoftwareReleaseDAO {
    void addRelease(SoftwareRelease softwareRelease);
    void addApplication(Integer appId, Integer releaseId);
    SoftwareRelease getReleaseById(int releaseId);
}
