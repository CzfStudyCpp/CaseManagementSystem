//
// //用户界面
// import { createRouter, createWebHistory } from 'vue-router';
// import HomePage from '@/views/Pages/HomePage/HomePage.vue';
// import DemandPage from '@/views/Pages/DemandPage/DemandPage.vue';
// import CasePage from '@/views/Pages/CasePage/CasePage.vue';
// import HelpPage from '@/views/Pages/HelpPage/HelpPage.vue';
// import UserProfile from '@/views/Pages/UserPage/UserProfile.vue';
// import LoginPage from '@/views/Pages/LoginPage/LoginPage.vue';
//
// // 用户中心子组件
// import UserInfoDisplay from '@/views/Pages/UserPage/UserInfoDisplay.vue'; // 个人信息展示
// import UserInfoEdit from '@/views/Pages/UserPage/UserInfoEdit.vue'; // 修改个人信息
// import ChangePassword from '@/views/Pages/UserPage/ChangePassword.vue'; // 修改密码
// import ChangeEmail from '@/views/Pages/UserPage/ChangeEmail.vue'; // 修改注册邮箱
// import ViewOtherUsers from '@/views/Pages/UserPage/ViewUsers.vue'; // 查看其他用户
//
// // 管理员页面
// import AdminLogin from '@/views/admin/login/AdminLogin.vue';
// import AdminDashboard from '@/views/admin/AdminDashboard.vue';
// import UserManagement from '@/views/admin/UserManagement/UserManage.vue';
// import NoticeManagement from '@/views/admin/NoticeManagement/NoticeManage.vue';
// import CaseManagement from '@/views/admin/CaseManagement/CaseManage.vue';
// import SkillTagManagement from "@/views/admin/SkillTagManagement/SkillTagManagement.vue";
//
// //管理员用户管理模块
// import ViewUsers from "@/views/admin/UserManagement/ViewUsers.vue";
// import EditUser from "@/views/admin/UserManagement/EditUser.vue";
// import AddUser from "@/views/admin/UserManagement/AddUser.vue";
// import DeleteUser from "@/views/admin/UserManagement/DeleteUser.vue";
//
// //注册界面
// // 注册相关页面
// import RegisterChoose from '@/views/Pages/RegisterPage/HomeRegister.vue';  // 选择账户类型页面
// import DeveloperRegister from '@/views/Pages/RegisterPage/DeveloperRegister.vue';  // 开发者注册页面
// import CompanyRegister from '@/views/Pages/RegisterPage/CompanyRegister.vue';  // 企业注册页面
// import PendingPage from '@/views/Pages/RegisterPage/Pending.vue';  // 待审核页面
//
// const routes = [
//     { path: '/', name: 'Home', component: HomePage },
//     { path: '/demand', name: 'Demand', component: DemandPage },
//     { path: '/case', name: 'Case', component: CasePage },
//     { path: '/help', name: 'Help', component: HelpPage },
//     // 用户中心路由
//     {
//         path: '/user',
//         component: UserProfile,
//         children: [
//             // {
//             //     path: '',
//             //     redirect: 'profile', // 默认跳转到个人信息展示
//             // },
//             {
//                 path: 'profile',
//                 name: 'UserProfileDisplay',
//                 component: UserInfoDisplay, // 个人信息展示组件
//             },
//             {
//                 path: 'edit-profile',
//                 name: 'UserProfileEdit',
//                 component: UserInfoEdit, // 修改个人信息组件
//             },
//             {
//                 path: 'change-password',
//                 name: 'ChangePassword',
//                 component: ChangePassword, // 修改密码
//             },
//             {
//                 path: 'change-email',
//                 name: 'ChangeEmail',
//                 component: ChangeEmail, // 修改注册邮箱
//             },
//             {
//                 path: 'view-users',
//                 name: 'ViewOtherUsers',
//                 component: ViewOtherUsers, // 查看其他用户
//             },
//         ],
//     },
//     { path: '/login', name: 'LoginPage', component: LoginPage},
//
//     // 注册相关页面
//     { path: '/register', name: 'RegisterChoose', component: RegisterChoose }, // 选择账户类型页面
//     { path: '/register/developer', name: 'DeveloperRegister', component: DeveloperRegister }, // 开发者注册页面
//     { path: '/register/company', name: 'CompanyRegister', component: CompanyRegister }, // 企业注册页面
//     { path: '/pending', name: 'PendingPage', component: PendingPage }, // 待审核页面
//
//
//     // 管理员登录页面
//     {
//         path: '/admin/login',
//         name: 'AdminLogin',
//         component: AdminLogin,
//         meta: { isAdminPage: true },
//     },
//
//     // 管理员后台页面
//     {
//         path: '/admin',
//         component: AdminDashboard,
//         meta: { requiresAdmin: true, isAdminPage: true },// 标记需要管理员权限
//         children: [
//             {
//                 path: 'users',
//                 component: UserManagement,
//                 meta: { requiresAdmin: true },
//                 children:[
//                     // {
//                     //     path: '',
//                     //     name: 'UserManagementHome',
//                     //     component: () => import('@/views/admin/UserManagement/UserManage.vue'), // 主页面
//                     // },
//                     { path: 'view', name: 'ViewUsers', component: ViewUsers },
//                     { path: 'edit', name: 'EditUser', component: EditUser },
//                     { path: 'add', name: 'AddUser', component: AddUser },
//                     { path: 'delete', name: 'DeleteUser', component: DeleteUser },
//               ],
//             },
//             { path: 'notices', name: 'NoticeManagement', component: NoticeManagement },
//             { path: 'cases', name: 'CaseManagement', component: CaseManagement },
//             {
//                 path: 'tags',
//                 name: 'TagManagement',
//                 component: SkillTagManagement,
//             }
//         ],
//     },
// ];
//
// const router = createRouter({
//     history: createWebHistory(),
//     routes,
// });
// import { useAdminStore } from '@/stores/admin/adminStore';
//
// // 路由守卫：验证管理员登录状态
// router.beforeEach((to, from, next) => {
//     const adminStore = useAdminStore(); // 获取管理员状态
//
//     // 验证管理员权限
//     if (to.matched.some((record) => record.meta.requiresAdmin)) {
//         if (!adminStore.isAdminLoggedIn) {
//             // 如果管理员未登录，跳转到登录页面
//             return next('/admin/login');
//         }
//     }
//
//     // 防止已登录管理员访问登录页面
//     if (to.path === '/admin/login' && adminStore.isAdminLoggedIn) {
//         return next('/admin'); // 已登录管理员直接跳转到后台页面
//     }
//
//     // 验证是否隐藏客户端 UI
//     if (to.matched.some((record) => record.meta.isAdminPage)) {
//         document.body.classList.add('admin-page'); // 添加管理员页面样式
//     } else {
//         document.body.classList.remove('admin-page'); // 移除管理员页面样式
//     }
//
//     next(); // 继续导航
// });
//
// export default router;


// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import { useUserStore } from "@/stores/user/userStore"; // 用户状态 Pinia store
import { useAdminStore } from "@/stores/admin/adminStore"; // 管理员状态 Pinia store

// 用户页面组件
import HomePage from '@/views/Pages/HomePage/HomePage.vue';
import DemandPage from '@/views/Pages/DemandPage/DemandPage.vue';
import DemandDetailPage from '@/views/Pages/DemandPage/DemandDetail.vue'
import HelpPage from '@/views/Pages/HelpPage/HelpPage.vue';
import UserProfile from '@/views/Pages/UserPage/UserProfile.vue';
import LoginPage from '@/views/Pages/LoginPage/LoginPage.vue';

// 用户中心子组件
import UserInfoDisplay from '@/views/Pages/UserPage/UserInfoDisplay.vue'; // 个人信息展示
import UserInfoEdit from '@/views/Pages/UserPage/UserInfoEdit.vue'; // 修改个人信息
import ChangePassword from '@/views/Pages/UserPage/ChangePassword.vue'; // 修改密码
import ChangeEmail from '@/views/Pages/UserPage/ChangeEmail.vue'; // 修改注册邮箱
import ViewOtherUsers from '@/views/Pages/UserPage/ViewUsers.vue'; // 查看其他用户
import FavoriteRequirements from "@/views/Pages/UserPage/FavoriteRequirements.vue";
import ManageRequirements from "@/views/Pages/UserPage/ManageRequirements.vue";

// 管理员页面
import AdminLogin from '@/views/admin/login/AdminLogin.vue';
import AdminDashboard from '@/views/admin/AdminDashboard.vue';
import UserManagement from '@/views/admin/UserManagement/UserManage.vue';
import NoticeManagement from '@/views/admin/NoticeManagement/NoticeManage.vue';
import SkillTagManagement from "@/views/admin/SkillTagManagement/SkillTagManagement.vue";
import DemandStatistics from "@/views/admin/DemandManagement/DemandStatistics.vue";
// 管理员用户管理模块
import ViewUsers from "@/views/admin/UserManagement/ViewUsers.vue";
import EditUser from "@/views/admin/UserManagement/EditUser.vue";
import AddUser from "@/views/admin/UserManagement/AddUser.vue";
import DeleteUser from "@/views/admin/UserManagement/DeleteUser.vue";
import AdminManagement from "../views/admin/UserManagement/AdminManagement.vue";


