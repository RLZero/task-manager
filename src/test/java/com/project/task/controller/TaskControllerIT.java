package com.project.task.controller;

import com.project.task.domain.CreateTaskRequest;
import com.project.task.domain.entity.Task;
import com.project.task.domain.entity.TaskPriority;
import com.project.task.repository.TaskRepository;
import com.project.task.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
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

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskService taskService;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17");

    @Test
    void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @BeforeEach
    public void cleanDB() {
        taskRepository.deleteAll();
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
    public void shouldReturnAllTasksSuccessfully() {
        CreateTaskRequest first = new CreateTaskRequest(
                "First Task",
                "First task description",
                TaskPriority.MEDIUM);

        CreateTaskRequest second = new CreateTaskRequest(
                "Second Task",
                "Second task description",
                TaskPriority.HIGH);

        taskService.createTask(first);
        taskService.createTask(second);

        restTestClient.get()
                .uri("/api/v1/tasks")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isNotEmpty()
                .jsonPath("$[0].title").isEqualTo("First Task")
                .jsonPath("$[0].description").isEqualTo("First task description")
                .jsonPath("$[0].priority").isEqualTo("MEDIUM")
                .jsonPath("$[1].id").isNotEmpty()
                .jsonPath("$[1].title").isEqualTo("Second Task")
                .jsonPath("$[1].description").isEqualTo("Second task description")
                .jsonPath("$[1].priority").isEqualTo("HIGH");
    }

    @Test
    public void shouldUpdateTaskSuccessfully() {
        CreateTaskRequest request = new CreateTaskRequest(
                "Initial Task",
                "Initial task description",
                TaskPriority.LOW);

        Task createdTask = taskService.createTask(request);

        String requestBody = """
                {
                    "title": "Updated Task",
                    "description": "Updated task description",
                    "status": "COMPLETE",
                    "priority": "HIGH"
                }
                """;

        restTestClient.put()
                .uri("/api/v1/tasks/%s".formatted(createdTask.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(createdTask.getId().toString())
                .jsonPath("$.title").isEqualTo("Updated Task")
                .jsonPath("$.description").isEqualTo("Updated task description")
                .jsonPath("$.status").isEqualTo("COMPLETE")
                .jsonPath("$.priority").isEqualTo("HIGH");
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
