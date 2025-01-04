package com.CloudApp.RequirementService.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("user_requirement_favorites") // 映射数据库表名
public class UserRequirementFavorite {
    @TableId(value = "id", type = IdType.AUTO) // 主键自增
    private Long id;

    @TableField("user_id") // 映射用户ID
    private Long userId;

    @TableField("requirement_id") // 映射需求ID
    private Long requirementId;

    @TableField("created_at") // 映射创建时间
    private LocalDateTime createdAt;

}