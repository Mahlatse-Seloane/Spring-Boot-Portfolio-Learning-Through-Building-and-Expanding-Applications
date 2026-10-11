package com.devtiro.task.domain.dto.response;

import com.devtiro.task.domain.entity.TaskPriority;
import com.devtiro.task.domain.entity.TaskStatus;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateTaskResponseDto (
        UUID id,
        String title,
        String description,
        LocalDate dueDate,
        TaskPriority priority,
        TaskStatus status
)
{}
