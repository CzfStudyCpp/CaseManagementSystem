package com.CloudApp.userServices.Controller;

import com.CloudApp.GlobalException.ErrorCode;
import com.CloudApp.LoginAndRegister.Entities.User;
import com.CloudApp.SkillManagement.DTO.SkillDTO;
import com.CloudApp.SkillManagement.DTO.UserSkillDTO;
import com.CloudApp.userServices.DTO.*;
import com.CloudApp.userServices.Service.UserManagementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.CloudApp.adminService.Response.ApiResponse;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserManagementService userManagementService;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    public UserController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    /**
     * 获取用户的个人信息和技能信息
     */
    @GetMapping("/getProfile")
    public ApiResponse getUserProfile(@RequestParam String email) {
        System.out.println("通过邮箱获取个人信息和技能信息...");
        try {
            UserProfileDTO userProfile = userManagementService.getUserProfileByEmail(email);
            System.out.println("成功获取个人信息和技能信息！");
            System.out.println(userProfile.toString());
            return ApiResponse.success("用户信息获取成功", userProfile, 200);
        } catch (Exception e) {
            System.err.println("获取个人信息失败：" + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error("获取用户信息失败: " + e.getMessage(), 500);
        }
    }

    /**
     * 更新用户个人信息
     */
    @PutMapping("/updateProfile")
    public ApiResponse updateUserProfile(@RequestBody Map<String, Object> payload) {
        try {
            System.out.println("开始更新用户个人信息...");

            // 提取邮箱
            String email = (String) payload.get("email");
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("邮箱不能为空");
            }

            // 使用 Jackson 的 ObjectMapper 将 Map 转换为相应的 DTO 对象
            UserDTO editableUser = objectMapper.convertValue(payload.get("editableUser"), UserDTO.class);
            DeveloperProfileDTO developerProfile = objectMapper.convertValue(payload.get("developerProfile"), DeveloperProfileDTO.class);
            CompanyProfileDTO companyProfile = objectMapper.convertValue(payload.get("companyProfile"), CompanyProfileDTO.class);

            // 封装成 UserProfileUpdateDTO
            UserProfileUpdateDTO userProfileUpdateDTO = new UserProfileUpdateDTO(editableUser, developerProfile, companyProfile);

            // 调用服务层进行更新操作
            UserProfileUpdateDTO updatedUserProfile = userManagementService.updateUserProfileByEmail(email, userProfileUpdateDTO);

            System.out.println("用户个人信息更新成功！");
            return ApiResponse.success("用户信息更新成功", updatedUserProfile, 200);
        } catch (Exception e) {
            System.err.println("更新用户个人信息失败：" + e.getMessage());
            //e.printStackTrace();
            return ApiResponse.error("更新用户信息失败: " + e.getMessage(), 500);
        }
    }


    /**
     * 更新用户技能信息
     */
    @PutMapping("/updateSkills")
    public ApiResponse updateUserSkills(@RequestBody Map<String, Object> payload) {
        try {
            System.out.println("开始更新用户技能信息...");
            String email = (String) payload.get("email");
            List<Map<String, Object>> skillsPayload = (List<Map<String, Object>>) payload.get("skills");
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("邮箱不能为空");
            }
            if (skillsPayload == null || skillsPayload.isEmpty()) {
                throw new IllegalArgumentException("技能信息不能为空");
            }
            List<UserSkillDTO> updatedSkills = userManagementService.updateUserSkillsByEmail(email, skillsPayload);
            System.out.println("用户技能信息更新成功！");
            return ApiResponse.success("用户技能更新成功", updatedSkills, 200);
        } catch (Exception e) {
            System.err.println("更新用户技能信息失败：" + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error("更新用户技能失败: " + e.getMessage(), 500);
        }
    }
    /**
     * 通过旧密码修改密码
     */
    @PostMapping("/changePassword")
    public ApiResponse changePassword(@RequestBody Map<String, String> payload) {
        try {
            String oldPassword = payload.get("oldPassword");
            String newPassword = payload.get("newPassword");
            String confirmPassword = payload.get("confirmPassword");

            if (oldPassword == null || oldPassword.isEmpty()) {
                return ApiResponse.error("旧密码不能为空", ErrorCode.BAD_REQUEST.getCode());
            }
            if (newPassword == null || newPassword.isEmpty()) {
                return ApiResponse.error("新密码不能为空", ErrorCode.BAD_REQUEST.getCode());
            }
            if (!newPassword.equals(confirmPassword)) {
                return ApiResponse.error("新密码和确认密码不一致", ErrorCode.BAD_REQUEST.getCode());
            }

            // 调用服务层方法执行密码更新
            boolean isUpdated = userManagementService.changePassword(oldPassword, newPassword);

            if (isUpdated) {
                return ApiResponse.success("密码修改成功", null, 200);
            } else {
                return ApiResponse.error("旧密码不正确", ErrorCode.INCORRECT_PASSWORD.getCode());
            }
        } catch (Exception e) {
            System.err.println("通过旧密码修改密码失败：" + e.getMessage());
           // e.printStackTrace();
            return ApiResponse.error("修改密码失败: " + e.getMessage(), 500);
        }
    }

    /**
     * 通过邮箱验证码修改密码
     */
    @PostMapping("/changePasswordByEmail")
    public ApiResponse changePasswordByEmail(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            String code = payload.get("code");
            String newPassword = payload.get("newPassword");
            String confirmPassword = payload.get("confirmPassword");

            if (email == null || email.isEmpty()) {
                return ApiResponse.error("邮箱不能为空", 400);
            }
            if (code == null || code.isEmpty()) {
                return ApiResponse.error("验证码不能为空", 400);
            }
            if (newPassword == null || newPassword.isEmpty()) {
                return ApiResponse.error("新密码不能为空", 400);
            }
            if (!newPassword.equals(confirmPassword)) {
                return ApiResponse.error("新密码和确认密码不一致", 400);
            }

            // 调用服务层方法验证验证码并更新密码
            int isUpdated = userManagementService.changePasswordByEmail(email, code, newPassword);

            if (isUpdated==1) {
                return ApiResponse.success("密码修改成功", null, 200);
            } else if(isUpdated==2) {
                return ApiResponse.error("验证码错误", ErrorCode.VERIFICATION_CODE_ERROR.getCode());
            }
            else{
                return ApiResponse.error("验证码无效或已过期", ErrorCode.VERIFICATION_CODE_INVALID.getCode());
            }
        } catch (Exception e) {
            System.err.println("通过邮箱验证码修改密码失败：" + e.getMessage());
            //e.printStackTrace();
            return ApiResponse.error("修改密码失败: " + e.getMessage(), 500);
        }
    }
    /**
     * 发送修改密码的验证码
     */
    @PostMapping("/sendChangePasswordCode")
    public ApiResponse sendChangePasswordCode(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            if (email == null || email.isEmpty()) {
                return ApiResponse.error("邮箱不能为空", 400);
            }

            // 调用服务层方法发送验证码
            boolean isSent = userManagementService.sendChangePasswordCode(email);

            if (isSent) {
                return ApiResponse.success("验证码已发送到您的邮箱", null, 200);
            } else {
                return ApiResponse.error("验证码发送失败，请稍后重试", ErrorCode.INTERNAL_SERVER_ERROR.getCode());
            }
        } catch (Exception e) {
            System.err.println("发送验证码失败：" + e.getMessage());
            //e.printStackTrace();
            return ApiResponse.error("发送验证码失败: " + e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR.getCode());
        }
    }

    /**
     * 发送修改邮箱的验证码
     */
    @PostMapping("/sendChangeEmailVerificationCode")
    public ApiResponse sendChangeEmailVerificationCode(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            if (email == null || email.isEmpty()) {
                return ApiResponse.error("邮箱不能为空", 400);
            }

            // 调用服务层方法发送验证码
            boolean isSent = userManagementService.sendChangeEmailVerificationCode(email);

            if (isSent) {
                return ApiResponse.success("验证码已发送到您的邮箱", null, 200);
            } else {
                return ApiResponse.error("验证码发送失败，请稍后重试", 500);
            }
        } catch (Exception e) {
            //System.err.println("发送验证码失败：" + e.getMessage());
            return ApiResponse.error("发送验证码失败: " + e.getMessage(), 500);
        }
    }

    /**
     * 修改邮箱
     */
    @PostMapping("/changeEmail")
    public ApiResponse changeEmail(@RequestBody Map<String, String> payload) {
        try {
            String currentEmail = payload.get("currentEmail");
            String newEmail = payload.get("newEmail");
            String verificationCode = payload.get("verificationCode");

            if (currentEmail == null || currentEmail.isEmpty()) {
                return ApiResponse.error("当前邮箱不能为空", 400);
            }
            if (newEmail == null || newEmail.isEmpty()) {
                return ApiResponse.error("新邮箱不能为空", 400);
            }
            if (verificationCode == null || verificationCode.isEmpty()) {
                return ApiResponse.error("验证码不能为空", 400);
            }

            // 调用服务层方法更改邮箱
            boolean isUpdated = userManagementService.changeEmail(currentEmail, newEmail, verificationCode);

            if (isUpdated) {
                return ApiResponse.success("邮箱修改成功，请等待管理员审核", null, 200);
            } else {
                return ApiResponse.error("邮箱修改失败，请稍后重试", 500);
            }
        } catch (Exception e) {
            System.err.println("修改邮箱失败：" + e.getMessage());
            return ApiResponse.error("修改邮箱失败: " + e.getMessage(), 500);
        }
    }

    /**
     * 获取用户分页列表
     * @param params 包含分页参数：page（当前页）和 size（每页大小）
     * @return 分页的用户列表
     */
    @GetMapping("/userList")
    public ApiResponse getUserList(@RequestParam Map<String, Object> params) {
        try {
            int page = Integer.parseInt(params.getOrDefault("page", "1").toString());
            int size = Integer.parseInt(params.getOrDefault("size", "10").toString());

            Map<String, Object> result = userManagementService.getUserList(page, size);

            return ApiResponse.success("用户列表获取成功", result, 200);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("用户列表获取失败: " + e.getMessage(), 500);
        }
    }

    /**
     * 获取用户详细信息
     * @param userId 用户ID
     * @return 用户的详细信息
     */
    @GetMapping("/userDetails/{userId}")
    public ApiResponse getUserDetails(@PathVariable Long userId) {
        try {
            UserProfileDTO userProfile = userManagementService.getUserProfileById(userId);
            return ApiResponse.success("用户详细信息获取成功", userProfile, 200);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("用户详细信息获取失败: " + e.getMessage(), 500);
        }
    }




}



