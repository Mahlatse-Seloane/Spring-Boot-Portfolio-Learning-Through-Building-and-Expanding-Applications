package com.devtiro.task.service.impl;

import com.devtiro.task.domain.dto.request.CreateTaskRequestDto;
import com.devtiro.task.domain.dto.request.UpdateTaskRequestDto;
import com.devtiro.task.domain.dto.response.CreateTaskResponseDto;
import com.devtiro.task.domain.dto.response.ReadTaskResponseDto;
import com.devtiro.task.domain.dto.response.UpdateTaskResponseDto;
import com.devtiro.task.domain.entity.Task;
import com.devtiro.task.exception.DuplicateResourceException;
import com.devtiro.task.exception.TaskNotFoundException;
import com.devtiro.task.mapper.TaskMapper;
import com.devtiro.task.repository.TaskRepository;
import com.devtiro.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService
{
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    @Transactional
    public CreateTaskResponseDto createTask(CreateTaskRequestDto request)
    {
        Task task = taskMapper.mapToEntity(request);
        if (taskRepository.existsByTitle(task.getTitle()))
            throw new DuplicateResourceException("This title already exists.");

        Task savedTask = taskRepository.save(task);
        return taskMapper.mapToCreateTaskResponseDto(savedTask);
    }

    @Override
    public List<ReadTaskResponseDto> getTasks()
    {
        return taskRepository.findAll(Sort.by(Direction.ASC, "created")).stream()
                .map(taskMapper::mapToReadTaskResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public UpdateTaskResponseDto updateTask(UUID taskId, UpdateTaskRequestDto request)
    {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        if (taskRepository.existsByTitleAndIdNot(request.title(), taskId))
            throw new DuplicateResourceException("This title already exists.");

        taskMapper.updateTaskFromRequest(task, request);
        Task updatedTask = taskRepository.save(task);

        return taskMapper.mapToUpdateTaskResponseDto(updatedTask);
    }

    @Override
    @Transactional
    public void deleteTask(UUID taskId)
    {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        taskRepository.delete(task);
    }
}