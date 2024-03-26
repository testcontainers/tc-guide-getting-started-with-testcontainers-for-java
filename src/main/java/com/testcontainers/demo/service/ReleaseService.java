package com.testcontainers.demo.service;

import com.testcontainers.demo.dao.IReleaseDAO;
import com.testcontainers.demo.dto.ReleaseDTO;
import com.testcontainers.demo.entity.Release;
import com.testcontainers.demo.helper.GitClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReleaseService implements IReleaseService {

    @Autowired
    private IReleaseDAO releaseDAO;

    @Autowired
    private GitClient gitClient;

    @Override
    public void addRelease(Release release) {
        releaseDAO.addRelease(release);
    }

    @Override
    public void addApplication(Integer appId, Integer releaseId) {
        releaseDAO.addApplication(appId, releaseId);
    }

    @Override
    public ReleaseDTO getReleaseById(Integer releaseId) {
        Release release = releaseDAO.getReleaseById(releaseId);
        if (release == null) {
            return null;
        }
        ReleaseDTO releaseDTO = new ReleaseDTO(release);
        List<String> gitTags = gitClient.getReleaseTags(release.getReleaseDate(), release.getApplications());
        releaseDTO.setGitTags(gitTags);
        return releaseDTO;
    }
}
