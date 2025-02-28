
<template>
  <div>
    <h1>管理员用户管理</h1>

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
      <tr
          v-for="user in paginatedUsers"
          :key="user.id"
          @click="openUserDetails(user)"
          class="user-row"
      >
        <td>{{ user.id }}</td>
        <td>{{ user.username }}</td>
        <td>{{ user.email }}</td>
        <td>{{ user.userType }}</td>
        <td>{{ user.status }}</td>
        <td>
          <button
              v-if="user.status === 'ACTIVE'"
              @click.stop="toggleUserStatus(user, 'SUSPENDED')"
              class="btn btn-warning"
          >
            封禁
          </button>
          <button
              v-else
              @click.stop="toggleUserStatus(user, 'ACTIVE')"
              class="btn btn-success"
          >
            解禁
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
      <span>第 {{ pagination.currentPage }} 页 / 共 {{ totalPages }} 页</span>
      <button
          :disabled="pagination.currentPage === totalPages"
          @click="changePage(1)"
      >
        下一页
      </button>
    </div>

    <!-- 弹窗：用户详细信息与编辑 -->
    <div v-if="isUserDetailsVisible" class="modal-overlay" @click="resetAndCloseModal">
      <div class="modal-content" @click.stop>
        <h2>用户详细信息</h2>

        <div v-if="selectedUser">
          <form @submit.prevent="saveUserInfo">
            <!-- 用户基本信息 -->
            <h3>基本信息</h3>
            <div class="form-group">
              <label for="username">用户名</label>
              <input
                  type="text"
                  id="username"
                  v-model="editableUser.username"
                  placeholder="用户名"
                  @input="checkUsername"
              />
              <p v-if="usernameError" class="error-text">{{ usernameError }}</p>
            </div>
            <div class="form-group">
              <label for="phone">电话</label>
              <input
                  type="text"
                  id="phone"
                  v-model="editableUser.phone"
                  placeholder="电话"
              />
            </div>

            <!-- 开发者信息 -->
            <div v-if="selectedUser.userType === 'DEVELOPER'" class="developer-info">
              <h3>开发者信息</h3>
              <div class="form-group">
                <label for="realName">真实姓名</label>
                <input
                    type="text"
                    v-model="editableDeveloperProfile.realName"
                    placeholder="真实姓名"
                />
              </div>
              <div class="form-group">
                <label for="github">GitHub 账号</label>
                <input
                    type="text"
                    v-model="editableDeveloperProfile.github"
                    placeholder="GitHub账号"
                />
              </div>
              <div class="form-group">
                <label for="portfolio">作品集链接</label>
                <input
                    type="text"
                    v-model="editableDeveloperProfile.portfolio"
                    placeholder="作品集链接"
                />
              </div>
              <div class="form-group">
                <label for="experience">工作经验</label>
                <input
                    type="text"
                    v-model="editableDeveloperProfile.experience"
                    placeholder="工作经验"
                />
              </div>
            </div>

            <!-- 企业信息 -->
            <div v-if="selectedUser.userType === 'COMPANY'" class="company-info">
              <h3>企业信息</h3>
              <div class="form-group">
                <label for="companyName">公司名称</label>
                <input
                    type="text"
                    v-model="editableCompanyProfile.companyName"
                    placeholder="公司名称"
                />
              </div>
              <div class="form-group">
                <label for="industry">行业</label>
                <input
                    type="text"
                    v-model="editableCompanyProfile.industry"
                    placeholder="行业"
                />
              </div>
              <div class="form-group">
                <label for="size">公司规模</label>
                <input
                    type="text"
                    v-model="editableCompanyProfile.size"
                    placeholder="公司规模"
                />
              </div>
              <div class="form-group">
                <label for="address">地址</label>
                <input
                    type="text"
                    v-model="editableCompanyProfile.address"
                    placeholder="公司地址"
                />
              </div>
              <div class="form-group">
                <label for="contactPerson">联系人</label>
                <input
                    type="text"
                    v-model="editableCompanyProfile.contactPerson"
                    placeholder="联系人"
                />
              </div>
              <div class="form-group">
                <label for="contactPhone">联系电话</label>
                <input
                    type="text"
                    v-model="editableCompanyProfile.contactPhone"
                    placeholder="联系电话"
                />
              </div>
            </div>

            <!-- 按钮 -->
            <div class="form-actions">
              <button type="button" class="btn btn-primary" @click="resetAndCloseModal">
                取消
              </button>
              <button type="submit" class="btn btn-submit">保存</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import axiosInstance from "@/http/axios.js";

