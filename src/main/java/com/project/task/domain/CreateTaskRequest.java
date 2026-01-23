package com.project.task.domain;

import com.project.task.domain.entity.TaskPriority;

public record CreateTaskRequest(
        String title,
        String description,
        TaskPriority priority) {

}
