package com.devtiro.task.exception;

import java.util.UUID;

public class TaskNotFoundException extends RuntimeException
{
    public TaskNotFoundException(UUID id)
    {
        super(String.format("Task with ID '%s' does not exist.", id));
    }
}
