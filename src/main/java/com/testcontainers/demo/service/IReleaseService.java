package com.testcontainers.demo.service;

import com.testcontainers.demo.entity.Release;

public interface IReleaseService {
    void addRelease(Release release);
    void addApplication(Integer appId, Integer releaseId);
}
