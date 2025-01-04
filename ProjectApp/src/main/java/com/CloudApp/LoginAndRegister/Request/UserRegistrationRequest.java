//package com.CloudApp.LoginAndRegister.Request;
//
//import lombok.Getter;
//
//public class UserRegistrationRequest {
//    @Getter
//    private String username;
//    @Getter
//    private String email;
//    private String phone;
//    private String password;
//    private String userType;  // 'developer' 或 'company'
//
//    void setUsername(String username) {
//        this.username = username;
//    }
//    void setEmail(String email) {
//        this.email = email;
//    }
//    void setPhone(String phone) {
//        this.phone = phone;
//    }
//    void setPassword(String password) {
//        this.password = password;
//    }
//    void setUserType(String userType) {
//        this.userType = userType;
//    }
//
//    public String getPhone() {
//        return phone;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//    public String getUserType() {
//        return userType;
//    }
//}


package com.CloudApp.LoginAndRegister.Request;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.*;


@Getter
@Setter
public class UserRegistrationRequest {
    // 通用字段
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3到20个字符之间")
    private String username;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "电话号码格式不正确")
    private String phone;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度至少为6位")
    private String password;

    @NotBlank(message = "用户类型不能为空")
    @Pattern(regexp = "developer|company", message = "用户类型必须为 'developer' 或 'company'")
    private String userType; // 用户类型: 'developer' 或 'company'

    // 嵌套的字段
    // 嵌套的字段
    private DeveloperInfo developerInfo = new DeveloperInfo(); // 初始化
    private CompanyInfo companyInfo = new CompanyInfo();       // 初始化


    // 开发者相关字段子对象
    @Getter
    @Setter
    public static class DeveloperInfo {
        @NotBlank(message = "开发者真实姓名不能为空")
        private String realName;

        private String github;  // GitHub 链接（可选）
        private String portfolio; // 作品集链接（可选）
        private String experience; // 项目经验（可选）
    }

    // 企业相关字段子对象
    @Getter
    @Setter
    public static class CompanyInfo {
        @NotBlank(message = "企业名称不能为空")
        private String companyName;

        private String industry; // 行业（可选）

        @Pattern(regexp = "small|medium|large", message = "企业规模必须为 'small', 'medium', 或 'large'")
        private String companySize;

        private String address; // 地址（可选）
        private String contactPerson; // 联系人姓名（可选）
        private String contactPhone; // 联系人电话（可选）

        private String businessLicense; // 营业执照路径（可选）
    }
}

