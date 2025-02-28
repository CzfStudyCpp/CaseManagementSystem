package com.CloudApp.LoginAndRegister.Entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.CloudApp.LoginAndRegister.Enums.UserStatus;
import com.CloudApp.LoginAndRegister.Enums.UserType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("pending_users")  // 映射到数据库中的 pending_users 表
public class PendingUser {

    @TableId(value = "id", type = IdType.AUTO)  // MyBatis-Plus 自增主键配置
    private Long id;

    @TableField("username")  // 映射到数据库中的 username 字段
    private String username;

    @TableField("email")  // 映射到数据库中的 email 字段
    private String email;

    @TableField("phone")  // 映射到数据库中的 phone 字段
    private String phone;

    @TableField("user_type")  // 映射到数据库中的 user_type 字段
    @JsonFormat(shape = JsonFormat.Shape.STRING) // 枚举序列化为字符串
    private UserType userType;  // 用户类型枚举

    @TableField("status")  // 映射到数据库中的 status 字段
    @JsonFormat(shape = JsonFormat.Shape.STRING) // 枚举序列化为字符串
    private UserStatus status = UserStatus.PENDING;  // 默认值为 PENDING

    @TableField("submitted_at")  // 映射到数据库中的 submitted_at 字段
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 时间格式化
    private LocalDateTime submittedAt = LocalDateTime.now();  // 默认提交时间

    @TableField("reviewed_at")  // 映射到数据库中的 reviewed_at 字段
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 时间格式化
    private LocalDateTime reviewedAt;  // 审核时间

    @TableField("rejection_reason")  // 映射到数据库中的 rejection_reason 字段
    @JsonInclude(JsonInclude.Include.NON_EMPTY)  // 空值字段不会序列化
    private String rejectionReason;  // 拒绝原因

    @Override
    public String toString() {
        return "PendingUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", userType=" + userType +
                ", status=" + status +
                ", submittedAt=" + submittedAt +
                ", reviewedAt=" + reviewedAt +
                ", rejectionReason='" + rejectionReason + '\'' +
                '}';
    }
}
