package com.CloudApp.RequirementService.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("requirement_skills") // 映射数据库表名
public class RequirementSkill {
    @TableId(value = "id", type = IdType.AUTO) // 主键自增
    private Long id;

    @TableField("requirement_id") // 映射需求ID
    private Long requirementId;

    @TableField("skill_id") // 映射技能ID
    private Long skillId;

    @TableField("created_at") // 映射创建时间
    private LocalDateTime createdAt;
}
