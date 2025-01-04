
<template>
  <div class="admin-management">
    <h1>管理员管理</h1>

    <!-- 添加管理员 -->
    <form @submit.prevent="addAdmin" class="add-admin-form">
      <h3>添加管理员</h3>
      <div class="form-group">
        <label for="adminUsername">用户名</label>
        <input
            type="text"
            id="adminUsername"
            v-model="newAdmin.username"
            placeholder="请输入管理员用户名"
            required
        />
      </div>
      <div class="form-group">
        <label for="adminEmail">邮箱</label>
        <input
            type="email"
            id="adminEmail"
            v-model="newAdmin.email"
            placeholder="请输入管理员邮箱（可选）"
        />
      </div>
      <div class="form-group">
        <label for="adminPassword">密码</label>
        <input
            type="password"
            id="adminPassword"
            v-model="newAdmin.password"
            placeholder="请输入密码"
            required
        />
      </div>
      <div class="form-group">
        <label for="adminRole">管理员级别</label>
        <select id="adminRole" v-model="newAdmin.role" required>
          <option value="SUPER_ADMIN">超级管理员</option>
          <option value="NORMAL_ADMIN">普通管理员</option>
        </select>
      </div>
      <div class="form-group">
        <label for="adminStatus">账号状态</label>
        <select id="adminStatus" v-model="newAdmin.status" required>
          <option value="ACTIVE">活跃</option>
          <option value="INACTIVE">未激活</option>
        </select>
      </div>
      <button type="submit" class="add-button">添加管理员</button>
    </form>

    <!-- 管理员列表 -->
    <div class="admin-list">
      <h3>管理员列表</h3>
      <table class="admin-table">
        <thead>
        <tr>
          <th>用户名</th>
          <th>邮箱</th>
          <th>账号级别</th>
          <th>状态</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="admin in paginatedAdmins" :key="admin.id">
          <td>{{ admin.username }}</td>
          <td>{{ admin.email || "无" }}</td>
          <td>{{ admin.role === "SUPER_ADMIN" ? "超级管理员" : "普通管理员" }}</td>
          <td>{{ admin.status === "ACTIVE" ? "活跃" : "未激活" }}</td>
          <td>
            <button class="delete-button" @click="deleteAdmin(admin.id)">删除</button>
          </td>
        </tr>
        </tbody>
      </table>
    </div>

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
import { ref, computed, onMounted } from "vue";
import axiosInstance from "@/http/axios.js"; // 确保配置了后端拦截器的 axios 实例

// 管理员列表和分页
const admins = ref([]);
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
});

// 新管理员表单
const newAdmin = ref({
  username: "",
  email: "",
  password: "", // 添加密码字段
  role: "NORMAL_ADMIN",
  status: "ACTIVE",
});

// 获取管理员列表
const fetchAdmins = async () => {
  try {
    const response = await axiosInstance.get("/api/admin/getAdmins", {
      params: {
        page: pagination.value.currentPage,
        size: pagination.value.pageSize,
      },
    });
    if (response.data && response.data.success) {
      admins.value = response.data.data.admins || [];
    } else {
      alert(response.data.message || "获取管理员列表失败，请稍后重试！");
    }
  } catch (error) {
    console.error("获取管理员列表失败：", error.message);
    alert("加载管理员列表失败，请稍后重试！");
  }
};

// 添加管理员
const addAdmin = async () => {
  try {
    await axiosInstance.post("/api/admin/addAdmin", newAdmin.value);
    alert("管理员添加成功！");
    newAdmin.value = {
      username: "",
      email: "",
      password: "",
      role: "NORMAL_ADMIN",
      status: "ACTIVE",
    }; // 重置表单
    fetchAdmins();
  } catch (error) {
    console.error("添加管理员失败：", error.message);
    alert("添加管理员失败，请稍后重试！");
  }
};

// 删除管理员
const deleteAdmin = async (adminId) => {
  if (!confirm("确定要删除该管理员吗？")) return;
  try {
    await axiosInstance.delete("/api/admin/deleteAdmin", { data: { adminId } });
    alert("管理员删除成功！");
    fetchAdmins();
  } catch (error) {
    console.error("删除管理员失败：", error.message);
    alert("删除管理员失败，请稍后重试！");
  }
};

// 分页逻辑
const paginatedAdmins = computed(() => {
  const start = (pagination.value.currentPage - 1) * pagination.value.pageSize;
  const end = start + pagination.value.pageSize;
  return admins.value.slice(start, end);
});
const totalPages = computed(() => Math.ceil(admins.value.length / pagination.value.pageSize));
const changePage = (direction) => {
  pagination.value.currentPage += direction;
  fetchAdmins();
};

// 初始化
onMounted(fetchAdmins);
</script>

<style scoped>
/* 表格样式 */
.admin-table {
  width: 100%;
  border-collapse: collapse;
  margin: 16px 0;
}

.admin-table th,
.admin-table td {
  border: 1px solid #ddd;
  padding: 12px 16px;
  text-align: left;
}

.admin-table th {
  background-color: #f4f4f4;
  font-weight: bold;
}

/* 删除按钮样式 */
.delete-button {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
}

.delete-button:hover {
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
