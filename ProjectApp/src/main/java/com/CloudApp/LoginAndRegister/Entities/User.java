package com.CloudApp.LoginAndRegister.Entities;

import com.CloudApp.LoginAndRegister.Enums.UserStatus;
import com.CloudApp.LoginAndRegister.Enums.UserType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("users")  // 映射到数据库中的 users 表
public class User {

    @TableId(value = "id", type = IdType.AUTO)  // 自增主键
    private Long id;  // 用户ID

    @TableField("username")  // 映射到数据库中的 username 字段
    private String username;  // 用户名

    @TableField("email")  // 映射到数据库中的 email 字段
    private String email;  // 用户邮箱

    @TableField("phone")  // 映射到数据库中的 phone 字段
    private String phone;  // 用户电话

    @TableField("user_type")  // 映射到数据库中的 user_type 字段
    private UserType userType;  // 用户类型（开发者或公司）

    @TableField("status")  // 映射到数据库中的 status 字段
    private UserStatus status = UserStatus.ACTIVE;  // 默认状态为 "ACTIVE"

    @TableField("created_at")  // 映射到数据库中的 created_at 字段
    private LocalDateTime createdAt = LocalDateTime.now();  // 创建时间

    @TableField("updated_at")  // 映射到数据库中的 updated_at 字段
    private LocalDateTime updatedAt = LocalDateTime.now();  // 更新时间

    // MyBatis-Plus 不会处理 Enum 类型的字符串转换，
    // 所以我们可以手动将 `UserStatus` 转换为字符串，或在数据库中存储它的字符串表示
    public String getStatus() {
        return status.toString();
    }

    public Serializable getName() {
        return username;
    }
}
