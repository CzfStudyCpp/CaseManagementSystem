package com.CloudApp.LoginAndRegister.Service;

import com.CloudApp.LoginAndRegister.Entities.Admin;
import com.CloudApp.LoginAndRegister.Entities.AdminPermissions;
import com.CloudApp.LoginAndRegister.Mapper.AdminMapper;
import com.CloudApp.LoginAndRegister.Mapper.AdminPermissionsMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminInitializationService {

    private final AdminMapper adminMapper;
    private final AdminPermissionsMapper adminPermissionsMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminInitializationService(AdminMapper adminMapper,
                                      AdminPermissionsMapper adminPermissionsMapper,
                                      PasswordEncoder passwordEncoder) {
        this.adminMapper = adminMapper;
        this.adminPermissionsMapper = adminPermissionsMapper;
        this.passwordEncoder = passwordEncoder;  // 使用加密密码
    }


    @Transactional
    public void initializeSuperAdmin() {
        // 检查是否已经有超级管理员
        Admin admin = adminMapper.selectByUsername("admin");

        if (admin == null) {
            // 如果没有找到超级管理员，则创建一个
            Admin newAdmin = new Admin();
            newAdmin.setUsername("admin");
            newAdmin.setEmail("2018249814@qq.com");
            newAdmin.setPassword(passwordEncoder.encode("123456"));  // 加密密码
            newAdmin.setAccessLevel("super_admin");
            newAdmin.setIsSuperAdmin(true);
            newAdmin.setStatus("active");

            // 插入管理员基本信息到 admin_account 表
            adminMapper.insert(newAdmin);

            // 创建权限信息，不设置 permission_id（让数据库自动生成）
            String[] permissionsList = {
                    "view_user",
                    "edit_user",
                    "approve_user",
                    "create_admin",
                    "delete_announcement",
                    "publish_announcement",
                    "delete_user"
            };

            for (String permission : permissionsList) {
                // 避免重复插入相同权限
                AdminPermissions permissions = new AdminPermissions();
                permissions.setAdminId(newAdmin.getAdminId());  // 使用插入后的 admin_id
                permissions.setPermission(permission);

                // 检查是否已经插入该权限，如果没有，插入
                AdminPermissions existingPermission = adminPermissionsMapper.selectOne(new QueryWrapper<AdminPermissions>()
                        .eq("admin_id", newAdmin.getAdminId())
                        .eq("permission", permission));

                if (existingPermission == null) {
                    adminPermissionsMapper.insert(permissions);  // 插入后，permission_id 会自动生成
                }
            }

            System.out.println("超级管理员及权限已成功初始化！");
        } else {
            System.out.println("超级管理员已存在！");
        }
    }


}
