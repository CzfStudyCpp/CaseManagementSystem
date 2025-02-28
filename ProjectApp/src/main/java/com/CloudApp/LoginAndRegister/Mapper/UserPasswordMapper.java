package com.CloudApp.LoginAndRegister.Mapper;

import com.CloudApp.LoginAndRegister.Entities.PendingUserPassword;
import com.CloudApp.LoginAndRegister.Entities.UserPassword;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserPasswordMapper extends BaseMapper<UserPassword> {

    // 通过 userId 查找对应的用户密码
    @Select("SELECT * FROM user_passwords WHERE user_id = #{userId}")
    UserPassword selectByUserId(@Param("userId") Long userId);

    // 插入密码到正式用户密码表
    @Insert("INSERT INTO user_passwords (user_id, password_hash) " +
            "VALUES (#{pendingUserPassword.pendingUserId}, #{pendingUserPassword.passwordHash})")
    void insertPassword(@Param("pendingUserPassword") PendingUserPassword pendingUserPassword);

    @Insert("INSERT INTO user_passwords (user_id, password_hash) "+
            "VALUES (#{userPassword.userId},#{userPassword.passwordHash})")
    void insertChangedPassword(@Param("userPassword") UserPassword userPassword);


    // 更新用户密码
    @Select("UPDATE user_passwords SET password_hash = #{userPassword.passwordHash} WHERE user_id = #{userPassword.userId}")
    void updatePassword(@Param("userPassword") UserPassword userPassword);
}

