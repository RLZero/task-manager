package com.project.task.domain.dto;

import com.project.task.domain.entity.TaskPriority;
import com.project.task.domain.entity.TaskStatus;

import java.util.UUID;

public record TaskDto(
        UUID id,
        String title,
        String description,
        TaskPriority priority,
        TaskStatus status
) {
}
