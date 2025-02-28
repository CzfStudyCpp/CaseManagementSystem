package com.CloudApp.adminService.Implement;

import com.CloudApp.LoginAndRegister.Entities.*;
import com.CloudApp.LoginAndRegister.Enums.UserStatus;
import com.CloudApp.LoginAndRegister.Mapper.*;
import com.CloudApp.adminService.DTO.AdminDTO;
import com.CloudApp.adminService.Service.AdminManageService;
import com.CloudApp.userServices.Entities.EmailChangeRequest;
import com.CloudApp.userServices.Mapper.EmailChangeRequestMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CloudApp.LoginAndRegister.Service.EmailService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminManageServiceImpl implements AdminManageService {

    //用户迁移需要的临时表和正式表对象，以及它们的映射数据库的工具
    private final PendingUserMapper pendingUserMapper;
    private final PendingCompanyProfileMapper pendingCompanyProfileMapper;
    private final PendingDeveloperProfileMapper pendingDeveloperProfileMapper;
    private final PendingUserPasswordMapper pendingUserPasswordMapper;
    private final UserMapper userMapper;
    private final UserPasswordMapper userPasswordMapper;
    private final CompanyProfileMapper companyProfileMapper;
    private final DeveloperProfileMapper developerProfileMapper;

    private final AdminMapper adminMapper;

    private final PasswordEncoder passwordEncoder;
    //邮件服务
    private final EmailService emailService;

    @Autowired
    private EmailChangeRequestMapper emailChangeRequestMapper;
    @Autowired
    public AdminManageServiceImpl(PendingUserMapper pendingUserMapper, PendingCompanyProfileMapper pendingCompanyProfileMapper, PendingDeveloperProfileMapper pendingDeveloperProfileMapper, PendingUserPasswordMapper pendingUserPasswordMapper,
                                  UserMapper userMapper, UserPasswordMapper userPasswordMapper,
                                  CompanyProfileMapper companyProfileMapper, DeveloperProfileMapper developerProfileMapper, AdminMapper adminMapper, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.pendingUserMapper = pendingUserMapper;
        this.pendingCompanyProfileMapper = pendingCompanyProfileMapper;
        this.pendingDeveloperProfileMapper = pendingDeveloperProfileMapper;
        this.pendingUserPasswordMapper = pendingUserPasswordMapper;
        this.userMapper = userMapper;
        this.userPasswordMapper = userPasswordMapper;
        this.companyProfileMapper = companyProfileMapper;
        this.developerProfileMapper = developerProfileMapper;
        this.adminMapper = adminMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public List<Map<String, Object>> getPendingDevelopers() {
        List<PendingUser> developers = pendingUserMapper.findPendingUsersByType("DEVELOPER");
        developers = developers != null ? developers : Collections.emptyList();
        System.out.println("获取开发者表的基本信息: " + developers); // 打印开发者列表

        List<Map<String, Object>> result = new ArrayList<>();

        for (PendingUser user : developers) {
            Map<String, Object> developerInfo = new HashMap<>();
            developerInfo.put("basicInfo", user); // 基本信息

            // 获取开发者扩展信息
            PendingDeveloperProfile profile = pendingDeveloperProfileMapper.selectByUserId(user.getId());
            System.out.println("获取开发者表的补充信息" + profile.toString()); // 打印开发者扩展信息System.out.println("Fetched profile: " + profile); // 打印开发者扩展信息
            developerInfo.put("profile", profile != null ? profile : new PendingDeveloperProfile());

            result.add(developerInfo);
        }
        System.out.println("Final developer data: " + result);

        return result;
    }

    @Override
    public List<Map<String, Object>> getPendingCompanies() {
        List<PendingUser> companies = pendingUserMapper.findPendingUsersByType("COMPANY");

        companies=companies!= null ? companies : Collections.emptyList();
        System.out.println("获取企业的基本信息 " + companies);
        List<Map<String, Object>> result = new ArrayList<>();

        for (PendingUser user : companies) {
            Map<String, Object> companyInfo = new HashMap<>();
            companyInfo.put("basicInfo", user); // 基本信息

            // 获取企业扩展信息
            PendingCompanyProfile profile = pendingCompanyProfileMapper.selectByUserId(user.getId());
            companyInfo.put("profile", profile != null ? profile : new PendingCompanyProfile());
            System.out.println("获取企业表的补充信息: " + profile);
            result.add(companyInfo);
        }
        System.out.println("Final Company data: " + result);
        return result;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveUser(Long id, String type) {
        // 获取 PendingUser
        PendingUser pendingUser = pendingUserMapper.findById(id);
        PendingUserPassword pendingUserPassword = pendingUserPasswordMapper.findByPendingUserId(id);
        if (pendingUser == null) {
            throw new IllegalArgumentException("待审核用户不存在");
        }

        // 将用户插入正式用户表,这个ID是去掉pending的ID,使用新ID
        Long newUserId = null;
        String mail=pendingUser.getEmail();
        System.out.println("帐号邮箱为"+mail);
        System.out.println("用户类型为"+pendingUser.getUserType());
        if ("DEVELOPER".equalsIgnoreCase(type)) {
            userMapper.insertDeveloper(pendingUser); // 插入正式用户表
            newUserId = userMapper.findByEmail(mail); // 获取正式用户的自增ID
            System.out.println("更新后的用户ID为"+newUserId);
            // 插入开发者表
            PendingDeveloperProfile pendingDeveloperProfile = pendingDeveloperProfileMapper.selectByUserId(id);
            if (pendingDeveloperProfile != null) {
                pendingDeveloperProfile.setUserId(newUserId); // 更新新ID
                developerProfileMapper.insertDeveloper(pendingDeveloperProfile);
            }
        } else if ("COMPANY".equalsIgnoreCase(type)) {
            userMapper.insertCompany(pendingUser); // 插入正式用户表
            newUserId = userMapper.findByEmail(mail); // 获取正式用户的自增ID

            // 插入企业表
            PendingCompanyProfile pendingCompanyProfile = pendingCompanyProfileMapper.selectByUserId(id);
            if (pendingCompanyProfile != null) {
                pendingCompanyProfile.setUserId(newUserId); // 更新新ID
                System.out.println("临时用户企业行业为"+pendingCompanyProfile.getIndustry());
                companyProfileMapper.insertCompany(pendingCompanyProfile);
            }
        } else {
            throw new IllegalArgumentException("未知用户类型");
        }
        pendingUserPassword.setPendingUserId(newUserId);
        System.out.println("临时密码表的内容为"+pendingUserPassword);
        // 插入密码数据
        userPasswordMapper.insertPassword(pendingUserPassword);
        System.out.println("准备删除临时用户");
        // 删除 PendingUser 及其关联数据（通过级联删除）
        pendingUserMapper.deleteById(id);
        System.out.println("审核通过，准备发送邮件");
        sendApproveNotificationEmail(mail, "审核通过");
    }

    @Override
    public void rejectUser(Long id) {
        PendingUser pendingUser = pendingUserMapper.findById(id);
        if (pendingUser == null) {
            throw new IllegalArgumentException("待审核用户不存在");
        }
        String mail=pendingUser.getEmail();
        // 删除待审核记录
        pendingUserMapper.deleteById(id);
        sendApproveNotificationEmail(mail, "审核未通过");
    }

    @Override
    public void sendApproveNotificationEmail(String email, String result) {

        emailService.sendApprovalResultEmail(email, result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long userId, String newStatus){
           User user =userMapper.getUserById(userId);
           if(user==null){
               throw new IllegalArgumentException("更新状态的目标用户不存在");
           }
           user.setStatus(UserStatus.valueOf(newStatus));
           userMapper.updateUser(user);
    }

    @Override
    public IPage<EmailChangeRequest> getEmailChangeRequests(Page<EmailChangeRequest> requestPage){

            // 调用 MyBatis-Plus 提供的分页查询方法
        return emailChangeRequestMapper.selectPage(requestPage, null);

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int handleEmailChangeRequest(String username, String currentEmail, String newEmail, String action) {
        try {
            if ("APPROVE".equals(action)) {
                // 更新用户表的邮箱字段
                int updatedUsers = userMapper.updateEmailByUsername(username, currentEmail, newEmail);
                if (updatedUsers == 0) {
                    throw new RuntimeException("更新用户邮箱失败，可能是用户不存在或邮箱不匹配");
                }

                // 删除邮箱更换请求记录
                int deletedRequests = emailChangeRequestMapper.deleteRequestByUsernameAndEmails(username, currentEmail, newEmail);
                if (deletedRequests == 0) {
                    throw new RuntimeException("删除邮箱更换请求记录失败");
                }

                sendApproveEmailNotificationEmail(currentEmail, "同意更换");
                return 1;
            } else if ("REJECT".equals(action)) {
                // 仅删除邮箱更换请求记录
                int deletedRequests = emailChangeRequestMapper.deleteRequestByUsernameAndEmails(username, currentEmail, newEmail);
                if (deletedRequests == 0) {
                    throw new RuntimeException("删除邮箱更换请求记录失败");
                }
                sendApproveEmailNotificationEmail(currentEmail, "拒绝更换");
                return 2;
            }

            return 3;
        } catch (Exception e) {
            // 捕获异常后向上抛出，触发事务回滚
            throw new RuntimeException("处理邮箱更换请求失败：" + e.getMessage(), e);
        }
    }
    void sendApproveEmailNotificationEmail(String email, String result){
        emailService.sendApprovalEmailResult(email, result);
    }

    @Override
    public Map<String, Object> getBannedUserList(int page, int size){
        try {
            // 使用 MyBatis-Plus 的分页对象
            Page<User> requestPage = new Page<>(page, size);

            // 调用 Mapper 方法进行分页查询
            IPage<User> userPage = userMapper.getBannedUsers(requestPage);

            // 构造返回结果
            return Map.of(
                    "users", userPage.getRecords(),
                    "currentPage", userPage.getCurrent(),
                    "totalPages", userPage.getPages(),
                    "totalUsers", userPage.getTotal()
            );
        }catch (Exception e) {
            // 打印日志（生产环境可以集成日志框架如 Log4j、SLF4J）
            System.err.println("获取封禁用户列表失败：" + e.getMessage());
            e.printStackTrace();

            // 抛出自定义异常或返回默认值（视具体需求）
            throw new RuntimeException("获取封禁用户列表时发生错误，请检查数据库或联系管理员", e);
        }
    }

    @Override
    public boolean deleteUserById(Long userId){
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false; // 用户不存在，删除失败
        }

        // 删除用户
        int rowsDeleted = userMapper.deleteById(userId);
        return rowsDeleted > 0;

    }

    @Override
    public Map<String, Object> getAdminList(int page, int size) {
        // 使用 MyBatis-Plus 分页对象
        Page<Admin> adminPage = new Page<>(page, size);

        // 执行分页查询
        IPage<Admin> resultPage = adminMapper.selectPage(adminPage, null);

        // 将查询结果转换为 DTO 列表
        List<AdminDTO> adminDTOList = resultPage.getRecords().stream()
                .map(admin -> new AdminDTO(
                        admin.getAdminId(),
                        admin.getUsername(),
                        admin.getEmail(),
                        admin.getAccessLevel(),
                        admin.getStatus()
                )).collect(Collectors.toList());

        // 构造返回结果
        return Map.of(
                "admins", adminDTOList, // 管理员信息列表
                "currentPage", resultPage.getCurrent(), // 当前页码
                "totalPages", resultPage.getPages(),    // 总页数
                "totalAdmins", resultPage.getTotal()    // 总管理员数量
        );
    }
    @Override
    @Transactional
    public boolean addAdmin(Admin admin) {
        try {
            String encodedPassword = passwordEncoder.encode(admin.getPassword());
            admin.setPassword(encodedPassword); // 设置加密后的密码
            int result = adminMapper.insert(admin);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("添加管理员失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean deleteAdmin(Long adminId) {
        try {
            int result = adminMapper.deleteById(adminId);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("删除管理员失败: " + e.getMessage());
        }
    }
}