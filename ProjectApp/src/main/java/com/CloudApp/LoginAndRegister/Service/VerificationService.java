package com.CloudApp.LoginAndRegister.Service;

import com.CloudApp.RedisCommon.Service.RedisEmailCacheService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationService {

    private final RedisEmailCacheService redisCacheService;

    @Autowired
    public VerificationService(RedisEmailCacheService redisCacheService) {
        this.redisCacheService = redisCacheService;
    }

    /**
     * 验证用户输入的验证码
     *
     * @param email 用户邮箱
     * @param code 用户输入的验证码
     * @return 验证是否成功
     */
    public boolean verifyCode(@NotBlank(message = "邮箱不能为空") @Email(message = "邮箱格式不正确") String email,
                              @NotBlank(message = "验证码不能为空") String code) {
        try {
            // 从 Redis 获取存储的验证码
            String storedCode = redisCacheService.getVerificationCode(email);

            // 验证码是否存在且匹配
            if (storedCode != null && storedCode.equals(code)) {
                // 删除验证码以防重用
                redisCacheService.deleteVerificationCode(email);
                return true;
            } else {
                // 验证失败
                return false;
            }
        } catch (Exception e) {
            // Redis 连接或其他异常
            throw new RuntimeException("验证码验证失败，请稍后再试: " + e.getMessage());
        }
    }

    /**
     * 检查用户的邮箱是否已经完成验证
     *
     * @param email 用户邮箱
     * @return 是否已验证
     */
    public boolean isVerified(@NotBlank(message = "邮箱不能为空") @Email(message = "邮箱格式不正确") String email) {
        try {
            // 从 Redis 获取存储的验证状态
            String verifiedStatus = redisCacheService.getVerifiedStatus(email);

            // 如果状态是已验证，返回 true
            return "verified".equalsIgnoreCase(verifiedStatus);
        } catch (Exception e) {
            // Redis 连接或其他异常
            throw new RuntimeException("验证状态检查失败，请稍后再试: " + e.getMessage());
        }
    }

    /**
     * 设置用户的邮箱为已验证状态
     *
     * @param email 用户邮箱
     */
    public void setVerified(@NotBlank(message = "邮箱不能为空") @Email(message = "邮箱格式不正确") String email) {
        try {
            // 将用户邮箱的验证状态存储为 "verified"
            redisCacheService.setVerifiedStatus(email, "verified");
        } catch (Exception e) {
            // Redis 连接或其他异常
            throw new RuntimeException("设置验证状态失败，请稍后再试: " + e.getMessage());
        }
    }

    /**
     * 清除用户邮箱的验证状态
     *
     * @param email 用户邮箱
     */
    public void clearVerifiedStatus(@NotBlank(message = "邮箱不能为空") @Email(message = "邮箱格式不正确") String email) {
        try {
            // 从 Redis 删除验证状态
            redisCacheService.deleteVerifiedStatus(email);
        } catch (Exception e) {
            // Redis 连接或其他异常
            throw new RuntimeException("清除验证状态失败，请稍后再试: " + e.getMessage());
        }
    }
}
