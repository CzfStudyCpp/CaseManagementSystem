package com.CloudApp.userServices.Implement;

import ch.qos.logback.classic.Logger;
import com.CloudApp.GlobalException.AccountException.IncorrectPasswordException;
import com.CloudApp.LoginAndRegister.Entities.CompanyProfile;
import com.CloudApp.LoginAndRegister.Entities.DeveloperProfile;
import com.CloudApp.LoginAndRegister.Entities.User;
import com.CloudApp.LoginAndRegister.Entities.UserPassword;
import com.CloudApp.LoginAndRegister.Enums.CompanySize;
import com.CloudApp.LoginAndRegister.Mapper.CompanyProfileMapper;
import com.CloudApp.LoginAndRegister.Mapper.DeveloperProfileMapper;
import com.CloudApp.LoginAndRegister.Mapper.UserPasswordMapper;
import com.CloudApp.LoginAndRegister.Service.EmailService;
import com.CloudApp.RedisCommon.Service.RedisEmailCacheService;
import com.CloudApp.SkillManagement.DTO.SkillDTO;
import com.CloudApp.SkillManagement.DTO.UserSkillDTO;
import com.CloudApp.SkillManagement.Entity.Skill;
import com.CloudApp.SkillManagement.Entity.UserSkill;
import com.CloudApp.SkillManagement.Mapper.SkillMapper;
import com.CloudApp.SkillManagement.Mapper.UserSkillMapper;
import com.CloudApp.LoginAndRegister.Mapper.UserMapper;
import com.CloudApp.userServices.DTO.*;
import com.CloudApp.userServices.Entities.EmailChangeRequest;
import com.CloudApp.userServices.Mapper.EmailChangeRequestMapper;
import com.CloudApp.userServices.Service.UserManagementService;
import org.apache.ibatis.jdbc.Null;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
@Service
public class UserManagementServiceImpl implements UserManagementService {

    private static final String EMAIL_CODE_KEY = "changePasswordEmail:";
    private final UserMapper userMapper;
    private final UserSkillMapper userSkillMapper;
    private final SkillMapper skillMapper;
    private final DeveloperProfileMapper developerMapper;
    private final CompanyProfileMapper companyMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(UserManagementServiceImpl.class);

    private static final String USER_PROFILE_CACHE_KEY = "userProfile:";
    private static final long CACHE_EXPIRE_TIME = 10; // 正常数据缓存时间（分钟）
    private static final long EMPTY_CACHE_EXPIRE_TIME = 5; // 空值缓存时间（分钟）
    private static final String EMPTY_PLACEHOLDER = "EMPTY";

    //密码加密
    private final PasswordEncoder passwordEncoder;
    private final UserPasswordMapper userPasswordMapper;

    @Autowired
    private RedisEmailCacheService redisEmailCacheService; //  RedisEmailCacheService

    @Autowired
    private EmailService emailService; // 邮件服务类


    EmailChangeRequestMapper emailChangeRequestMapper;
    @Autowired
    public UserManagementServiceImpl(
            UserMapper userMapper,
            UserSkillMapper userSkillMapper,
            SkillMapper skillMapper,
            DeveloperProfileMapper developerMapper,
            CompanyProfileMapper companyMapper,
            RedisTemplate<String, Object> redisTemplate, PasswordEncoder passwordEncoder,
            UserPasswordMapper userPasswordMapper,
            EmailChangeRequestMapper emailChangeRequestMapper) {
        this.userMapper = userMapper;
        this.userSkillMapper = userSkillMapper;
        this.skillMapper = skillMapper;
        this.developerMapper = developerMapper;
        this.companyMapper = companyMapper;
        this.redisTemplate = redisTemplate;
        this.passwordEncoder = passwordEncoder;
        this.userPasswordMapper = userPasswordMapper;
        this.emailChangeRequestMapper = emailChangeRequestMapper;
    }

