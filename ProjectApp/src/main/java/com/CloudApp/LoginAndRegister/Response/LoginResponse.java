package com.CloudApp.LoginAndRegister.Response;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class LoginResponse {

    private String token;        // 登录生成的 Token
    private String userType;     // 用户类型
    private String username;     // 用户名

    // 全参构造方法
    public LoginResponse(String token, String userType, String username) {
        this.token = token;
        this.userType = userType;
        this.username = username;
    }
}
