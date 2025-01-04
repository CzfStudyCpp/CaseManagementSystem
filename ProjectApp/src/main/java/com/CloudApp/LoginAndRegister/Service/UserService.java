package com.CloudApp.LoginAndRegister.Service;

import com.CloudApp.LoginAndRegister.Enums.CompanySize;
import com.CloudApp.LoginAndRegister.Enums.UserType;
import com.CloudApp.LoginAndRegister.Mapper.*;
import com.CloudApp.LoginAndRegister.Entities.PendingUser;
import com.CloudApp.LoginAndRegister.Entities.PendingUserPassword;
import com.CloudApp.LoginAndRegister.Entities.PendingDeveloperProfile;
import com.CloudApp.LoginAndRegister.Entities.PendingCompanyProfile;
import com.CloudApp.LoginAndRegister.Request.UserRegistrationRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;
//用户的注册
@Service
public class UserService {
    private static final String REDIS_PENDING_EMAIL_KEY_PREFIX = "pending:email:";
    private static final String REDIS_REGISTERED_EMAIL_KEY_PREFIX = "registered:email:";
    private static final String REDIS_PENDING_USERNAME_KEY_PREFIX = "pending:username:";
    private static final String REDIS_REGISTERED_USERNAME_KEY_PREFIX = "registered:username:";
    private static final long CACHE_EXPIRATION = 30; // 缓存过期时间,30分钟
    private static final long EMPTY_CACHE_EXPIRATION = 10;//空值缓存十分钟过期

