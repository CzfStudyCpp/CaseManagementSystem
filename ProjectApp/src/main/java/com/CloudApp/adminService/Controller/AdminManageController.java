package com.CloudApp.adminService.Controller;


import com.CloudApp.GlobalException.ErrorCode;
import com.CloudApp.LoginAndRegister.Entities.Admin;
import com.CloudApp.LoginAndRegister.Entities.PendingUser;
import com.CloudApp.RequirementService.DTO.SkillStatisticsDTO;
import com.CloudApp.RequirementService.Service.RequirementService;
import com.CloudApp.adminService.DTO.AdminAddDTO;
import com.CloudApp.adminService.DTO.AdminDTO;
import com.CloudApp.adminService.Service.AdminManageService;
import com.CloudApp.adminService.Response.ApiResponse;
import com.CloudApp.userServices.DTO.*;
import com.CloudApp.userServices.Entities.EmailChangeRequest;
import com.CloudApp.userServices.Service.UserManagementService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.CloudApp.SkillManagement.Entity.Skill;
import com.CloudApp.SkillManagement.Service.SkillService;


import java.util.*;


@RestController
@RequestMapping("/admin")
public class AdminManageController {

    private final AdminManageService adminManageService;

    private final SkillService skillService;

    //直接复用用户管理的业务处理。减少代码
    private final UserManagementService userManagementService;


    private final RequirementService requirementService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    public AdminManageController(AdminManageService adminManageService, SkillService skillService, UserManagementService userManagementService, RequirementService requirementService) {
        this.adminManageService = adminManageService;
        this.skillService = skillService;
        this.userManagementService = userManagementService;

        this.requirementService = requirementService;
    }

