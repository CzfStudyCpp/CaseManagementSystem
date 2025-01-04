package com.CloudApp.adminService.Service;

import com.CloudApp.LoginAndRegister.Entities.Admin;
import com.CloudApp.LoginAndRegister.Entities.PendingUser;
import com.CloudApp.LoginAndRegister.Enums.UserType;
import com.CloudApp.userServices.Entities.EmailChangeRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface AdminManageService {
     List<Map<String, Object>> getPendingDevelopers(); // 获取待审核开发者列表
     List<Map<String, Object>> getPendingCompanies(); // 获取待审核企业列表
    void approveUser(Long id, String type); // 审核通过用户
    void rejectUser(Long id); // 拒绝用户审核


    void sendApproveNotificationEmail(String email, String result);

    void updateUserStatus(Long userId, String newStatus);

    IPage<EmailChangeRequest> getEmailChangeRequests(Page<EmailChangeRequest> requestPage);


    @Transactional(rollbackFor = Exception.class) // 添加事务管理，出现异常时回滚
    int handleEmailChangeRequest(String username, String currentEmail, String newEmail, String action);

    Map<String, Object> getBannedUserList(int page, int size);

    boolean deleteUserById(Long userId);

    Map<String, Object> getAdminList(int page, int size);

    boolean addAdmin(Admin admin);

    boolean deleteAdmin(Long adminId);
}
