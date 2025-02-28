package com.CloudApp.AnnouncementManagementService.Controller;


import com.CloudApp.AnnouncementManagementService.DTO.AnnouncementDTO;
import com.CloudApp.AnnouncementManagementService.Service.AnnouncementService;
import com.CloudApp.adminService.Response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnnouncementGetter {
    private final AnnouncementService announcementService;

    @Autowired
    public AnnouncementGetter(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping("/getLatestAnnouncement")
    public ApiResponse getLatestAnnouncement() {
        try {
            AnnouncementDTO latestAnnouncement = announcementService.getLatestAnnouncement();
            if (latestAnnouncement != null) {
                return ApiResponse.success("获取最新公告成功", latestAnnouncement, 200);
            }
            return ApiResponse.error("暂无最新公告", 404);
        } catch (Exception e) {
            return ApiResponse.error("获取最新公告失败: " + e.getMessage(), 500);
        }
    }
}
