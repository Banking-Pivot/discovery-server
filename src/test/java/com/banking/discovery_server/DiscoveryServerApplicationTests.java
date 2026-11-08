package com.banking.discovery_server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Boots the registry on a random port and talks to it over HTTP the way the dashboard
 * users and the Eureka clients do.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DiscoveryServerApplicationTests {

    @LocalServerPort
    private int port;

    private RestClient http;

    @BeforeEach
    void client() {
        http = RestClient.create("http://localhost:" + port);
    }

    @Test
    void servesTheDashboardAtTheRoot() {
        ResponseEntity<String> dashboard = http.get().uri("/").retrieve().toEntity(String.class);

        assertThat(dashboard.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(dashboard.getHeaders().getContentType().isCompatibleWith(MediaType.TEXT_HTML)).isTrue();
        assertThat(dashboard.getBody()).contains("Instances currently registered with Eureka");
    }

    /** A service registers itself with a POST, as the Eureka client of every microservice does. */
    @Test
    void registersAServiceAndListsIt() {
        ResponseEntity<Void> registration = http.post().uri("/eureka/apps/DEMO-SERVICE")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {"instance": {
                          "instanceId": "localhost:demo-service:9999",
                          "app": "DEMO-SERVICE",
                          "hostName": "localhost",
                          "ipAddr": "127.0.0.1",
                          "vipAddress": "demo-service",
                          "status": "UP",
                          "port": {"$": 9999, "@enabled": "true"},
                          "dataCenterInfo": {
                            "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
                            "name": "MyOwn"
                          }
                        }}
                        """)
                .retrieve()
                .toBodilessEntity();

        assertThat(registration.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(http.get().uri("/eureka/apps/DEMO-SERVICE").accept(MediaType.APPLICATION_JSON)
                .retrieve().body(String.class))
                .contains("\"instanceId\":\"localhost:demo-service:9999\"", "\"status\":\"UP\"");
        assertThat(http.get().uri("/").retrieve().body(String.class)).contains("DEMO-SERVICE");
    }
}
