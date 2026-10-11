package com.devtiro.task.exception;

public class DuplicateResourceException extends RuntimeException
{
    public DuplicateResourceException(String message)
    {
        super(message);
    }
}
