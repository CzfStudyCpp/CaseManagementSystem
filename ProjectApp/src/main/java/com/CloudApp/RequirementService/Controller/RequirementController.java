package com.CloudApp.RequirementService.Controller;

import com.CloudApp.RequirementService.DTO.RequirementCommentDTO;
import com.CloudApp.RequirementService.DTO.RequirementDTO;
import com.CloudApp.RequirementService.Entity.RequirementComment;
import com.CloudApp.RequirementService.Service.RequirementService;
import com.CloudApp.adminService.Response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/requirements")
public class RequirementController {

    @Autowired
    private RequirementService requirementService;

    // 获取推荐需求
    @GetMapping("/recommend")
    public ApiResponse getRecommendedRequirements() {
        try {

            List<RequirementDTO> recommendations = requirementService.getRecommendedRequirements();
            System.out.println("生成的推荐列表为" + recommendations);
            return ApiResponse.success("获取推荐需求成功", recommendations, 200);
        } catch (Exception e) {
            return ApiResponse.error("获取推荐需求失败: " + e.getMessage(), 500);
        }
    }

    // 根据技能筛选需求
    @PostMapping("/by-skills")
    public ApiResponse getRequirementsBySkills(@RequestBody List<Long> skillIds) {
        try {
            List<RequirementDTO> filteredRequirements = requirementService.getRequirementsBySkills(skillIds);
            return ApiResponse.success("根据技能获取需求成功", filteredRequirements, 200);
        } catch (Exception e) {
            return ApiResponse.error("根据技能获取需求失败: " + e.getMessage(), 500);
        }
    }

    @GetMapping("/detail/{id}")
    public ApiResponse getRequirementDetail(@PathVariable Long id) {
        try {
            RequirementDTO requirement = requirementService.getRequirementDetail(id);
            if (requirement != null) {
                return ApiResponse.success("获取需求详情成功", requirement, 200);
            } else {
                return ApiResponse.error("未找到对应的需求", 501);
            }
        } catch (Exception e) {
            return ApiResponse.error("获取需求详情失败：" + e.getMessage(), 500);
        }
    }

    // 检查用户是否已关注某个需求
    @GetMapping("/checkFollow/{id}")
    public ApiResponse checkIfFollowed(@PathVariable Long id) {
        try {
            // 从 Spring Security 上下文中获取用户名
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if (username == null || username.isEmpty() || "anonymousUser".equals(username)) {
                return ApiResponse.error("用户未登录", 401); // 未登录用户
            }

            // 检查是否已关注
            boolean isFollowed = requirementService.checkIfFollowed(id, username);
            return ApiResponse.success("检查关注状态成功", isFollowed, 200);

        } catch (Exception e) {
            return ApiResponse.error("检查关注状态失败：" + e.getMessage(), 500);
        }
    }

    // 处理关注或取消关注
    @PostMapping("/toggleFollow")
    public ApiResponse toggleFollow(@RequestBody Map<String, Object> payload) {
        Long requirementId = ((Number) payload.get("demandId")).longValue(); // 获取需求 ID
        String action = (String) payload.get("action"); // 获取操作类型（follow 或 unfollow）
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (username == null || "anonymousUser".equals(username)) {
            return ApiResponse.error("用户未登录", 401);
        }

        try {
            if ("follow".equals(action)) {
                boolean success = requirementService.followRequirement(requirementId, username);
                return success
                        ? ApiResponse.success("关注成功", null, 200)
                        : ApiResponse.error("关注失败", 500);
            } else if ("unfollow".equals(action)) {
                boolean success = requirementService.unfollowRequirement(requirementId, username);
                return success
                        ? ApiResponse.success("取消关注成功", null, 200)
                        : ApiResponse.error("取消关注失败", 500);
            } else {
                return ApiResponse.error("无效的操作类型", 400);
            }
        } catch (Exception e) {
            return ApiResponse.error("操作失败：" + e.getMessage(), 500);
        }
    }


    // 发布评论
    @PostMapping("addComments")
    public ApiResponse addComment(@RequestBody Map<String, Object> payload) {
        // 使用 `Long.valueOf` 将字符串解析为 `Long`
        Long requirementId;
        try {
            requirementId = Long.valueOf(payload.get("requirementId").toString());
        } catch (NumberFormatException e) {
            return ApiResponse.error("无效的需求ID", 400);
        }
        String content = (String) payload.get("content");

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(username)) {
            return ApiResponse.error("用户未登录", 401);
        }

