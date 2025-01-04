<template>
  <div class="pending-container">
    <h2>注册信息已提交</h2>
    <p>感谢您的注册，您的账户正在审核中。审核结果将通过您的邮箱通知。</p>

    <!-- 显示审核状态 -->
    <div class="status">
      <p class="status-text">当前状态：<strong>审核中</strong></p>
    </div>

    <!-- 返回按钮 -->
    <div class="buttons">
      <router-link to="/" class="btn btn-primary">返回首页</router-link>
      <router-link to="/login" class="btn btn-secondary">去登录</router-link>
    </div>
  </div>
</template>

<script setup>

</script>

<style scoped>
.pending-container {
  text-align: center;
  padding: 40px;
  background-color: #f9f9f9;
  border-radius: 8px;
  border: 1px solid #ddd;
  margin: 40px auto;
  max-width: 500px;
}

h2 {
  color: #4CAF50;
  font-size: 24px;
}

p {
  font-size: 18px;
  color: #555;
}

.status {
  margin-top: 20px;
}

.status-text {
  font-size: 20px;
  color: #FF9800;  /* 使用橙色强调审核中状态 */
}

.buttons {
  margin-top: 40px;
}

.btn {
  display: inline-block;
  padding: 10px 20px;
  font-size: 16px;
  margin: 5px;
  border-radius: 4px;
  text-decoration: none;
}

.btn-primary {
  background-color: #4CAF50;
  color: white;
}

.btn-primary:hover {
  background-color: #45a049;
}

.btn-secondary {
  background-color: #f44336;
  color: white;
}

.btn-secondary:hover {
  background-color: #e53935;
}
</style>
