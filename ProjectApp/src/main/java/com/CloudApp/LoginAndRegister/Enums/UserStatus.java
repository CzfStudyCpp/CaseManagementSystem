package com.CloudApp.LoginAndRegister.Enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserStatus {
    PENDING,    // 待审核
    ACTIVE,     // 已激活
    SUSPENDED,  // 已冻结
    INACTIVE;   // 非激活状态

    @JsonCreator
    public static UserStatus fromString(String value) {
        return UserStatus.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toString() {
        return name();
    }
}
