package com.testcontainers.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;

import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;

import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class KafkaContainerConfig {

    private static final Logger logger = LoggerFactory.getLogger(KafkaContainerConfig.class.getName());

    @Bean
    @ServiceConnection
    public KafkaContainer kafka() {
        Network network = Network.newNetwork();

        KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"))
            .withEnv("KAFKA_CONFLUENT_SCHEMA_REGISTRY_URL", "http://schemaregistry:8085")
            .withNetworkAliases("kafka")
            .withNetwork(network);

        Startables.deepStart(kafka).join();
        return kafka;
    }
}
