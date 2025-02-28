package com.CloudApp.RedisCommon.Service;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisEmailCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisEmailCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 存储验证码到 Redis，设置过期时间为3分钟
     *
     * @param email 用户邮箱
     * @param code  验证码
     */
    public void storeVerificationCode(@NotBlank @Email String email, @NotBlank String code) {
        redisTemplate.opsForValue().set("verificationCode:" + email, code, 3, TimeUnit.MINUTES);
    }

    /**
     * 获取存储的验证码
     *
     * @param email 用户邮箱
     * @return 验证码
     */
    public String getVerificationCode(@NotBlank @Email String email) {
        return (String) redisTemplate.opsForValue().get("verificationCode:" + email);
    }

    /**
     * 删除验证码
     *
     * @param email 用户邮箱
     */
    public void deleteVerificationCode(@NotBlank @Email String email) {
        redisTemplate.delete("verificationCode:" + email);
    }

    /**
     * 获取用户的验证状态
     *
     * @param email 用户邮箱
     * @return 返回验证状态，如 "verified"，若无状态返回 null
     */
    public String getVerifiedStatus(@NotBlank @Email String email) {
        return (String) redisTemplate.opsForValue().get("verifiedStatus:" + email);
    }

    /**
     * 设置用户的验证状态，默认永久有效
     *
     * @param email    用户邮箱
     * @param verified 验证状态，比如 "verified"
     *     保持验证状态为2小时
     *
     */
    public void setVerifiedStatus(@NotBlank @Email String email, @NotBlank String verified) {
        redisTemplate.opsForValue().set("verifiedStatus:" + email, verified, 2, TimeUnit.HOURS);
    }

    /**
     * 删除用户的验证状态（可用于清除验证状态）
     *
     * @param email 用户邮箱
     */
    public void deleteVerifiedStatus(@NotBlank @Email String email) {
        redisTemplate.delete("verifiedStatus:" + email);
    }
}
