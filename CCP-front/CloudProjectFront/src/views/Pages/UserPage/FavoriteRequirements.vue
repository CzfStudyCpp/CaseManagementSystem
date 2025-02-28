<template>
  <div class="demand-page">
    <h2>我关注的需求</h2>

    <!-- 如果没有关注的需求 -->
    <div v-if="favoriteRequirements.length === 0" class="empty-container">
      <p>暂无关注的需求</p>
    </div>

    <!-- 需求表格 -->
    <div v-else>
      <table class="demand-table">
        <thead>
        <tr>
          <th style="width: 15%;">需求标题</th>
          <th style="width: 10%;">状态</th>
          <th style="width: 20%;">预算</th>
          <th style="width: 35%;">技能要求</th>
          <th style="width: 15%;">关注时间</th>
          <th style="width: 25%;">操作</th>
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
                  {{
                                 demand.status === "PUBLISHED"
                                     ? "已发布"
                                     : demand.status === "DRAFT"
                                         ? "草稿"
                                         : "已完成"
                               }}
                </span>
          </td>
          <td>￥{{ demand.budget.toFixed(2) }}</td>
          <td>
            <div>
                <span v-for="skill in demand.skills" :key="skill" class="skill-tag">
                  {{ skill }}
                </span>
            </div>
          </td>
          <td>{{ formatDate(demand.createdAt) }}</td>
          <td class="action-buttons">
            <button class="action-btn view-btn" @click="viewDetail(demand, true)">查看</button>
            <button class="action-btn unfollow-btn" @click="unfollowDemand(demand)">取消关注</button>
          </td>
        </tr>
        </tbody>
      </table>

      <!-- 分页控制 -->
      <div class="pagination">
        <button :disabled="pagination.currentPage === 1" @click="changePage(-1)">
          上一页
        </button>
        <span>第 {{ pagination.currentPage }} 页 / 共 {{ totalPages }} 页</span>
        <button :disabled="pagination.currentPage === totalPages" @click="changePage(1)">
          下一页
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import axiosInstance from "@/http/axios.js";

// 数据存储
const favoriteRequirements = ref([]); // 用户关注的需求

// 分页状态
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
});

// 分页计算
const paginatedDemands = computed(() => {
  const start = (pagination.value.currentPage - 1) * pagination.value.pageSize;
  const end = start + pagination.value.pageSize;
  return favoriteRequirements.value.slice(start, end);
});

// 总页数计算
const totalPages = computed(() =>
    Math.ceil(favoriteRequirements.value.length / pagination.value.pageSize)
);

// 加载用户关注的需求
const fetchFavoriteRequirements = async () => {
  try {
    const response = await axiosInstance.get("/api/requirements/favorite-requirements");
    if (response.data.success) {
      favoriteRequirements.value = response.data.data;
    } else {
      alert("获取关注的需求失败：" + response.data.message);
    }
  } catch (error) {
    console.error("获取关注的需求失败：", error);
    alert("获取关注的需求失败，请稍后重试！");
  }
};

// 查看需求详情
const viewDetail = (demand, openInNewTab = false) => {
  const url = `/demand-detail/${demand.id}`; // 根据需求 ID 拼接详情页 URL
  if (openInNewTab) {
    window.open(url, "_blank"); // 新标签页打开
  } else {
    window.location.href = url; // 当前页面跳转
  }
};

// 取消关注需求
const unfollowDemand = async (demand) => {
  try {
    const response = await axiosInstance.post("/api/requirements/toggleFollow", {
      demandId: demand.id,
      action: "unfollow",
    });
    if (response.data.success) {
      favoriteRequirements.value = favoriteRequirements.value.filter(
          (item) => item.id !== demand.id
      );
      alert("取消关注成功");
    } else {
      alert("取消关注失败：" + response.data.message);
    }
  } catch (error) {
    console.error("取消关注失败：", error);
    alert("取消关注失败，请稍后重试！");
  }
};

// 分页切换
const changePage = (direction) => {
  pagination.value.currentPage += direction;
};

// 日期格式化函数
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
  fetchFavoriteRequirements();
});
</script>

<style scoped>
/* 样式统一 */
.demand-page {
  padding: 20px;
}

.demand-table {
  width: 100%;
  margin: 20px 0;
  border-collapse: collapse;
}

.demand-table th,
.demand-table td {
  border: 1px solid #ddd;
  padding: 8px 12px;
  text-align: left;
}

.demand-table th {
  background-color: #f9f9f9;
  font-weight: bold;
}

.demand-table tr:hover {
  background-color: #f5f5f5;
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
  background-color: #007bff; /* 蓝色背景表示已完成 */
}


.action-buttons {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
}

.action-btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
  width: 90px; /* 统一宽度 */
  text-align: center;
}

.action-btn.view-btn {
  background-color: #28a745;
}

.action-btn.unfollow-btn {
  background-color: #dc3545;
}

.action-btn:hover {
  opacity: 0.9;
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

.empty-container {
  text-align: center;
  color: #666;
  margin-top: 50px;
}
</style>
