package com.devtiro.task.mapper.impl;

import com.devtiro.task.domain.dto.request.CreateTaskRequestDto;
import com.devtiro.task.domain.dto.request.UpdateTaskRequestDto;
import com.devtiro.task.domain.dto.response.CreateTaskResponseDto;
import com.devtiro.task.domain.dto.response.ReadTaskResponseDto;
import com.devtiro.task.domain.dto.response.UpdateTaskResponseDto;
import com.devtiro.task.domain.entity.Task;
import com.devtiro.task.domain.entity.TaskStatus;
import com.devtiro.task.mapper.TaskMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TaskMapperImpl implements TaskMapper
{
    public Task mapToEntity(CreateTaskRequestDto dto)
    {
        Instant now = Instant.now();
        return new Task(
                null,
                dto.title(),
                dto.description(),
                dto.dueDate(),
                TaskStatus.OPEN,
                dto.priority(),
                now,
                now
        );
    }

    public CreateTaskResponseDto mapToCreateTaskResponseDto(Task task)
    {
        return new CreateTaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getPriority(),
                task.getStatus()
        );
    }

    public ReadTaskResponseDto mapToReadTaskResponseDto(Task task)
    {
        return new ReadTaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getPriority(),
                task.getStatus()
        );

    }

    public UpdateTaskResponseDto mapToUpdateTaskResponseDto(Task task)
    {
        return new UpdateTaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getPriority(),
                task.getStatus()
        );
    }

    public void updateTaskFromRequest(Task task, UpdateTaskRequestDto request)
    {
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDueDate(request.dueDate());
        task.setStatus(request.status());
        task.setPriority(request.priority());
        task.setUpdated(Instant.now());
    }
}
