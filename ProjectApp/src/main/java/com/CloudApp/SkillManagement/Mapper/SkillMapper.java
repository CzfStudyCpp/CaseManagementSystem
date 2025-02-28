package com.CloudApp.SkillManagement.Mapper;

import com.CloudApp.SkillManagement.Entity.Skill;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;
@Mapper
public interface SkillMapper extends BaseMapper<Skill> {
    /**
     * 根据技能名称查询技能
     */
    @Select("SELECT * FROM skills WHERE name = #{name}")
    Skill findByName(@Param("name") String name);

    /**
     * 检查技能名称是否已存在
     */
    @Select("SELECT COUNT(*) > 0 FROM skills WHERE name = #{name}")
    boolean existsByName(@Param("name") String name);

    /**
     * 获取所有技能标签
     */
    @Select("SELECT * FROM skills")
    List<Skill> findAllSkills();

    @Select("SELECT * FROM skills WHERE id = #{skillId}")
    Skill getSkillById(@Param("skillId") Long skillId);


    @Select("SELECT * FROM skills")
    List<Skill> getAllSkills();

    @Select("SELECT id FROM skills WHERE name = #{name}")
    Long getSkillIdByName(@Param("name")String skillName);
}

