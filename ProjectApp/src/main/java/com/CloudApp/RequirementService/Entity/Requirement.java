package com.CloudApp.RequirementService.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("requirements") // 映射数据库表名
public class Requirement {
    @TableId(value = "id", type = IdType.AUTO) // 主键自增
    private Long id;

    @TableField("title") // 映射数据库字段
    private String title;

    @TableField("description")
    private String description;

    @TableField("budget")
    private BigDecimal budget;

    @TableField("status")
    private String status;

    @TableField("user_id")
    private Long userId;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableField("solution")
    private String solution;

}