package com.CloudApp.RedisCommon.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
public class RedisTokenCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    // 使用构造函数注入
    public RedisTokenCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 存储 JWT Token到 Redis
    public void storeJwtToken(String username, String token) {
        redisTemplate.opsForValue().set("jwt:" + username, token, 1, TimeUnit.HOURS);  // 设置过期时间为1小时
    }

    // 获取存储的 JWT Token
    public String getJwtToken(String username) {
        return (String) redisTemplate.opsForValue().get("jwt:" + username);
    }

    // 删除存储的 JWT Token
    public void deleteJwtToken(String username) {
        redisTemplate.delete("jwt:" + username);
    }
}
