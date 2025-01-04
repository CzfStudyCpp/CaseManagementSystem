<!--<template>-->
<!--  <div>-->
<!--    <h1>审核邮箱更换请求</h1>-->

<!--    &lt;!&ndash; 邮箱更换请求分页浏览 &ndash;&gt;-->
<!--    <table class="email-change-table">-->
<!--      <thead>-->
<!--      <tr>-->
<!--        <th>用户名</th>-->
<!--        <th>当前邮箱</th>-->
<!--        <th>申请更换邮箱</th>-->
<!--        <th>操作</th>-->
<!--      </tr>-->
<!--      </thead>-->
<!--      <tbody>-->
<!--      <tr-->
<!--          v-for="request in paginatedRequests"-->
<!--          :key="request.id"-->
<!--      >-->
<!--        <td>{{ request.username }}</td>-->
<!--        <td>{{ request.currentEmail }}</td>-->
<!--        <td>{{ request.targetEmail }}</td>-->
<!--        <td>-->
<!--          <button class="approve-btn" @click="handleEmailChange(request, 'APPROVE')">-->
<!--            同意-->
<!--          </button>-->
<!--          <button class="reject-btn" @click="handleEmailChange(request, 'REJECT')">-->
<!--            拒绝-->
<!--          </button>-->
<!--        </td>-->
<!--      </tr>-->
<!--      </tbody>-->
<!--    </table>-->

<!--    &lt;!&ndash; 分页控制 &ndash;&gt;-->
<!--    <div class="pagination">-->
<!--      <button :disabled="pagination.currentPage === 1" @click="changePage(-1)">-->
<!--        上一页-->
<!--      </button>-->
<!--      <span>第 {{ pagination.currentPage }} 页 / 共 {{ totalPages }} 页</span>-->
<!--      <button :disabled="pagination.currentPage === totalPages" @click="changePage(1)">-->
<!--        下一页-->
<!--      </button>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->

<!--<script setup>-->
<!--import { ref, reactive, computed, onMounted } from "vue";-->
<!--import axiosInstance from "@/http/axios.js"; // 确保 axiosInstance 配置了后端拦截器-->

<!--// 邮箱更换请求列表与分页状态-->
<!--const requests = ref([]);-->
<!--const pagination = reactive({-->
<!--  currentPage: 1,-->
<!--  pageSize: 10,-->
<!--});-->

<!--// 当前页的请求-->
<!--const paginatedRequests = computed(() => {-->
<!--  const start = (pagination.currentPage - 1) * pagination.pageSize;-->
<!--  const end = start + pagination.pageSize;-->
<!--  return requests.value.slice(start, end);-->
<!--});-->

<!--// 计算总页数-->
<!--const totalPages = computed(() => Math.ceil(requests.value.length / pagination.pageSize));-->

<!--// 获取邮箱更换请求列表-->
<!--const fetchRequests = async () => {-->
<!--  try {-->
<!--    const response = await axiosInstance.get("/api/admin/getEmailChangeRequests");-->
<!--    if (response.data && response.data.success) {-->
<!--      requests.value = response.data.data.requests || [];-->
<!--    } else {-->
<!--      console.error("获取邮箱更换请求失败：", response.data.message);-->
<!--      alert(response.data.message || "获取邮箱更换请求失败，请稍后重试！");-->
<!--    }-->
<!--  } catch (error) {-->
<!--    console.error("获取邮箱更换请求失败：", error.response?.data?.message || error.message);-->
<!--    alert("加载邮箱更换请求失败，请检查网络或稍后重试！");-->
<!--  }-->
<!--};-->

<!--// 处理邮箱更换请求（同意或拒绝）-->
<!--const handleEmailChange = async (request, action) => {-->
<!--  try {-->
<!--    const response = await axiosInstance.post("/api/admin/handleEmailChange", {-->
<!--      username: request.username,-->
<!--      currentEmail: request.currentEmail,-->
<!--      targetEmail: request.targetEmail,-->
<!--      action,-->
<!--    });-->

<!--    if (response.data.success) {-->
<!--      alert(`操作成功：${action === "APPROVE" ? "同意" : "拒绝"}邮箱更换请求`);-->
<!--      // 从本地列表移除已处理的请求-->
<!--      const requestIndex = requests.value.findIndex((r) => r.id === request.id);-->
<!--      if (requestIndex !== -1) {-->
<!--        requests.value.splice(requestIndex, 1);-->
<!--      }-->
<!--    } else {-->
<!--      alert(`操作失败：${response.data.message}`);-->
<!--    }-->
<!--  } catch (error) {-->
<!--    console.error("处理邮箱更换请求失败：", error.response?.data?.message || error.message);-->
<!--    alert("处理邮箱更换请求失败，请稍后重试！");-->
<!--  }-->
<!--};-->

<!--// 分页控制-->
<!--const changePage = (direction) => {-->
<!--  pagination.currentPage += direction;-->
<!--};-->

<!--// 初始化加载邮箱更换请求列表-->
<!--onMounted(() => {-->
<!--  fetchRequests();-->
<!--});-->
<!--</script>-->

<!--<style scoped>-->
<!--/* 表格样式 */-->
<!--.email-change-table {-->
<!--  width: 100%;-->
<!--  border-collapse: collapse;-->
<!--  margin: 16px 0;-->
<!--}-->

<!--.email-change-table th,-->
<!--.email-change-table td {-->
<!--  border: 1px solid #ddd;-->
<!--  padding: 12px 16px;-->
<!--  text-align: left;-->
<!--}-->

<!--.email-change-table th {-->
<!--  background-color: #f4f4f4;-->
<!--  font-weight: bold;-->
<!--}-->

