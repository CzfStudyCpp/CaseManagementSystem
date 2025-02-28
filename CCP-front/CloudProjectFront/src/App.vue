<!--<template>-->
<!--  <div id="app">-->
<!--    <nav-bar />-->
<!--    <router-view />-->
<!--    <page-footer />-->
<!--  </div>-->
<!--</template>-->

<!--<script setup>-->


<!--import NavBar from '@/components/Navbar.vue';-->
<!--import PageFooter from '@/components/PageFooter.vue';-->
<!--</script>-->

<!--<style src="@/styles/common.css"></style>-->


<!--<template>-->
<!--  <div id="app">-->
<!--    &lt;!&ndash; 动态显示导航栏 &ndash;&gt;-->
<!--    <nav-bar v-if="!isAdminPage" />-->

<!--    &lt;!&ndash; 路由视图 &ndash;&gt;-->
<!--    <router-view />-->

<!--    &lt;!&ndash; 动态显示页脚 &ndash;&gt;-->
<!--    <page-footer v-if="!isAdminPage" />-->
<!--  </div>-->
<!--</template>-->

<!--<script setup>-->
<!--import { computed } from 'vue';-->
<!--import { useRoute } from 'vue-router';-->
<!--import NavBar from '@/components/Navbar.vue';-->
<!--import PageFooter from '@/components/PageFooter.vue';-->

<!--// 使用路由信息判断是否是管理员页面-->
<!--const route = useRoute();-->
<!--const isAdminPage = computed(() => route.meta?.isAdminPage === true);-->
<!--</script>-->

<template>
  <div id="app">
    <!-- 动态显示导航栏 -->
    <nav-bar v-if="!isAdminPage" />

    <!-- 路由视图 -->
    <router-view />

    <!-- 动态显示页脚 -->
    <page-footer v-if="!isAdminPage" />
  </div>
</template>

<script setup>
import { computed, onMounted } from "vue";
import { useRoute } from "vue-router";
import { useUserStore } from "@/stores/user/userStore"; // 引入 Pinia 的 userStore
import NavBar from "@/components/Navbar.vue";
import PageFooter from "@/components/PageFooter.vue";
import { useAdminStore } from "@/stores/admin/adminStore"; // 引入管理员的 Pinia store

// 使用路由信息判断是否是管理员页面
const route = useRoute();
const isAdminPage = computed(() => route.meta?.isAdminPage === true);

// 自动登录
const userStore = useUserStore();
onMounted(() => {
  if (isAdminPage.value) {
    adminStore.autoLoginAdmin(); // 如果是管理员页面，调用管理员的自动登录函数
  } else {
    userStore.autoLogin(); // 如果是普通用户页面，调用用户的自动登录函数
  }
});
</script>

<style src="@/styles/common.css"></style>


<style src="@/styles/common.css"></style>
