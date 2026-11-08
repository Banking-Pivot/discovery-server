package com.banking.discovery_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Service registry of the banking system, built on Netflix Eureka.
 *
 * <p>Every microservice registers here under its {@code spring.application.name}, and the
 * API Gateway resolves its {@code lb://<service>} routes with this registry. The dashboard
 * that lists the registered instances is served at the root path.
 */
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServerApplication {

    /**
     * Starts the registry.
     *
     * @param args command line arguments, passed to Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServerApplication.class, args);
    }
}
