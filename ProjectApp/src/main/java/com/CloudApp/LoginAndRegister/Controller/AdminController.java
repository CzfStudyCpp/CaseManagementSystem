package com.CloudApp.LoginAndRegister.Controller;

import com.CloudApp.LoginAndRegister.Request.AdminLoginRequest;
import com.CloudApp.LoginAndRegister.Response.AdminLoginResponse;
import com.CloudApp.LoginAndRegister.Service.AdminLoginService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@ControllerAdvice
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminLoginService adminLoginService;  // 用于管理员登录验证

    @GetMapping ("/test")
    public String adminLogin(){
        return "success";
    }


    // 构造函数注入
    @Autowired
    public AdminController(AdminLoginService adminLoginService) {
        this.adminLoginService = adminLoginService;

       // System.out.println("Admin Login Request(管理员登陆接口控制器初始化): ");
    }



    // 管理员登录接口
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> adminLogin(@RequestBody AdminLoginRequest loginRequest) {
        System.out.println("Admin Login Request(管理员登陆接口被访问): " + loginRequest);
        try {
            // 使用 AdminLoginService 进行身份认证并生成 JWT Token
            AdminLoginResponse response = adminLoginService.login(loginRequest);

            // 返回生成的 JWT Token和管理员身份
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            // 登录失败时返回错误信息
            return ResponseEntity.status(401).body("登录失败: " + e.getMessage());
        }
    }


}
