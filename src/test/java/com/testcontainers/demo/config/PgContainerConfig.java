package com.testcontainers.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@TestConfiguration(proxyBeanMethods = false)
public class PgContainerConfig {

    @Bean
    @ServiceConnection
    public PostgreSQLContainer getPostgres() {
        return new PostgreSQLContainer<>("postgres:15-alpine")
            .withCopyToContainer(
                MountableFile.forClasspathResource("ratings_schema.sql"),
                "/docker-entrypoint-initdb.d/ratings_schema.sql"
            )
            .withLabel("com.testcontainers.desktop.service", "postgres");
    }

}
