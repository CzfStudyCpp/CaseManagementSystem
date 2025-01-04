package com.CloudApp.LoginAndRegister.Entities;

import com.CloudApp.LoginAndRegister.Enums.CompanySize;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("company_profiles")  // 对应数据库中的 company_profiles 表
public class CompanyProfile {

    @TableId(value = "id", type = IdType.AUTO)  // 使用 MyBatis-Plus 自动生成主键
    private Long id;  // 公司ID

    // 设置 userId
    @Setter
    @TableField("user_id")  // 映射到数据库中的 user_id 字段
    private Long userId;  // 用户ID, 用于与 User 表关联

    @TableField("company_name")  // 映射到数据库中的 company_name 字段
    private String companyName;  // 公司名称

    @TableField("industry")  // 映射到数据库中的 industry 字段
    private String industry;  // 行业

    @TableField("size")  // 映射到数据库中的 size 字段
    private CompanySize size;  // 公司规模

    @TableField("address")  // 映射到数据库中的 address 字段
    private String address;  // 地址

    @TableField("contact_person")  // 映射到数据库中的 contact_person 字段
    private String contactPerson;  // 联系人

    @TableField("contact_phone")  // 映射到数据库中的 contact_phone 字段
    private String contactPhone;  // 联系电话

    @TableField("business_license")  // 映射到数据库中的 business_license 字段
    private String businessLicense;  // 营业执照

    @TableField("created_at")  // 映射到数据库中的 created_at 字段
    private LocalDateTime createdAt;  // 创建时间

    @TableField("updated_at")  // 映射到数据库中的 updated_at 字段
    private LocalDateTime updatedAt;  // 更新时间

    // 在 MyBatis-Plus 中，通常不使用 JPA 的 @PrePersist 和 @PreUpdate
    // 如果需要自动更新 createdAt 和 updatedAt，可以在 Service 层处理

}
