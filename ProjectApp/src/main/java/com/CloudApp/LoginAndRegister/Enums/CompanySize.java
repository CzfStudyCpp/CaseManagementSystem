package com.CloudApp.LoginAndRegister.Enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CompanySize {
    SMALL, MEDIUM, LARGE;

    @JsonCreator
    public static CompanySize fromString(String value) {
        // 忽略大小写进行匹配
        return CompanySize.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toString() {
        // 序列化时返回大写形式
        return name();
    }
}

