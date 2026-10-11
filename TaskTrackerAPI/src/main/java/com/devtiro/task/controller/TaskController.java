package com.devtiro.task.controller;

import com.devtiro.task.domain.dto.request.CreateTaskRequestDto;
import com.devtiro.task.domain.dto.request.UpdateTaskRequestDto;
import com.devtiro.task.domain.dto.response.CreateTaskResponseDto;
import com.devtiro.task.domain.dto.response.ReadTaskResponseDto;
import com.devtiro.task.domain.dto.response.UpdateTaskResponseDto;
import com.devtiro.task.service.TaskService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController
{
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<@NonNull CreateTaskResponseDto> createTask(
            @Valid @RequestBody CreateTaskRequestDto request
    )
    {
        CreateTaskResponseDto response = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<@NonNull List<ReadTaskResponseDto>> getTasks()
    {
        List<ReadTaskResponseDto> response = taskService.getTasks();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<@NonNull UpdateTaskResponseDto> updateTask(
            @PathVariable UUID taskId,
            @Valid @RequestBody UpdateTaskRequestDto request)
    {
        UpdateTaskResponseDto response = taskService.updateTask(taskId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<@NonNull Void> deleteTask(@PathVariable UUID taskId)
    {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}