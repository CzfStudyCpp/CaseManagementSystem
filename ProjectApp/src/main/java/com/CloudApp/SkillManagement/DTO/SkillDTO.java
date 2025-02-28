package com.CloudApp.SkillManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor // 自动生成全参构造函数
@NoArgsConstructor  // 自动生成无参构造函数
public class SkillDTO {
    private Long id;
    private String name;
}

