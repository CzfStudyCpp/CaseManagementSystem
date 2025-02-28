package com.CloudApp.RequirementService.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.CloudApp.RequirementService.Entity.Requirement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Mapper 接口：需求表
 */
@Mapper
public interface RequirementMapper extends BaseMapper<Requirement> {




    @Select("SELECT * FROM requirements WHERE status IN ('PUBLISHED', 'COMPLETED')")
    List<Requirement> selectAllPublishedRequirements();
    @Select("SELECT DISTINCT r.* FROM requirements r " +
            "JOIN requirement_skills rs ON r.id = rs.requirement_id " +
            "WHERE rs.skill_id = #{skillId} AND r.status IN ('PUBLISHED', 'COMPLETED')")
    List<Requirement> selectBySkillId(@Param("skillId") Long skillId);


    @Select("SELECT * FROM requirements WHERE user_id = #{userId}")
    List<Requirement> selectByUserId(@Param("userId") Long userId);

    default void updateRequirement(Requirement requirement) {
        this.updateById(requirement); // MyBatis-Plus 提供的方法
    }

}
