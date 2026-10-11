package com.devtiro.task.controller;

import com.devtiro.task.domain.dto.request.CreateTaskRequestDto;
import com.devtiro.task.domain.entity.TaskPriority;
import com.devtiro.task.service.TaskService;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class GlobalExceptionHandlerTest
{
    private static final String TASKS_URL = "/api/v1/tasks";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    private CreateTaskRequestDto validRequest()
    {
        return new CreateTaskRequestDto(
                "Some title",
                "Some description",
                LocalDate.now().plusDays(3),
                TaskPriority.MEDIUM
        );
    }

    @Nested
    class validation
    {
        @Test
        void shouldReturn400WithFieldErrors_whenRequiredFieldsAreInvalid() throws Exception
        {
            // Arrange
            String body = """
                    {"title": "   ", "priority": null}
                    """;

            // Act & Assert
            mockMvc.perform(post(TASKS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.errorMessage").value("Validation Failed"))
                    .andExpect(jsonPath("$.path").value(TASKS_URL))
                    .andExpect(jsonPath("$.fieldErrors.title")
                            .value("Title must be between 1 and 255 characters"))
                    .andExpect(jsonPath("$.fieldErrors.priority")
                            .value("Task priority must be provided"));

            verifyNoInteractions(taskService);
        }

        @Test
        void shouldReturn400WithDueDateError_whenDueDateIsInThePast() throws Exception
        {
            // Arrange
            String body = """
                    {"title": "Some title", "priority": "HIGH", "dueDate": "2000-01-01"}
                    """;

            // Act & Assert
            mockMvc.perform(post(TASKS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorMessage").value("Validation Failed"))
                    .andExpect(jsonPath("$.fieldErrors.dueDate").exists());

            verifyNoInteractions(taskService);
        }
    }

    @Nested
    class unreadableRequests
    {
        @Test
        void shouldReturn400_whenJsonIsMalformed() throws Exception
        {
            mockMvc.perform(post(TASKS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{not valid json")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.errorMessage").value("Malformed JSON request"))
                    .andExpect(jsonPath("$.path").value(TASKS_URL));

            verifyNoInteractions(taskService);
        }

        @Test
        void shouldReturn400_whenEnumValueIsUnknown() throws Exception
        {
            // Arrange
            String body = """
                    {"title": "Some title", "priority": "URGENT"}
                    """;

            // Act & Assert
            mockMvc.perform(post(TASKS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorMessage").value("Malformed JSON request"));

            verifyNoInteractions(taskService);
        }

        @Test
        void shouldReturn400_whenTaskIdIsNotAUuid() throws Exception
        {
            mockMvc.perform(get(TASKS_URL + "/not-a-uuid").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.errorMessage").value("Invalid value for parameter 'taskId'"))
                    .andExpect(jsonPath("$.path").value(TASKS_URL + "/not-a-uuid"));

            verifyNoInteractions(taskService);
        }
    }

    @Nested
    class dataIntegrityViolation
    {
        @Test
        void shouldReturn409WithTitleMessage_whenTitleConstraintIsViolated() throws Exception
        {
            // Arrange: H2 reports the constraint name upper-cased and index-suffixed
            ConstraintViolationException cause = new ConstraintViolationException(
                    "could not execute statement",
                    new SQLException("duplicate"),
                    "PUBLIC.UK_TASKS_TITLE_INDEX_4");

            when(taskService.createTask(any()))
                    .thenThrow(new DataIntegrityViolationException("conflict", cause));

            // Act & Assert
            mockMvc.perform(post(TASKS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest()))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.errorMessage").value("This title already exists."));
        }

        @Test
        void shouldReturn409WithGenericMessage_whenAnotherConstraintIsViolated() throws Exception
        {
            // Arrange
            ConstraintViolationException cause = new ConstraintViolationException(
                    "could not execute statement",
                    new SQLException("other"),
                    "fk_something_else");

            when(taskService.createTask(any()))
                    .thenThrow(new DataIntegrityViolationException("conflict", cause));

            // Act & Assert
            mockMvc.perform(post(TASKS_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest()))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.errorMessage").value("A data integrity conflict occurred"));
        }
    }

    @Nested
    class unexpectedErrors
    {
        @Test
        void shouldReturn500WithGenericMessage_andNotLeakInternalDetails() throws Exception
        {
            // Arrange
            when(taskService.getTasks()).thenThrow(new IllegalStateException("secret internal detail"));

            // Act & Assert
            mockMvc.perform(get(TASKS_URL).accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.status").value(500))
                    .andExpect(jsonPath("$.errorMessage")
                            .value("Something went wrong. Please try again later"))
                    .andExpect(jsonPath("$.path").value(TASKS_URL));
        }
    }
}