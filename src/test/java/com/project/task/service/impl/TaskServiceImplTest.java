package com.project.task.service.impl;

import com.project.task.domain.CreateTaskRequest;
import com.project.task.domain.UpdateTaskRequest;
import com.project.task.domain.entity.Task;
import com.project.task.domain.entity.TaskPriority;
import com.project.task.domain.entity.TaskStatus;
import com.project.task.exception.TaskNotFoundException;
import com.project.task.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Test
    void shouldUpdateTaskWhenTaskExists() {

        UUID taskId = UUID.randomUUID();
        Task task = Task.create(
                "Redesign Website",
                "Create a new design for the company",
                TaskPriority.LOW);

        UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest(
                "Updated Website",
                "Update the the design for the company",
                TaskStatus.COMPLETE,
                TaskPriority.HIGH
        );

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        Task taskUpdated = taskService.updateTask(taskId, updateTaskRequest);

        assertEquals("Updated Website", taskUpdated.getTitle());
        assertEquals("Update the the design for the company", taskUpdated.getDescription());
        assertEquals(TaskStatus.COMPLETE, taskUpdated.getStatus());
        assertEquals(TaskPriority.HIGH, taskUpdated.getPriority());

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void shouldThrowTaskNotFoundExceptionWhenUpdatingNonExistingTask() {
        UUID taskId = UUID.randomUUID();

        UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest(
                "Updated Website",
                "Update the the design for the company",
                TaskStatus.COMPLETE,
                TaskPriority.HIGH
        );

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(taskId, updateTaskRequest));

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldDeleteTask() {
        UUID uuid = UUID.randomUUID();

        when(taskRepository.existsById(uuid)).thenReturn(true);
        taskService.deleteTask(uuid);

        verify(taskRepository, times(1)).deleteById(uuid);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingTask() {
        UUID uuid = UUID.randomUUID();

        when(taskRepository.existsById(uuid)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(uuid));
        verify(taskRepository, never()).deleteById(uuid);
    }
}