package com.CloudApp.userServices.Service;

import com.CloudApp.SkillManagement.DTO.UserSkillDTO;
import com.CloudApp.userServices.DTO.UserProfileDTO;
import com.CloudApp.SkillManagement.DTO.SkillDTO;
import com.CloudApp.userServices.DTO.UserDTO;
import com.CloudApp.userServices.DTO.UserProfileUpdateDTO;

import java.util.List;
import java.util.Map;

public interface UserManagementService {

    //获得用户的个人信息
    UserProfileDTO getUserProfileByEmail(String email);

    //更新用户信息
    UserProfileUpdateDTO updateUserProfileByEmail(String email, UserProfileUpdateDTO payload);

    //更新用户的技能
    List<UserSkillDTO> updateUserSkillsByEmail(String email, List<Map<String, Object>> skillsPayload);

    //改密码
    boolean changePassword(String oldPassword, String newPassword);
    //通过邮箱改
    int changePasswordByEmail(String email, String code,String newPassword);

    boolean sendChangePasswordCode(String email);

    boolean changeEmail(String currentEmail, String newEmail, String verificationCode);

    boolean sendChangeEmailVerificationCode(String email);

    Map<String, Object> getUserList(int page, int size);

    UserProfileDTO getUserProfileById(Long userId);


}
