package com.CloudApp.LoginAndRegister.Mapper;

import com.CloudApp.LoginAndRegister.Entities.PendingDeveloperProfile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PendingDeveloperProfileMapper extends BaseMapper<PendingDeveloperProfile> {

    /**
     * 根据用户ID查询待审核开发者信息
     *
     * @param userId 用户ID
     * @return 待审核开发者信息
     */
    @Select("SELECT * FROM pending_developer_profiles WHERE user_id = #{userId}")
    PendingDeveloperProfile selectByUserId(Long userId);

    /**
     * 根据用户ID删除待审核开发者信息
     *
     * @param userId 用户ID
     */
    @Delete("DELETE FROM pending_developer_profiles WHERE user_id = #{userId}")
    void deleteByUserId(Long userId);
}

