package com.CloudApp.adminService.Response;

import com.CloudApp.GlobalException.ErrorCode;

//成功 200
//服务器错误 500
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;
    private int statusCode;

    // 全参构造方法
    public ApiResponse(boolean success, String message, Object data, int statusCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.statusCode = statusCode;
    }

    // 静态方法用于快速生成成功响应
    public static ApiResponse success(String message, Object data, int statusCode) {
        return new ApiResponse(true, message, data, statusCode);
    }

    public static ApiResponse success(String message, int statusCode) {
        return new ApiResponse(true, message, null, statusCode);
    }

    public static ApiResponse success(ErrorCode errorCode, Object data) {
        return new ApiResponse(true, errorCode.getMessage(), data, errorCode.getCode());
    }

    public static ApiResponse success(ErrorCode errorCode) {
        return new ApiResponse(true, errorCode.getMessage(), null, errorCode.getCode());
    }

    // 静态方法用于快速生成失败响应
    public static ApiResponse error(String message, int statusCode) {
        return new ApiResponse(false, message, null, statusCode);
    }

    public static ApiResponse error(ErrorCode errorCode) {
        return new ApiResponse(false, errorCode.getMessage(), null, errorCode.getCode());
    }

    public static ApiResponse error(ErrorCode errorCode, Object data) {
        return new ApiResponse(false, errorCode.getMessage(), data, errorCode.getCode());
    }

    // Getter 和 Setter 方法
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
