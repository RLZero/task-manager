package com.project.task.domain.dto;

import com.project.task.domain.entity.TaskPriority;
import com.project.task.domain.entity.TaskStatus;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record UpdateTaskRequestDto(
        @NotBlank
        @Length(min = 1, max = 255, message = ERROR_MESSAGE_TITLE_LENGTH)
        String title,
        @Length(max = 1000, message = ERROR_MESSAGE_DESCRIPTION_LENGTH)
        @Nullable
        String description,
        @NotNull(message = ERROR_MESSAGE_STATUS)
        TaskStatus status,
        @NotNull(message = ERROR_MESSAGE_PRIORITY_NOT_NULL)
        TaskPriority priority
) {
    private static final String ERROR_MESSAGE_TITLE_LENGTH =
            "Title must be between 1 and 255 characters.";

    private static final String ERROR_MESSAGE_DESCRIPTION_LENGTH =
            "Description must not exceed 1000 characters.";

    private static final String ERROR_MESSAGE_STATUS =
            "Status must not be null.";

    private static final String ERROR_MESSAGE_PRIORITY_NOT_NULL =
            "Priority must not be null.";
}
