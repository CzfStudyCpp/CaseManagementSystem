package com.CloudApp.RequirementService.DTO;

import lombok.Data;

@Data
public class UserRequirementFavoriteDTO {
    private Long userId;        // 用户ID
    private Long requirementId; // 需求ID
}

