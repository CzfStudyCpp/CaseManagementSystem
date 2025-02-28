package com.CloudApp.LoginAndRegister.Mapper;

import com.CloudApp.LoginAndRegister.Entities.Admin;
import com.CloudApp.LoginAndRegister.Entities.PendingDeveloperProfile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

    // 根据用户名查找管理员
    @Select("SELECT * FROM admin_account WHERE username = #{username}")
    Admin selectByUsername(String username);

    // 根据邮箱查找管理员
    @Select("SELECT * FROM admin_account WHERE email = #{email}")
    Admin selectByEmail(String email);
}
