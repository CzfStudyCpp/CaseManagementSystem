package com.CloudApp.LoginAndRegister.Service;

import com.CloudApp.RedisCommon.Service.RedisTokenCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogoutService {
    @Autowired
    RedisTokenCacheService redisTokenCacheService;

    public void logout(String userName) {
        // 删除 Redis 中的 Token
        redisTokenCacheService.deleteJwtToken(userName);
        System.out.println("用户 " + userName + " 的 Token 已成功删除");

    }
}
