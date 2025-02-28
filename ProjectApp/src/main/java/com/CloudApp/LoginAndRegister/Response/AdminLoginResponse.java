package com.CloudApp.LoginAndRegister.Response;

import lombok.Getter;
import lombok.Setter;

public class AdminLoginResponse {

    // Getter and Setter
    @Setter
    @Getter
    private String token;
    @Setter
    @Getter
    private String accessLevel;

    public AdminLoginResponse(String token,String accessLevel) {
        this.token = token;
        this.accessLevel = accessLevel;
    }

}