<!--.approve-btn {-->
<!--  background-color: #28a745;-->
<!--  color: white;-->
<!--  border: none;-->
<!--  padding: 8px 12px;-->
<!--  border-radius: 4px;-->
<!--  cursor: pointer;-->
<!--  margin-right: 8px;-->
<!--}-->

<!--.reject-btn {-->
<!--  background-color: #dc3545;-->
<!--  color: white;-->
<!--  border: none;-->
<!--  padding: 8px 12px;-->
<!--  border-radius: 4px;-->
<!--  cursor: pointer;-->
<!--}-->

<!--.approve-btn:hover,-->
<!--.reject-btn:hover {-->
<!--  opacity: 0.9;-->
<!--}-->

<!--/* 分页样式 */-->
<!--.pagination {-->
<!--  display: flex;-->
<!--  justify-content: center;-->
<!--  align-items: center;-->
<!--  margin-top: 16px;-->
<!--}-->

<!--.pagination button {-->
<!--  margin: 0 8px;-->
<!--  padding: 8px 12px;-->
<!--  background-color: #007bff;-->
<!--  color: white;-->
<!--  border: none;-->
<!--  border-radius: 4px;-->
<!--  cursor: pointer;-->
<!--}-->

<!--.pagination button:disabled {-->
<!--  background-color: #ccc;-->
<!--  cursor: not-allowed;-->
<!--}-->

<!--.pagination span {-->
<!--  font-size: 14px;-->
<!--}-->
<!--</style>-->


<template>
  <div>
    <h1>审核邮箱更换请求</h1>

    <!-- 邮箱更换请求分页浏览 -->
    <table class="email-change-table">
      <thead>
      <tr>
        <th>用户名</th>
        <th>当前邮箱</th>
        <th>申请更换邮箱</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <tr
          v-for="request in requests"
          :key="request.id"
      >
        <td>{{ request.username }}</td>
        <td>{{ request.currentEmail }}</td>
        <td>{{ request.newEmail }}</td>
        <td>
          <button class="approve-btn" @click="handleEmailChange(request, 'APPROVE')">
            同意
          </button>
          <button class="reject-btn" @click="handleEmailChange(request, 'REJECT')">
            拒绝
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
      <span>第 {{ pagination.currentPage }} 页 / 共 {{ pagination.totalPages }} 页</span>
      <button :disabled="pagination.currentPage === pagination.totalPages" @click="changePage(1)">
        下一页
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import axiosInstance from "@/http/axios.js"; // 确保 axiosInstance 配置了后端拦截器

// 邮箱更换请求列表与分页状态
const requests = ref([]);
const pagination = reactive({
  currentPage: 1,
  totalPages: 0,
  pageSize: 10,
  totalRequests: 0,
});

// 获取邮箱更换请求列表
const fetchRequests = async () => {
  try {
    const response = await axiosInstance.get("/api/admin/getEmailChangeRequests", {
      params: {
        page: pagination.currentPage,
        size: pagination.pageSize,
      },
    });

    if (response.data && response.data.success) {
      const { requests: fetchedRequests, currentPage, totalPages, totalRequests } = response.data.data;

      // 更新请求列表和分页数据
      requests.value = fetchedRequests || [];
      pagination.currentPage = currentPage;
      pagination.totalPages = totalPages;
      pagination.totalRequests = totalRequests;
    } else {
      console.error("获取邮箱更换请求失败：", response.data.message);
      alert(response.data.message || "获取邮箱更换请求失败，请稍后重试！");
    }
  } catch (error) {
    console.error("获取邮箱更换请求失败：", error.response?.data?.message || error.message);
    alert("加载邮箱更换请求失败，请检查网络或稍后重试！");
  }
};

// 处理邮箱更换请求（同意或拒绝）
const handleEmailChange = async (request, action) => {
  try {
    const response = await axiosInstance.post("/api/admin/handleEmailChange", {
      username: request.username,
      currentEmail: request.currentEmail,
      targetEmail: request.newEmail,
      action,
    });

    if (response.data.success) {
      alert(`操作成功：${action === "APPROVE" ? "同意" : "拒绝"}邮箱更换请求`);
      // 从本地列表移除已处理的请求
      const requestIndex = requests.value.findIndex((r) => r.id === request.id);
      if (requestIndex !== -1) {
        requests.value.splice(requestIndex, 1);

        // 如果移除后列表为空，尝试刷新当前页数据
        if (requests.value.length === 0 && pagination.currentPage > 1) {
          changePage(-1);
        }
      }
    } else {
      alert(`操作失败：${response.data.message}`);
    }
  } catch (error) {
    console.error("处理邮箱更换请求失败：", error.response?.data?.message || error.message);
    alert("处理邮箱更换请求失败，请稍后重试！");
  }
};

// 分页控制
const changePage = async (direction) => {
  if (
      (direction === -1 && pagination.currentPage > 1) ||
      (direction === 1 && pagination.currentPage < pagination.totalPages)
  ) {
    pagination.currentPage += direction;
    await fetchRequests(); // 切换页码时重新加载数据
  }
};

// 初始化加载邮箱更换请求列表
onMounted(() => {
  fetchRequests();
});
</script>

<style scoped>
/* 表格样式 */
.email-change-table {
  width: 100%;
  border-collapse: collapse;
  margin: 16px 0;
}

.email-change-table th,
.email-change-table td {
  border: 1px solid #ddd;
  padding: 12px 16px;
  text-align: left;
}

.email-change-table th {
  background-color: #f4f4f4;
  font-weight: bold;
}

.approve-btn {
  background-color: #28a745;
  color: white;
  border: none;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 8px;
}

.reject-btn {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
}

.approve-btn:hover,
.reject-btn:hover {
  opacity: 0.9;
}

/* 分页样式 */
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