// 注册页面组件
import RegisterChoose from '@/views/Pages/RegisterPage/HomeRegister.vue';  //

import PendingPage from '@/views/Pages/RegisterPage/Pending.vue';  // 待审核页面

// 路由配置
const routes = [
    { path: '/', name: 'Home', component: HomePage },
    { path: '/demand', name: 'Demand', component: DemandPage },
    { path: '/help', name: 'Help', component: HelpPage },
    { path: '/demand-detail/:id', name: 'DemandDetail', component: DemandDetailPage },
    // 用户中心路由
    {
        path: '/user',
        component: UserProfile,
        meta: { requiresAuth: true }, // 标记需要用户登录才能访问
        redirect:'/user/profile',
        children: [
            {
                path: '',
                name: 'UserProfileDisplay',
                component: UserInfoDisplay,
            },
            {
                path: 'profile',
                name: 'UserProfileDisplay',
                component: UserInfoDisplay,
            },
            {
                path: 'edit-profile',
                name: 'UserProfileEdit',
                component: UserInfoEdit,
            },
            {
                path: 'change-password',
                name: 'ChangePassword',
                component: ChangePassword,
            },
            {
                path: 'change-email',
                name: 'ChangeEmail',
                component: ChangeEmail,
            },
            {
                path: 'view-users',
                name: 'ViewOtherUsers',
                component: ViewOtherUsers,
            },
            {
                path: "favorite-requirements",
                name: "FavoriteRequirements",
                component: FavoriteRequirements,
            },
            {
                path: "manage-my-requirements",
                name: "ManageMyRequirements",
                component:ManageRequirements, // 对应的视图组件
            },
        ],
    },
    { path: '/login', name: 'LoginPage', component: LoginPage },

    // 注册页面路由
    { path: '/register', name: 'RegisterChoose', component: RegisterChoose },
    { path: '/pending', name: 'PendingPage', component: PendingPage },


    // 管理员登录页面
    {
        path: '/admin/login',
        name: 'AdminLogin',
        component: AdminLogin,
        meta: { isAdminPage: true },
    },

    // 管理员后台页面
    {
        path: '/admin',
        component: AdminDashboard,
        meta: { requiresAdmin: true, isAdminPage: true },
        children: [
            {
                path: 'users',
                component: UserManagement,
                meta: { requiresAdmin: true },
                redirect:'/admin/users/view',
                children: [

                    { path: 'view', name: 'ViewUsers', component: ViewUsers },
                    { path: 'edit', name: 'EditUser', component: EditUser },
                    { path: 'add', name: 'AddUser', component: AddUser },
                    { path: 'delete', name: 'DeleteUser', component: DeleteUser },
                    {
                        path: 'adminManagement',
                        name: 'AdminManagement',
                        component: AdminManagement,
                        meta: { requiresSuperAdmin: true } // 仅超级管理员可访问
                    },
                ],
            },
            { path: 'notices', name: 'NoticeManagement', component: NoticeManagement },
            { path: 'cases', name: 'CaseManagement', component: DemandStatistics },
            { path: 'tags', name: 'TagManagement', component: SkillTagManagement },
        ],
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

// 路由守卫
router.beforeEach(async (to, from, next) => { // 加上 async
    const adminStore = useAdminStore(); // 获取管理员状态

    // 自动登录管理员（确保刷新时恢复状态）
    if (to.matched.some((record) => record.meta.requiresAdmin)) {
        if (!adminStore.isAdminLoggedIn) {
            await adminStore.autoLoginAdmin(); // 恢复管理员登录状态
        }
    }


    // 验证用户是否登录
    if (to.matched.some((record) => record.meta.requiresAuth)) {
        const userStore = useUserStore(); // 获取用户状态
        if (!userStore.isLoggedIn) {
            // 如果用户未登录，跳转到登录页面
            return next('/login');
        }
    }

    // 验证管理员权限
    if (to.matched.some((record) => record.meta.requiresAdmin)) {
        if (!adminStore.isAdminLoggedIn) {
            // 如果管理员未登录，跳转到管理员登录页面
            return next('/admin/login');
        }
    }
    // 验证超级管理员权限
    if (to.matched.some((record) => record.meta.requiresSuperAdmin)) {
        if (adminStore.adminInfo.role !== 'SUPER_ADMIN') {
            alert("权限不足，仅超级管理员可访问！");
            return next('/admin/users'); // 跳转到管理员主页面
        }
    }

    // **已登录管理员访问登录页重定向**
    if (to.path === '/admin/login' && adminStore.isAdminLoggedIn) {
        console.log('已登录管理员访问登录页，跳转到后台');
        return next('/admin');
    }

    next(); // 继续导航
});

export default router;
