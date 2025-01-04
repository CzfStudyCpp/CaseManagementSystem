package com.CloudApp.AnnouncementManagementService.Implement;

import com.CloudApp.AnnouncementManagementService.DTO.AnnouncementDTO;
import com.CloudApp.AnnouncementManagementService.Entity.Announcement;
import com.CloudApp.AnnouncementManagementService.Mapper.AnnouncementMapper;
import com.CloudApp.AnnouncementManagementService.Service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public boolean addAnnouncement(AnnouncementDTO announcementDTO) {
        Announcement announcement = new Announcement();
        BeanUtils.copyProperties(announcementDTO, announcement);
        announcement.setCreatedDate(LocalDateTime.now());
        announcement.setLastUpdatedDate(LocalDateTime.now());
        return announcementMapper.insert(announcement) > 0;
    }

    @Override
    public Map<String, Object> getAnnouncements(int page, int size) {
        // 创建分页请求
        Page<Announcement> pageRequest = new Page<>(page, size);
        // 查询分页数据
        IPage<Announcement> announcementPage = announcementMapper.selectPage(pageRequest, null);

        // 将查询结果转换为 DTO 列表
        List<AnnouncementDTO> announcementDTOs = announcementPage.getRecords().stream()
                .map(announcement -> new AnnouncementDTO(
                        announcement.getId(),
                        announcement.getTitle(),
                        announcement.getContent(), // 包含公告内容
                        announcement.getIsPublished(),
                        announcement.getLastUpdatedDate()
                ))
                .collect(Collectors.toList());

        // 构造返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("announcements", announcementDTOs); // 返回 DTO 列表
        result.put("currentPage", announcementPage.getCurrent()); // 当前页码
        result.put("totalPages", announcementPage.getPages()); // 总页数
        result.put("totalAnnouncements", announcementPage.getTotal()); // 总记录数
        return result;
    }


    @Override
    public boolean updateAnnouncement(AnnouncementDTO announcementDTO) {
        Announcement announcement = announcementMapper.selectById(announcementDTO.getId());
        if (announcement != null) {
            BeanUtils.copyProperties(announcementDTO, announcement);
            announcement.setLastUpdatedDate(LocalDateTime.now());
            return announcementMapper.updateById(announcement) > 0;
        }
        return false;
    }

    @Override
    public boolean deleteAnnouncement(Long id) {
        return announcementMapper.deleteById(id) > 0;
    }

    @Override
    public AnnouncementDTO getLatestAnnouncement() {
        // 查询最新的一条已发布的公告
        Announcement latestAnnouncement = announcementMapper.selectLatestPublishedAnnouncement();
        if (latestAnnouncement != null) {
            // 将实体对象转换为 DTO
            AnnouncementDTO latestAnnouncementDTO = new AnnouncementDTO();
            BeanUtils.copyProperties(latestAnnouncement, latestAnnouncementDTO);
            return latestAnnouncementDTO;
        }
        return null; // 如果没有最新公告，则返回 null
    }
}