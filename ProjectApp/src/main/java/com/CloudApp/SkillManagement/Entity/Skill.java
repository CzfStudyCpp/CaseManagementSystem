package com.CloudApp.SkillManagement.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("skills") // 指定表名
public class Skill {
    @TableId(value = "id", type = IdType.AUTO) // 指定主键自增
    private Long id;

    @TableField("name")
    private String name;
    @TableField("description")
    private String description;
    @TableField("category")
    private String category;

    @TableField("created_at")
    @JsonIgnore // 忽略序列化
    private LocalDateTime createdAt;

    @TableField("updated_at")
    @JsonIgnore // 忽略序列化
    private LocalDateTime updatedAt;
}

