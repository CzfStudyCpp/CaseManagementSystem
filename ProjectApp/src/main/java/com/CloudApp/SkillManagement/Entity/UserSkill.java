package com.CloudApp.SkillManagement.Entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_skills") // 映射到 user_skills 表
public class UserSkill {
    @TableId(value = "id", type = IdType.AUTO) // 指定主键自增
    private Long id;

    @TableField("user_id") // 用户ID
    private Long userId;

    @TableField("skill_id") // 技能ID
    private Long skillId;

    @TableField("proficiency") // 技能熟练度
    private String proficiency;

    @TableField("created_at") // 创建时间
    @JsonIgnore // 忽略序列化
    private LocalDateTime createdAt;

    @TableField("updated_at") // 更新时间
    @JsonIgnore // 忽略序列化
    private LocalDateTime updatedAt;

}
