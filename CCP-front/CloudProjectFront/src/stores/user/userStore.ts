
import { defineStore } from "pinia";
import axios from "axios";
//axiosInstance =axios();
export const useUserStore = defineStore("user", {
    state: () => ({
        // 用户信息
        userInfo: {
            email: "",    // 用户邮箱
            userType: "", // 用户类型
            username: "", // 用户名（新增字段）
            token: "",    // 用户登录的 token
        },
        isLoggedIn: false, // 登录状态
    }),
    getters: {
        // 获取用户邮箱
        userEmail: (state) => state.userInfo.email,
        // 获取用户类型
        userType: (state) => state.userInfo.userType,
        // 获取用户名
        username: (state) => state.userInfo.username,
        userToken:(state)=>state.userInfo.token,
    },
    actions: {
        // 登录
        login(userData) {
            this.userInfo = userData;
            this.isLoggedIn = true;

            // 将登录状态和用户信息保存到 localStorage
            localStorage.setItem("userInfo", JSON.stringify(userData));
            localStorage.setItem("isLoggedIn", "true");
        },

        // 登出
        async logout() {
            try {
                // 发起后端登出请求
                await axios.post("/api/auth/logout", null, {
                    headers: {
                        Authorization: `Bearer ${this.userInfo.token}`, // 使用用户的 token
                    },
                });
                console.log("登出请求成功");
            } catch (error) {
                console.error("登出请求失败：", error.response?.data || error.message);
                alert("登出时遇到问题，请稍后重试！");
            }

            // 清除用户信息
            this.userInfo = { email: "", token: "", userType: "", username: "" };
            this.isLoggedIn = false;

            // 清除 localStorage
            localStorage.removeItem("userInfo");
            localStorage.removeItem("isLoggedIn");
        },

        // 自动登录（从 localStorage 恢复登录状态）
        autoLogin() {
            const storedUserInfo = localStorage.getItem("userInfo");
            const storedIsLoggedIn = localStorage.getItem("isLoggedIn") === "true";

            if (storedUserInfo && storedIsLoggedIn) {
                this.userInfo = JSON.parse(storedUserInfo);
                this.isLoggedIn = true;
            }
        },
    },
});
