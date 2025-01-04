package com.CloudApp.LoginAndRegister.Entities;

import com.baomidou.mybatisplus.annotation.*;
import com.CloudApp.LoginAndRegister.Enums.CompanySize;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("pending_company_profiles")  // 映射到数据库中的 pending_company_profiles 表
public class PendingCompanyProfile {

    @TableId(value = "id", type = IdType.AUTO)  // MyBatis-Plus 自增主键配置
    private Long id;

    @TableField("user_id")  // 映射到数据库中的 user_id 字段
    private Long userId;

    @TableField("company_name")  // 映射到数据库中的 company_name 字段
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String companyName;

    @TableField("industry")  // 映射到数据库中的 industry 字段
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String industry;

    @TableField("size")  // 映射到数据库中的 size 字段
    @JsonFormat(shape = JsonFormat.Shape.STRING)  // 枚举序列化为字符串
    private CompanySize size;

    @TableField("address")  // 映射到数据库中的 address 字段
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String address;

    @TableField("contact_person")  // 映射到数据库中的 contact_person 字段
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String contactPerson;

    @TableField("contact_phone")  // 映射到数据库中的 contact_phone 字段
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String contactPhone;

    @TableField("business_license")  // 映射到数据库中的 business_license 字段
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String businessLicense;

    @TableField("created_at")  // 映射到数据库中的 created_at 字段
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt = LocalDateTime.now();

    @TableField("updated_at")  // 映射到数据库中的 updated_at 字段
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public void setPendingUser(PendingUser pendingUser) {
        this.userId = pendingUser.getId();
    }
    @Override
    public String toString() {
        return "PendingCompanyProfile{" +
                "id=" + id +
                ", userId=" + userId +
                ", companyName='" + companyName + '\'' +
                ", industry='" + industry + '\'' +
                ", size=" + size +
                ", address='" + address + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", businessLicense='" + businessLicense + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
