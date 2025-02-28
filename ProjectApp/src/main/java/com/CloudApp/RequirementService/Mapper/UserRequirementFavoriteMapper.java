package com.CloudApp.RequirementService.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.CloudApp.RequirementService.Entity.UserRequirementFavorite;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Mapper 接口：用户需求关注表
 */
@Mapper
public interface UserRequirementFavoriteMapper extends BaseMapper<UserRequirementFavorite> {
    @Select("SELECT requirement_id FROM user_requirement_favorites WHERE user_id = #{userId}")
    List<Long> selectFavoriteRequirementIds(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM user_requirement_favorites WHERE requirement_id = #{requirementId}")
    int countFavoritesByRequirementId(@Param("requirementId") Long requirementId);

    @Select("SELECT COUNT(*) FROM user_requirement_favorites WHERE user_id = #{userId} AND requirement_id = #{requirementId}")
    int countByUserIdAndRequirementId(@Param("userId") Long userId, @Param("requirementId") Long requirementId);

    // 插入关注记录
    @Insert("INSERT INTO user_requirement_favorites (user_id, requirement_id, created_at) VALUES (#{userId}, #{requirementId}, NOW())")
    int insertFavorite(@Param("userId") Long userId, @Param("requirementId") Long requirementId);

    // 删除关注记录
    @Delete("DELETE FROM user_requirement_favorites WHERE user_id = #{userId} AND requirement_id = #{requirementId}")
    int deleteFavorite(@Param("userId") Long userId, @Param("requirementId") Long requirementId);

}