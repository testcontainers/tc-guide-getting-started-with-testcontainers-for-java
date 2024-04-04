package com.testcontainers.demo.app;

import com.testcontainers.demo.config.KafkaExtendedContainerConfig;
import com.testcontainers.demo.DemoApplication;
import com.testcontainers.demo.config.MockGitClientComponent;
import com.testcontainers.demo.config.PgContainerConfig;
import org.springframework.boot.SpringApplication;

import java.io.IOException;

public class RunApplicationWithTestcontainers {

    public static void main(String[] args) throws IOException {
        MockGitClientComponent.setupGitClientMock();
        args = new String[]{"--spring.profiles.active=local-container"};
        SpringApplication.from(DemoApplication::main).with(KafkaExtendedContainerConfig.class, PgContainerConfig.class).run(args);
    }

}
