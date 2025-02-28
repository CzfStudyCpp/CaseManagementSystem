package com.CloudApp.userServices.DTO;

import com.CloudApp.SkillManagement.DTO.SkillDTO;
import com.CloudApp.SkillManagement.DTO.UserSkillDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {
    @Getter
    private UserDTO userProfile;            // 用户个人信息
    private List<UserSkillDTO> userSkills; // 用户技能信息
    private List<SkillDTO> skills;    // 所有技能选项

    private DeveloperProfileDTO developerProfile; // 开发者信息
    private CompanyProfileDTO companyProfile;     // 企业信息

}
