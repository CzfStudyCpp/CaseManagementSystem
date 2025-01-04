<!--<template>-->
<!--  <div>-->
<!--    <h2>用户管理模块</h2>-->
<!--    <div class="button-container">-->
<!--      <el-button @click="navigateTo('view')" type="primary">查看用户信息</el-button>-->
<!--      <el-button @click="navigateTo('edit')" type="success">修改用户注册邮箱</el-button>-->
<!--      <el-button @click="navigateTo('add')" type="warning">添加用户</el-button>-->
<!--      <el-button @click="navigateTo('delete')" type="danger">删除用户</el-button>-->
<!--    </div>-->
<!--    &lt;!&ndash; 用于渲染子路由 &ndash;&gt;-->
<!--    <router-view />-->
<!--  </div>-->
<!--</template>-->

<!--<script>-->
<!--import {useRouter} from "vue-router";-->

<!--export default {-->
<!--  name: "UserManage",-->
<!--  setup() {-->
<!--    const router = useRouter();-->

<!--    const navigateTo = (action) => {-->
<!--      const targetPath = `/admin/users/${action}`;-->
<!--      console.log("Navigating to:", targetPath); // 输出目标路径-->
<!--      router.push(targetPath);-->
<!--    };-->

<!--    return {-->
<!--      navigateTo,-->
<!--    };-->
<!--  },-->
<!--};-->
<!--</script>-->

<!--<style scoped>-->
<!--.button-container {-->
<!--  display: flex;-->
<!--  flex-wrap: wrap;-->
<!--  gap: 10px;-->
<!--  margin-top: 20px;-->
<!--}-->

<!--.el-button {-->
<!--  width: 200px;-->
<!--  height: 50px;-->
<!--  font-size: 16px;-->
<!--}-->
<!--</style>-->


<template>
  <div>
    <h2>用户管理模块</h2>
    <div class="button-container">
      <el-button @click="navigateTo('view')" type="primary">查看用户信息</el-button>
      <el-button @click="navigateTo('edit')" type="success">修改用户注册邮箱</el-button>
      <el-button @click="navigateTo('add')" type="warning">添加用户</el-button>
      <el-button @click="navigateTo('delete')" type="danger">删除用户</el-button>
      <el-button @click="navigateTo('adminManagement')" type="info">管理员管理</el-button> <!-- 新增按钮 -->
    </div>
    <!-- 用于渲染子路由 -->
    <router-view />
  </div>
</template>

<script>
import { useRouter } from "vue-router";

export default {
  name: "UserManage",
  setup() {
    const router = useRouter();

    const navigateTo = (action) => {
      const targetPath = `/admin/users/${action}`;
      console.log("Navigating to:", targetPath); // 输出目标路径
      router.push(targetPath);
    };

    return {
      navigateTo,
    };
  },
};
</script>

<style scoped>
.button-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 20px;
}

.el-button {
  width: 200px;
  height: 50px;
  font-size: 16px;
}
</style>
