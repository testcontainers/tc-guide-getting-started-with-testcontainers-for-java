package com.testcontainers.demo.service;

import com.testcontainers.demo.dto.ReleaseDTO;
import com.testcontainers.demo.entity.SoftwareRelease;

public interface ISoftwareReleaseService {
    Integer addRelease(SoftwareRelease softwareRelease);
    void addApplication(Integer appId, Integer releaseId);

    /**
     * Get release information by id
     * @param releaseId - id of the release
     * @return  release information
     */
    ReleaseDTO getReleaseById(Integer releaseId);
}
