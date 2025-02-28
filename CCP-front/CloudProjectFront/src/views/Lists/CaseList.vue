<template>
  <div class="case-list">
    <h2>案例列表</h2>
    <div v-for="caseItem in cases" :key="caseItem.id" class="case-card">
      <h3>{{ caseItem.title }}</h3>
      <p>描述: {{ caseItem.description }}</p>
      <p>完成日期: {{ caseItem.date }}</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CaseList',
  data() {
    return {
      cases: [
        { id: 1, title: '分布式数据库优化', description: '针对分布式数据库的性能优化方案', date: '2024-10-15' },
        { id: 2, title: '微服务架构案例', description: '设计和实现微服务架构的案例', date: '2024-09-30' },
        { id: 3, title: '大数据分析平台', description: '使用大数据技术进行实时数据分析', date: '2024-08-20' },
      ],
    };
  },
};
</script>

<style scoped>
.case-list {
  margin-top: 2rem;
}
.case-card {
  border: 1px solid #ccc;
  padding: 1rem;
  margin-bottom: 1rem;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
}
.case-card:hover {
  transform: scale(1.02);
}
</style>