    private final PendingUserMapper pendingUserMapper;
    private final PendingUserPasswordMapper pendingUserPasswordMapper;
    private final PendingDeveloperProfileMapper pendingDeveloperProfileMapper;
    private final PendingCompanyProfileMapper pendingCompanyProfileMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    public UserService(PendingUserMapper pendingUserMapper, PendingUserPasswordMapper pendingUserPasswordMapper,
                       PendingDeveloperProfileMapper pendingDeveloperProfileMapper, PendingCompanyProfileMapper pendingCompanyProfileMapper,
                       PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.pendingUserMapper = pendingUserMapper;
        this.pendingUserPasswordMapper = pendingUserPasswordMapper;
        this.pendingDeveloperProfileMapper = pendingDeveloperProfileMapper;
        this.pendingCompanyProfileMapper = pendingCompanyProfileMapper;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    /**
     * 注册用户并存储到临时表中
     * Spring 的事务管理基于 AOP 的代理机制,使用事务是防止存在基本信息写入成功但补充信息填写失败的情况
     * 使用 @Transactional 确保所有操作要么全部成功，要么全部回滚
     */
    @Transactional(rollbackFor = Exception.class)
    // 注册用户并存储到临时表中
    public void registerPendingUser(UserRegistrationRequest request) {
        // 校验邮箱和用户名唯一性
        if (pendingUserMapper.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("该邮箱已正在审核中");
        }
        if (pendingUserMapper.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("该用户名已存在");
        }

        // 创建用户临时记录
        PendingUser pendingUser = new PendingUser();
        pendingUser.setUsername(request.getUsername());
        pendingUser.setEmail(request.getEmail());
        pendingUser.setPhone(request.getPhone());
        pendingUser.setUserType(UserType.valueOf(request.getUserType().toUpperCase())); // 用户类型（DEVELOPER 或 COMPANY）
        pendingUserMapper.insert(pendingUser);

        // 创建用户密码临时记录
        PendingUserPassword pendingUserPassword = new PendingUserPassword();
        pendingUserPassword.setPendingUserId(pendingUser.getId());
        pendingUserPassword.setPasswordHash(passwordEncoder.encode(request.getPassword())); // 密码加密
        pendingUserPasswordMapper.insert(pendingUserPassword);

        // 根据用户类型动态处理
        if (UserType.DEVELOPER.name().equalsIgnoreCase(request.getUserType())) {
            handleDeveloperRegistration(request, pendingUser);
        } else if (UserType.COMPANY.name().equalsIgnoreCase(request.getUserType())) {
            handleCompanyRegistration(request, pendingUser);
        } else {
            throw new IllegalArgumentException("无效的用户类型");
        }
        // 清除相关 Redis 缓存
        clearUserRegistrationCache(request.getEmail(), request.getUsername());
    }

    private void clearUserRegistrationCache(@NotBlank(message = "邮箱不能为空") @Email(message = "邮箱格式不正确") String email, @NotBlank(message = "用户名不能为空") @Size(min = 3, max = 20, message = "用户名长度必须在3到20个字符之间") String username) {
        // 构造 Redis 缓存键
        String pendingEmailKey = REDIS_PENDING_EMAIL_KEY_PREFIX + email;
        String registeredEmailKey = REDIS_REGISTERED_EMAIL_KEY_PREFIX + email;
        String pendingUsernameKey = REDIS_PENDING_USERNAME_KEY_PREFIX + username;
        String registeredUsernameKey = REDIS_REGISTERED_USERNAME_KEY_PREFIX + username;


        // 删除缓存
        redisTemplate.delete(pendingEmailKey);
        redisTemplate.delete(registeredEmailKey);
        redisTemplate.delete(pendingUsernameKey);
        redisTemplate.delete(registeredUsernameKey);

    }

    // 处理开发者注册信息
    private void handleDeveloperRegistration(UserRegistrationRequest request, PendingUser pendingUser) {
        UserRegistrationRequest.DeveloperInfo developerInfo = request.getDeveloperInfo();
        if (developerInfo == null) {
            throw new IllegalArgumentException("开发者信息不能为空");
        }

        PendingDeveloperProfile developerProfile = new PendingDeveloperProfile();
        developerProfile.setPendingUser(pendingUser); // 关联用户信息
        developerProfile.setRealName(developerInfo.getRealName()); // 开发者姓名
        developerProfile.setGithub(developerInfo.getGithub()); // GitHub 链接
        developerProfile.setPortfolio(developerInfo.getPortfolio()); // 作品集链接
        developerProfile.setExperience(developerInfo.getExperience()); // 项目经验
        pendingDeveloperProfileMapper.insert(developerProfile); // 保存到数据库
    }

    // 处理企业用户注册信息
    private void handleCompanyRegistration(UserRegistrationRequest request, PendingUser pendingUser) {
        UserRegistrationRequest.CompanyInfo companyInfo = request.getCompanyInfo();
        if (companyInfo == null) {
            throw new IllegalArgumentException("企业信息不能为空");
        }

        PendingCompanyProfile companyProfile = new PendingCompanyProfile();
        companyProfile.setPendingUser(pendingUser); // 关联用户信息
        companyProfile.setCompanyName(companyInfo.getCompanyName()); // 企业名称
        companyProfile.setIndustry(companyInfo.getIndustry()); // 所属行业
        companyProfile.setSize(CompanySize.valueOf(companyInfo.getCompanySize().toUpperCase())); // 企业规模
        companyProfile.setAddress(companyInfo.getAddress()); // 企业地址
        companyProfile.setContactPerson(companyInfo.getContactPerson()); // 联系人姓名
        companyProfile.setContactPhone(companyInfo.getContactPhone()); // 联系人电话
        companyProfile.setBusinessLicense(companyInfo.getBusinessLicense()); // 营业执照
        pendingCompanyProfileMapper.insert(companyProfile); // 保存到数据库
    }

    /**
     * 检查邮箱是否已注册或在待审核表中
     *
     * @param email 用户邮箱
     * @return 如果邮箱已注册或待审核返回 true，否则返回 false
     */
    public boolean isEmailRegistered(String email) {
        // 构造 Redis 缓存键
        String pendingKey = REDIS_PENDING_EMAIL_KEY_PREFIX + email;
        String registeredKey = REDIS_REGISTERED_EMAIL_KEY_PREFIX + email;

        // 检查 Redis 缓存（Pending 表）
        Boolean isInPendingCache = (Boolean) redisTemplate.opsForValue().get(pendingKey);
        if (isInPendingCache != null) {
            // 如果缓存存在（包括 false），直接返回结果
            return isInPendingCache;
        }

        // 检查 Redis 缓存（正式用户表）
        Boolean isInRegisteredCache = (Boolean) redisTemplate.opsForValue().get(registeredKey);
        if (isInRegisteredCache != null) {
            // 如果缓存存在（包括 false），直接返回结果
            return isInRegisteredCache;
        }

        // 如果缓存未命中，查询数据库
        boolean isInPendingUsers = pendingUserMapper.existsByEmail(email);
        boolean isInRegisteredUsers = userMapper.existsByEmail(email);

        // 更新 Redis 缓存
        if (isInPendingUsers) {
            // 如果存在，缓存较长时间
            redisTemplate.opsForValue().set(pendingKey, true, CACHE_EXPIRATION, TimeUnit.MINUTES);
        } else {
            // 如果不存在，缓存空值，较短过期时间
            redisTemplate.opsForValue().set(pendingKey, false, EMPTY_CACHE_EXPIRATION, TimeUnit.MINUTES);
        }

        if (isInRegisteredUsers) {
            // 如果存在，缓存较长时间
            redisTemplate.opsForValue().set(registeredKey, true, CACHE_EXPIRATION, TimeUnit.MINUTES);
        } else {
            // 如果不存在，缓存空值，较短过期时间
            redisTemplate.opsForValue().set(registeredKey, false, EMPTY_CACHE_EXPIRATION, TimeUnit.MINUTES);
        }

        // 返回结果
        return isInPendingUsers || isInRegisteredUsers;
    }


    public boolean isUsernameRegistered(String username) {
        // 构造 Redis 缓存键
        String pendingKey = REDIS_PENDING_USERNAME_KEY_PREFIX + username;
        String registeredKey = REDIS_REGISTERED_USERNAME_KEY_PREFIX + username;

        // 检查 Redis 缓存（Pending 表）
        Boolean isInPendingCache = (Boolean) redisTemplate.opsForValue().get(pendingKey);
        if (isInPendingCache != null) {
            // 如果缓存存在（包括 false），直接返回结果
            return isInPendingCache;
        }

        // 检查 Redis 缓存（正式用户表）
        Boolean isInRegisteredCache = (Boolean) redisTemplate.opsForValue().get(registeredKey);
        if (isInRegisteredCache != null) {
            // 如果缓存存在（包括 false），直接返回结果
            return isInRegisteredCache;
        }

        // 如果缓存未命中，查询数据库
        boolean isInPendingUsers = pendingUserMapper.existsByUsername(username);
        boolean isInRegisteredUsers = userMapper.existsByUsername(username);

        // 更新 Redis 缓存
        if (isInPendingUsers) {
            // 如果存在，缓存较长时间
            redisTemplate.opsForValue().set(pendingKey, true, CACHE_EXPIRATION, TimeUnit.MINUTES);
        } else {
            // 如果不存在，缓存空值，较短过期时间
            redisTemplate.opsForValue().set(pendingKey, false, EMPTY_CACHE_EXPIRATION, TimeUnit.MINUTES);
        }

        if (isInRegisteredUsers) {
            // 如果存在，缓存较长时间
            redisTemplate.opsForValue().set(registeredKey, true, CACHE_EXPIRATION, TimeUnit.MINUTES);
        } else {
            // 如果不存在，缓存空值，较短过期时间
            redisTemplate.opsForValue().set(registeredKey, false, EMPTY_CACHE_EXPIRATION, TimeUnit.MINUTES);
        }

        // 返回结果
        return isInPendingUsers || isInRegisteredUsers;
    }

}