// 用户列表与分页状态
const users = ref([]);
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
});

// 编辑中的用户信息
const editableUser = reactive({});
const editableDeveloperProfile = reactive({});
const editableCompanyProfile = reactive({});
const usernameError = ref("");

// 当前用户编辑弹窗状态
const isUserDetailsVisible = ref(false);
const selectedUser = ref(null);

// 计算总页数
const totalPages = computed(() => Math.ceil(users.value.length / pagination.pageSize));

// 当前页的用户
const paginatedUsers = computed(() => {
  const start = (pagination.currentPage - 1) * pagination.pageSize;
  const end = start + pagination.pageSize;
  return users.value.slice(start, end);
});

// 获取用户列表
const fetchUsers = async () => {
  try {
    const response = await axiosInstance.get("/api/admin/getUserList", {
      params: {
        page: pagination.currentPage,
        size: pagination.pageSize,
      },
    });
    if (response.data && response.data.success) {
      users.value = response.data.data.users || [];
    } else {
      alert("获取用户列表失败，请稍后重试！");
    }
  } catch {
    alert("加载用户列表失败，请检查网络或稍后重试！");
  }
};

// 验证用户名
const checkUsername = async () => {
  const username = editableUser.username;

  if (!username) {
    usernameError.value = "用户名不能为空";
    return;
  }

  if (username.includes("@")) {
    usernameError.value = "用户名不能包含 @ 符号";
    return;
  }

  try {
    await axiosInstance.get(`/api/register/check-username?username=${username}`);
    usernameError.value = ""; // 清空错误信息
  } catch (error) {
    if (error.response && error.response.status === 400) {
      usernameError.value = error.response.data.message || "用户名验证失败";
    } else {
      console.error("验证用户名时发生错误：", error);
      usernameError.value = "用户名验证失败，请稍后重试！";
    }
  }
};

const openUserDetails = async (user) => {
  try {
    console.log("尝试打开用户详情弹窗，用户信息：", user);

    // 从最新的用户列表中获取最新的用户数据
    const latestUser = users.value.find((u) => u.id === user.id);

    if (!latestUser) {
      alert("未找到该用户的最新信息，请刷新页面重试！");
      return;
    }

    console.log("从用户列表中获取的最新用户数据：", latestUser);

    // 从后端获取最新的详细信息
    const response = await axiosInstance.get(`/api/admin/userDetails/${latestUser.id}`);
    console.log("后端返回的用户详情接口响应：", response.data);

    if (response.data && response.data.success) {
      const { user: userProfile, developerProfile, companyProfile } = response.data.data;

      console.log("解析后的用户基本信息：", userProfile);
      console.log("解析后的开发者信息：", developerProfile);
      console.log("解析后的公司信息：", companyProfile);

      // 更新弹窗中的数据
      Object.assign(editableUser, userProfile || {});
      Object.assign(editableDeveloperProfile, developerProfile || {});
      Object.assign(editableCompanyProfile, companyProfile || {});

      // 确保 `selectedUser` 同步后端返回的详细信息
      selectedUser.value = {
        ...userProfile,
        developerProfile: developerProfile || null,
        companyProfile: companyProfile || null,
      };

      // 更新本地 `users` 列表中的对应用户
      const userIndex = users.value.findIndex((u) => u.id === userProfile.id);
      if (userIndex !== -1) {
        users.value[userIndex] = {
          ...users.value[userIndex], // 原来的用户信息
          ...userProfile, // 更新基本信息
          developerProfile: developerProfile || null, // 更新开发者信息
          companyProfile: companyProfile || null, // 更新企业信息
        };
      }

      console.log("同步到 selectedUser 的最终数据：", selectedUser.value);
      console.log("更新后的 users 列表：", users.value);

      isUserDetailsVisible.value = true;
    } else {
      alert("加载用户详情失败，请稍后重试！");
    }
  } catch (error) {
    console.error("加载用户详情失败：", error);
    alert("加载用户详情失败，请稍后重试！");
  }
};



