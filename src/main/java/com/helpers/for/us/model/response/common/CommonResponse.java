package com.you.and.me.model.response.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonResponse<T> {
    private int code;
    private String status;
    private String message;
    private T data;
    private Instant timestamp = Instant.now();
}
