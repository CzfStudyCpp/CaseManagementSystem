package com.CloudApp.LoginAndRegister.Mapper;

import com.CloudApp.LoginAndRegister.Entities.PendingUserPassword;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PendingUserPasswordMapper extends BaseMapper<PendingUserPassword> {

    /**
     * 根据待审核用户的 ID 获取其密码信息
     * @param userId 待审核用户的 ID
     * @return 用户密码信息
     */
    @Select("SELECT * FROM pending_user_passwords WHERE pending_user_id = #{userId}")
    PendingUserPassword findByPendingUserId(Long userId);
}
