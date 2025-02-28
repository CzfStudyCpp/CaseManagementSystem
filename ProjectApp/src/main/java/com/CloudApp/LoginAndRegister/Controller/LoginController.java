//package com.CloudApp.LoginAndRegister.Controller;
////登录认证的请求回复处理接口
//import com.CloudApp.LoginAndRegister.Request.LoginRequest;
//import com.CloudApp.LoginAndRegister.Response.LoginResponse;
//
//
//import com.CloudApp.LoginAndRegister.Utils.JwtUtils;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestBody;
//
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//
//@RestController
//@RequestMapping("/api/login")
//public class LoginController {
//
//    private final AuthenticationManager authenticationManager;
//    private final JwtUtils jwtUtils;
//
//    // 构造函数注入
//    public LoginController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
//        this.authenticationManager = authenticationManager;
//        this.jwtUtils = jwtUtils;
//    }
//
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
//        try {
//            // 进行身份认证
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
//            );
//
//            // 提取用户名，生成 JWT 令牌
//            String username = authentication.getName();  // 获取用户名
//            String token = jwtUtils.generateToken(username);  // 使用用户名生成 token
//
//            return ResponseEntity.ok(new LoginResponse(token));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("登录失败: " + e.getMessage());
//        }
//    }
//}







package com.CloudApp.LoginAndRegister.Controller;

import com.CloudApp.GlobalException.AccountException.AccountLockedException;
import com.CloudApp.GlobalException.AccountException.AccountNotFoundException;
import com.CloudApp.GlobalException.AccountException.IncorrectPasswordException;
import com.CloudApp.GlobalException.AccountException.UserPasswordNotFoundException;
import com.CloudApp.GlobalException.ErrorCode;

import com.CloudApp.LoginAndRegister.Entities.User;
import com.CloudApp.LoginAndRegister.Service.LoginService;
import com.CloudApp.LoginAndRegister.Request.LoginRequest;
import com.CloudApp.LoginAndRegister.Response.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;

@ControllerAdvice
@RestController
@RequestMapping("/user")
public class LoginController {



    private final LoginService loginService;



    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "邮箱或密码不能为空",
                    "code", ErrorCode.INVALID_REQUEST.getCode()
            ));
        }

        try {
            // 调用业务逻辑
            Map<String, String> loginResult = loginService.login(loginRequest.getEmail(), loginRequest.getPassword());

            // 从业务层获取 token 和用户类型
            String token = loginResult.get("token");
            String userType = loginResult.get("userType");
            String username = loginResult.get("username");

            // 封装响应
            LoginResponse loginResponse = new LoginResponse(token, userType, username);

            return ResponseEntity.ok(loginResponse);
        } catch (AccountNotFoundException | UserPasswordNotFoundException | IncorrectPasswordException |
                 AccountLockedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "message", e.getMessage(),
                    "code", ErrorCode.UNAUTHORIZED.getCode()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "登录失败，请稍后重试",
                    "code", ErrorCode.INTERNAL_SERVER_ERROR.getCode()
            ));
        }
    }


}

