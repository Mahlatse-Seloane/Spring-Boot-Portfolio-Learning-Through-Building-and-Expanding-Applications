package com.devtiro.task.domain.dto.response;

import java.time.LocalDateTime;

public record ExceptionResponseDto(
    LocalDateTime timeStamp,
    int status,
    String errorPhrase,
    String errorMessage,
    String path
)
{}
