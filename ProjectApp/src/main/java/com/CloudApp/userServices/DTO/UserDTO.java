package com.CloudApp.userServices.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;            // 用户ID
    private String username;    // 用户名
    private String email;       // 用户邮箱
    private String phone;       // 用户电话
    private String userType;    // 用户类型（developer 或 company）
    private String status;      // 用户状态（active, inactive, suspended）
}