<template>
  <div class="project-list">
    <h2>需求列表</h2>
    <div v-for="project in projects" :key="project.id" class="project-card">
      <h3>{{ project.title }}</h3>
      <p>预算: {{ project.budget }}</p>
      <p>发布时间: {{ project.date }}</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ProjectList',
  data() {
    return {
      projects: [
        { id: 1, title: '游戏开发', budget: '¥5000以下', date: '2024-11-26' },
        { id: 2, title: '加气站数据系统', budget: '¥5000-10000', date: '2024-11-26' },
        { id: 3, title: '智能家居控制App优化', budget: '¥10000-50000', date: '2024-11-25' },
      ],
    };
  },
};
</script>

<style scoped>
.project-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); /* 根据可用空间自动填充 */
  gap: 1.5rem; /* 网格间隙 */
}

.project-card {
  border: 1px solid #ccc;
  padding: 1rem;
  margin-bottom: 1rem;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
}

.project-card:hover {
  transform: scale(1.02); /* 悬停效果 */
}


</style>
