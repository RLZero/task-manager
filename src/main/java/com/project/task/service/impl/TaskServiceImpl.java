package com.project.task.service.impl;

import com.project.task.domain.CreateTaskRequest;
import com.project.task.domain.entity.Task;
import com.project.task.repository.TaskRepository;
import com.project.task.service.TaskService;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task createTask(CreateTaskRequest request) {

        Task task = Task.create(
                request.title(),
                request.description(),
                request.priority());

        return taskRepository.save(task);
    }
}
