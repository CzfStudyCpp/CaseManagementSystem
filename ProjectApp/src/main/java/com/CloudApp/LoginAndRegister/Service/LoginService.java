package com.CloudApp.LoginAndRegister.Service;

import com.CloudApp.GlobalException.AccountException.AccountLockedException;
import com.CloudApp.GlobalException.AccountException.AccountNotFoundException;
import com.CloudApp.GlobalException.AccountException.IncorrectPasswordException;
import com.CloudApp.GlobalException.AccountException.UserPasswordNotFoundException;
import com.CloudApp.LoginAndRegister.Mapper.UserMapper;
import com.CloudApp.LoginAndRegister.Entities.User;
import com.CloudApp.LoginAndRegister.Entities.UserPassword;
import com.CloudApp.LoginAndRegister.Mapper.UserPasswordMapper;
import com.CloudApp.LoginAndRegister.Utils.JwtUtils;
import com.CloudApp.RedisCommon.Service.RedisTokenCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class LoginService {

    private final UserMapper userMapper;  // UserMapper 用来查询 User
    private final UserPasswordMapper userPasswordMapper;  // 用来查询 UserPassword
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final RedisTokenCacheService redisTokenCacheService;

    private final RedisTemplate<String, Object> redisTemplate;


    private static final int MAX_LOGIN_ATTEMPTS = 5; // 最大登录失败次数
    private static final int LOGIN_ATTEMPTS_EXPIRE = 10; // 登录失败计数有效时间（分钟）

    // 构造函数注入
    @Autowired
    public LoginService(UserMapper userMapper, UserPasswordMapper userPasswordMapper,
                        JwtUtils jwtUtils, PasswordEncoder passwordEncoder, RedisTokenCacheService redisCacheService, RedisTemplate<String, Object> redisTemplate) {
        this.userMapper = userMapper;
        this.userPasswordMapper = userPasswordMapper;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.redisTokenCacheService = redisCacheService;
        this.redisTemplate = redisTemplate;
    }

    // 检查登录失败次数
    private void checkLoginAttempts(String email) {
        String key = "login:attempts:" + email;
        // 从 Redis 获取登录失败次数
        Object attemptsObj = redisTemplate.opsForValue().get(key);
        Integer attempts = attemptsObj != null ? Integer.parseInt(attemptsObj.toString()) : 0;
        if (attempts != null && attempts >= MAX_LOGIN_ATTEMPTS) {
            throw new RuntimeException("登录失败次数过多，请稍后再试！");
        }
    }

    // 增加登录失败计数
    private void incrementLoginAttempts(String email) {
        String key = "login:attempts:" + email;
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, LOGIN_ATTEMPTS_EXPIRE, TimeUnit.MINUTES); // 设置过期时间
    }


    // 清除登录失败计数（登录成功时调用）
    private void clearLoginAttempts(String email) {
        String key = "login:attempts:" + email;
        redisTemplate.delete(key);
    }

    public Map<String, String> login(String email, String password) {
        // 检查是否超出登录失败次数
        checkLoginAttempts(email);

        // 查找用户
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            throw new AccountNotFoundException("邮箱未注册");
        }

        // 查找对应的用户密码
        UserPassword userPassword = userPasswordMapper.selectByUserId(user.getId());
        if (userPassword == null) {
            incrementLoginAttempts(email);
            throw new UserPasswordNotFoundException("用户密码信息不存在");
        }

        // 校验密码
        if (!passwordEncoder.matches(password, userPassword.getPasswordHash())) {
            incrementLoginAttempts(email);
            throw new IncorrectPasswordException("密码错误");
        }

        // 校验账号状态
        if ("SUSPENDED".equals(user.getStatus())) {
            incrementLoginAttempts(email);
            throw new AccountLockedException("账号已被冻结");
        }

        // 登录成功，清除失败计数
        clearLoginAttempts(email);

        // 检查 Redis 中是否已存储 JWT Token
        String token = redisTokenCacheService.getJwtToken(user.getUsername());
        if (token == null) {
            // 如果 Redis 中没有 Token，生成新的 JWT Token
            String role = user.getUserType().toString(); // 获取用户的类型（DEVELOPER 或 COMPANY）
            token = jwtUtils.generateToken(user.getUsername(), role);
            // 将生成的 JWT Token 存储到 Redis
            redisTokenCacheService.storeJwtToken(user.getUsername(), token);
        }

        // 返回 token、userType 和 username
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("userType", user.getUserType().toString());
        response.put("username", user.getUsername());
        return response;
    }


}