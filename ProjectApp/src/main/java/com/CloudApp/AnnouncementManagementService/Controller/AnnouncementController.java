package com.CloudApp.AnnouncementManagementService.Controller;


import com.CloudApp.adminService.Response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.CloudApp.AnnouncementManagementService.DTO.AnnouncementDTO;
import com.CloudApp.AnnouncementManagementService.Service.AnnouncementService;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @PostMapping("/addAnnouncement")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'NORMAL_ADMIN')")
    public ApiResponse addAnnouncement(@RequestBody AnnouncementDTO announcementDTO) {
        try {
            boolean isAdded = announcementService.addAnnouncement(announcementDTO);
            if (isAdded) {
                return ApiResponse.success("公告添加成功", null, 200);
            }
            return ApiResponse.error("添加公告失败", 500);
        } catch (Exception e) {
            return ApiResponse.error("添加公告失败: " + e.getMessage(), 500);
        }
    }

    @GetMapping("/getAnnouncements")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'NORMAL_ADMIN')")
    public ApiResponse getAnnouncements(@RequestParam int page, @RequestParam int size) {
        try {
            Map<String, Object> result = announcementService.getAnnouncements(page, size);
            return ApiResponse.success("获取公告列表成功", result, 200);
        } catch (Exception e) {
            return ApiResponse.error("获取公告列表失败: " + e.getMessage(), 500);
        }
    }

    @PutMapping("/updateAnnouncement")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'NORMAL_ADMIN')")
    public ApiResponse updateAnnouncement(@RequestBody AnnouncementDTO announcementDTO) {
        try {
            boolean isUpdated = announcementService.updateAnnouncement(announcementDTO);
            if (isUpdated) {
                return ApiResponse.success("公告更新成功", null, 200);
            }
            return ApiResponse.error("公告更新失败", 500);
        } catch (Exception e) {
            return ApiResponse.error("公告更新失败: " + e.getMessage(), 500);
        }
    }

    @DeleteMapping("/deleteAnnouncement/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'NORMAL_ADMIN')")
    public ApiResponse deleteAnnouncement(@PathVariable Long id) {
        try {
            boolean isDeleted = announcementService.deleteAnnouncement(id);
            if (isDeleted) {
                return ApiResponse.success("公告删除成功", null, 200);
            }
            return ApiResponse.error("公告删除失败", 500);
        } catch (Exception e) {
            return ApiResponse.error("公告删除失败: " + e.getMessage(), 500);
        }
    }
}
