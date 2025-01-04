package com.CloudApp.LoginAndRegister.Service;

import com.CloudApp.RedisCommon.Service.RedisEmailCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//注册邮件服务
@Service
public class RegistrationService {

    private final EmailService emailService;
    private final RedisEmailCacheService redisCacheService;


    @Autowired
    public RegistrationService(EmailService emailService, RedisEmailCacheService redisCacheService) {
        this.emailService = emailService;
        this.redisCacheService = redisCacheService;
    }

    /**
     * 用户注册时发送验证码
     * @param email 用户邮箱
     */
    public void sendVerificationCode(String email) {
        try {
            // 生成验证码
            String verificationCode = emailService.generateVerificationCode();

            // 调用 RedisEmailCacheService 将验证码存储到 Redis，过期时间由 RedisEmailCacheService 管理
            redisCacheService.storeVerificationCode(email, verificationCode);

            // 发送验证码到用户邮箱
            emailService.sendVerificationEmail(email, verificationCode);
        } catch (Exception e) {
            // 捕获异常并抛出，避免服务中断
            throw new RuntimeException("发送验证码失败，请稍后重试: " + e.getMessage());
        }
    }
}
