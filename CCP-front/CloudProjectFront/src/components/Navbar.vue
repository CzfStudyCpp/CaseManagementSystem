
<template>
  <nav class="navbar">
    <div class="navbar-container">
      <!-- 标题 -->
      <h1 class="navbar-title">云协作案例共享平台</h1>

      <!-- 菜单 -->
      <ul class="navbar-menu">
        <li><router-link to="/">首页</router-link></li>
        <li><router-link to="/demand">需求</router-link></li>
        <li><router-link to="/help">帮助</router-link></li>
        <li><router-link to="/user" @click.prevent="navigateToUserCenter">个人中心</router-link></li>
      </ul>

      <!-- 登录/注册 或 用户菜单 -->
      <div class="auth-section">
        <!-- 未登录 -->
        <div v-if="!isLoggedIn" class="auth-buttons">
          <button class="login-button" @click="navigateToLogin">登录</button>
          <button class="login-button" @click="navigateToRegister">注册</button>
        </div>

        <!-- 已登录 -->
        <div class="user-info" v-if="isLoggedIn" style="position: relative;">
          <span class="user-name" @click.stop="toggleMenu">你好，{{ username }}</span>
          <div v-if="showMenu" class="dropdown-menu" @click.stop>
            <ul>
              <li @click="handleNavigateToUserCenter">个人中心</li>
              <li @click="handleLogout">退出登录</li>
            </ul>
          </div>
        </div>

      </div>
    </div>
  </nav>
</template>

<script setup>
import {ref, computed, onMounted, onUnmounted} from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user/userStore";

const router = useRouter();
const userStore = useUserStore();

// 获取用户登录状态和信息
const isLoggedIn = computed(() => userStore.isLoggedIn);
const email = computed(() => userStore.userEmail);
const username=computed(()=>userStore.username);
// 菜单显示控制
const showMenu = ref(false);
function toggleMenu() {
  showMenu.value = !showMenu.value; // 切换 showMenu 的值
}


// 登录、注册、个人中心、登出逻辑
function navigateToLogin() {
  router.push("/login");
}

function navigateToRegister() {
  router.push("/register");
}

function navigateToUserCenter() {
  if (isLoggedIn.value) {
    router.push("/user"); // 已登录跳转
  } else {
    alert("请先登录！");
    router.push("/login"); // 未登录跳转
  }
}

function logout() {
  userStore.logout();
  router.push("/login");
}

// 点击“个人中心”，跳转并关闭菜单
function handleNavigateToUserCenter() {
  showMenu.value = false; // 关闭菜单
  if (isLoggedIn.value) {
    router.push("/user");
  } else {
    alert("请先登录！");
    router.push("/login");
  }
}

// 点击“退出登录”，登出并关闭菜单
async function handleLogout() {
  try {
    // 向后端发起登出请求
    await userStore.logout(); // 调用 store 中的 logout 操作，包含后端请求
    showMenu.value = false; // 关闭菜单
    router.push("/login"); // 跳转到登录页
  } catch (error) {
    console.error("登出失败：", error);
    alert("登出失败，请稍后再试！");
  }
}
function closeMenuOnOutsideClick(event) {
  if (!event.target.closest(".user-info")) {
    showMenu.value = false;
  }
}
onMounted(() => {
  document.addEventListener("click", closeMenuOnOutsideClick);
});

onUnmounted(() => {
  document.removeEventListener("click", closeMenuOnOutsideClick);
});
</script>

<style src="../styles/navbar/navbar.css"></style>
