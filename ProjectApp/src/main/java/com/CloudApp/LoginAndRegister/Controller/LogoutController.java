package com.CloudApp.LoginAndRegister.Controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.CloudApp.LoginAndRegister.Service.LogoutService;
@RestController
@RequestMapping("/auth")
public class LogoutController {

    @Autowired
    LogoutService logoutService;
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        try {
            // 从 SecurityContextHolder 获取当前登录用户的用户名
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            // 调用登出服务清理 Redis 中的 Token
            logoutService.logout(username);

            return ResponseEntity.ok("用户 " + username + " 已成功登出");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("登出失败：" + e.getMessage());
        }
    }
}
