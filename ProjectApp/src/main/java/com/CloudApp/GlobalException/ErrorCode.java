package com.CloudApp.GlobalException;

public enum ErrorCode {

    // 通用错误
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权的请求"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源未找到"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),

    // 验证码及验证相关错误
    VERIFICATION_CODE_INVALID(1001, "验证码无效或已过期"),
    VERIFICATION_CODE_MISSING(1002, "验证码缺失"),
    VERIFICATION_CODE_ERROR(1006, "验证码错误"),
    EMAIL_ALREADY_REGISTERED(1003, "邮箱已被注册"),
    USERNAME_ALREADY_EXISTS(1004, "用户名已存在"),
    INVALID_EMAIL_FORMAT(1005, "邮箱格式无效"),
    USERNAME_ALREADY_REGISTERED(1008, "用户名已被注册"),
    ACCOUNT_NOT_FOUND(1009, "账号未注册"),
    USER_PASSWORD_NOT_FOUND(1010,"用户密码信息不存在"),
    INVALID_REQUEST(1011, "无效的请求"),
    ACCOUNT_LOCKED(1012, "账号被冻结"),
    INCORRECT_PASSWORD(1013, "密码错误"),

    // 注册相关错误
    USER_TYPE_INVALID(2001, "无效的用户类型"),
    COMPANY_INFO_MISSING(2002, "企业信息缺失"),
    DEVELOPER_INFO_MISSING(2003, "开发者信息缺失"),
    DUPLICATE_REGISTRATION(2004, "重复提交注册请求"),
    USER_PENDING_APPROVAL(2005, "用户注册正在审批中"),

    // 文件处理相关错误
    FILE_UPLOAD_ERROR(3001, "文件上传失败"),
    FILE_FORMAT_INVALID(3002, "文件格式无效"),
    FILE_SIZE_EXCEEDED(3003, "文件大小超出限制"),
    FILE_STORAGE_ERROR(3004, "文件存储失败"),
    FILE_NOT_FOUND(3005, "文件未找到"),

    // 安全和权限相关错误
    TOKEN_EXPIRED(4001, "访问令牌已过期"),
    TOKEN_INVALID(4002, "访问令牌无效"),
    PERMISSION_DENIED(4003, "权限不足，无法执行操作"),

    // 数据库相关错误
    DATABASE_CONNECTION_ERROR(5001, "数据库连接失败"),
    DATABASE_CONSTRAINT_VIOLATION(5002, "数据库约束违反"),
    DATA_INTEGRITY_ERROR(5003, "数据完整性错误"),

    // 前端 UI 通用状态码
    VALIDATION_ERROR(9001, "前端输入校验错误"),
    UI_RENDER_ERROR(9002, "页面渲染错误"),
    NETWORK_ERROR(9003, "网络连接错误");


    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
