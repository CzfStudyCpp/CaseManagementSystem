
<template>
  <div class="home">
    <!-- 首页顶部介绍 -->
    <h1>欢迎来到案例管理平台</h1>
    <div class="hero" @mouseover="onHover" @mouseleave="onLeave">
      <h1 class="hero-title" :class="{ hoverEffect: isHovered }">{{ subtitle }}</h1>
    </div>

    <!-- 公告栏和需求榜单 -->
    <section class="notice-demand">
      <!-- 公告栏 -->
      <div class="notice">
        <h2 class="section-title">公告栏</h2>
        <ul>
          <li v-if="latestAnnouncement">
            <strong>{{ latestAnnouncement.title }}</strong>
            <p>{{ latestAnnouncement.content }}</p>
            <p class="timestamp">更新时间: {{ formatDate(latestAnnouncement.lastUpdatedDate) }}</p>
          </li>
          <li v-else>暂无最新公告。</li>
        </ul>
      </div>

      <!-- 需求榜单 -->
      <div class="demand">
        <h2 class="section-title">推荐需求榜单</h2>
        <ul>
          <li
              v-for="demand in recommendedDemands"
              :key="demand.id"
              @click="navigateToDemandDetail(demand.id)"
              class="clickable-demand-item"
          >
            {{ demand.title }}（预算 ¥{{ demand.budget.toFixed(2) }}）
          </li>
        </ul>
        <p v-if="recommendedDemands.length === 0">暂无推荐需求。</p>
      </div>

    </section>

    <!-- 网站特色服务 -->
    <section class="services">
      <h2 class="section-title">全方位服务保障</h2>
      <div class="service-cards">
        <div class="card" @mouseover="addHoverEffect" @mouseleave="removeHoverEffect">
          <img src="@/assets/图标.webp" alt="服务图标1" />
          <h3>免费需求对接</h3>
          <p>无需中介费用，直接与开发者合作，减少沟通成本。</p>
        </div>
        <div class="card" @mouseover="addHoverEffect" @mouseleave="removeHoverEffect">
          <img src="@/assets/图标.webp" alt="服务图标2" />
          <h3>海量案例，深入学习</h3>
          <p>提供业内的海量案例，提供技术参考和学习资源。</p>
        </div>
        <div class="card" @mouseover="addHoverEffect" @mouseleave="removeHoverEffect">
          <img src="@/assets/图标.webp" alt="服务图标3" />
          <h3>专业服务支持</h3>
          <p>团队提供实时支持，解决项目遇到的任何问题。</p>
        </div>
      </div>
    </section>

    <!-- 合作案例 -->
    <section class="cases">
      <h2 class="section-title">合作案例</h2>
      <div class="case-cards">
        <div class="case-card" @mouseover="addCaseHoverEffect" @mouseleave="removeCaseHoverEffect">
          <img src="@/assets/图标.webp" alt="华为" />
          <h3>华为</h3>
          <p>资金支持 ¥xxxxxxxx</p>
        </div>
        <div class="case-card" @mouseover="addCaseHoverEffect" @mouseleave="removeCaseHoverEffect">
          <img src="@/assets/图标.webp" alt="联想" />
          <h3>联想</h3>
          <p>资金支持 ¥xxxxxxxx</p>
        </div>
        <div class="case-card" @mouseover="addCaseHoverEffect" @mouseleave="removeCaseHoverEffect">
          <img src="@/assets/图标.webp" alt="阿里巴巴" />
          <h3>阿里巴巴</h3>
          <p>资金支持 ¥xxxxxxxxx</p>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axiosInstance from '@/http/axios.js'; // 使用封装的 axios 实例
import { useRouter } from 'vue-router'; // 导入 useRouter
// 默认文本内容
const subtitle = ref('重塑技术社区生态，连接企业与开发者，提供全方位的技术服务');

// 鼠标悬浮状态
const isHovered = ref(false);

const router = useRouter(); // 初始化路由实例

