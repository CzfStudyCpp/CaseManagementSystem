package com.CloudApp.LoginAndRegister.Entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// MyBatis-Plus 用于标注实体类对应的数据库表
@Setter
@Getter
@TableName("admin_account")  // 与数据库表 'admin_account' 进行映射
public class Admin {

    // MyBatis-Plus 自增主键配置
    @TableId(value = "admin_id", type = IdType.AUTO)
    private Long adminId;  // 管理员ID

    @TableField("username")  // 映射数据库字段 'username'
    private String username;  // 管理员用户名

    @TableField("email")  // 映射数据库字段 'email'
    private String email;  // 管理员邮箱

    @TableField("password")  // 映射数据库字段 'password'
    private String password;  // 加密后的密码

    @TableField("status")  // 映射数据库字段 'status'
    private String status;  // 账号状态，如 "active", "inactive"

    @TableField("created_at")  // 映射数据库字段 'created_at'
    private LocalDateTime createdAt;  // 账号创建时间

    @TableField("updated_at")  // 映射数据库字段 'updated_at'
    private LocalDateTime updatedAt;  // 账号最后更新时间

    @TableField("access_level")  // 映射数据库字段 'access_level'
    private String accessLevel;

    @TableField("is_super_admin")  // 映射数据库字段 'is_super_admin'
    private Boolean isSuperAdmin;  // 是否为超级管理员
}
