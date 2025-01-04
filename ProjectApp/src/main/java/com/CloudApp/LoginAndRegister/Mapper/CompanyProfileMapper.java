package com.CloudApp.LoginAndRegister.Mapper;

import com.CloudApp.LoginAndRegister.Entities.CompanyProfile;
import com.CloudApp.LoginAndRegister.Entities.PendingCompanyProfile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

@Mapper
public interface CompanyProfileMapper extends BaseMapper<CompanyProfile> {

    /**
     * 根据用户 ID 查询企业信息
     *
     * @param userId 用户 ID
     * @return CompanyProfile 对象
     */
    @Select("SELECT * FROM company_profiles WHERE user_id = #{userId}")
    CompanyProfile selectByUserId(@Param("userId") Long userId);

    /**
     * 将待审核企业信息插入到正式表中
     *
     * @param pendingCompanyProfile 待审核企业信息
     */
    @Insert("INSERT INTO company_profiles (user_id, company_name, industry, size, address, contact_person, contact_phone, business_license) " +
            "VALUES (#{pendingCompanyProfile.userId}, #{pendingCompanyProfile.companyName}, #{pendingCompanyProfile.industry}, " +
            "#{pendingCompanyProfile.size}, #{pendingCompanyProfile.address}, #{pendingCompanyProfile.contactPerson}, " +
            "#{pendingCompanyProfile.contactPhone}, #{pendingCompanyProfile.businessLicense})")
    void insertCompany(@Param("pendingCompanyProfile") PendingCompanyProfile pendingCompanyProfile);

    /**
     * 根据用户 ID 删除企业信息
     *
     * @param userId 用户 ID
     */
    @Delete("DELETE FROM company_profiles WHERE user_id = #{userId}")
    void deleteByUserId(@Param("userId") Long userId);

    @Update("UPDATE company_profiles SET " +
            "company_name = #{companyProfile.companyName}, " +
            "industry = #{companyProfile.industry}, " +
            "size = #{companyProfile.size}, " +
            "address = #{companyProfile.address}, " +
            "contact_person = #{companyProfile.contactPerson}, " +
            "contact_phone = #{companyProfile.contactPhone}, " +
            "business_license = #{companyProfile.businessLicense} " +
            "WHERE user_id = #{companyProfile.userId}")
    void updateCompanyProfile(@Param("companyProfile") CompanyProfile companyProfile);
}

