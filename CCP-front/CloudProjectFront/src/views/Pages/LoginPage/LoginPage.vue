


<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="login-title">登录</h2>
      <form @submit.prevent="handleLogin">
        <!-- 账号输入框 -->
        <div class="form-group">
          <label for="account">账号</label>
          <input
              type="text"
              id="account"
              placeholder="请输入邮箱或用户名"
              v-model="account"
          />
        </div>

        <!-- 密码输入框 -->
        <div class="form-group">
          <label for="password">密码</label>
          <input
              type="password"
              id="password"
              placeholder="请输入密码"
              v-model="password"
          />
        </div>

        <!-- 登录按钮 -->
        <button type="submit" class="login-button">登录</button>
      </form>

      <!-- 注册提示 -->
      <div class="register-tip">
        <p>没有账号？<a @click="navigateToRegister">去注册</a></p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user/userStore";
import axiosInstance from "@/http/axios.js"; // 引入 axiosInstance


const account = ref(""); // 用户输入的账号
const password = ref(""); // 用户输入的密码
const router = useRouter();
const userStore = useUserStore(); // 使用 Pinia 的用户状态管理


// 登录按钮处理
async function handleLogin() {
  if (!account.value || !password.value) {
    alert("请完整填写账号和密码！");
    return;
  }

  try {
    // 调用后端登录接口
    const response = await axiosInstance.post("/api/user/login", {
      email: account.value,
      password: password.value,
    });

    // 获取后端返回的数据（包含 token 和 userType）
    const { token, userType, username } = response.data;

    // 将用户信息存入 userStore
    userStore.login({
      email: account.value, // 登录表单的邮箱
      token: token,         // 后端返回的 token
      userType: userType,   // 后端返回的用户类型
      username: username,   // 用户名（可选）
    });

    // 显示登录成功的提示
    alert(`欢迎 ${username || "用户"} 登录成功！`);

    // 普通用户登录成功后跳转到主页
    router.push("/");
  } catch (error) {
    console.error("登录失败：", error);
    alert("登录失败：" + (error.response?.data.message || error.message));
  }
}

// 注册跳转
function navigateToRegister() {
  router.push("/register");
}

</script>


<style src="@/styles/login/login.css"></style>
