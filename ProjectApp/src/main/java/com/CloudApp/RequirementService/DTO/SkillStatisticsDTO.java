package com.CloudApp.RequirementService.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillStatisticsDTO {
    private String skillName;    // 技能名称
    private Long completedCount; // 已完成需求数量
    private Long pendingCount;   // 未完成需求数量
}