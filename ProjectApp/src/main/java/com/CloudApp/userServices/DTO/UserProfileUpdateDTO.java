package com.CloudApp.userServices.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileUpdateDTO {
    private UserDTO userProfile;
    private DeveloperProfileDTO developerProfile; // 开发者信息
    private CompanyProfileDTO companyProfile;     // 企业信息
}
