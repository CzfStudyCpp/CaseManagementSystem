package com.CloudApp.LoginAndRegister.Service;

import com.CloudApp.LoginAndRegister.Mapper.AdminMapper;
import com.CloudApp.LoginAndRegister.Entities.Admin;
import com.CloudApp.LoginAndRegister.Request.AdminLoginRequest;
import com.CloudApp.LoginAndRegister.Response.AdminLoginResponse;
import com.CloudApp.LoginAndRegister.Utils.JwtUtils;
import com.CloudApp.RedisCommon.Service.RedisTokenCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminLoginService {

    private final AdminMapper adminMapper; // Admin Mapper 用来查询管理员
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RedisTokenCacheService redisTokenCacheService;

    @Autowired
    public AdminLoginService(AdminMapper adminMapper, PasswordEncoder passwordEncoder,
                             JwtUtils jwtUtils, RedisTokenCacheService redisTokenCacheService) {
        this.adminMapper = adminMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.redisTokenCacheService = redisTokenCacheService;
    }

    public AdminLoginResponse login(AdminLoginRequest request) {
        // 查找管理员
        Admin admin = adminMapper.selectByUsername(request.getUsername());
        if (admin == null) {
            throw new RuntimeException("管理员不存在");
        }

        // 校验密码
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new RuntimeException("密码错误");

        }

        System.out.println("查询到的管理员用户名: " + admin.getUsername());
        System.out.println("查询到的管理员加密密码: " + admin.getPassword());
        // 校验账号状态
        if ("inactive".equals(admin.getStatus())) {
            throw new RuntimeException("账号已被冻结");
        }

        // 判断管理员角色
        String role = admin.getIsSuperAdmin() ? "SUPER_ADMIN" : "NORMAL_ADMIN";

        // 生成 JWT Token，包含角色信息
        String token = jwtUtils.generateToken(admin.getUsername(), role);


        // 将 JWT 存入 Redis
        redisTokenCacheService.storeJwtToken(admin.getUsername(), token);

        return new AdminLoginResponse(token,role);
    }
}

