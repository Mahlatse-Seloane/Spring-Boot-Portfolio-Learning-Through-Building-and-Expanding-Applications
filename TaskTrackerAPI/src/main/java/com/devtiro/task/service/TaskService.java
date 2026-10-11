package com.devtiro.task.service;

import com.devtiro.task.domain.dto.request.CreateTaskRequestDto;
import com.devtiro.task.domain.dto.request.UpdateTaskRequestDto;
import com.devtiro.task.domain.dto.response.CreateTaskResponseDto;
import com.devtiro.task.domain.dto.response.ReadTaskResponseDto;
import com.devtiro.task.domain.dto.response.UpdateTaskResponseDto;

import java.util.List;
import java.util.UUID;

public interface TaskService
{
    CreateTaskResponseDto createTask(CreateTaskRequestDto request);

    List<ReadTaskResponseDto> getTasks();

    UpdateTaskResponseDto updateTask(UUID taskId, UpdateTaskRequestDto request);

    void deleteTask(UUID taskId);
}
