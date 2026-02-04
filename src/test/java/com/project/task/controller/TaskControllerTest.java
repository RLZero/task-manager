package com.project.task.controller;

import com.project.task.domain.CreateTaskRequest;
import com.project.task.domain.UpdateTaskRequest;
import com.project.task.domain.dto.CreateTaskRequestDto;
import com.project.task.domain.dto.TaskDto;
import com.project.task.domain.dto.UpdateTaskRequestDto;
import com.project.task.domain.entity.Task;
import com.project.task.domain.entity.TaskPriority;
import com.project.task.domain.entity.TaskStatus;
import com.project.task.domain.mapper.TaskMapper;
import com.project.task.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private TaskMapper taskMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldCreateTaskSuccessfully() {

        CreateTaskRequestDto taskRequestDto = new CreateTaskRequestDto(
                "Redesign Website",
                "Update the company website with a new design",
                TaskPriority.LOW);

        CreateTaskRequest taskRequest = new CreateTaskRequest(
                "Redesign Website",
                "Update the company website with a new design",
                TaskPriority.LOW);

        Task task = Task.create(
                "Redesign Website",
                "Update the company website with a new design",
                TaskPriority.LOW);

        TaskDto taskDto = new TaskDto(task.getId(), task.getTitle(), task.getDescription(), task.getPriority(), task.getStatus());

        when(taskMapper.fromDto(taskRequestDto)).thenReturn(taskRequest);
        when(taskService.createTask(taskRequest)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskDto);

        var result = mockMvc.post()
                .uri("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequestDto));

        assertThat(result).hasStatus(HttpStatus.CREATED);
        assertThat(result).bodyJson().extractingPath("$.title").isEqualTo("Redesign Website");
        assertThat(result).bodyJson().extractingPath("$.description").isEqualTo("Update the company website with a new design");
        assertThat(result).bodyJson().extractingPath("$.priority").isEqualTo(TaskPriority.LOW.toString());
    }

    @Test
    public void shouldReturnAllTask() {
        Task firstTask = Task.create(
                "firstTask",
                "first task description",
                TaskPriority.LOW);

        Task secondTask = Task.create(
                "second Task",
                "second task description",
                TaskPriority.MEDIUM);

        List<Task> tasks = List.of(firstTask, secondTask);

        given(taskService.getAllTasks()).willReturn(tasks);

        mockMvc.get()
            .uri("/api/v1/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .assertThat()
            .hasStatusOk()
            .bodyJson().extractingPath("$").isNotEmpty();
    }

    @Test
    public void shouldUpdateTask() {
        UUID taskId = UUID.randomUUID();
        UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest(
                "Updated Website",
                "Update the design for the company",
                TaskStatus.COMPLETE,
                TaskPriority.HIGH
        );
        UpdateTaskRequestDto updateRequestDto = new UpdateTaskRequestDto(
                "Updated Website",
                "Update the design for the company",
                TaskStatus.COMPLETE,
                TaskPriority.HIGH
        );
        Task task = Task.create(
                "INITIAL Website",
                "Initial design for the company",
                TaskPriority.LOW
        );
        TaskDto taskDto = new TaskDto(taskId, task.getTitle(), task.getDescription(), task.getPriority(), task.getStatus());

        when(taskMapper.fromDto(any(UpdateTaskRequestDto.class))).thenReturn(updateTaskRequest);
        when(taskService.updateTask(eq(taskId), any(UpdateTaskRequest.class))).thenReturn(task);
        when(taskMapper.toDto(any(Task.class))).thenReturn(taskDto);

        mockMvc.put()
                .uri("/api/v1/tasks/%s".formatted(taskId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequestDto))
                .assertThat()
                .hasStatusOk()
                .bodyJson().extractingPath("$.id").isEqualTo(taskId.toString());
    }

    @Test
    void shouldDeleteTaskById() {
        UUID taskId = UUID.randomUUID();

        mockMvc.delete()
                .uri("/api/v1/tasks/%s".formatted(taskId))
                .assertThat()
                .hasStatus(HttpStatus.NO_CONTENT);
    }

}