
import { defineStore } from 'pinia';
import axios from "axios";
import router from "@/router";
export const useAdminStore = defineStore('admin', {
    state: () => ({
        adminInfo: {
            username: '', // 管理员用户名
            role: '', // 管理员角色（例如 SUPER_ADMIN 或 NORMAL_ADMIN）
        },
        isAdminLoggedIn: false, // 管理员登录状态
        authToken: localStorage.getItem('authToken') || '', // 从本地存储中获取 token
    }),
    actions: {
        // 登录管理员
        loginAdmin(adminData, token) {
            this.adminInfo = adminData; // 设置管理员信息
            this.isAdminLoggedIn = true; // 设置为已登录
            this.authToken = token; // 存储 JWT Token

            // 将信息存储到 localStorage
            localStorage.setItem('adminInfo', JSON.stringify(adminData));
            localStorage.setItem('isAdminLoggedIn', 'true');
            localStorage.setItem('authToken', token);
        },

        async logoutAdmin() {
            try {
                // 发起后端登出请求
                const response = await axios.post("/api/auth/logout", null, {
                    headers: {
                        Authorization: `Bearer ${this.authToken}`, // 使用管理员的 token
                    },
                });

                if (response.status === 200) {
                    alert("管理员登出成功");
                } else {
                    throw new Error("登出失败：服务器返回错误状态");
                }

                // 清空管理员信息
                this.adminInfo = { username: "", role: "" };
                this.isAdminLoggedIn = false;
                this.authToken = "";

                // 清除 localStorage
                localStorage.removeItem("adminInfo");
                localStorage.removeItem("isAdminLoggedIn");
                localStorage.removeItem("authToken");

                // 跳转到管理员登录页面
                router.push('/admin/login');
            } catch (error) {
                console.error("管理员登出请求失败：", error.response?.data || error.message);
                alert("管理员登出时遇到问题，请稍后重试！");
            }
        },

        // 自动登录管理员（从 localStorage 恢复登录状态）
        autoLoginAdmin() {
            const storedAdminInfo = localStorage.getItem('adminInfo'); // 获取管理员信息
            const storedIsAdminLoggedIn = localStorage.getItem('isAdminLoggedIn') === 'true'; // 检查登录状态
            const storedAuthToken = localStorage.getItem('authToken'); // 获取 token

            if (storedAdminInfo && storedIsAdminLoggedIn && storedAuthToken) {
                this.adminInfo = JSON.parse(storedAdminInfo); // 恢复管理员信息
                this.isAdminLoggedIn = true; // 恢复登录状态
                this.authToken = storedAuthToken; // 恢复 token
            } else {
                // 如果任何信息缺失，执行注销逻辑
                this.logoutAdmin();
            }
        },

        // 检查是否为超级管理员
        isSuperAdmin() {
            return this.adminInfo.role === 'SUPER_ADMIN';
        },
    },
});
