package com.CloudApp.LoginAndRegister.Enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserType {
    DEVELOPER,  // 开发者
    COMPANY;    // 企业

    @JsonCreator
    public static UserType fromString(String value) {
        return UserType.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toString() {
        return name();
    }
}