        try {
            requirementService.addComment(requirementId, content, username);
            return ApiResponse.success("评论发布成功", null, 200);
        } catch (Exception e) {
            return ApiResponse.error("评论发布失败：" + e.getMessage(), 500);
        }
    }

    // 获取评论列表
    @GetMapping("/{requirementId}/comments")
    public ApiResponse getCommentsByRequirementId(
            @PathVariable Long requirementId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize
    ) {
        try {
            List<RequirementCommentDTO> comments = requirementService.getCommentsByRequirementId(requirementId, page, pageSize);
            int totalCount = requirementService.countCommentsByRequirementId(requirementId);

            Map<String, Object> response = new HashMap<>();
            response.put("comments", comments);
            response.put("totalCount", totalCount);
            response.put("page", page);
            response.put("pageSize", pageSize);

            return ApiResponse.success("获取评论成功", response, 200);
        } catch (Exception e) {
            return ApiResponse.error("获取评论失败：" + e.getMessage(), 500);
        }
    }

    /**
     * 获取用户关注的需求
     */
    @GetMapping("/favorite-requirements")
    public ApiResponse getFavoriteRequirements() {
        //上下文获取用户
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(username)) {
            return ApiResponse.error("用户未登录", 401);
        }

        try {
            List<RequirementDTO> favoriteRequirements = requirementService.getFavoriteRequirements(username);
            return ApiResponse.success("获取用户关注的需求成功", favoriteRequirements, 200);
        } catch (Exception e) {
            return ApiResponse.error("获取用户关注的需求失败：" + e.getMessage(), 500);
        }
    }

    /**
     * 获取企业需求列表
     */
    @GetMapping("/getMyDemands")
    public ApiResponse getMyDemands() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(username)) {
            return ApiResponse.error("用户未登录", 401);
        }

        try {
            List<RequirementDTO> myDemands = requirementService.getMyDemands(username);
            return ApiResponse.success("获取企业需求列表成功", myDemands, 200);
        } catch (Exception e) {
            return ApiResponse.error("获取企业需求列表失败：" + e.getMessage(), 500);
        }
    }

    /**
     * 添加需求
     */
    @PostMapping("/AddDemands")
    public ApiResponse addDemands(@RequestBody RequirementDTO requirementDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(username)) {
            return ApiResponse.error("用户未登录", 401);
        }

        try {
            requirementService.addDemand(requirementDTO, username);
            return ApiResponse.success("需求添加成功", null, 200);
        } catch (Exception e) {
            return ApiResponse.error("需求添加失败：" + e.getMessage(), 500);
        }
    }

    /**
     * 更新需求
     */
    @PutMapping("/updateDemands/{id}")
    public ApiResponse updateDemands(@PathVariable Long id, @RequestBody RequirementDTO requirementDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(username)) {
            return ApiResponse.error("用户未登录", 401);
        }

        try {
            requirementService.updateDemand(id, requirementDTO, username);
            return ApiResponse.success("需求更新成功", null, 200);
        } catch (Exception e) {
            return ApiResponse.error("需求更新失败：" + e.getMessage(), 500);
        }
    }


    @DeleteMapping("/deleteDemand/{id}")
    public ApiResponse deleteDemand(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(username)) {
            return ApiResponse.error("用户未登录", 401);
        }

        try {
            requirementService.deleteDemand(id, username);
            return ApiResponse.success("需求删除成功", null, 200);
        } catch (Exception e) {
            return ApiResponse.error("需求删除失败：" + e.getMessage(), 500);
        }
    }

    @PostMapping("/inviteCollaborate")
    public ApiResponse inviteCollaborate(@RequestBody Map<String, Object> payload) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(username)) {
            return ApiResponse.error("用户未登录", 401);
        }

        Long demandId;
        String inviteeUsername;
        try {
            demandId = Long.valueOf(payload.get("demandId").toString()); // 获取需求 ID
            inviteeUsername = payload.get("username").toString(); // 被邀请的用户名
        } catch (Exception e) {
            return ApiResponse.error("无效的请求参数：" + e.getMessage(), 400);
        }

        // 防止邀请自己
        if (username.equals(inviteeUsername)) {
            return ApiResponse.error("不能邀请自己协作", 400);
        }

        try {
            boolean success = requirementService.inviteCollaborate(demandId, username, inviteeUsername);
            return success
                    ? ApiResponse.success("协作邀请发送成功", null, 200)
                    : ApiResponse.error("协作邀请发送失败", 500);
        } catch (Exception e) {
            return ApiResponse.error("协作邀请失败：" + e.getMessage(), 500);
        }
    }
}

