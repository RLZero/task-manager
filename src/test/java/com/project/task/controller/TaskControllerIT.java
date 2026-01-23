package com.project.task.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureRestTestClient
public class TaskControllerIT {

    @Autowired
    private RestTestClient restTestClient;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17");

    @Test
    void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    public void shouldCreateTaskSuccessfully() {

        String requestBody = """
                {
                    "title": "Redesign Website",
                    "description": "Update the company website with a new design",
                    "priority": "LOW"
                }
                """;

        restTestClient.post()
                .uri("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.title").isEqualTo("Redesign Website")
                .jsonPath("$.description").isEqualTo("Update the company website with a new design")
                .jsonPath("$.priority").isEqualTo("LOW");
    }

    @Test
    public void shouldReturn400WhenPriorityIsNull() {
        String requestBody = """
                {
                    "title": "Redesign Website",
                    "description": "Update the company website with a new design",
                    "priority": null
                }
                """;

        restTestClient.post()
                .uri("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Priority must not be null.");
    }

    @Test
    public void shouldReturn400WhenTitleIsTooLong() {
        String longTitle = "t".repeat(256);
        String requestBody = """
                {
                    "title": "%s",
                    "description": "Update the company website with a new design",
                    "priority": "HIGH"
                }
                """.formatted(longTitle);

        restTestClient.post()
                .uri("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Title must be between 1 and 255 characters.");

    }

    @Test
    public void shouldReturn400WhenDescriptionIsTooLong() {
        String longDescription = "d".repeat(1001);
        String bodyRequest = """
                {
                    "title": "Redesign Website",
                    "description": "%s",
                    "priority": "MEDIUM"
                }
                """.formatted(longDescription);

        restTestClient.post()
                .uri("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .body(bodyRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Description must not exceed 1000 characters.");
    }
}
