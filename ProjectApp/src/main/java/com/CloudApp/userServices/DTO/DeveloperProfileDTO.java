package com.CloudApp.userServices.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeveloperProfileDTO {
    private Long userId;
    private String realName;   // 真实姓名
    private String github;     // GitHub 账号
    private String portfolio;  // 作品集链接
    private String experience; // 工作经验



//    public DeveloperProfileDTO(Long userId, String realName, String github, String portfolio, String experience) {
//        this.userId = userId;
//        this.realName = realName;
//        this.github = github;
//        this.portfolio = portfolio;
//        this.experience = experience;
//    }
}
