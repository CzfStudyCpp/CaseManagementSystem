
<template>
  <div>
    <h1>封禁账号管理</h1>

    <!-- 用户分页浏览 -->
    <table class="user-table">
      <thead>
      <tr>
        <th>用户ID</th>
        <th>用户名</th>
        <th>邮箱</th>
        <th>账号类型</th>
        <th>状态</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="user in paginatedUsers" :key="user.id">
        <td>{{ user.id }}</td>
        <td>{{ user.username }}</td>
        <td>{{ user.email }}</td>
        <td>{{ user.userType }}</td>
        <td>{{ user.status }}</td>
        <td>
          <button class="delete-btn" @click="deleteUser(user.id)">删除</button>
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
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import axiosInstance from "@/http/axios.js"; // 确保 axiosInstance 配置了后端拦截器

// 封禁用户列表与分页状态
const bannedUsers = ref([]); // 存储被封禁的用户
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
});

// 当前页的用户
const paginatedUsers = computed(() => {
  const start = (pagination.currentPage - 1) * pagination.pageSize;
  const end = start + pagination.pageSize;
  return bannedUsers.value.slice(start, end);
});

// 计算总页数
const totalPages = computed(() => Math.ceil(bannedUsers.value.length / pagination.pageSize));

const fetchBannedUsers = async () => {
  try {
    // 传递分页参数
    const response = await axiosInstance.get("/api/admin/getBannedUser", {
      params: {
        page: pagination.currentPage, // 当前页
        size: pagination.pageSize, // 每页大小
      },
    });
    if (response.data && response.data.success) {
      // 确保前端正确解构后端返回的数据
      const data = response.data.data;
      bannedUsers.value = data.users || []; // 用户列表
      pagination.currentPage = data.currentPage; // 当前页码
      pagination.totalPages = data.totalPages; // 总页数
    } else {
      console.error("获取被封禁用户失败：", response.data.message);
      alert(response.data.message || "获取被封禁用户失败，请稍后重试！");
    }
  } catch (error) {
    console.error("获取被封禁用户失败：", error.response?.data?.message || error.message);
    alert("加载被封禁用户失败，请检查网络或稍后重试！");
  }
};


// 删除用户操作
const deleteUser = async (userId) => {
  if (!confirm("确定要删除该用户吗？此操作不可撤销！")) return;

  try {
    const response = await axiosInstance.delete("/api/admin/deleteUser", {
      data: { userId }, // 将 userId 作为请求体传递
    });

    if (response.data && response.data.success) {
      alert("用户删除成功");
      // 从本地列表中移除已删除的用户
      const userIndex = bannedUsers.value.findIndex((user) => user.id === userId);
      if (userIndex !== -1) {
        bannedUsers.value.splice(userIndex, 1);

        // 如果移除后列表为空，尝试刷新当前页数据
        if (bannedUsers.value.length === 0 && pagination.currentPage > 1) {
          changePage(-1);
        }
      }
    } else {
      alert(`删除用户失败：${response.data.message}`);
    }
  } catch (error) {
    console.error("删除用户失败：", error.response?.data?.message || error.message);
    alert("删除用户失败，请稍后重试！");
  }
};


// 分页控制
const changePage = (direction) => {
  pagination.currentPage += direction;
};

// 初始化加载被封禁用户列表
onMounted(() => {
  fetchBannedUsers();
});
</script>

<style scoped>
/* 表格样式 */
.user-table {
  width: 100%;
  border-collapse: collapse;
  margin: 16px 0;
}

.user-table th,
.user-table td {
  border: 1px solid #ddd;
  padding: 12px 16px;
  text-align: left;
}

.user-table th {
  background-color: #f4f4f4;
  font-weight: bold;
}

.user-row:hover {
  background-color: #f9f9f9;
  cursor: pointer;
}

/* 删除按钮样式 */
.delete-btn {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
}

.delete-btn:hover {
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
