package com.CloudApp.LoginAndRegister.Mapper;

import com.CloudApp.LoginAndRegister.Entities.PendingUser;
import com.CloudApp.LoginAndRegister.Entities.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 检查邮箱是否已注册
     *
     * @param email 用户邮箱
     * @return 如果已存在则返回 true，否则返回 false
     */
    @Select("SELECT COUNT(*) > 0 FROM users WHERE email = #{email}")
    boolean existsByEmail(@Param("email") String email);

    /**
     * 根据邮箱查询用户
     *
     * @param email 用户邮箱
     * @return 查询到的用户信息
     */
    @Select("SELECT * FROM users WHERE email = #{email} LIMIT 1")
    User selectByEmail(@Param("email") String email);

    /**
     * 插入开发者账户信息到正式表
     *
     * @param pendingUser 待审核用户信息
     */
    @Insert("INSERT INTO users (username, email, phone, user_type, created_at, updated_at) " +
            "VALUES (#{username}, #{email}, #{phone}, 'DEVELOPER', NOW(), NOW())")
    void insertDeveloper(PendingUser pendingUser);

    /**
     * 插入企业账户信息到正式表
     *
     * @param pendingUser 待审核用户信息
     */
    @Insert("INSERT INTO users (username, email, phone, user_type, created_at, updated_at) " +
            "VALUES (#{username}, #{email}, #{phone}, 'COMPANY', NOW(), NOW())")
    void insertCompany(PendingUser pendingUser);

    /**
     * 根据邮箱找ID
     *
     * @param email 用户的邮箱
     * @return 用户 ID
     */
    @Select("SELECT id FROM users WHERE email = #{email} LIMIT 1")
    Long findByEmail(@Param("email") String email);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 如果已存在则返回 true，否则返回 false
     */
    @Select("SELECT COUNT(*) > 0 FROM users WHERE username = #{username}")
    boolean existsByUsername(@Param("username") String username);

    /**
     * 根据用户 ID 查询用户
     *
     * @param userId 用户 ID
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE id = #{userId}")
    User getUserById(@Param("userId") Long userId);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE username = #{username} LIMIT 1")
    User getUserByUsername(@Param("username") String username);

    /**
     * 更新用户信息
     *
     * @param user 用户对象
     */
    @Update("UPDATE users SET " +
            "username = #{username}, " +
            "email = #{email}, " +
            "phone = #{phone}, " +
            "user_type = #{userType}, " +
            "status = #{status}, " +
            "updated_at = NOW() " +
            "WHERE id = #{id}")
    void updateUser(User user);

    @Select("SELECT id, username, email, phone, user_type, status " +
            "FROM users " +
            "ORDER BY created_at DESC " +
            "LIMIT #{size} OFFSET #{offset}")
    List<User> getUsersPaginated(@Param("offset") int offset, @Param("size") int size);
    @Select("SELECT COUNT(*) FROM users")
    int getUserCount();


    /**
     * 更新用户邮箱
     *
     * @param username 用户名
     * @param currentEmail 当前邮箱
     * @param newEmail 新邮箱
     * @return 更新的行数
     */
    @Update("UPDATE users " +
            "SET email = #{newEmail} " +
            "WHERE username = #{username} AND email = #{currentEmail}")
    int updateEmailByUsername(@Param("username") String username,
                              @Param("currentEmail") String currentEmail,
                              @Param("newEmail") String newEmail);

    @Select("SELECT * FROM users WHERE status = 'SUSPENDED'")
    IPage<User> getBannedUsers(Page<User> requestPage);

}
