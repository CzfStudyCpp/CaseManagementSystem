package com.CloudApp.SkillManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSkillDTO {
    private Long id;
    private String name;
    private String proficiency; // 用户的熟练度
}