
<!--<template>-->
<!--  <div class="admin-login">-->
<!--    <h1>管理员登录</h1>-->
<!--    <form @submit.prevent="handleLogin">-->
<!--      <input type="text" v-model="username" placeholder="用户名" required />-->
<!--      <input type="password" v-model="password" placeholder="密码" required />-->
<!--      <button type="submit" :disabled="isLoading">登录</button>-->
<!--    </form>-->
<!--    <p v-if="errorMessage">{{ errorMessage }}</p>-->
<!--  </div>-->
<!--</template>-->

<!--<script setup>-->
<!--import { ref } from 'vue';-->
<!--import { useAdminStore } from '@/stores/admin/adminStore.ts';-->
<!--import { useRouter } from 'vue-router';-->
<!--import axiosInstance from '@/http/axios';  // 引入axios实例-->

<!--// 创建状态-->
<!--const username = ref('');-->
<!--const password = ref('');-->
<!--const errorMessage = ref('');-->
<!--const isLoading = ref(false);-->

<!--// 引用 Pinia 的管理员存储-->
<!--const adminStore = useAdminStore();-->
<!--const router = useRouter();-->

<!--async function handleLogin() {-->
<!--console.log("1111111"); // 调试信息：登录流程开始-->

<!--if (!username.value || !password.value) {-->
<!--  errorMessage.value = '用户名或密码不能为空！';-->
<!--  return;-->
<!--}-->

<!--if (isLoading.value) return; // 防止重复点击-->

<!--isLoading.value = true; // 设置加载状态-->
<!--try {-->
<!--  console.log("222222"); // 调试信息：发送请求到后端-->

<!--  // 发送请求到后端进行管理员登录-->
<!--  const response = await axiosInstance.post('/api/admin/login', {-->
<!--    username: username.value,-->
<!--    password: password.value,-->
<!--  });-->

<!--  console.log("Response:", response);-->

<!--  // 后端返回 JWT Token-->
<!--  const { token } = response.data;-->

<!--  // 存储 Token-->
<!--  localStorage.setItem('authToken', token);-->

<!--  // 更新管理员登录状态-->
<!--  adminStore.loginAdmin({-->
<!--    id: 'admin123',-->
<!--    username: username.value,-->
<!--  });-->

<!--  // 跳转到后台页面-->
<!--  await router.push('/admin');-->
<!--} catch (error) {-->
<!--  // 显示错误信息-->
<!--  if (error.response) {-->
<!--    // 错误响应-->
<!--    console.error('Error Status:', error.response.status); // 打印状态码-->
<!--    console.error('Error Data:', error.response.data); // 打印响应数据-->
<!--    console.error('Error Headers:', error.response.headers); // 打印响应头部-->

<!--    if (error.response.status === 401) {-->
<!--      errorMessage.value = '未授权，请检查用户名或密码。';-->
<!--    } else {-->
<!--      errorMessage.value = `发生了错误: ${error.response.data.message || '未知错误'}`;-->
<!--    }-->
<!--  } else if (error.request) {-->
<!--    // 请求未响应（网络错误等）-->
<!--    console.error('Error Request:', error.request); // 打印请求-->
<!--    errorMessage.value = '请求未响应，请检查网络连接。';-->
<!--  } else {-->
<!--    // 其他错误-->
<!--    console.error('Error Message:', error.message); // 打印错误消息-->
<!--    errorMessage.value = `发生了错误: ${error.message}`;-->
<!--  }-->
<!--} finally {-->
<!--  isLoading.value = false; // 重置加载状态-->
<!--}-->
<!--}-->


<!--</script>-->

<!--<style scoped>-->
<!--.admin-login {-->
<!--  text-align: center;-->
<!--  margin: 100px auto;-->
<!--  width: 300px;-->
<!--  font-family: Arial, sans-serif;-->
<!--}-->

<!--input {-->
<!--  display: block;-->
<!--  margin: 10px auto;-->
<!--  padding: 10px;-->
<!--  width: 100%;-->
<!--  box-sizing: border-box;-->
<!--}-->

<!--button {-->
<!--  padding: 10px 20px;-->
<!--  margin-top: 10px;-->
<!--  cursor: pointer;-->
<!--  background-color: #2a2a72;-->
<!--  color: #fff;-->
<!--  border: none;-->
<!--  border-radius: 5px;-->
<!--  transition: background-color 0.3s ease;-->
<!--}-->

<!--button:hover {-->
<!--  background-color: #1b1b5f;-->
<!--}-->

<!--p {-->
<!--  color: red;-->
<!--  font-size: 14px;-->
<!--}-->
<!--</style>-->



<template>
  <div class="admin-login">
    <h1>管理员登录</h1>
    <form @submit.prevent="handleLogin">
      <input type="text" v-model="username" placeholder="用户名" required />
      <input type="password" v-model="password" placeholder="密码" required />
      <button type="submit" :disabled="isLoading">登录</button>
    </form>
    <p v-if="errorMessage">{{ errorMessage }}</p>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useAdminStore } from '@/stores/admin/adminStore';
import { useRouter } from 'vue-router';
import axiosInstance from '@/http/axios.js'; // 引入 axios 实例

// 创建状态
const username = ref('');
const password = ref('');
const errorMessage = ref('');
const isLoading = ref(false);

// 引用 Pinia 的管理员存储
const adminStore = useAdminStore();
const router = useRouter();

async function handleLogin() {
  if (!username.value || !password.value) {
    errorMessage.value = '用户名或密码不能为空！';
    return;
  }

  if (isLoading.value) return; // 防止重复点击

  isLoading.value = true; // 设置加载状态
  try {
    // 发送请求到后端进行管理员登录
    const response = await axiosInstance.post('/api/admin/login', {
      username: username.value,
      password: password.value,
    });

    // 解构后端返回的 token 和 accessLevel
    const { token, accessLevel } = response.data;

    // 更新管理员登录状态，存储到 Pinia
    adminStore.loginAdmin(
        {
          username: username.value,
          role: accessLevel, // 管理员身份
        },
        token // JWT Token
    );
    // 打印管理员身份
    console.log("管理员身份:", accessLevel);

    // 跳转到后台管理页面
    await router.push('/admin');
  } catch (error) {
    // 处理错误信息
    if (error.response?.status === 401) {
      errorMessage.value = '未授权，请检查用户名或密码。';
    } else {
      errorMessage.value = error.response?.data?.message || '登录失败，请稍后重试。';
    }
  } finally {
    isLoading.value = false; // 重置加载状态
  }
}
</script>

<style scoped>
.admin-login {
  text-align: center;
  margin: 100px auto;
  width: 300px;
  font-family: Arial, sans-serif;
}

input {
  display: block;
  margin: 10px auto;
  padding: 10px;
  width: 100%;
  box-sizing: border-box;
}

button {
  padding: 10px 20px;
  margin-top: 10px;
  cursor: pointer;
  background-color: #2a2a72;
  color: #fff;
  border: none;
  border-radius: 5px;
  transition: background-color 0.3s ease;
}

button:hover {
  background-color: #1b1b5f;
}

p {
  color: red;
  font-size: 14px;
}
</style>
