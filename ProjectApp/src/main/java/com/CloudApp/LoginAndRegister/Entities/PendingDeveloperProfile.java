package com.CloudApp.LoginAndRegister.Entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@TableName("pending_developer_profiles")  // 映射到数据库中的 pending_developer_profiles 表
public class PendingDeveloperProfile {

    @TableId(value = "id", type = IdType.AUTO)  // MyBatis-Plus 自增主键配置
    private Long id;

    @TableField("user_id")  // 映射到数据库中的 user_id 字段
    private Long userId;

    @TableField("real_name")  // 映射到数据库中的 real_name 字段
    @JsonInclude(JsonInclude.Include.NON_EMPTY)  // 空值字段不会序列化
    private String realName;

    @TableField("github")  // 映射到数据库中的 github 字段
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String github;

    @TableField("portfolio")  // 映射到数据库中的 portfolio 字段
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String portfolio;

    @TableField("experience")  // 映射到数据库中的 experience 字段
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String experience;

    @TableField("created_at")  // 映射到数据库中的 created_at 字段
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 时间格式化
    private LocalDateTime createdAt = LocalDateTime.now();

    @TableField("updated_at")  // 映射到数据库中的 updated_at 字段
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 时间格式化
    private LocalDateTime updatedAt = LocalDateTime.now();

    public void setPendingUser(PendingUser pendingUser) {
        userId = pendingUser.getId();
    }

    @Override
    public String toString() {
        return "PendingDeveloperProfile{" +
                "id=" + id +
                ", userId=" + userId +
                ", realName='" + realName + '\'' +
                ", github='" + github + '\'' +
                ", portfolio='" + portfolio + '\'' +
                ", experience='" + experience + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
