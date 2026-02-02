package com.project.task.service;

import com.project.task.domain.CreateTaskRequest;
import com.project.task.domain.UpdateTaskRequest;
import com.project.task.domain.entity.Task;

import java.util.List;
import java.util.UUID;

public interface TaskService {

    Task createTask(CreateTaskRequest request);

    List<Task> getAllTasks();

    Task updateTask(UUID taskId, UpdateTaskRequest request);

    void deleteTask(UUID taskId);
}
