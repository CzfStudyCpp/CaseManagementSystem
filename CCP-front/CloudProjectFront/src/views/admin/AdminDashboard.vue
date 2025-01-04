


<!--<template>-->
<!--  <div class="admin-dashboard">-->
<!--    &lt;!&ndash; 顶部标题 &ndash;&gt;-->
<!--    <header class="dashboard-header">-->
<!--      <h1>云协作案例共享平台后台管理系统</h1>-->
<!--      <button @click="handleLogout" class="logout-button">登出</button>-->
<!--    </header>-->

<!--    &lt;!&ndash; 注意事项 &ndash;&gt;-->
<!--    <section class="dashboard-notice">-->
<!--      <h3>管理员须知</h3>-->
<!--      <ul>-->
<!--        <li>请妥善管理用户信息，确保数据安全。</li>-->
<!--        <li>审核发布的公告内容，保持平台公正性。</li>-->
<!--        <li>及时处理案例反馈，保障平台高效运转。</li>-->
<!--      </ul>-->
<!--    </section>-->

<!--    &lt;!&ndash; 主体部分 &ndash;&gt;-->
<!--    <div class="main-section">-->
<!--      &lt;!&ndash; 侧边栏 &ndash;&gt;-->
<!--      <aside class="dashboard-sidebar">-->
<!--        <ul>-->
<!--          <li @click="navigateTo('users')" :class="{ active: activeModule === 'users' }">用户管理</li>-->
<!--          <li @click="navigateTo('notices')" :class="{ active: activeModule === 'notices' }">公告管理</li>-->
<!--          <li @click="navigateTo('cases')" :class="{ active: activeModule === 'cases' }">案例管理</li>-->
<!--        </ul>-->
<!--      </aside>-->

<!--      &lt;!&ndash; 内容展示区 &ndash;&gt;-->
<!--      <main class="dashboard-content">-->
<!--        <router-view />-->
<!--      </main>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->

<!--<script setup>-->
<!--import { ref } from "vue";-->
<!--import { useRouter } from "vue-router";-->
<!--import { useAdminStore } from "@/stores/admin/adminStore";-->
<!--import { onMounted, onUnmounted } from 'vue';-->

<!--onMounted(() => {-->
<!--  document.body.classList.add('admin-page');-->
<!--});-->

<!--onUnmounted(() => {-->
<!--  document.body.classList.remove('admin-page');-->
<!--});-->

<!--const router = useRouter();-->
<!--const adminStore = useAdminStore();-->
<!--const activeModule = ref("users");-->

<!--const handleLogout = () => {-->
<!--  adminStore.logoutAdmin(); // 清除管理员登录状态-->
<!--  router.push("/admin/login"); // 跳转到登录页面-->
<!--};-->

<!--function navigateTo(module) {-->
<!--  activeModule.value = module;-->
<!--  const path = `/admin/${module}`;-->
<!--  console.log('Navigating to:', path);  // 打印路径查看拼接结果-->
<!--  router.push(path);-->
<!--}-->
<!--</script>-->

<!--<style src="@/styles/admin/adminDashboard/adminDashboard.css"></style>-->

<template>
  <div class="admin-dashboard">
    <!-- 顶部标题 -->
    <header class="dashboard-header">
      <h1>云协作案例共享平台后台管理系统</h1>
      <button @click="handleLogout" class="logout-button">登出</button>
    </header>

    <!-- 注意事项 -->
    <section class="dashboard-notice">
      <h3>管理员须知</h3>
      <ul>
        <li>请妥善管理用户信息，确保数据安全。</li>
        <li>审核发布的公告内容，保持平台公正性。</li>
        <li>及时处理案例反馈，保障平台高效运转。</li>
      </ul>
    </section>

    <!-- 主体部分 -->
    <div class="main-section">
      <!-- 侧边栏 -->
      <aside class="dashboard-sidebar">
        <ul>
          <li @click="navigateTo('users')" :class="{ active: activeModule === 'users' }">用户管理</li>
          <li @click="navigateTo('notices')" :class="{ active: activeModule === 'notices' }">公告管理</li>
          <li @click="navigateTo('cases')" :class="{ active: activeModule === 'cases' }">案例管理</li>
          <li @click="navigateTo('tags')" :class="{ active: activeModule === 'tags' }">标签管理</li>
        </ul>
      </aside>

      <!-- 内容展示区 -->
      <main class="dashboard-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAdminStore } from "@/stores/admin/adminStore";
import { onMounted, onUnmounted } from "vue";

onMounted(() => {
  document.body.classList.add("admin-page");
});

onUnmounted(() => {
  document.body.classList.remove("admin-page");
});

const router = useRouter();
const adminStore = useAdminStore();
const activeModule = ref("users");

const handleLogout = () => {
  adminStore.logoutAdmin(); // 清除管理员登录状态
};

function navigateTo(module) {
  activeModule.value = module;
  const path = `/admin/${module}`;
  console.log("Navigating to:", path); // 打印路径查看拼接结果
  router.push(path);
}
</script>

<style src="@/styles/admin/adminDashboard/adminDashboard.css"></style>







