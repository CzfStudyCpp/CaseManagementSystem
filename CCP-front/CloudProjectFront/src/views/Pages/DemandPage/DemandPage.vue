
<template>
  <div class="demand-page">
    <!-- 左侧选项栏 -->
    <div class="sidebar">
      <h2>需求分类</h2>
      <!-- 推荐列表按钮 -->
      <div class="recommend-button">
        <button @click="fetchRecommendedDemands" class="recommend-btn">
          推荐列表
        </button>
      </div>

      <!-- 技能标签分类 -->
      <el-submenu index="skills">
        <template #title>技能标签分类</template>
        <el-checkbox-group v-model="selectedSkills" @change="fetchBySkills">
          <el-checkbox
              v-for="skill in paginatedSkills"
              :label="skill.id"
              :key="skill.id"
          >
            {{ skill.name }}
          </el-checkbox>
        </el-checkbox-group>
      </el-submenu>

      <!-- 技能标签分页 -->
      <div class="skills-pagination">
        <button
            :disabled="skillsPagination.currentPage === 1"
            @click="changeSkillsPage(-1)"
        >
          上一页
        </button>
        <span>
          第 {{ skillsPagination.currentPage }} 页 / 共 {{ skillsTotalPages }} 页
        </span>
        <button
            :disabled="skillsPagination.currentPage === skillsTotalPages"
            @click="changeSkillsPage(1)"
        >
          下一页
        </button>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="content">
      <div class="content-header">
        <!-- 合并后的按钮，仅对企业账户开放 -->
        <el-button
            type="primary"
            @click="goToManageDemands"
            v-if="isEnterpriseUser"
        >
          发布或查看我的需求
        </el-button>
      </div>

      <!-- 表格部分 -->
      <table class="demand-table">
        <thead>
        <tr>
          <th style="width: 15%;">需求标题</th>
          <th style="width: 10%;">状态</th>
          <th style="width: 20%;">预算</th>
          <th style="width: 35%;">技能要求</th>
          <th style="width: 15%;">创建时间</th>
          <th style="width: 15%;">操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="demand in paginatedDemands" :key="demand.id">
          <td>{{ demand.title }}</td>
          <td>
              <span
                  class="status-badge"
                  :class="{
                  published: demand.status === 'PUBLISHED',
                  draft: demand.status === 'DRAFT',
                  completed: demand.status === 'COMPLETED',
                }"
              >
                {{ demand.status === 'PUBLISHED'
                  ? '已发布'
                  : demand.status === 'DRAFT'
                      ? '草稿'
                      : '已完成' }}
              </span>
          </td>
          <td>￥{{ demand.budget.toFixed(2) }}</td>
          <td>
            <div>
                <span
                    v-for="skill in demand.skills"
                    :key="skill"
                    class="skill-tag"
                >
                  {{ skill }}
                </span>
            </div>
          </td>
          <td>{{ formatDate(demand.createdAt) }}</td>
          <td class="action-buttons">
            <!-- "查看"按钮 -->
            <button
                class="action-btn"
                @click="viewDetail(demand, true)"
            >
              查看
            </button>
          </td>
        </tr>
        </tbody>
      </table>

      <!-- 分页控制 -->
      <div class="pagination">
        <button :disabled="pagination.currentPage === 1" @click="changePage(-1)">
          上一页
        </button>
        <span>
          第 {{ pagination.currentPage }} 页 / 共 {{ totalPages }} 页
        </span>
        <button :disabled="pagination.currentPage === totalPages" @click="changePage(1)">
          下一页
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { ElMessage } from "element-plus";
import axiosInstance from "@/http/axios.js";
import { useDemandStore } from "@/stores/demand/demandStore.ts"; // 引入 Pinia Store
import { useUserStore } from "@/stores/user/userStore.ts";

const userStore = useUserStore(); // 获取用户 Store 实例
const demandStore = useDemandStore(); // 获取 Store 实例

const isEnterpriseUser = computed(() => userStore.userType === "COMPANY");

// 数据存储
const demands = ref([]); // 全部需求数据
const skills = ref([]); // 技能标签数据
const selectedSkills = ref([]); // 已选技能标签

// 分页状态
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
});

// 技能标签分页状态
const skillsPagination = reactive({
  currentPage: 1,
  pageSize: 5,
});

// 跳转到管理需求界面
const goToManageDemands = () => {
  window.location.href = "/user/manage-my-requirements"; // 跳转到管理需求界面
};

// 分页计算
const paginatedDemands = computed(() => {
  const start = (pagination.currentPage - 1) * pagination.pageSize;
  const end = start + pagination.pageSize;
  return demands.value.slice(start, end);
});

const paginatedSkills = computed(() => {
  const start = (skillsPagination.currentPage - 1) * skillsPagination.pageSize;
  const end = start + skillsPagination.pageSize;
  return skills.value.slice(start, end);
});

// 总页数计算
const totalPages = computed(() =>
    Math.ceil(demands.value.length / pagination.pageSize)
);
const skillsTotalPages = computed(() =>
    Math.ceil(skills.value.length / skillsPagination.pageSize)
);

