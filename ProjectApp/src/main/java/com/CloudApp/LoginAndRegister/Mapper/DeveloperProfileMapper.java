package com.CloudApp.LoginAndRegister.Mapper;

import com.CloudApp.LoginAndRegister.Entities.DeveloperProfile;
import com.CloudApp.LoginAndRegister.Entities.PendingDeveloperProfile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

@Mapper
public interface DeveloperProfileMapper extends BaseMapper<DeveloperProfile> {

    // 通过 userId 查找开发者信息
    @Select("SELECT * FROM developer_profiles WHERE user_id = #{userId}")
    DeveloperProfile selectByUserId(@Param("userId") Long userId);

    // 插入开发者信息
    @Insert("INSERT INTO developer_profiles (user_id, real_name, github, portfolio, experience) " +
            "VALUES (#{pending.userId}, #{pending.realName}, #{pending.github}, #{pending.portfolio}, #{pending.experience})")
    void insertDeveloper(@Param("pending") PendingDeveloperProfile pendingDeveloperProfile);

    // 更新开发者信息
    @Update("UPDATE developer_profiles SET " +
            "real_name = #{developerProfile.realName}, " +
            "github = #{developerProfile.github}, " +
            "portfolio = #{developerProfile.portfolio}, " +
            "experience = #{developerProfile.experience} " +
            "WHERE user_id = #{developerProfile.userId}")
    void updateDeveloperProfile(@Param("developerProfile") DeveloperProfile developerProfile);
}
