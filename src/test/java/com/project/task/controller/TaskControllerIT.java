package com.project.task.controller;

import com.project.task.domain.CreateTaskRequest;
import com.project.task.domain.UpdateTaskRequest;
import com.project.task.domain.dto.CreateTaskRequestDto;
import com.project.task.domain.entity.Task;
import com.project.task.domain.entity.TaskPriority;
import com.project.task.domain.entity.TaskStatus;
import com.project.task.repository.TaskRepository;
import com.project.task.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureRestTestClient
@ActiveProfiles("test")
public class TaskControllerIT {

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

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

        CreateTaskRequestDto taskRequestDto = new CreateTaskRequestDto(
                "Redesign Website",
                "Update the company website with a new design",
                TaskPriority.LOW);

        restTestClient.post()
                .uri("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(taskRequestDto))
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

        UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest(
                "Updated Task",
                "Updated task description",
                TaskStatus.COMPLETE,
                TaskPriority.HIGH
        );

        restTestClient.put()
                .uri("/api/v1/tasks/%s".formatted(createdTask.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(updateTaskRequest))
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
    public void shouldReturn400WhenPriorityIsNullOnCreatingTask() {

        CreateTaskRequest request = new CreateTaskRequest(
                "Initial Task",
                "Initial task description",
                null);

        restTestClient.post()
                .uri("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Priority must not be null.");
    }

    @Test
    public void shouldReturn400WhenTitleIsTooLongOnCreatingTask() {
        String longTitle = "t".repeat(256);
        CreateTaskRequest request = new CreateTaskRequest(
                longTitle,
                "Initial task description",
                TaskPriority.HIGH);

        restTestClient.post()
                .uri("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Title must be between 1 and 255 characters.");

    }

    @Test
    public void shouldReturn400WhenDescriptionIsTooLongOnCreatingTask() {
        String longDescription = "d".repeat(1001);
        CreateTaskRequest request = new CreateTaskRequest(
                "Initial Task",
                longDescription,
                TaskPriority.HIGH);

        restTestClient.post()
                .uri("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Description must not exceed 1000 characters.");
    }

    @Test
    public void shouldDeleteTaskSuccessfully() {
        CreateTaskRequest request = new CreateTaskRequest(
                "Task to be deleted",
                "This task will be deleted in the test",
                TaskPriority.MEDIUM);

        Task createdTask = taskService.createTask(request);

        restTestClient.delete()
                .uri("/api/v1/tasks/%s".formatted(createdTask.getId()))
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    public void shouldReturn404WhenDeletingNonExistantTask() {

        UUID taskId = UUID.randomUUID();

        restTestClient.delete()
                .uri("/api/v1/tasks/%s".formatted(taskId))
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Task with id '%s' does not exist".formatted(taskId));
    }

    @Test
    public void shouldReturn400WhenDeletingWithInvalidUUID() {

        String invalidUUID = "invalid-uuid";

        restTestClient.delete()
                .uri("/api/v1/tasks/%s".formatted(invalidUUID))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.error")
                .isEqualTo("Invalid UUID format. Expected format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
    }
}
