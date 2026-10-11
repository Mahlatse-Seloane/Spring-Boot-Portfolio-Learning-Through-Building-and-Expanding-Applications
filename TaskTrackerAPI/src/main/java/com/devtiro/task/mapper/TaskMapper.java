package com.devtiro.task.mapper;

import com.devtiro.task.domain.dto.request.CreateTaskRequestDto;
import com.devtiro.task.domain.dto.request.UpdateTaskRequestDto;
import com.devtiro.task.domain.dto.response.CreateTaskResponseDto;
import com.devtiro.task.domain.dto.response.ReadTaskResponseDto;
import com.devtiro.task.domain.dto.response.UpdateTaskResponseDto;
import com.devtiro.task.domain.entity.Task;
import com.devtiro.task.domain.entity.TaskStatus;

import java.time.Instant;

public interface TaskMapper
{
    Task mapToEntity(CreateTaskRequestDto dto);

    CreateTaskResponseDto mapToCreateTaskResponseDto(Task task);

    ReadTaskResponseDto mapToReadTaskResponseDto(Task task);

    UpdateTaskResponseDto mapToUpdateTaskResponseDto(Task task);

    void updateTaskFromRequest(Task task, UpdateTaskRequestDto dto);
}
