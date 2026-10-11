package com.devtiro.task.controller;

import com.devtiro.task.domain.dto.request.CreateTaskRequestDto;
import com.devtiro.task.domain.dto.request.UpdateTaskRequestDto;
import com.devtiro.task.domain.dto.response.CreateTaskResponseDto;
import com.devtiro.task.domain.dto.response.ReadTaskResponseDto;
import com.devtiro.task.domain.dto.response.UpdateTaskResponseDto;
import com.devtiro.task.domain.entity.TaskPriority;
import com.devtiro.task.domain.entity.TaskStatus;
import com.devtiro.task.exception.DuplicateResourceException;
import com.devtiro.task.exception.TaskNotFoundException;
import com.devtiro.task.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class TaskControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID taskId;
    private ReadTaskResponseDto existingTask;

    @BeforeEach
    void setup()
    {
        taskId = UUID.randomUUID();
        existingTask = new ReadTaskResponseDto(
                taskId,
                "Existing task",
                "Some description",
                LocalDate.now().plusDays(3),
                TaskPriority.MEDIUM,
                TaskStatus.OPEN
        );
    }

    @Nested
    class getTasks
    {
        ReadTaskResponseDto firstDto;
        ReadTaskResponseDto secondDto;

        @BeforeEach
        void setup()
        {
            firstDto = new ReadTaskResponseDto(
                    UUID.randomUUID(),
                    "First task dto",
                    "Some description",
                    LocalDate.now().plusDays(3),
                    TaskPriority.MEDIUM,
                    TaskStatus.OPEN
            );

            secondDto = new ReadTaskResponseDto(
                    UUID.randomUUID(),
                    "Second task dto",
                    "Some description",
                    LocalDate.now().plusDays(3),
                    TaskPriority.MEDIUM,
                    TaskStatus.OPEN
            );
        }

        @Test
        void shouldReturnAListOfReadTaskResponseDtos_whenTasksExists() throws Exception
        {
            // Arrange
            List<ReadTaskResponseDto> list = List.of(firstDto, secondDto);
            when(taskService.getTasks()).thenReturn(list);

            // Act & Assert
            mockMvc.perform(get("/api/v1/tasks").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(list.getFirst().id().toString()))
                    .andExpect(jsonPath("$[1].id").value(list.getLast().id().toString()))
                    .andExpect(jsonPath("$[0].title").value(list.getFirst().title()))
                    .andExpect(jsonPath("$[1].title").value(list.getLast().title()))
                    .andExpect(jsonPath("$[0].description").value(list.getFirst().description()))
                    .andExpect(jsonPath("$[1].description").value(list.getLast().description()))
                    .andExpect(jsonPath("$[0].dueDate").value(list.getFirst().dueDate().toString()))
                    .andExpect(jsonPath("$[1].dueDate").value(list.getLast().dueDate().toString()))
                    .andExpect(jsonPath("$[0].priority").value(list.getFirst().priority().toString()))
                    .andExpect(jsonPath("$[1].priority").value(list.getLast().priority().toString()))
                    .andExpect(jsonPath("$[0].status").value(list.getFirst().status().toString()))
                    .andExpect(jsonPath("$[1].status").value(list.getLast().status().toString()));

            // Assert
            verify(taskService).getTasks();
        }

        @Test
        void shouldReturnEmptyListOfReadTaskResponseDtos_whenTasksDoNoExists() throws Exception
        {
            // Arrange
            when(taskService.getTasks()).thenReturn(List.of());

            // Act & Assert
            mockMvc.perform(get("/api/v1/tasks").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(0));

            // Assert
            verify(taskService).getTasks();
        }
    }

    @Nested
    class createTask
    {
        CreateTaskRequestDto request;

        @BeforeEach
        void setup()
        {
            request = new CreateTaskRequestDto(
                    "Unique title",
                    "Some description",
                    LocalDate.now().plusDays(3),
                    TaskPriority.MEDIUM
            );
        }

        @Test
        void shouldSaveAndReturnCreateTaskResponseDto_whenTitleIsUnique() throws Exception
        {
            // Arrange
            CreateTaskResponseDto response = new CreateTaskResponseDto(
                    UUID.randomUUID(),
                    request.title(),
                    request.description(),
                    request.dueDate(),
                    request.priority(),
                    TaskStatus.OPEN
            );

            when(taskService.createTask(request)).thenReturn(response);

            // Act & Assert
            mockMvc.perform(post("/api/v1/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(response.id().toString()))
                    .andExpect(jsonPath("$.title").value(response.title()))
                    .andExpect(jsonPath("$.description").value(response.description()))
                    .andExpect(jsonPath("$.dueDate").value(response.dueDate().toString()))
                    .andExpect(jsonPath("$.priority").value(response.priority().toString()))
                    .andExpect(jsonPath("$.status").value(response.status().toString()));

            // Assert
            verify(taskService).createTask(request);
        }

        @Test
        void shouldThrowDuplicateResourceException_whenTaskAlreadyExists() throws Exception
        {
            // Arrange
            String message = "This title already exists.";
            when(taskService.createTask(request)).thenThrow(new DuplicateResourceException(message));

            // Act & Assert
            mockMvc.perform(post("/api/v1/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.timeStamp").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                    .andExpect(jsonPath("$.errorPhrase").value(HttpStatus.CONFLICT.getReasonPhrase()))
                    .andExpect(jsonPath("$.errorMessage").value(new DuplicateResourceException(message).getMessage()))
                    .andExpect(jsonPath("$.path").value("/api/v1/tasks"));

            // Assert
            verify(taskService).createTask(request);
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
                    LocalDate.now().plusDays(2),
                    TaskPriority.HIGH,
                    TaskStatus.OPEN
            );
        }

        @Test
        void shouldReturnAUpdateTaskResponseDto_whenTaskExistsAndIsNotDuplicate() throws Exception
        {
            // Arrange
            UpdateTaskResponseDto response = new UpdateTaskResponseDto(
                    taskId,
                    request.title(),
                    request.description(),
                    request.dueDate(),
                    request.priority(),
                    request.status()
            );

            when(taskService.updateTask(taskId, request)).thenReturn(response);

            // Act + Assert
            mockMvc.perform(put(String.format("/api/v1/tasks/%s", taskId))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(response.id().toString()))
                    .andExpect(jsonPath("$.title").value(response.title()))
                    .andExpect(jsonPath("$.description").value(response.description()))
                    .andExpect(jsonPath("$.dueDate").value(response.dueDate().toString()))
                    .andExpect(jsonPath("$.priority").value(response.priority().toString()))
                    .andExpect(jsonPath("$.status").value(response.status().toString()));

            // Assert
            verify(taskService).updateTask(taskId, request);
        }

        @Test
        void shouldThrowTaskNotFoundException_whenTaskDoesNotExist() throws Exception
        {
            // Arrange
            when(taskService.updateTask(taskId, request)).thenThrow(new TaskNotFoundException(taskId));

            // Act & Assert
            mockMvc.perform(put(String.format("/api/v1/tasks/%s", taskId))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.timeStamp").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.errorPhrase").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                    .andExpect(jsonPath("$.errorMessage").value(new TaskNotFoundException(taskId).getMessage()))
                    .andExpect(jsonPath("$.path").value(String.format("/api/v1/tasks/%s", taskId)));

            // Assert
            verify(taskService).updateTask(taskId, request);
        }

        @Test
        void shouldThrowDuplicateResourceException_whenTaskAlreadyExists() throws Exception
        {
            // Arrange
            String message = "This title already exists.";
            when(taskService.updateTask(taskId, request)).thenThrow(new DuplicateResourceException(message));

            // Act & Assert
            mockMvc.perform(put(String.format("/api/v1/tasks/%s", taskId))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.timeStamp").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                    .andExpect(jsonPath("$.errorPhrase").value(HttpStatus.CONFLICT.getReasonPhrase()))
                    .andExpect(jsonPath("$.errorMessage").value(new DuplicateResourceException(message).getMessage()))
                    .andExpect(jsonPath("$.path").value(String.format("/api/v1/tasks/%s", taskId)));

            // Assert
            verify(taskService).updateTask(taskId, request);
        }
    }

    @Nested
    class deleteTask
    {
        @Test
        void shouldReturnAReadTaskResponseDto_whenTaskExists() throws Exception
        {
            // Act & Assert
            mockMvc.perform(delete(String.format("/api/v1/tasks/%s", taskId)).accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            // Assert
            verify(taskService).deleteTask(taskId);
        }

        @Test
        void shouldThrowTaskNotFoundException_whenTaskDoesNotExist() throws Exception
        {
            // Arrange
            doThrow(new TaskNotFoundException(taskId)).when(taskService).deleteTask(taskId);

            // Act & Assert
            mockMvc.perform(delete(String.format("/api/v1/tasks/%s", taskId)).accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.timeStamp").exists())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.errorPhrase").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                    .andExpect(jsonPath("$.errorMessage").value(new TaskNotFoundException(taskId).getMessage()))
                    .andExpect(jsonPath("$.path").value(String.format("/api/v1/tasks/%s", taskId)));

            // Assert
            verify(taskService).deleteTask(taskId);
        }
    }
}