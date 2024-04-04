package com.testcontainers.demo.helper;

import com.testcontainers.demo.entity.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Component
public class GitClient {
    private static final Logger LOG = LoggerFactory.getLogger(GitClient.class);

    private static final String GIT_URL_PATH = "/gitTags";

    @Value("${gitClient.url}")
    private String gitUrl;
    
    public List<String> getReleaseTags(LocalDate releaseDate, List<Application> applications) {
        LOG.info("Getting release tags from git for release date: {}, applications: {}, and git URL: {}", releaseDate, applications, gitUrl);
        
        List<String> releaseTags = new ArrayList<>();
        
        try {
            StringBuilder urlBuilder = new StringBuilder(gitUrl + GIT_URL_PATH);
            urlBuilder.append("?releaseDate=").append(releaseDate);
            urlBuilder.append("&applications=[");
            urlBuilder.append(
                applications.stream()
                    .map(
                        application ->
                            application.getName().replace(" ","_"))
                    .collect(Collectors.joining(",")));
            urlBuilder.append("]");
            
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    releaseTags.add(line);
                }
                reader.close();
            } else {
                LOG.error("Failed to retrieve release tags from git. Response code: {}", responseCode);
            }
            
            connection.disconnect();
        } catch (Exception e) {
            LOG.error("Error occurred while retrieving release tags from git.", e);
        }
        
        return releaseTags;
    }
}
