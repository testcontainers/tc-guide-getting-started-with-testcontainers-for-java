package com.testcontainers.demo.dao;

import com.testcontainers.demo.entity.Release;

public interface IReleaseDAO {
    void addRelease(Release release);
    void addApplication(Integer appId, Integer releaseId);
    Release getReleaseById(int releaseId);
}
