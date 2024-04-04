package com.testcontainers.demo.config;

import jakarta.annotation.PreDestroy;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Profile("local-container")
public class MockGitClientComponent {

    public static MockWebServer gitClientMockWebServer;

    public static void setupGitClientMock() throws IOException {
        gitClientMockWebServer = new MockWebServer();
        gitClientMockWebServer.start(9091);
        for (int i = 0; i < 100; i++) {
            gitClientMockWebServer.enqueue(new MockResponse().setBody("version" + i));
        }
    }

    @PreDestroy
    public void destroy() throws IOException {
        gitClientMockWebServer.shutdown();
    }
}