const saveUserInfo = async () => {
  try {
    if (usernameError.value) {
      alert("请修正用户名错误后再保存！");
      return;
    }

    const payload = {
      editableUser: { ...editableUser },
      developerProfile: editableDeveloperProfile,
      companyProfile: editableCompanyProfile,
    };

    const response = await axiosInstance.put(`/api/admin/updateUser/${editableUser.id}`, payload);

    if (response.data.success) {
      alert("用户信息保存成功！");
      const updatedUser = response.data.data;

      // 更新本地用户列表
      const userIndex = users.value.findIndex((user) => user.id === updatedUser.userProfile.id);
      if (userIndex !== -1) {
        users.value[userIndex] = {
          ...users.value[userIndex], // 原来的用户信息
          ...updatedUser.userProfile, // 更新基本信息
          developerProfile: updatedUser.developerProfile || null, // 更新开发者信息
          companyProfile: updatedUser.companyProfile || null, // 更新企业信息
        };

        // 同步更新 selectedUser
        selectedUser.value = users.value[userIndex];
      }

      // 同步更新弹窗中的数据
      Object.assign(editableUser, updatedUser.userProfile);
      Object.assign(editableDeveloperProfile, updatedUser.developerProfile || {});
      Object.assign(editableCompanyProfile, updatedUser.companyProfile || {});

      resetAndCloseModal();
    } else {
      console.error("保存失败，后端返回错误：", response.data.message);
      alert(`保存失败：${response.data.message}`);
    }
  } catch (error) {
    console.error("保存失败，捕获到异常：", error);
    alert("保存失败，请稍后重试！");
  }
};
const toggleUserStatus = async (user, newStatus) => {
  try {
    // 调用后端接口
    const response = await axiosInstance.put(`/api/admin/updateUserStatus`, {
      userId: user.id, // 添加用户ID
      status: newStatus, // 状态
    });

    // 打印后端返回数据
    console.log("后端返回数据：", response.data);

    // 检查是否成功
    if (response.data.success) {
      const { userId, status } = response.data.data; // 从返回的数据中获取 userId 和 status
      alert(`用户状态已更新为: ${status === 'ACTIVE' ? '活跃' : '封禁'}`);

      // 更新本地用户列表中的状态
      const userIndex = users.value.findIndex((u) => u.id === userId);
      if (userIndex !== -1) {
        users.value[userIndex].status = status;
      }
    } else {
      // 打印失败信息
      console.error("更新失败，后端返回信息：", response.data);
      alert(`更新用户状态失败: ${response.data.message}`);
    }
  } catch (error) {
    // 捕获错误并打印
    console.error("更新用户状态失败：", error);
    alert("更新用户状态失败，请稍后重试！");
  }
};





const resetAndCloseModal = () => {
  // 确保关闭弹窗时同步数据
  if (selectedUser.value) {
    Object.assign(editableUser, selectedUser.value || {});
    Object.assign(editableDeveloperProfile, selectedUser.value.developerProfile || {});
    Object.assign(editableCompanyProfile, selectedUser.value.companyProfile || {});
  }

  usernameError.value = "";
  isUserDetailsVisible.value = false;
};



// 分页控制
const changePage = (direction) => {
  pagination.currentPage += direction;
  fetchUsers();
};

// 初始化加载用户列表
onMounted(() => {
  fetchUsers();
});
</script>



<style scoped>

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 4px;
}

.form-group input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.form-actions {
  display: flex;
  justify-content: space-between;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-submit {
  background-color: #28a745;
  color: white;
}

/* 表格样式 */
.user-table {
  width: 100%;
  border-collapse: collapse;
  margin: 16px 0;
}

.user-table th,
.user-table td {
  border: 1px solid #ddd;
  padding: 12px;
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

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}

.pagination button {
  margin: 0 8px;
  padding: 8px 16px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 8px;
  width: 400px;
}

.btn-warning {
  background-color: #ffc107;
  color: white;
}

.btn-success {
  background-color: #28a745;
  color: white;
}
</style>


