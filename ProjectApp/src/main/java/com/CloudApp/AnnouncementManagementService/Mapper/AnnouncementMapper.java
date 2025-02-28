package com.CloudApp.AnnouncementManagementService.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.CloudApp.AnnouncementManagementService.Entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * AnnouncementMapper 用于对公告表进行操作
 */
@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {

    @Select("SELECT * FROM announcements WHERE is_published = true ORDER BY last_updated_date DESC LIMIT 1")
    Announcement selectLatestPublishedAnnouncement();
    // 如果有复杂的自定义查询，可以在这里添加
}
