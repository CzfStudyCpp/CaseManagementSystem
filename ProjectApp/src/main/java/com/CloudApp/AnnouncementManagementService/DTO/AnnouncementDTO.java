package com.CloudApp.AnnouncementManagementService.DTO;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class AnnouncementDTO {

    // Getters and Setters
    private Long id; // 公告ID
    private String title; // 公告标题
    private String content; // 公告内容
    private Boolean isPublished; // 公告发布状态 (true: 发布, false: 草稿)
    private LocalDateTime lastUpdatedDate; // 最后更新日期
    private LocalDateTime createdDate; // 创建日期

    // Constructors
    public AnnouncementDTO() {
    }

    public AnnouncementDTO(Long id, String title, String content, Boolean isPublished, LocalDateTime lastUpdatedDate, LocalDateTime createdDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.isPublished = isPublished;
        this.lastUpdatedDate = lastUpdatedDate;
        this.createdDate = createdDate;
    }


    public AnnouncementDTO(Long id, String title, String content, Boolean isPublished, LocalDateTime lastUpdatedDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.isPublished = isPublished;
        this.lastUpdatedDate = lastUpdatedDate;
    }
}
