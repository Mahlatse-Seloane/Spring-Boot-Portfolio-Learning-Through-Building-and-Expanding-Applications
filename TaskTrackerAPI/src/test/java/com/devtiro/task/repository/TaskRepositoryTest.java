package com.devtiro.task.repository;

import com.devtiro.task.domain.entity.Task;
import com.devtiro.task.domain.entity.TaskPriority;
import com.devtiro.task.domain.entity.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest
{
    @Autowired
    private TaskRepository taskRepository;

    private Task existingTask;

    @BeforeEach
    void setup()
    {
        existingTask = new Task(
                null,
                "Existing title",
                "Some description",
                LocalDate.now().plusDays(3),
                TaskStatus.OPEN,
                TaskPriority.MEDIUM,
                Instant.now(),
                Instant.now()
        );
    }

    @Nested
    class existsByTitle
    {
        @Test
        void shouldReturnTrue_whenTitleExists()
        {
            // Arrange
            Task savedTask = taskRepository.save(existingTask);

            // Act
            boolean result = taskRepository.existsByTitle(savedTask.getTitle());

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        void shouldReturnFalse_whenTitleDoesNotExist()
        {
            // Arrange
            Task task = new Task(
                    null,
                    "Unique title",
                    "Some description",
                    LocalDate.now().plusDays(3),
                    TaskStatus.OPEN,
                    TaskPriority.MEDIUM,
                    Instant.now(),
                    Instant.now()
            );

            // Act
            boolean result = taskRepository.existsByTitle(task.getTitle());

            // Assert
            assertThat(result).isFalse();
        }
    }

    @Nested
    class existsByTitleAndIdNot
    {
        @Test
        void shouldReturnTrue_whenADifferentTaskHasThisTitle()
        {
            // Arrange
            Task taskA = taskRepository.save(new Task(
                    null,
                    "Task A",
                    "Some description",
                    LocalDate.now().plusDays(3),
                    TaskStatus.OPEN,
                    TaskPriority.MEDIUM,
                    Instant.now(),
                    Instant.now()
            ));

            Task taskB = taskRepository.save(new Task(
                    null,
                    "Task B",
                    "Some description",
                    LocalDate.now().plusDays(3),
                    TaskStatus.OPEN,
                    TaskPriority.MEDIUM,
                    Instant.now(),
                    Instant.now()
            ));

            // Act
            boolean result = taskRepository.existsByTitleAndIdNot("Task A", taskB.getId());

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        void shouldReturnFalse_whenOnlyThisSameTaskHasTheTitle()
        {
            // Arrange: a single saved task
            Task savedTask = taskRepository.save(new Task(
                    null,
                    "Existing title",
                    "Some description",
                    LocalDate.now().plusDays(3),
                    TaskStatus.OPEN,
                    TaskPriority.MEDIUM,
                    Instant.now(),
                    Instant.now()
            ));

            // Act: simulate "updating" the task but keeping its own current title,
            // excluding its own ID from the search
            boolean result = taskRepository.existsByTitleAndIdNot(
                    savedTask.getTitle(), savedTask.getId());

            // Assert: no OTHER task has this title, so this is not a conflict —
            // you're allowed to keep your own existing title.
            assertThat(result).isFalse();
        }
    }
}