// 查看需求详情
const viewDetail = (demand, openInNewTab = false) => {
  demandStore.setSelectedDemand(demand);
  const url = `/demand-detail/${demand.id}`; // 根据需求 ID 拼接详情页 URL
  if (openInNewTab) {
    window.open(url, "_blank"); // 新标签页打开
  } else {
    window.location.href = url; // 当前页面跳转
  }
};

// 分页切换
const changePage = (direction) => {
  pagination.currentPage += direction;
};

const changeSkillsPage = (direction) => {
  skillsPagination.currentPage += direction;
};

// 获取推荐需求
const fetchRecommendedDemands = async () => {
  try {
    const response = await axiosInstance.get("/api/requirements/recommend");
    if (response.data.success) {
      demands.value = response.data.data;
      demandStore.setDemands(response.data.data); // 存储到 Pinia
    } else {
      ElMessage.error("获取推荐需求失败");
    }
  } catch (error) {
    console.error("获取推荐需求失败", error);
    ElMessage.error("获取推荐需求失败，请稍后重试");
  }
};

// 根据技能筛选需求
const fetchBySkills = async () => {
  try {
    const response = await axiosInstance.post(
        "/api/requirements/by-skills",
        selectedSkills.value
    );
    if (response.data.success) {
      demands.value = response.data.data;
      demandStore.setDemands(response.data.data); // 存储到 Pinia
      pagination.currentPage = 1; // 重置分页
    } else {
      ElMessage.error("获取分类需求失败");
    }
  } catch (error) {
    console.error("获取分类需求失败", error);
    ElMessage.error("获取分类需求失败，请稍后重试");
  }
};

// 加载技能标签
const fetchSkills = async () => {
  try {
    const response = await axiosInstance.get("/api/admin/getSkills");
    if (response.data.success) {
      skills.value = response.data.data;
    } else {
      ElMessage.error("获取技能标签失败");
    }
  } catch (error) {
    console.error("获取技能标签失败", error);
    ElMessage.error("获取技能标签失败，请稍后重试");
  }
};

// 辅助函数：格式化日期
const formatDate = (dateString) => {
  if (!dateString) return "无";
  const options = {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
  };
  return new Intl.DateTimeFormat("zh-CN", options).format(new Date(dateString));
};

// 初始化加载
onMounted(() => {
  fetchSkills();
  fetchRecommendedDemands();
});
</script>
<style scoped>
.demand-page {
  display: flex;
}

.action-btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 4px 16px;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 8px;
  min-width: 80px;
  text-align: center;
  font-size: 14px;
  line-height: 1.5;
  display: inline-block;
}

.action-btn:hover {
  background-color: #0056b3;
}

.action-buttons {
  display: flex;
  justify-content: space-evenly;
  align-items: center;
}

.sidebar {
  width: 25%; /* 左侧导航栏占 25% */
  padding: 20px;
  background-color: #f8f9fa;
  border-right: 1px solid #ddd;
}

.recommend-button {
  margin-bottom: 20px; /* 与其他内容留出一定距离 */
}

.recommend-btn {
  width: 100%;
  padding: 10px 15px;
  font-size: 16px;
  font-weight: bold;
  color: white;
  background-color: #007bff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease; /* 添加平滑过渡效果 */
}

.recommend-btn:hover {
  background-color: #0056b3; /* 鼠标悬浮时背景变深 */
  border: 1px solid #0056b3; /* 鼠标悬浮时添加边框 */
}
.skills-pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.skills-pagination button {
  padding: 5px 10px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.skills-pagination button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.content {
  width: 75%; /* 主内容占 75% */
  padding: 20px;
}

.demand-table {
  width: 100%;
  margin: 0 auto;
  border-collapse: collapse;
  margin-top: 20px;
  font-size: 16px;
}

.demand-table th,
.demand-table td {
  border: 1px solid #ddd;
  padding: 12px 16px;
  text-align: left;
}

.demand-table th {
  background-color: #f4f4f4;
  font-weight: bold;
}

.demand-table tr:hover {
  background-color: #f9f9f9;
}

.skill-tag {
  display: inline-block;
  background-color: #007bff;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  margin-right: 8px;
  font-size: 14px;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: white;
}

.status-badge.published {
  background-color: #28a745;
}

.status-badge.draft {
  background-color: #6c757d;
}

.status-badge.completed {
  background-color: #007bff;
}

.action-btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 4px 16px; /* 统一按钮内边距 */
  border-radius: 4px;
  cursor: pointer;
  margin-right: 8px; /* 按钮间距 */
  min-width: 80px; /* 设置按钮最小宽度 */
  text-align: center; /* 按钮文字居中 */
  font-size: 14px; /* 按钮文字大小 */
  line-height: 1.5; /* 统一行高 */
  display: inline-block; /* 确保按钮是块状布局 */
  height: auto; /* 避免高度冲突 */
}

.action-buttons {
  display: flex; /* 按钮水平排列 */
  justify-content: space-evenly;
  align-items: center; /* 按钮垂直居中 */
}

.action-btn:hover {
  background-color: #0056b3;
}
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 16px;
}

.pagination button {
  margin: 0 8px;
  padding: 8px 12px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.pagination span {
  font-size: 14px;
}
</style>