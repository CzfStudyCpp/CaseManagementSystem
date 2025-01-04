

<template>
  <div>
    <h1>查看其他用户</h1>

    <!-- 用户分页浏览 -->
    <table class="user-table">
      <thead>
      <tr>
        <th>用户名</th>
        <th>邮箱</th>
        <th>账号类型</th>
      </tr>
      </thead>
      <tbody>
      <tr
          v-for="user in paginatedUsers"
          :key="user.id"
          @click="openUserDetails(user)"
          class="user-row"
      >
        <td>{{ user.username }}</td>
        <td>{{ user.email }}</td>
        <td>{{ user.userType }}</td>
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

    <!-- 弹窗：用户详细信息 -->
    <div v-if="isUserDetailsVisible" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <h2>用户详细信息</h2>
        <button class="close-button" @click="closeModal">关闭</button>

        <div v-if="selectedUser">
          <h3>个人信息</h3>
          <p><strong>用户名：</strong>{{ selectedUser.username }}</p>
          <p><strong>邮箱：</strong>{{ selectedUser.email }}</p>
          <p><strong>账号类型：</strong>{{ selectedUser.userType }}</p>
          <p><strong>状态：</strong>{{ selectedUser.status }}</p>

          <!-- 技能信息 -->
          <div v-if="selectedUser.userSkills?.length > 0">
            <h3>技能信息</h3>
            <ul>
              <li v-for="skill in selectedUser.userSkills" :key="skill.id">
                {{ skill.name }} - {{ getProficiencyLabel(skill.proficiency) }}
              </li>
            </ul>
          </div>

          <!-- 开发者信息 -->
          <div v-if="selectedUser.userType === 'DEVELOPER'">
            <h4>开发者信息</h4>
            <p><strong>真实姓名：</strong>{{ selectedUser.developerProfile?.realName || '未填写' }}</p>
            <p><strong>GitHub 账号：</strong>
              <a :href="selectedUser.developerProfile?.github" target="_blank">{{ selectedUser.developerProfile?.github || '未填写' }}</a>
            </p>
            <p><strong>作品集链接：</strong>
              <a :href="selectedUser.developerProfile?.portfolio" target="_blank">{{ selectedUser.developerProfile?.portfolio || '未填写' }}</a>
            </p>
            <p><strong>工作经验：</strong>{{ selectedUser.developerProfile?.experience || '未填写' }}</p>
          </div>

          <!-- 企业信息 -->
          <div v-if="selectedUser.userType === 'COMPANY'">
            <h4>企业信息</h4>
            <p><strong>公司名称：</strong>{{ selectedUser.companyProfile?.companyName || '未填写' }}</p>
            <p><strong>行业：</strong>{{ selectedUser.companyProfile?.industry || '未填写' }}</p>
            <p><strong>公司规模：</strong>{{ selectedUser.companyProfile?.size || '未填写' }}</p>
            <p><strong>地址：</strong>{{ selectedUser.companyProfile?.address || '未填写' }}</p>
            <p><strong>联系人：</strong>{{ selectedUser.companyProfile?.contactPerson || '未填写' }}</p>
            <p><strong>联系电话：</strong>{{ selectedUser.companyProfile?.contactPhone || '未填写' }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import axiosInstance from "@/http/axios.js"; // 确保 axiosInstance 配置了拦截器

// 用户列表与分页状态
const users = ref([]);
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
});

// 计算总页数
const totalPages = computed(() => Math.ceil(users.value.length / pagination.pageSize));

// 当前页的用户
const paginatedUsers = computed(() => {
  const start = (pagination.currentPage - 1) * pagination.pageSize;
  const end = start + pagination.pageSize;
  return users.value.slice(start, end);
});

// 用户详细信息弹窗状态
const isUserDetailsVisible = ref(false);
const selectedUser = ref(null);

// 获取用户列表
const fetchUsers = async () => {
  try {
    const response = await axiosInstance.get("/api/user/userList");
    if (response.data && response.data.success) {
      users.value = response.data.data.users || [];
    } else {
      console.error("获取用户列表失败：", response.data.message);
      alert(response.data.message || "获取用户列表失败，请稍后重试！");
    }
  } catch (error) {
    console.error("获取用户列表失败：", error.response?.data?.message || error.message);
    alert("加载用户列表失败，请检查网络或稍后重试！");
  }
};

// 定义技能熟练度映射
const getProficiencyLabel = (proficiency) => {
  const proficiencyMap = {
    BEGINNER: "初级入门",
    INTERMEDIATE: "中级",
    EXPERT: "专家",
  };
  return proficiencyMap[proficiency] || "未知";
};
const openUserDetails = async (user) => {
  try {
    const response = await axiosInstance.get(`/api/user/userDetails/${user.id}`);
    if (response.data && response.data.success) {
      const { userProfile, userSkills, developerProfile, companyProfile } = response.data.data;

      // 合并数据到 selectedUser
      selectedUser.value = {
        ...userProfile, // 用户基本信息
        userSkills: userSkills || [], // 用户技能信息
        developerProfile: developerProfile || null, // 开发者补充信息
        companyProfile: companyProfile || null, // 企业补充信息
      };

      isUserDetailsVisible.value = true;
    } else {
      console.error("获取用户详情失败：", response.data.message);
      alert(response.data.message || "加载用户详情失败，请稍后重试！");
    }
  } catch (error) {
    console.error("加载用户详情失败：", error.response?.data?.message || error.message);
    alert("加载用户详情失败，请稍后重试！");
  }
};

// 分页控制
const changePage = (direction) => {
  pagination.currentPage += direction;
};

// 关闭弹窗
const closeModal = () => {
  isUserDetailsVisible.value = false;
  selectedUser.value = null;
};

onMounted(() => {
  fetchUsers();
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

/* 分页控制样式 */
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
  z-index: 1000; /* 确保在所有元素上方 */
}

.modal-content {
  background: white;
  padding: 16px;
  border-radius: 8px;
  width: 400px;
  max-height: 80vh; /* 防止内容过长溢出 */
  overflow-y: auto; /* 如果内容过长，显示滚动条 */
  z-index: 1001; /* 确保内容也在其他元素前面 */
}

.close-button {
  float: right;
  background: red;
  color: white;
  border: none;
  padding: 4px 8px;
  cursor: pointer;
}
</style>
