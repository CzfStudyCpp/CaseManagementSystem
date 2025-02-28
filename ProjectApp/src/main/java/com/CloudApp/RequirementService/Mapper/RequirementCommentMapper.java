package com.CloudApp.RequirementService.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.CloudApp.RequirementService.Entity.RequirementComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Mapper 接口：需求评论表
 */
@Mapper
public interface RequirementCommentMapper extends BaseMapper<RequirementComment> {
    // 分页获取评论列表
    @Select("""
            SELECT *
            FROM requirement_comments
            WHERE requirement_id = #{requirementId}
            ORDER BY created_at DESC
            LIMIT #{offset}, #{limit}
            """)
    List<RequirementComment> getCommentsByRequirementIdWithPagination(
            @Param("requirementId") Long requirementId,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    // 获取评论总数
    @Select("SELECT COUNT(*) FROM requirement_comments WHERE requirement_id = #{requirementId}")
    int countCommentsByRequirementId(@Param("requirementId") Long requirementId);
}