    @Override
    public UserProfileDTO getUserProfileByEmail(String email) {
        System.out.println("通过邮箱获取用户信息和技能信息: " + email);
        String cacheKey = USER_PROFILE_CACHE_KEY + email; // 统一 Redis key


        //redisTemplate.delete(cacheKey);
        // 检查缓存是否有数据
        Object cachedData = redisTemplate.opsForValue().get(cacheKey);
        if (cachedData != null) {
            if (cachedData.equals(EMPTY_PLACEHOLDER)) {
                System.out.println("redis为空，返回空值");
                return null;
            }
            System.out.println("redis不为空，返回取得的值");
            return (UserProfileDTO) cachedData;
        }

        // 从数据库中查询用户信息
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            redisTemplate.opsForValue().set(cacheKey, EMPTY_PLACEHOLDER, EMPTY_CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
            System.out.println("数据库为空，返回空值");
            return null;
        }

        UserDTO userDTO = toUserDTO(user);

        // 获取用户技能
        List<UserSkillDTO> userSkills = userSkillMapper.getUserSkillsByUserId(user.getId())
                .stream()
                .map(userSkill -> {
                    Skill skill = skillMapper.getSkillById(userSkill.getSkillId());
                    return new UserSkillDTO(skill.getId(), skill.getName(), userSkill.getProficiency());
                })
                .collect(Collectors.toList());

        // 获取所有技能选项
        List<SkillDTO> allSkills = skillMapper.getAllSkills()
                .stream()
                .map(skill -> new SkillDTO(skill.getId(), skill.getName()))
                .collect(Collectors.toList());

        // 根据用户类型获取补充信息
        DeveloperProfileDTO developerProfileDTO = null;
        CompanyProfileDTO companyProfileDTO = null;

        if ("DEVELOPER".equals(userDTO.getUserType())) {
            System.out.println("当前用户为开发者,正在获取数据");
            DeveloperProfile developerProfile = developerMapper.selectByUserId(user.getId());
            if (developerProfile != null) {
                developerProfileDTO = toDeveloperProfileDTO(developerProfile);
            }
        } else if ("COMPANY".equals(userDTO.getUserType())) {
            CompanyProfile companyProfile = companyMapper.selectByUserId(user.getId());
            if (companyProfile != null) {
                companyProfileDTO = toCompanyProfileDTO(companyProfile);
            }
        }

        // 组装最终的 UserProfileDTO
        UserProfileDTO userProfile = new UserProfileDTO(userDTO, userSkills, allSkills, developerProfileDTO, companyProfileDTO);

        // 缓存并返回
        redisTemplate.opsForValue().set(cacheKey, userProfile, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        return userProfile;
    }

    @Override
    public UserProfileUpdateDTO updateUserProfileByEmail(String email, UserProfileUpdateDTO payload) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }

        // 获取用户实体
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 更新用户基本信息
        UserDTO userDTO = payload.getUserProfile();
        user.setUsername(userDTO.getUsername());
        user.setPhone(userDTO.getPhone());
        userMapper.updateUser(user);

        // 根据用户类型更新补充信息
        if ("DEVELOPER".equals(userDTO.getUserType()) && payload.getDeveloperProfile() != null) {
            DeveloperProfileDTO developerProfileDTO = payload.getDeveloperProfile();

            DeveloperProfile developerProfile = new DeveloperProfile();
            developerProfile.setUserId(user.getId());
            developerProfile.setRealName(developerProfileDTO.getRealName());
            developerProfile.setGithub(developerProfileDTO.getGithub());
            developerProfile.setPortfolio(developerProfileDTO.getPortfolio());
            developerProfile.setExperience(developerProfileDTO.getExperience());

            developerMapper.updateDeveloperProfile(developerProfile);
        }

