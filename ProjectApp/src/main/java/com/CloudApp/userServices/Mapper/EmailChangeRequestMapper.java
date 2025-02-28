package com.CloudApp.userServices.Mapper;
import com.CloudApp.userServices.Entities.EmailChangeRequest;
import org.apache.ibatis.annotations.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
@Mapper
public interface EmailChangeRequestMapper extends BaseMapper<EmailChangeRequest> {

    @Insert("INSERT INTO email_change_requests (user_id, username, current_email, new_email, status) " +
            "VALUES (#{userId}, #{username}, #{currentEmail}, #{newEmail}, #{status})")
    void insertRequest(EmailChangeRequest request);

    @Select("SELECT * FROM email_change_requests WHERE id = #{id}")
    EmailChangeRequest selectById(Long id);

    /**
     * 根据用户名和邮箱删除邮箱变更请求记录
     *
     * @param username 用户名
     * @param currentEmail 当前邮箱
     * @param newEmail 申请的新邮箱
     * @return 删除的行数
     */
    @Delete("DELETE FROM email_change_requests " +
            "WHERE username = #{username} AND current_email = #{currentEmail} AND new_email = #{newEmail}")
    int deleteRequestByUsernameAndEmails(@Param("username") String username,
                                         @Param("currentEmail") String currentEmail,
                                         @Param("newEmail") String newEmail);

}