// 跳转到需求详情页
function navigateToDemandDetail(demandId) {
  const url = `/demand-detail/${demandId}`; // 需求详情页 URL
  window.open(url, '_blank'); // 在新标签页中打开
}
// 鼠标悬浮时切换为励志语句，添加效果
function onHover() {
  subtitle.value = '博学慎思，明辨笃行';
  isHovered.value = true; // 激活效果
}

// 鼠标离开时恢复默认文本，移除效果
function onLeave() {
  subtitle.value = '重塑技术社区生态，连接企业与开发者，提供全方位的技术服务';
  isHovered.value = false; // 取消效果
}

// 最新公告
const latestAnnouncement = ref(null);
const recommendedDemands = ref([]); // 推荐需求
// 获取最新公告
const fetchLatestAnnouncement = async () => {
  try {
    // 获取最新公告
    const announcementResponse = await axiosInstance.get('/api/getLatestAnnouncement');
    if (announcementResponse.data && announcementResponse.data.success) {
      latestAnnouncement.value = announcementResponse.data.data;
    } else {
      console.error('获取最新公告失败:', announcementResponse.data.message);
    }

    // 获取推荐需求
    const demandResponse = await axiosInstance.get('/api/requirements/recommend');
    if (demandResponse.data && demandResponse.data.success) {
      recommendedDemands.value = demandResponse.data.data.slice(0, 3); // 仅保留前三个需求
    } else {
      console.error('获取推荐需求失败:', demandResponse.data.message);
    }
  } catch (error) {
    console.error('获取数据失败:', error.message);
  }
};

// 辅助函数：格式化日期
const formatDate = (dateString) => {
  const date = new Date(dateString);
  return `${date.toLocaleDateString()} ${date.toLocaleTimeString()}`;
};

// 动态效果：特色服务卡片
function addHoverEffect(event) {
  event.currentTarget.style.transform = 'scale(1.05)';
  event.currentTarget.style.transition = 'transform 0.3s ease';
}

function removeHoverEffect(event) {
  event.currentTarget.style.transform = 'scale(1)';
}

// 动态效果：合作案例卡片
function addCaseHoverEffect(event) {
  event.currentTarget.style.transform = 'translateY(-10px)';
  event.currentTarget.style.boxShadow = '0px 8px 15px rgba(0, 0, 0, 0.2)';
  event.currentTarget.style.transition = 'all 0.3s ease';
}

function removeCaseHoverEffect(event) {
  event.currentTarget.style.transform = 'translateY(0)';
  event.currentTarget.style.boxShadow = '0px 4px 6px rgba(0, 0, 0, 0.1)';
}

// 在页面加载时调用获取最新公告的函数
onMounted(() => {
  fetchLatestAnnouncement();
});
</script>

<style scoped>
/* 鼠标悬浮时的文字动态效果 */
.hero-title {
  font-size: 36px;
  transition: color 0.3s ease, transform 0.3s ease;
}

.hero-title.hoverEffect {
  color: #007bff; /* 改变颜色 */
  transform: scale(1.1); /* 字体虚浮的效果 */
  text-shadow: 0px 4px 10px rgba(0, 123, 255, 0.5); /* 增加光晕效果 */
}

/* 公告栏样式 */
.notice {
  margin-bottom: 20px;
}

.notice h3 {
  font-size: 18px;
  margin-bottom: 10px;
}

.notice p {
  font-size: 14px;
  margin: 5px 0;
}

.timestamp {
  font-size: 12px;
  color: gray;
}

/* 服务保障样式 */
.services {
  margin-top: 30px;
  text-align: center;
}

.service-cards {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 20px;
  margin-top: 15px;
}

.card {
  flex: 1;
  max-width: 30%;
  background-color: #f8f9fa;
  border-radius: 10px;
  padding: 15px;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.card img {
  max-width: 100px;
  margin-bottom: 10px;
}

/* 合作案例样式 */
.cases {
  margin-top: 30px;
  text-align: center;
}

.case-cards {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 20px;
  margin-top: 15px;
}

.case-card {
  flex: 1;
  max-width: 30%;
  background-color: #ffffff;
  border-radius: 10px;
  padding: 15px;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.case-card img {
  max-width: 100px;
  margin-bottom: 10px;
}
</style>






