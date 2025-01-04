package com.CloudApp.RequirementService.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.CloudApp.RequirementService.Entity.RequirementSkill;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Mapper 接口：需求技能关联表
 */
@Mapper
public interface RequirementSkillMapper extends BaseMapper<RequirementSkill> {


    /**
     * 查询单个需求的技能 ID 列表
     *
     * @param requirementId 需求 ID
     * @return 技能 ID 列表
     */
    @Select("SELECT skill_id FROM requirement_skills WHERE requirement_id = #{requirementId}")
    List<Long> selectSkillIdsByRequirementId(@Param("requirementId") Long requirementId);

    @Select("SELECT s.name FROM requirement_skills rs " +
            "JOIN skills s ON rs.skill_id = s.id " +
            "WHERE rs.requirement_id = #{requirementId}")
    List<String> selectSkillNamesByRequirementId(@Param("requirementId") Long requirementId);


    @Insert("INSERT INTO requirement_skills (requirement_id, skill_id) VALUES (#{id}, #{skillId})")
    void insertRequirementSkill(@Param("id") Long id, @Param("skillId") Long skillId);

    @Delete("DELETE FROM requirement_skills WHERE requirement_id = #{id}")
    void deleteSkillsByRequirementId(@Param("id") Long id);
    @Select("SELECT requirement_id FROM requirement_skills WHERE skill_id = #{skillId}")
    List<Long> getRequirementIdsBySkillId(@Param("skillId") Long skillId);


}
