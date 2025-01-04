package com.CloudApp.userServices.Entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("email_change_requests") // 映射到数据库表 email_change_requests
public class EmailChangeRequest {

    @TableId(value = "id", type = IdType.AUTO) // 自增主键
    private Long id;

    @TableField("user_id") // 映射数据库字段 user_id
    private Long userId;

    @TableField("username") // 映射数据库字段 username
    private String username;

    @TableField("current_email") // 映射数据库字段 current_email
    private String currentEmail;

    @TableField("new_email") // 映射数据库字段 new_email
    private String newEmail;

    @TableField("status") // 映射数据库字段 status
    private String status; // 状态: PENDING, APPROVED, REJECTED

    @TableField("request_time") // 映射数据库字段 request_time
    private LocalDateTime requestTime; // 请求时间
}