        if ("COMPANY".equals(userDTO.getUserType()) && payload.getCompanyProfile() != null) {
            CompanyProfileDTO companyProfileDTO = payload.getCompanyProfile();

            CompanyProfile companyProfile = new CompanyProfile();
            companyProfile.setUserId(user.getId());
            companyProfile.setCompanyName(companyProfileDTO.getCompanyName());
            companyProfile.setIndustry(companyProfileDTO.getIndustry());
            companyProfile.setSize(CompanySize.valueOf(companyProfileDTO.getSize()));
            companyProfile.setAddress(companyProfileDTO.getAddress());
            companyProfile.setContactPerson(companyProfileDTO.getContactPerson());
            companyProfile.setContactPhone(companyProfileDTO.getContactPhone());

            companyMapper.updateCompanyProfile(companyProfile);
        }

        // 清除缓存，确保最新数据
        String cacheKey = USER_PROFILE_CACHE_KEY + email; // 使用统一的 Redis key
        redisTemplate.delete(cacheKey); // 删除旧缓存

        // 返回更新后的 UserProfileUpdateDTO（只返回更新后的对象，不缓存）
        return new UserProfileUpdateDTO(userDTO, payload.getDeveloperProfile(), payload.getCompanyProfile());
    }


    @Override
    @Transactional
    public List<UserSkillDTO> updateUserSkillsByEmail(String email, List<Map<String, Object>> skillsPayload) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }

        User user = userMapper.selectByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        Long userId = user.getId();

        // 删除旧的技能关联
        userSkillMapper.deleteUserSkillsByUserId(userId);

        // 添加新的技能关联
        List<UserSkillDTO> updatedSkills = skillsPayload.stream()
                .map(skillData -> {
                    UserSkill userSkill = new UserSkill();
                    userSkill.setUserId(userId);
                    userSkill.setSkillId(((Number) skillData.get("skill_id")).longValue());
                    userSkill.setProficiency((String) skillData.get("proficiency"));
                    userSkillMapper.insertUserSkill(userSkill);

                    Skill skill = skillMapper.getSkillById(userSkill.getSkillId());
                    return toUserSkillDTO(skill, userSkill.getProficiency());
                })
                .collect(Collectors.toList());

        // 更新缓存
        invalidateCacheByEmail(email);

        return updatedSkills;
    }

    private UserSkillDTO toUserSkillDTO(Skill skill, String proficiency) {
        return new UserSkillDTO(skill.getId(), skill.getName(), proficiency != null ? proficiency : "unknown");
    }

    private UserDTO toUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getUserType().toString(),
                user.getStatus()
        );
    }

    private DeveloperProfileDTO toDeveloperProfileDTO(DeveloperProfile developerProfile) {
        return new DeveloperProfileDTO(
                developerProfile.getUserId(),
                developerProfile.getRealName(),
                developerProfile.getGithub(),
                developerProfile.getPortfolio(),
                developerProfile.getExperience()
        );
    }

    private CompanyProfileDTO toCompanyProfileDTO(CompanyProfile companyProfile) {
        return new CompanyProfileDTO(
                companyProfile.getUserId(),
                companyProfile.getCompanyName(),
                companyProfile.getIndustry(),
                companyProfile.getSize().toString(),
                companyProfile.getAddress(),
                companyProfile.getContactPerson(),
                companyProfile.getContactPhone()
        );
    }

    /**
     * 清除用户缓存
     */
    private void invalidateCacheByEmail(String email) {
        String cacheKey = USER_PROFILE_CACHE_KEY + email;
        redisTemplate.delete(cacheKey);
    }

    @Transactional
    @Override
    public boolean changePassword(String oldPassword, String newPassword) {
        // 获取当前用户
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

       // System.out.println("11111111111115574445afadfafwwwwwwwwwwwwwwww");
        if (currentUsername == null || "anonymousUser".equals(currentUsername)) {
            throw new IllegalArgumentException("当前用户未登录或用户名无效");
        }

        User user = userMapper.getUserByUsername(currentUsername);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        UserPassword userPassword = userPasswordMapper.selectByUserId(user.getId());
        if (userPassword == null) {
            throw new IllegalArgumentException("用户密码信息不存在");
        }

        // 校验旧密码
        if (!passwordEncoder.matches(oldPassword, userPassword.getPasswordHash())) {
            throw new IncorrectPasswordException("旧密码错误");
        }


        // 更新新密码，确保存储的是加密后的密码
        String newHashPassword = passwordEncoder.encode(newPassword);
        userPassword.setPasswordHash(newHashPassword);
        userPasswordMapper.updatePassword(userPassword);
        return true;
    }


    @Transactional
    @Override
    public int changePasswordByEmail(String email, String code, String newPassword) {
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        String storedCode = redisEmailCacheService.getVerificationCode(email);
        if (storedCode == null) {
            return 3; // 验证码已过期
        }

        if (!storedCode.equals(code)) {
            return 2; // 验证码错误
        }

        redisEmailCacheService.deleteVerificationCode(email);

        UserPassword userPassword = userPasswordMapper.selectByUserId(user.getId());
        if (userPassword == null) {
            throw new IllegalArgumentException("用户密码信息不存在");
        }

        // 更新新密码，确保存储的是加密后的密码
        String newHashPassword = passwordEncoder.encode(newPassword);
        userPassword.setPasswordHash(newHashPassword);
        userPasswordMapper.updatePassword(userPassword);

        return 1; // 修改成功
    }



    @Override
    public boolean sendChangePasswordCode(String email) {
        // 查询用户是否存在
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 生成随机验证码
        String verificationCode = emailService.generateVerificationCode();

        // 使用 RedisEmailCacheService 存储验证码到 Redis
        redisEmailCacheService.storeVerificationCode(email, verificationCode);

        // 使用邮件服务发送验证码邮件
        try {
            emailService.sendPasswordVerificationEmail(email, verificationCode);
            logger.info("验证码已发送至邮箱: {}, 验证码: {}", email, verificationCode);
        } catch (RuntimeException e) {
            logger.error("邮件发送失败: {}", e.getMessage());
            throw new RuntimeException("邮件发送失败，请稍后重试");
        }

        return true;
    }

    public boolean changeEmail(String currentEmail, String newEmail, String verificationCode){
        // 验证当前邮箱是否存在
        User user = userMapper.selectByEmail(currentEmail);
        if (user == null) {
            throw new IllegalArgumentException("当前邮箱不存在");
        }

        // 验证验证码
        String storedCode = redisEmailCacheService.getVerificationCode(newEmail);
        if (storedCode == null) {
            throw new IllegalArgumentException("验证码无效或已过期");
        }
        if (!storedCode.equals(verificationCode)) {
            throw new IllegalArgumentException("验证码错误");
        }

        // 删除验证码
        redisEmailCacheService.deleteVerificationCode(newEmail);

        // 将更改请求存入 email_change_requests 表
        EmailChangeRequest request = new EmailChangeRequest();
        request.setUserId(user.getId());
        request.setUsername(user.getUsername());
        request.setCurrentEmail(currentEmail);
        request.setNewEmail(newEmail);
        request.setStatus("PENDING");


        emailChangeRequestMapper.insertRequest(request);

        return true;

    }


    public boolean sendChangeEmailVerificationCode(String email){
        // 检查邮箱是否存在

        // 生成验证码并存储到 Redis
        String verificationCode = emailService.generateVerificationCode();
        redisEmailCacheService.storeVerificationCode(email, verificationCode);

        try {
            // 调用邮件服务发送验证码
            emailService.sendVerificationEmail(email, verificationCode);
            logger.info("验证码已发送至邮箱: {}, 验证码: {}", email, verificationCode);
        }catch (RuntimeException e) {
            logger.error("邮件发送失败: {}", e.getMessage());
            throw new RuntimeException("邮件发送失败，请稍后重试");
        }
        return true;
    }

    @Override
    public Map<String, Object> getUserList(int page, int size) {
        // 计算偏移量
        int offset = (page - 1) * size;

        // 获取用户列表数据
        List<User> users = userMapper.getUsersPaginated(offset, size);
        if (users == null || users.isEmpty()) {
            throw new IllegalArgumentException("没有用户数据");
        }

        // 转换为 UserDTO 列表
        List<UserDTO> userDTOList = users.stream()
                .map(this::toUserDTO)
                .collect(Collectors.toList());

        // 获取用户总数
        int total = userMapper.getUserCount();

        // 构建返回的结果
        Map<String, Object> result = new HashMap<>();
        result.put("users", userDTOList);
        result.put("total", total);

        return result;
    }


    @Override
    public UserProfileDTO getUserProfileById(Long userId) {
        System.out.println("通过用户ID获取用户信息和技能信息: " + userId);

        // 从数据库获取用户信息（确保存在）
        User user = userMapper.getUserById(userId);
        if (user == null) {
            System.out.println("数据库中未找到用户，返回空值");
            return null;
        }

        // 构建 Redis 缓存 Key
        String cacheKeyEmail = USER_PROFILE_CACHE_KEY + user.getEmail(); // 用于更新用户的 Key

        // 从 cacheKeyEmail 读取 UserProfileDTO
        Object cachedData = redisTemplate.opsForValue().get(cacheKeyEmail);
        if (cachedData != null) {
            if (cachedData.equals(EMPTY_PLACEHOLDER)) {
                System.out.println("Redis缓存为空值占位，返回空值");
                return null;
            }

            if (cachedData instanceof UserProfileDTO) {
                System.out.println("Redis缓存命中（UserProfileDTO），直接返回");
                return (UserProfileDTO) cachedData;
            }

            // 如果类型不匹配，清除缓存并继续流程
            System.out.println("Redis缓存类型不匹配（cacheKeyEmail），清除缓存");
            redisTemplate.delete(cacheKeyEmail);
        }

        // 如果缓存未命中，则从数据库加载数据
        System.out.println("Redis缓存未命中，从数据库加载数据");

        UserDTO userDTO = toUserDTO(user);

        // 获取用户技能信息
        List<UserSkillDTO> userSkills = userSkillMapper.getUserSkillsByUserId(userId).stream()
                .map(userSkill -> {
                    Skill skill = skillMapper.getSkillById(userSkill.getSkillId());
                    return new UserSkillDTO(skill.getId(), skill.getName(), userSkill.getProficiency());
                })
                .collect(Collectors.toList());

        DeveloperProfileDTO developerProfileDTO = null;
        CompanyProfileDTO companyProfileDTO = null;

        // 根据用户类型加载开发者或企业信息
        if ("DEVELOPER".equals(userDTO.getUserType())) {
            DeveloperProfile developerProfile = developerMapper.selectByUserId(userId);
            if (developerProfile != null) {
                developerProfileDTO = toDeveloperProfileDTO(developerProfile);
            }
        } else if ("COMPANY".equals(userDTO.getUserType())) {
            CompanyProfile companyProfile = companyMapper.selectByUserId(userId);
            if (companyProfile != null) {
                companyProfileDTO = toCompanyProfileDTO(companyProfile);
            }
        }

        // 构建 UserProfileDTO
        UserProfileDTO userProfile = new UserProfileDTO(userDTO, userSkills, null, developerProfileDTO, companyProfileDTO);

        // 将最新数据写入 Redis 缓存
        redisTemplate.opsForValue().set(cacheKeyEmail, userProfile, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        System.out.println("从数据库加载数据，刷新Redis缓存（cacheKeyEmail）");

        return userProfile;
    }




    private UserProfileDTO convertToUserProfileDTO(UserProfileUpdateDTO updateDTO) {
        // 构建 UserDTO
        UserDTO userDTO = updateDTO.getUserProfile();

        // 构建 DeveloperProfileDTO 或 CompanyProfileDTO
        DeveloperProfileDTO developerProfileDTO = updateDTO.getDeveloperProfile();
        CompanyProfileDTO companyProfileDTO = updateDTO.getCompanyProfile();

        // 构建空的用户技能信息
        List<UserSkillDTO> userSkills = new ArrayList<>();

        // 返回组装后的 UserProfileDTO
        return new UserProfileDTO(userDTO, userSkills, null, developerProfileDTO, companyProfileDTO);
    }


}



