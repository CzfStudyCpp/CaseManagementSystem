package com.CloudApp.LoginAndRegister.Entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("user_passwords")  // 映射到数据库表 user_passwords
public class UserPassword {

    @TableId(value = "id", type = IdType.AUTO)  // 主键，自动递增
    private Long id;

    // 设置用户ID的方法
    @Setter
    @TableField("user_id")  // 数据库字段映射
    private Long userId;

    @TableField("password_hash")  // 数据库字段映射
    private String passwordHash;

}
