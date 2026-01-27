package com.project.task.service;

import com.project.task.domain.CreateTaskRequest;
import com.project.task.domain.entity.Task;

import java.util.List;

public interface TaskService {

    Task createTask(CreateTaskRequest request);

    List<Task> getAllTasks();
}
