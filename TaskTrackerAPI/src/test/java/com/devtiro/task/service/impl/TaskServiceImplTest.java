package com.devtiro.task.service.impl;

import com.devtiro.task.domain.dto.request.CreateTaskRequestDto;
import com.devtiro.task.domain.dto.request.UpdateTaskRequestDto;
import com.devtiro.task.domain.dto.response.CreateTaskResponseDto;
import com.devtiro.task.domain.dto.response.ReadTaskResponseDto;
import com.devtiro.task.domain.dto.response.UpdateTaskResponseDto;
import com.devtiro.task.domain.entity.Task;
import com.devtiro.task.domain.entity.TaskPriority;
import com.devtiro.task.domain.entity.TaskStatus;
import com.devtiro.task.exception.DuplicateResourceException;
import com.devtiro.task.exception.TaskNotFoundException;
import com.devtiro.task.mapper.TaskMapper;
import com.devtiro.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest
{
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private UUID taskId;
    private Task existingTask;

    @BeforeEach
    void setUp()
    {
        taskId = UUID.randomUUID();
        existingTask = new Task(
                taskId,
                "Existing task",
                "Some description",
                LocalDate.now().plusDays(3),
                TaskStatus.OPEN,
                TaskPriority.MEDIUM,
                Instant.now(),
                Instant.now()
        );
    }

    @Nested
    class createTask
    {
        @Test
        void shouldThrowDuplicateResourceException_whenTitleAlreadyExists()
        {
            // Arrange
            CreateTaskRequestDto request = new CreateTaskRequestDto(
                    "Existing task",
                    "Some description",
                    LocalDate.now().plusDays(3),
                    TaskPriority.MEDIUM
            );

            when(taskMapper.mapToEntity(request)).thenReturn(existingTask);
            when(taskRepository.existsByTitle(existingTask.getTitle())).thenReturn(true);

            // Act + Assert
            assertThatThrownBy(() -> taskService.createTask(request))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessage("This title already exists.");

            verify(taskRepository, never()).save(any());
        }

        @Test
        void shouldSaveAndReturnCreateTaskResponseDto_whenTitleIsUnique()
        {
            // Arrange
            CreateTaskRequestDto request = new CreateTaskRequestDto(
                    "Unique task",
                    "Some description",
                    LocalDate.now().plusDays(3),
                    TaskPriority.MEDIUM
            );

            Task task = new Task(
                    null,
                    request.title(),
                    request.description(),
                    request.dueDate(),
                    TaskStatus.OPEN,
                    request.priority(),
                    Instant.now(),
                    Instant.now()
            );

            Task savedTask = new Task(
                    UUID.randomUUID(),
                    request.title(),
                    request.description(),
                    request.dueDate(),
                    TaskStatus.OPEN,
                    request.priority(),
                    Instant.now(),
                    Instant.now()
            );

            CreateTaskResponseDto response = new CreateTaskResponseDto(
                    savedTask.getId(),
                    savedTask.getTitle(),
                    savedTask.getDescription(),
                    savedTask.getDueDate(),
                    savedTask.getPriority(),
                    savedTask.getStatus()
            );

            when(taskMapper.mapToEntity(request)).thenReturn(task);
            when(taskRepository.existsByTitle(task.getTitle())).thenReturn(false);
            when(taskRepository.save(task)).thenReturn(savedTask);
            when(taskMapper.mapToCreateTaskResponseDto(savedTask)).thenReturn(response);

            // Act
            CreateTaskResponseDto createdTask = taskService.createTask(request);

            // Assert
            assertThat(createdTask).isEqualTo(response);
            verify(taskRepository).save(task);
        }
    }

    @Nested
    class updateTask
    {
        UpdateTaskRequestDto request;

        @BeforeEach
        void setup()
        {
            request = new UpdateTaskRequestDto(
                    "Existing title",
                    "Some description",
                    LocalDate.now().plusDays(3),
                    TaskPriority.MEDIUM,
                    TaskStatus.OPEN
            );
        }

        @Test
        void shouldThrowTaskNotFoundException_whenTaskDoesNotExist()
        {
            // Arrange
            when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

            // Act + Assert
            assertThatThrownBy(() -> taskService.updateTask(taskId, request))
                    .isInstanceOf(TaskNotFoundException.class)
                    .hasMessage(String.format("Task with ID '%s' does not exist.", taskId));

            // Assert
            verify(taskRepository).findById(taskId);
            verify(taskRepository, never()).save(any(Task.class));
        }

        @Test
        void shouldThrowDuplicateResourceException_whenTitleAlreadyExistsByTitleAndIdNot()
        {
            // Arrange
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
            when(taskRepository.existsByTitleAndIdNot(request.title(), taskId)).thenReturn(true);

            // Act + Assert
            assertThatThrownBy(() -> taskService.updateTask(taskId, request))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessage("This title already exists.");

            // Assert
            verify(taskRepository).findById(taskId);
            verify(taskRepository).existsByTitleAndIdNot(request.title(), taskId);
            verify(taskRepository, never()).save(any(Task.class));
        }

        @Test
        void shouldUpdateSaveAndReturnUpdateTaskResponseDto_whenTitleIsUnique()
        {
            // Arrange
            Task updatedTask = new Task(
                    existingTask.getId(),
                    request.title(),
                    request.description(),
                    request.dueDate(),
                    request.status(),
                    request.priority(),
                    existingTask.getCreated(),
                    Instant.now()
            );

            UpdateTaskResponseDto response = new UpdateTaskResponseDto(
                    updatedTask.getId(),
                    updatedTask.getTitle(),
                    updatedTask.getDescription(),
                    updatedTask.getDueDate(),
                    updatedTask.getPriority(),
                    updatedTask.getStatus()
            );

            when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
            when(taskRepository.existsByTitleAndIdNot(request.title(), taskId)).thenReturn(false);
            when(taskRepository.save(existingTask)).thenReturn(updatedTask);
            when(taskMapper.mapToUpdateTaskResponseDto(updatedTask)).thenReturn(response);

            // Act + Assert
            UpdateTaskResponseDto result = taskService.updateTask(taskId, request);

            // Assert
            assertThat(result).isEqualTo(response);
            verify(taskRepository).findById(taskId);
            verify(taskRepository).existsByTitleAndIdNot(request.title(), taskId);
            verify(taskMapper).updateTaskFromRequest(existingTask, request);
            verify(taskRepository).save(existingTask);
            verify(taskMapper).mapToUpdateTaskResponseDto(updatedTask);
        }
    }

    @Nested
    class getTasks
    {
        Task first;
        Task second;
        ReadTaskResponseDto firstDto;
        ReadTaskResponseDto secondDto;

        @BeforeEach
        void setup()
        {
             first = new Task(
                    UUID.randomUUID(),
                    "First task",
                    "Some description",
                    LocalDate.now().plusDays(3),
                    TaskStatus.OPEN,
                    TaskPriority.MEDIUM,
                    Instant.now(),
                    Instant.now()
            );

             second = new Task(
                    UUID.randomUUID(),
                    "Second task",
                    "Some description",
                    LocalDate.now().plusDays(3),
                    TaskStatus.OPEN,
                    TaskPriority.MEDIUM,
                    Instant.now(),
                    Instant.now()
            );

            firstDto = new ReadTaskResponseDto(
                    taskId,
                    "First task dto",
                    "Some description",
                    LocalDate.now().plusDays(3),
                    TaskPriority.MEDIUM,
                    TaskStatus.OPEN
            );

            secondDto = new ReadTaskResponseDto(
                    taskId,
                    "Second task dto",
                    "Some description",
                    LocalDate.now().plusDays(3),
                    TaskPriority.MEDIUM,
                    TaskStatus.OPEN
            );
        }

        @Test
        void shouldReturnAListOfReadTaskResponseDto_inAscendingOrder()
        {
            // Arrange
            when(taskRepository.findAll(Sort.by(Direction.ASC, "created"))).thenReturn(List.of(first, second));
            when(taskMapper.mapToReadTaskResponseDto(first)).thenReturn(firstDto);
            when(taskMapper.mapToReadTaskResponseDto(second)).thenReturn(secondDto);

            // Act
            List<ReadTaskResponseDto> result = taskService.getTasks();

            // Assert
            assertThat(result).isEqualTo(List.of(firstDto, secondDto));
            verify(taskRepository).findAll(Sort.by(Direction.ASC, "created"));
        }
    }

    @Nested
    class deleteTask
    {
        @Test
        void shouldThrowTaskNotFoundException_whenTaskDoesNotExist()
        {
            // Arrange
            when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

            // Act + Assert
            assertThatThrownBy(() -> taskService.deleteTask(taskId))
                    .isInstanceOf(TaskNotFoundException.class)
                    .hasMessage(String.format("Task with ID '%s' does not exist.", taskId));

            // Assert
            verify(taskRepository).findById(taskId);
            verify(taskRepository, never()).delete(any(Task.class));
        }

        @Test
        void shouldDeleteTask_whenTaskDoesExists()
        {
            // Arrange
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));

            // Act
            taskService.deleteTask(taskId);

            // Assert
            verify(taskRepository).findById(taskId);
            verify(taskRepository).delete(existingTask);
        }
    }
}