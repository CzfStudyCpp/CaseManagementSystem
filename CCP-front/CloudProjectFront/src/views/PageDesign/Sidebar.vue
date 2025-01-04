<template>
  <div class="sidebar">
    <h2>发布项目</h2>
    <button @click="publishProject">立即发布</button>
    <div class="recommended-projects">
      <h3>推荐项目</h3>
      <ul>
        <li>项目A - ¥5000</li>
        <li>项目B - ¥8000</li>
        <li>项目C - ¥10000</li>
      </ul>
    </div>
  </div>
</template>

<style scoped>
.sidebar {
  flex: 1;
  border: 1px solid #ccc;
  padding: 1rem;
  background-color: #f9f9f9;  /* 添加背景色 */
}

.recommended-projects {
  margin-top: 2rem;
}

button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 0.5rem;
  cursor: pointer;
  margin-bottom: 1rem;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #0056b3;  /* 悬停时改变按钮颜色 */
}
</style>
