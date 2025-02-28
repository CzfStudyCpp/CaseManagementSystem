package com.CloudApp.GlobalException;

import com.CloudApp.GlobalException.AccountException.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 处理 IllegalArgumentException 异常
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        String requestPath = request.getDescription(false).replace("uri=", "");
        logger.error("非法参数异常: {}, 请求路径: {}", ex.getMessage(), requestPath);

        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.BAD_REQUEST.getMessage(),
                ex.getMessage(),
                ErrorCode.BAD_REQUEST.getCode(),
                requestPath
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 处理 RuntimeException 异常
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        String requestPath = request.getDescription(false).replace("uri=", "");
        logger.error("运行时异常: {}, 请求路径: {}", ex.getMessage(), requestPath, ex);

        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                ex.getMessage(),
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                requestPath
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 处理 MethodArgumentNotValidException 校验失败
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        });

        String requestPath = request.getDescription(false).replace("uri=", "");

        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.BAD_REQUEST.getMessage(),
                "字段校验错误",
                ErrorCode.BAD_REQUEST.getCode(),
                requestPath
        );
        errorResponse.setFieldErrors(fieldErrors); // 设置字段校验错误信息

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 处理通用 Exception 异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        String requestPath = request.getDescription(false).replace("uri=", "");
        logger.error("未知异常: {}, 请求路径: {}", ex.getMessage(), requestPath, ex);

        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                ex.getMessage(),
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                requestPath
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, WebRequest request) {
        String requestPath = request.getDescription(false).replace("uri=", "");
        logger.error("业务异常: {}, 请求路径: {}", ex.getMessage(), requestPath);

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getErrorCode().getMessage(), // 从 ErrorCode 获取用户友好的错误信息
                ex.getMessage(), // 具体错误描述
                ex.getErrorCode().getCode(), // 错误码
                requestPath
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理账号未找到异常
     */
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAccountNotFound(AccountNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "message", ex.getMessage(),
                "code", ErrorCode.ACCOUNT_NOT_FOUND.getCode()
        ));
    }

    /**
     * 处理用户密码不存在异常
     */
    @ExceptionHandler(UserPasswordNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserPasswordNotFound(UserPasswordNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "message", ex.getMessage(),
                "code", ErrorCode.USER_PASSWORD_NOT_FOUND.getCode()
        ));
    }

    /**
     * 处理密码错误异常
     */
    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<Map<String, Object>> handleIncorrectPassword(IncorrectPasswordException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "message", ex.getMessage(),
                "code", ErrorCode.INCORRECT_PASSWORD.getCode()
        ));
    }

    /**
     * 处理账号锁定异常
     */
    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<Map<String, Object>> handleAccountLocked(AccountLockedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "message", ex.getMessage(),
                "code", ErrorCode.ACCOUNT_LOCKED.getCode()
        ));
    }
}
