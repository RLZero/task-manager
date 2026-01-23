package com.project.task.controller;

import com.project.task.domain.CreateTaskRequest;
import com.project.task.domain.dto.CreateTaskRequestDto;
import com.project.task.domain.dto.TaskDto;
import com.project.task.domain.mapper.TaskMapper;
import com.project.task.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    private final TaskMapper taskMapper;

    public TaskController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody CreateTaskRequestDto createTaskRequestDto) {
        CreateTaskRequest request = taskMapper.fromDto(createTaskRequestDto);
        TaskDto taskDto = taskMapper.toDto(taskService.createTask(request));
        return new ResponseEntity<>(taskDto, HttpStatus.CREATED);
    }

}
