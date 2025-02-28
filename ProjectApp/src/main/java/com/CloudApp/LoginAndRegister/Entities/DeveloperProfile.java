package com.CloudApp.LoginAndRegister.Entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("developer_profiles")  // 对应数据库中的 developer_profiles 表
public class DeveloperProfile {

    @TableId(value = "id", type = IdType.AUTO)  // MyBatis-Plus 自增主键配置
    private Long id;  // 开发者ID

    @Setter
    @TableField("user_id")  // 映射到数据库中的 user_id 字段
    private Long userId;  // 用户ID，用于与 User 表关联

    @TableField("real_name")  // 映射到数据库中的 real_name 字段
    private String realName;  // 开发者真实姓名

    @TableField("github")  // 映射到数据库中的 github 字段
    private String github;  // GitHub 账号

    @TableField("portfolio")  // 映射到数据库中的 portfolio 字段
    private String portfolio;  // 作品集链接

    @TableField("experience")  // 映射到数据库中的 experience 字段
    private String experience;  // 工作经验

    @TableField("created_at")  // 映射到数据库中的 created_at 字段
    private LocalDateTime createdAt = LocalDateTime.now();  // 创建时间

    @TableField("updated_at")  // 映射到数据库中的 updated_at 字段
    private LocalDateTime updatedAt = LocalDateTime.now();  // 更新时间

}
