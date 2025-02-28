package com.CloudApp.LoginAndRegister.Mapper;

import com.CloudApp.LoginAndRegister.Entities.PendingUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PendingUserMapper extends BaseMapper<PendingUser> {

    /**
     * 根据邮箱检查是否已存在
     * @param email 邮箱地址
     * @return 是否已存在
     */
    @Select("SELECT COUNT(*) > 0 FROM pending_users WHERE email = #{email}")
    boolean existsByEmail(String email);

    /**
     * 根据用户名检查是否已存在
     * @param username 用户名
     * @return 是否已存在
     */
    @Select("SELECT COUNT(*) > 0 FROM pending_users WHERE username = #{username}")
    boolean existsByUsername(String username);

    /**
     * 根据用户类型（开发者/企业）获取待审核用户列表
     * @param type 用户类型（DEVELOPER 或 COMPANY）
     * @return 待审核用户列表
     */
    @Select("SELECT * FROM pending_users WHERE user_type = #{type}")
    List<PendingUser> findPendingUsersByType(String type);

    /**
     * 根据用户ID查询用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    @Select("SELECT * FROM pending_users WHERE id = #{id}")
    PendingUser findById(Long id);
}
