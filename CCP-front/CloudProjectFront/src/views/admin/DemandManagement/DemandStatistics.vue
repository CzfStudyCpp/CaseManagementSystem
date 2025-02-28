<template>
  <div class="skill-statistics">
    <h2>技能需求统计</h2>
    <div id="barChart" style="width: 100%; height: 400px;"></div>
  </div>
</template>
<script setup>
import * as echarts from 'echarts';
import axiosInstance from '@/http/axios.js';
import { ref, onMounted } from 'vue';

const skillStatistics = ref([]);

const fetchSkillStatistics = async () => {
  try {
    const response = await axiosInstance.get('/api/admin/skill-statistics');
    if (response.data.success) {
      skillStatistics.value = response.data.data;
      renderChart();
    } else {
      console.error('获取技能需求统计失败:', response.data.message);
    }
  } catch (error) {
    console.error('获取技能需求统计失败:', error.message);
  }
};

const renderChart = () => {
  const chart = echarts.init(document.getElementById('barChart'));

  const skills = skillStatistics.value.map(stat => stat.skillName);
  const completedCounts = skillStatistics.value.map(stat => stat.completedCount || 0); // 确保为 0 的值显示
  const pendingCounts = skillStatistics.value.map(stat => stat.pendingCount || 0); // 确保为 0 的值显示

  const option = {
    title: {
      text: '技能需求统计分析',
      left: 'center',
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
    },
    legend: {
      data: ['已完成', '未完成'],
      bottom: 0,
    },
    xAxis: {
      type: 'category',
      data: skills,
    },
    yAxis: {
      type: 'value',
    },
    series: [
      {
        name: '已完成',
        type: 'bar',
        stack: 'total',
        emphasis: {
          focus: 'series',
        },
        data: completedCounts,
        color: '#28a745',
      },
      {
        name: '未完成',
        type: 'bar',
        stack: 'total',
        emphasis: {
          focus: 'series',
        },
        data: pendingCounts,
        color: '#dc3545',
      },
    ],
  };

  chart.setOption(option);
};

// 页面加载时获取统计数据
onMounted(() => {
  fetchSkillStatistics();
});
</script>

<style scoped>
.skill-statistics {
  padding: 20px;
}

#barChart {
  margin: 0 auto;
}
</style>