    /**
     * 获取待审核用户
     *
     * @return 待审核用户列表（开发者和企业账户）
     */
    @GetMapping("/getPendingUsers")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'NORMAL_ADMIN')")
    public ApiResponse getPendingUsers() {
        try {
            List<Map<String, Object>> developers = adminManageService.getPendingDevelopers();
            List<Map<String, Object>> companies = adminManageService.getPendingCompanies();

            // 确保返回的数据不为 null
            developers = developers != null ? developers : Collections.emptyList();
            companies = companies != null ? companies : Collections.emptyList();

            return ApiResponse.success("获取待审核用户成功", Map.of(
                    "developers", developers,
                    "companies", companies
            ), 200);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getMessage(), 500);
        }
    }

    /**
     * 审核通过用户
     *
     * @param id   用户ID
     * @param type 用户类型（developer 或 company）
     * @return 操作结果
     */
    @PostMapping("/approveUser")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'NORMAL_ADMIN')")
    public ApiResponse approveUser(@RequestParam("id") Long id, @RequestParam("type") String type) {
        System.out.println("收到通过请求: 正在处理用户，用户ID为"+id+"用户类型为"+type); // 调试日志

        try {
            adminManageService.approveUser(id, type);
            // 返回成功响应，状态码为 200
            return ApiResponse.success("审核通过成功", 200);
        } catch (Exception e) {
            // 返回错误响应，使用 ErrorCode.INTERNAL_SERVER_ERROR
            return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + ": " + e.getMessage(), 500);
        }
    }

    /**
     * 拒绝审核用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @PostMapping("/rejectUser")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'NORMAL_ADMIN')")
    public ApiResponse rejectUser(@RequestParam("id") Long id) {
        try {
            adminManageService.rejectUser(id);
            // 返回成功响应，状态码为 200
            return ApiResponse.success("拒绝用户成功", 200);
        } catch (Exception e) {
            // 返回错误响应，使用 ErrorCode.INTERNAL_SERVER_ERROR
            return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + ": " + e.getMessage(), 500);
        }
    }

    /**
     * 获取所有技能标签
     */
    @GetMapping("/getSkills")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'NORMAL_ADMIN','DEVELOPER','COMPANY')")
    public ApiResponse getAllSkills() {
        try {
            List<Skill> skills = skillService.getAllSkills();
            return ApiResponse.success("获取技能标签成功", skills, 200);
        } catch (Exception e) {
            return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + ": " + e.getMessage(), 500);
        }
    }

    /**
     * 添加新的技能标签
     */
    @PostMapping("/addSkills")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'NORMAL_ADMIN')")
    public ApiResponse addSkill(@RequestBody Skill skill) {
        try {
            Skill newSkill = skillService.addSkill(skill);
            return ApiResponse.success("技能标签添加成功", newSkill, 200);
        } catch (Exception e) {
            return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + ": " + e.getMessage(), 500);
        }
    }

    /**
     * 删除技能标签
     */
    @DeleteMapping("/deleteSkills/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'NORMAL_ADMIN')")
    public ApiResponse deleteSkill(@PathVariable Long id) {
        try {
            skillService.deleteSkill(id);
            return ApiResponse.success("技能标签删除成功", 200);
        } catch (Exception e) {
            return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + ": " + e.getMessage(), 500);
        }
    }

    /**
     * 更新技能标签
     */
    @PutMapping("/updateSkills/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'NORMAL_ADMIN')")
    public ApiResponse updateSkill(@PathVariable Long id, @RequestBody Skill skill) {
        try {
            Skill updatedSkill = skillService.updateSkill(id, skill);
            return ApiResponse.success("技能标签更新成功", updatedSkill, 200);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error("非法请求：" + e.getMessage(), 400); // 错误请求
        } catch (Exception e) {
            return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getMessage() + ": " + e.getMessage(), 500);
        }
    }

    /**
     * 获取用户分页列表（所有管理员都可以访问）
     *
     * @param params 包含分页参数：page（当前页）和 size（每页大小）
     * @return 分页的用户列表
     */
    @GetMapping("/getUserList")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'NORMAL_ADMIN')")
    public ApiResponse getUserList(@RequestParam Map<String, Object> params) {
        try {
            int page = Integer.parseInt(params.getOrDefault("page", "1").toString());
            int size = Integer.parseInt(params.getOrDefault("size", "10").toString());

            // 调用复用的 userServices 方法
            Map<String, Object> result = userManagementService.getUserList(page, size);

            return ApiResponse.success("用户列表获取成功", result, 200);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("用户列表获取失败: " + e.getMessage(), 500);
        }
    }

    /**
     * 获取用户详细信息（所有管理员都可以访问）
     *
     * @param userId 用户ID
     * @return 用户的详细信息
     */
    @GetMapping("/userDetails/{userId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'NORMAL_ADMIN')")
    public ApiResponse getUserDetails(@PathVariable Long userId) {
        try {
            UserProfileDTO userProfile = userManagementService.getUserProfileById(userId);
            System.out.println("用户详细信息获取成功"+userProfile);
            return ApiResponse.success("用户详细信息获取成功", userProfile, 200);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("用户详细信息获取失败: " + e.getMessage(), 500);
        }
    }

    @PutMapping("/updateUser/{userId}")
    public ApiResponse updateUserByAdmin(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> payload) {
        try {
            System.out.println("管理员开始更新用户信息...");

            // 提取 UserDTO (基础用户信息)
            UserDTO editableUser = objectMapper.convertValue(payload.get("editableUser"), UserDTO.class);
            if (editableUser.getEmail() == null || editableUser.getEmail().isEmpty()) {
                throw new IllegalArgumentException("用户邮箱不能为空");
            }

            // 提取开发者信息
            DeveloperProfileDTO developerProfile = objectMapper.convertValue(payload.get("developerProfile"), DeveloperProfileDTO.class);

            // 提取企业信息
            CompanyProfileDTO companyProfile = objectMapper.convertValue(payload.get("companyProfile"), CompanyProfileDTO.class);

            // 构建更新 DTO
            UserProfileUpdateDTO userProfileUpdateDTO = new UserProfileUpdateDTO(editableUser, developerProfile, companyProfile);

            // 调用服务层更新逻辑
            UserProfileUpdateDTO updatedUserProfile = userManagementService.updateUserProfileByEmail(
                    editableUser.getEmail(), // 直接从 `editableUser` 中提取邮箱
                    userProfileUpdateDTO
            );

            System.out.println("管理员成功更新用户信息！");
            return ApiResponse.success("用户信息更新成功", updatedUserProfile, 200);
        } catch (Exception e) {
            System.err.println("管理员更新用户信息失败：" + e.getMessage());
            return ApiResponse.error("更新用户信息失败: " + e.getMessage(), 500);
        }
    }

    @PutMapping("/updateUserStatus")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'NORMAL_ADMIN')")
    public ApiResponse updateUserStatus(@RequestBody Map<String, Object> requestBody) {
        try {
            // 从请求体中获取用户ID和目标状态
            Long userId = ((Number) requestBody.get("userId")).longValue();
            String newStatus = (String) requestBody.get("status");
            System.out.println("用户状态更新目标，用户ID：" + userId + "，新状态：" + newStatus);

            // 校验请求数据
            if (userId == null || newStatus == null || (!newStatus.equals("ACTIVE") && !newStatus.equals("SUSPENDED"))) {
                return ApiResponse.error("无效的请求参数", 400);
            }

            // 调用 Service 更新用户状态
            adminManageService.updateUserStatus(userId, newStatus);
            System.out.println("用户状态更新成功，用户ID：" + userId + "，新状态：" + newStatus);

            // 返回成功响应
            return ApiResponse.success("用户状态更新成功", Map.of("userId", userId, "status", newStatus), 200);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("用户状态更新失败: " + e.getMessage(), 500);
        }
    }


    @GetMapping("/getEmailChangeRequests")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'NORMAL_ADMIN')")
    public ApiResponse getEmailChangeRequests(@RequestParam(defaultValue = "1") Integer page,
                                              @RequestParam(defaultValue = "10") Integer size) {
        try {
            // 使用 MyBatis-Plus 提供的分页功能
            Page<EmailChangeRequest> requestPage = new Page<>(page, size);

            // 查询分页数据
            IPage<EmailChangeRequest> emailChangeRequests = adminManageService.getEmailChangeRequests(requestPage);

            // 构造响应数据
            Map<String, Object> data = Map.of(
                    "requests", emailChangeRequests.getRecords(),
                    "currentPage", emailChangeRequests.getCurrent(),
                    "totalPages", emailChangeRequests.getPages(),
                    "totalRequests", emailChangeRequests.getTotal()
            );

            return ApiResponse.success("邮箱更换请求列表获取成功", data, 200);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取邮箱更换请求列表失败: " + e.getMessage(), 500);
        }
    }

    @PostMapping("/handleEmailChange")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'NORMAL_ADMIN')")
    public ApiResponse handleEmailChange(@RequestBody Map<String, String> request) {
        try {
            // 解构前端传递的数据
            String username = request.get("username");
            String currentEmail = request.get("currentEmail");
            String targetEmail = request.get("targetEmail");
            String action = request.get("action");

            if (username == null || currentEmail == null || targetEmail == null || action == null) {
                throw new IllegalArgumentException("请求参数不完整");
            }

            int isApproved=adminManageService.handleEmailChangeRequest(username, currentEmail, targetEmail, action);

            // 返回成功响应
            Map<String, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("currentEmail", currentEmail);
            response.put("targetEmail", targetEmail);
            response.put("action", action);
            String result;
            if(isApproved==1) {
                result = "邮箱同意变更请求已成功";
                return ApiResponse.success("邮箱同意变更请求已成功", response, 200);
            }
            else if(isApproved==2) {return ApiResponse.success("邮箱拒绝变更请求已成功", response, 200);}
            else  return ApiResponse.success("邮箱变更请求失败", response, 500);

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("处理邮箱更换请求失败: " + e.getMessage(), 500);
        }
    }

    @GetMapping("/getBannedUser")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'NORMAL_ADMIN')")
    public ApiResponse getBannedUserList(@RequestParam Map<String, Object> params) {
        try {
            // 解析分页参数
            int page = Integer.parseInt(params.getOrDefault("page", "1").toString());
            int size = Integer.parseInt(params.getOrDefault("size", "10").toString());

            // 调用服务方法获取封禁用户列表
            Map<String, Object> result = adminManageService.getBannedUserList(page, size);

            return ApiResponse.success("封禁用户列表获取成功", result, 200);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("封禁用户列表获取失败: " + e.getMessage(), 500);
        }
    }
    @DeleteMapping("/deleteUser")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'NORMAL_ADMIN')")
    public ApiResponse deleteUser(@RequestBody Map<String, Object> payload) {
        try {
            // 从请求体中获取 userId
            Long userId = Long.valueOf(payload.get("userId").toString());

            // 调用服务层方法删除用户
            boolean isDeleted = adminManageService.deleteUserById(userId);
            if (isDeleted) {
                return ApiResponse.success("用户删除成功", null, 200);
            } else {
                return ApiResponse.error("删除用户失败：用户不存在", 404);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("删除用户失败: " + e.getMessage(), 500);
        }


    }


    @GetMapping("/getAdmins")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse getAdmins(@RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        try {
            Map<String, Object> result = adminManageService.getAdminList(page, size);
            return ApiResponse.success("管理员列表获取成功", result, 200);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取管理员列表失败: " + e.getMessage(), 500);
        }
    }

    @PostMapping("/addAdmin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse addAdmin(@RequestBody AdminAddDTO adminAddDTO) {
        try {
            Admin admin = new Admin();
            admin.setUsername(adminAddDTO.getUsername());
            admin.setEmail(adminAddDTO.getEmail());
            admin.setAccessLevel(adminAddDTO.getRole());
            admin.setStatus(adminAddDTO.getStatus());
            admin.setPassword(adminAddDTO.getPassword());
            admin.setIsSuperAdmin(Objects.equals(adminAddDTO.getStatus(), "SUPER_ADMIN"));
            boolean isAdded = adminManageService.addAdmin(admin);
            System.out.println("接收到的管理员信息: " + admin.getAccessLevel());
            if (isAdded) {
                return ApiResponse.success("管理员添加成功", null, 200);
            } else {
                return ApiResponse.error("管理员添加失败，请检查输入信息", 400);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("添加管理员失败: " + e.getMessage(), 500);
        }
    }

    @DeleteMapping("/deleteAdmin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse deleteAdmin(@RequestBody Map<String, Long> request) {
        try {
            Long adminId = request.get("adminId");
            boolean isDeleted = adminManageService.deleteAdmin(adminId);
            if (isDeleted) {
                return ApiResponse.success("管理员删除成功", null, 200);
            } else {
                return ApiResponse.error("管理员删除失败，请检查管理员ID", 400);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("删除管理员失败: " + e.getMessage(), 500);
        }
    }



    // 获取每个技能的已完成和未完成需求数量统计
    @GetMapping("/skill-statistics")
    public ApiResponse getSkillStatistics() {
        try {
            List<Map<String, Object>> statistics = requirementService.getSkillStatistics();
            return ApiResponse.success("获取技能需求统计成功", statistics, 200);
        } catch (Exception e) {
            return ApiResponse.error("获取技能需求统计失败：" + e.getMessage(), 500);
        }
    }

}

