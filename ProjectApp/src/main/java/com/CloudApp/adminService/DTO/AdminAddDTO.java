package com.CloudApp.adminService.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminAddDTO {

    private String username; // 用户名
    private String email;    // 邮箱
    private String password;
    private String role;     // 管理员级别
    private String status;   // 管理员状态
}
