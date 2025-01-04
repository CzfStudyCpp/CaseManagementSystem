package com.CloudApp.SkillManagement.Mapper;
import com.CloudApp.SkillManagement.Entity.UserSkill;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

@Mapper
public interface UserSkillMapper extends BaseMapper<UserSkill> {

    /**
     * 根据用户ID获取用户技能列表
     */
    @Select("SELECT * FROM user_skills WHERE user_id = #{userId}")
    List<UserSkill> getUserSkillsByUserId(@Param("userId") Long userId);

    /**
     * 根据技能ID获取技能详情
     */
    @Select("SELECT * FROM user_skills WHERE id = #{id}")
    UserSkill getUserSkillById(@Param("id") Long id);

    /**
     * 删除用户的所有技能
     */
    @Delete("DELETE FROM user_skills WHERE user_id = #{userId}")
    void deleteUserSkillsByUserId(@Param("userId") Long userId);

    @org.apache.ibatis.annotations.Insert("INSERT INTO user_skills (user_id, skill_id, proficiency) " +
            "VALUES (#{userId}, #{skillId}, #{proficiency})")
    void insertUserSkill(UserSkill userSkill);

    List<UserSkill> selectByUserId(Long userId);
}