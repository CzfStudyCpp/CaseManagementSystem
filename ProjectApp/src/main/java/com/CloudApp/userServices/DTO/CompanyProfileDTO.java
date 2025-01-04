package com.CloudApp.userServices.DTO;

import com.CloudApp.LoginAndRegister.Enums.CompanySize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyProfileDTO {
    private Long    userId;
    private String companyName;    // 公司名称
    private String industry;       // 行业
    private String size;           // 公司规模
    private String address;        // 地址
    private String contactPerson;  // 联系人
    private String contactPhone;   // 联系电话



//    public CompanyProfileDTO(Long userId, String companyName, String industry, CompanySize size, String address, String contactPerson, String contactPhone) {
//        this.userId = userId;
//        this.companyName = companyName;
//        this.industry = industry;
//        this.size = size.toString();
//        this.address = address;
//        this.contactPerson = contactPerson;
//        this.contactPhone = contactPhone;
//    }
}
