// import axios from 'axios';
//
// const axiosInstance = axios.create({
//     timeout: 10000, // 设置请求超时
// });
//
// // 请求拦截器
// axiosInstance.interceptors.request.use(
//     (config) => {
//         //添加 CSRF token 到请求头
//         // const csrfToken = getCsrfToken();
//         // if (csrfToken) {
//         //     config.headers['X-XSRF-TOKEN'] = csrfToken; // 将 CSRF token 添加到请求头
//         // }
//
//         // 添加 JWT Token
//         const token = localStorage.getItem('authToken');
//         if (token) {
//             config.headers['Authorization'] = `Bearer ${token}`; // 将 JWT token 加入请求头
//         }
//
//         return config;
//     },
//     (error) => {
//         return Promise.reject(error);
//     }
// );
//
// // 响应拦截器
// axiosInstance.interceptors.response.use(
//     (response) => {
//         return response; // 正常响应，返回数据
//     },
//     (error) => {
//         const { response } = error;
//         if (response) {
//             // 处理不同的错误
//             if (response.status === 401) {
//                 console.error('Token expired or unauthorized');
//                 localStorage.removeItem('authToken'); // 清除 authToken
//                 // 跳转到登录页
//                 // 调用回调函数进行路由跳转
//                 // if (redirectToLogin) {
//                 //     redirectToLogin();
//                 // }
//             } else if (response.status === 400) {
//                 console.error('Bad request: ', response.data);
//             } else if (response.status === 500) {
//                 console.error('Server error: ', response.data);
//             } else {
//                 console.error('Unexpected error: ', response.data);
//             }
//         } else {
//             console.error('Network error');
//         }
//         return Promise.reject(error);
//     }
// );
//
// export default axiosInstance;

import axios from "axios";
import { useAdminStore } from "@/stores/admin/adminStore.ts"; // 引入管理员 Pinia Store
import { useUserStore } from "@/stores/user/userStore.ts"; // 引入用户 Pinia Store

// 创建 axios 实例
const axiosInstance = axios.create({
    timeout: 10000, // 请求超时时间
});

// 请求拦截器
axiosInstance.interceptors.request.use(
    (config) => {
        // 根据当前的业务需求动态设置 token
        const adminStore = useAdminStore(); // 获取管理员 Store
        const userStore = useUserStore(); // 获取用户 Store

        let token = null;

        // 如果管理员已登录，使用管理员 Token
        if (adminStore.isAdminLoggedIn) {
            token = adminStore.authToken;
        }
        // 如果用户已登录，使用用户 Token
        else if (userStore.isLoggedIn) {
            token = userStore.userToken;
        }


        // 如果存在 token，则设置 Authorization 头
        if (token) {
            config.headers["Authorization"] = `Bearer ${token}`;
            console.log("Attached Token:", token);
        }
        else {
            console.warn("No token found, request sent without Authorization header");
        }

        return config;
    },
    (error) => {
        // 请求发送失败
        return Promise.reject(error);
    }
);

// 响应拦截器
axiosInstance.interceptors.response.use(
    (response) => {
        // 响应正常，直接返回
        return response;
    },
    (error) => {
        const { response } = error;

        if (response) {
            const adminStore = useAdminStore();
            const userStore = useUserStore();

            // 根据 HTTP 状态码进行处理
            if (response.status === 401) {
                console.error("401 Unauthorized - Token 已过期或无效");

                // 根据登录身份（管理员或普通用户）执行登出操作
                if (adminStore.isAdminLoggedIn) {
                    adminStore.logoutAdmin();
                } else if (userStore.isLoggedIn) {
                    userStore.logout();
                }

                // 跳转到登录页面
                alert("登录状态已过期，请重新登录！");
                window.location.href = "/login";
            } else if (response.status === 400) {
                console.error("400 Bad Request: ", response.data);
                alert(`请求错误: ${response.data.message || "未知错误"}`);
            } else if (response.status === 500) {
                console.error("500 Internal Server Error: ", response.data);
                alert("服务器错误，请稍后重试！");
            } else {
                console.error("Unexpected error: ", response.data);
            }
        } else {
            console.error("Network Error");
            alert("网络错误，请检查你的网络连接！");
        }

        return Promise.reject(error);
    }
);

export default axiosInstance;
