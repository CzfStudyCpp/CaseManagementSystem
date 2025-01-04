package com.CloudApp.RequirementService.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequirementCommentDTO {
    private Long id; // 评论 ID
    private String username; // 用户名
    private String content; // 评论内容
    private LocalDateTime createdAt; // 评论时间
}
