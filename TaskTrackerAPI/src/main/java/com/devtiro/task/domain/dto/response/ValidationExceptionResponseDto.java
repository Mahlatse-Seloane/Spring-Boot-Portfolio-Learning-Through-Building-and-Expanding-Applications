package com.devtiro.task.domain.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationExceptionResponseDto(
        LocalDateTime timeStamp,
        int status,
        String errorPhrase,
        String errorMessage,
        String path,
        Map<String, String> fieldErrors)
{}
