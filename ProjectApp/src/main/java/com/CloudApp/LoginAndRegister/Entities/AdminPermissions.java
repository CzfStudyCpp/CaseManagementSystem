package com.CloudApp.LoginAndRegister.Entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("admin_permissions")
public class AdminPermissions {

    @TableId(value = "permission_id", type = IdType.AUTO)
    private Long permissionId;  // 主键自增字段

    @TableField("admin_id")  // 映射到数据库字段 'admin_id'
    private Long adminId;  // 管理员ID

    @TableField("permission")  // 映射到数据库字段 'permission'
    private String permission;  // 权限字段

    @TableField("granted_at")  // 映射到数据库字段 'granted_at'
    private LocalDateTime grantedAt;  // 授权时间，建议使用 LocalDateTime 类型
}
