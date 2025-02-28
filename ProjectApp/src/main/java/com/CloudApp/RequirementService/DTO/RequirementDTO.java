package com.CloudApp.RequirementService.DTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RequirementDTO {
    private Long id;                // 需求ID
    private String title;           // 需求标题
    private String description;     // 需求描述
    private BigDecimal budget;      // 需求预算
    private String status;          // 需求状态 (DRAFT, PUBLISHED,COMPLETED)
    private Long userId;            // 发布者用户ID
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;


    private Integer score;          // 推荐评分，用于排序

    private int favoritesCount;

    private List<String> skills;    // 技能列表，新增字段

    private String solution;

}
