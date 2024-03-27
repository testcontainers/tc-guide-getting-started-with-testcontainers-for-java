package com.testcontainers.demo.app;

import com.testcontainers.demo.config.ContainerConfig;
import com.testcontainers.demo.DemoApplication;
import org.springframework.boot.SpringApplication;

public class RunApplicationWithTestcontainers {

    public static void main(String[] args) {
        SpringApplication.from(DemoApplication::main).with(ContainerConfig.class).run(args);
    }
}
