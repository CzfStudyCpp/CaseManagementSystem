

<!--<template>-->
<!--  <div class="user-profile-container">-->
<!--    &lt;!&ndash; 侧边栏菜单 &ndash;&gt;-->
<!--    <div class="sidebar">-->
<!--      <el-menu-->
<!--          :default-active="activeMenu"-->
<!--          router-->
<!--          class="menu"-->
<!--      >-->
<!--        &lt;!&ndash; 展示个人信息 &ndash;&gt;-->
<!--        <el-menu-item index="/user/profile" @click="navigateTo('/user/profile')">-->
<!--          <i class="el-icon-user"></i>-->
<!--          <span>个人信息展示</span>-->
<!--        </el-menu-item>-->

<!--        &lt;!&ndash; 修改个人信息 &ndash;&gt;-->
<!--        <el-menu-item index="/user/edit-profile" @click="navigateTo('/user/edit-profile')">-->
<!--          <i class="el-icon-edit"></i>-->
<!--          <span>修改个人信息</span>-->
<!--        </el-menu-item>-->

<!--        &lt;!&ndash; 修改密码 &ndash;&gt;-->
<!--        <el-menu-item index="/user/change-password" @click="navigateTo('/user/change-password')">-->
<!--          <i class="el-icon-lock"></i>-->
<!--          <span>修改密码</span>-->
<!--        </el-menu-item>-->

<!--        &lt;!&ndash; 修改邮箱 &ndash;&gt;-->
<!--        <el-menu-item index="/user/change-email" @click="navigateTo('/user/change-email')">-->
<!--          <i class="el-icon-mail"></i>-->
<!--          <span>修改注册邮箱</span>-->
<!--        </el-menu-item>-->

<!--        &lt;!&ndash; 查看其他用户 &ndash;&gt;-->
<!--        <el-menu-item index="/user/view-users" @click="navigateTo('/user/view-users')">-->
<!--          <i class="el-icon-s-custom"></i>-->
<!--          <span>查看其他用户</span>-->
<!--        </el-menu-item>-->
<!--      </el-menu>-->
<!--    </div>-->

<!--    &lt;!&ndash; 内容区域 &ndash;&gt;-->
<!--    <div class="content">-->
<!--      <router-view></router-view>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->

<!--<script setup>-->
<!--import { ref } from "vue";-->
<!--import { useRouter, useRoute } from "vue-router";-->

<!--// 路由实例-->
<!--const router = useRouter();-->
<!--const route = useRoute();-->
<!--const activeMenu = ref(route.path);-->

<!--// 菜单导航功能-->
<!--const navigateTo = (path) => {-->
<!--  activeMenu.value = path; // 更新激活状态-->
<!--  router.push(path);-->
<!--};-->
<!--</script>-->

<!--<style scoped>-->
<!--.user-profile-container {-->
<!--  display: flex;-->
<!--  height: 100%;-->
<!--}-->

<!--.sidebar {-->
<!--  width: 200px;-->
<!--  background-color: #f5f5f5;-->
<!--  border-right: 1px solid #ddd;-->
<!--}-->

<!--.content {-->
<!--  flex: 1;-->
<!--  padding: 20px;-->
<!--}-->

<!--.el-menu {-->
<!--  height: 100%;-->
<!--  border-right: none;-->
<!--}-->
<!--</style>-->
<template>
  <div class="user-profile-container">
    <!-- 侧边栏菜单 -->
    <div class="sidebar">
      <el-menu
          :default-active="activeMenu"
          router
          class="menu"
      >
        <!-- 展示个人信息 -->
        <el-menu-item index="/user/profile" @click="navigateTo('/user/profile')">
          <i class="el-icon-user"></i>
          <span>个人信息展示</span>
        </el-menu-item>

        <!-- 修改个人信息 -->
        <el-menu-item index="/user/edit-profile" @click="navigateTo('/user/edit-profile')">
          <i class="el-icon-edit"></i>
          <span>修改个人信息</span>
        </el-menu-item>

        <!-- 修改密码 -->
        <el-menu-item index="/user/change-password" @click="navigateTo('/user/change-password')">
          <i class="el-icon-lock"></i>
          <span>修改密码</span>
        </el-menu-item>

        <!-- 修改邮箱 -->
        <el-menu-item index="/user/change-email" @click="navigateTo('/user/change-email')">
          <i class="el-icon-mail"></i>
          <span>修改注册邮箱</span>
        </el-menu-item>

        <!-- 查看其他用户 -->
        <el-menu-item index="/user/view-users" @click="navigateTo('/user/view-users')">
          <i class="el-icon-s-custom"></i>
          <span>查看其他用户</span>
        </el-menu-item>

        <!-- 查看关注的需求 -->
        <el-menu-item index="/user/favorite-requirements" @click="navigateTo('/user/favorite-requirements')">
          <i class="el-icon-star-on"></i>
          <span>查看关注的需求</span>
        </el-menu-item>

        <!-- 管理我的需求（仅企业账户可见） -->
        <el-menu-item
            v-if="isEnterpriseUser"
            index="/user/manage-my-requirements"
            @click="navigateTo('/user/manage-my-requirements')"
        >
          <i class="el-icon-suitcase"></i>
          <span>管理我的需求</span>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 内容区域 -->
    <div class="content">
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup>
import {ref, computed} from "vue";
import {useRouter, useRoute} from "vue-router";
import {useUserStore} from "@/stores/user/userStore.ts"; // 引入用户 Store

// 路由实例
const router = useRouter();
const route = useRoute();
const activeMenu = ref(route.path);

// 菜单导航功能
const navigateTo = (path) => {
  activeMenu.value = path; // 更新激活状态
  router.push(path);
};

// 检查用户是否是企业账户
const userStore = useUserStore();
const isEnterpriseUser = computed(() => userStore.userType === "COMPANY"); // 根据用户角色判断
</script>

<style scoped>
.user-profile-container {
  display: flex;
  height: 100%;
}

.sidebar {
  width: 200px;
  background-color: #f5f5f5;
  border-right: 1px solid #ddd;
}

.content {
  flex: 1;
  padding: 20px;
}

.el-menu {
  height: 100%;
  border-right: none;
}
</style>
