package com.CloudApp.AnnouncementManagementService.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@TableName("announcements") // 映射数据库表名
public class Announcement {

    // Getters and Setters
    @TableId(value = "id", type = IdType.AUTO)
    private Long id; // 公告ID，主键

    @TableField("title")
    private String title; // 公告标题

    @TableField("content")
    private String content; // 公告内容

    @TableField("is_published")
    private Boolean isPublished; // 公告发布状态 (true: 发布, false: 草稿)

    @TableField("last_updated_date")
    private LocalDateTime lastUpdatedDate; // 最后更新日期

    @TableField("created_date")
    private LocalDateTime createdDate; // 创建日期

}
