package com.project.task.service.impl;

import com.project.task.domain.CreateTaskRequest;
import com.project.task.domain.entity.Task;
import com.project.task.domain.entity.TaskPriority;
import com.project.task.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    void shouldCreateTaskWhenRequestIsValid() {
        CreateTaskRequest taskRequest = new CreateTaskRequest(
                "Redesign Website",
                "Update the company website with a new design",
                TaskPriority.MEDIUM);

        Task savedTask = Task.create(
                "Redesign Website",
                "Update the company website with a new design",
                TaskPriority.MEDIUM);

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        Task result = taskService.createTask(taskRequest);

        assertNotNull(result);
        assertEquals("Redesign Website", result.getTitle());
        assertEquals("Update the company website with a new design", result.getDescription());
        assertEquals(TaskPriority.MEDIUM, result.getPriority());

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void shouldReturnALlTasks() {
        Task firstTask = Task.create(
                "firstTask",
                "first task description",
                TaskPriority.LOW);

        Task secondTask = Task.create(
                "second Task",
                "second task description",
                TaskPriority.MEDIUM);

        List<Task> tasks = List.of(firstTask, secondTask);

        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> result = taskService.getAllTasks();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(taskRepository, times(1)).findAll();
    }
}