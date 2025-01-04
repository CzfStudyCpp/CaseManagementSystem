package com.CloudApp.LoginAndRegister.Entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("pending_user_passwords")  // 映射到数据库中的 pending_user_passwords 表
public class PendingUserPassword {

    @TableId(value = "id", type = IdType.AUTO)  // 自增主键配置
    private Long id;

    // 这里的 setPendingUserId 方法是为了设置 pendingUserId 字段的值，
    // 不再使用 @ManyToOne 和 @JoinColumn 进行关系映射，因为我们不再使用 JPA
    @Setter
    @TableField("pending_user_id")  // 映射到数据库中的 pending_user_id 字段
    private Long pendingUserId;  // 临时用户 ID

    @Setter
    @TableField("password_hash")  // 映射到数据库中的 password_hash 字段
    private String passwordHash;  // 密码哈希

    @Override
    public String toString() {
        return "PendingUserPassword{" +
                "id=" + id +
                ", pendingUserId=" + pendingUserId +
                ", passwordHash='" + passwordHash + '\'' +
                '}';
    }
}
