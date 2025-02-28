package com.CloudApp.RequirementService.Entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("requirement_comments") // 映射数据库表名
public class RequirementComment {
    @TableId(value = "id", type = IdType.AUTO) // 主键自增
    private Long id;

    @TableField("requirement_id") // 映射需求ID
    private Long requirementId;

    @TableField("user_id") // 映射评论用户ID
    private Long userId;

    @TableField("content") // 映射评论内容
    private String content;

    @TableField("created_at") // 映射创建时间
    private LocalDateTime createdAt;
}
