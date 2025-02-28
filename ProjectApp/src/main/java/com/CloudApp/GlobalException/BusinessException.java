package com.CloudApp.GlobalException;

import com.CloudApp.GlobalException.ErrorCode;

// 自定义业务异常类
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode; // 自定义错误码

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // 使用 ErrorCode 中的消息作为异常消息
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage); // 使用自定义消息作为异常消息
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
