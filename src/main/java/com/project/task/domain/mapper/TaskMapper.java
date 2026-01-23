package com.project.task.domain.mapper;

import com.project.task.domain.CreateTaskRequest;
import com.project.task.domain.dto.CreateTaskRequestDto;
import com.project.task.domain.dto.TaskDto;
import com.project.task.domain.entity.Task;

public interface TaskMapper {

    CreateTaskRequest fromDto(CreateTaskRequestDto dto);

    TaskDto toDto(Task task);
}
