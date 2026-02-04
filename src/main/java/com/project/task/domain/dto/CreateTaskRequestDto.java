package com.project.task.domain.dto;

import com.project.task.domain.entity.TaskPriority;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTaskRequestDto(
        @NotBlank
        @Size(min = 1, max = 255, message = ERROR_MESSAGE_TITLE_LENGTH)
        String title,
        @Size(max = 1000, message = ERROR_MESSAGE_DESCRIPTION_LENGTH)
        @Nullable
        String description,
        @NotNull(message = ERROR_MESSAGE_PRIORITY_NOT_NULL)
        TaskPriority priority
) {
    private static final String ERROR_MESSAGE_TITLE_LENGTH =
            "Title must be between 1 and 255 characters.";

    private static final String ERROR_MESSAGE_DESCRIPTION_LENGTH =
            "Description must not exceed 1000 characters.";

    private static final String ERROR_MESSAGE_PRIORITY_NOT_NULL =
            "Priority must not be null.";
}
