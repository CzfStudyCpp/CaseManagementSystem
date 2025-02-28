package com.CloudApp.AnnouncementManagementService.Service;

import com.CloudApp.AnnouncementManagementService.DTO.AnnouncementDTO;

import java.util.Map;

public interface AnnouncementService {
    
     boolean addAnnouncement(AnnouncementDTO announcementDTO);

    Map<String, Object> getAnnouncements(int page, int size);

    boolean updateAnnouncement(AnnouncementDTO announcementDTO);

    boolean deleteAnnouncement(Long id);

    AnnouncementDTO getLatestAnnouncement();
}
