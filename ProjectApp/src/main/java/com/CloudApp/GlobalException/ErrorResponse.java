package com.CloudApp.GlobalException;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class ErrorResponse {
    private String message; // 错误消息
    private String details; // 错误的详细信息
    private int status; // HTTP 状态码
    private LocalDateTime timestamp; // 时间戳
    private String path; // 请求路径
    private String errorCode; // 自定义错误代码
    private Map<String, String> fieldErrors; // 字段校验错误信息（可选）

    public ErrorResponse(String message, String details, int status, String path) {
        this.message = message;
        this.details = details;
        this.status = status;
        this.timestamp = LocalDateTime.now(); // 当前时间
        this.path = path;
    }

    public ErrorResponse(String message, String details, int status, String path, Map<String, String> fieldErrors) {
        this(message, details, status, path);
        this.fieldErrors = fieldErrors;
    }

    public ErrorResponse(String message, String details, int status, String path, String errorCode) {
        this(message, details, status, path);
        this.errorCode = errorCode;
    }
}
