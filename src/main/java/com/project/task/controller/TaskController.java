package com.project.task.controller;

import com.project.task.domain.CreateTaskRequest;
import com.project.task.domain.UpdateTaskRequest;
import com.project.task.domain.dto.CreateTaskRequestDto;
import com.project.task.domain.dto.TaskDto;
import com.project.task.domain.dto.UpdateTaskRequestDto;
import com.project.task.domain.entity.Task;
import com.project.task.domain.mapper.TaskMapper;
import com.project.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Task Management",
        description = "APIs for managing tasks"
)
@RestController
@RequestMapping(path = "/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    private final TaskMapper taskMapper;

    public TaskController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @Operation(summary = "Create a new task")
    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody CreateTaskRequestDto createTaskRequestDto) {
        CreateTaskRequest request = taskMapper.fromDto(createTaskRequestDto);
        TaskDto taskDto = taskMapper.toDto(taskService.createTask(request));
        return new ResponseEntity<>(taskDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all tasks")
    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        List<TaskDto> taskDtos = tasks.stream().map(taskMapper::toDto).toList();
        return ResponseEntity.ok(taskDtos);
    }

    @Operation(summary = "Update an existing task")
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable UUID taskId,
                                              @Valid @RequestBody UpdateTaskRequestDto updateTaskRequestDto) {
        UpdateTaskRequest request = taskMapper.fromDto(updateTaskRequestDto);
        Task task = taskService.updateTask(taskId, request);
        TaskDto taskDto = taskMapper.toDto(task);
        return ResponseEntity.ok(taskDto);
    }

    @Operation(summary = "Delete a task")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

}
