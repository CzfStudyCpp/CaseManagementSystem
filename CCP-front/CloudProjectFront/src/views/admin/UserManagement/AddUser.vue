<!--<template>-->
<!--  <div>-->
<!--    <h3>待审核用户</h3>-->
<!--    &lt;!&ndash; Tab 切换分类浏览 &ndash;&gt;-->
<!--    <el-tabs v-model="activeTab">-->
<!--      &lt;!&ndash; 开发人员账户 &ndash;&gt;-->
<!--      <el-tab-pane label="开发人员账户" name="DEVELOPER">-->
<!--        <el-table :data="developerAccounts" border style="width: 100%">-->
<!--          <el-table-column prop="username" label="用户名" width="150" />-->
<!--          <el-table-column prop="email" label="邮箱" width="200" />-->
<!--          <el-table-column prop="phone" label="电话号码" width="150" />-->
<!--          <el-table-column prop="realName" label="真实姓名" width="150" />-->
<!--          <el-table-column prop="experience" label="项目经历" />-->
<!--          <el-table-column label="操作" width="200">-->
<!--            <template #default="scope">-->
<!--              <el-button-->
<!--                  :loading="processingId === scope.row.id"-->
<!--                  type="success"-->
<!--                  size="mini"-->
<!--                  @click="approveAccount(scope.row, 'DEVELOPER')"-->
<!--              >-->
<!--                通过-->
<!--              </el-button>-->
<!--              <el-button-->
<!--                  :loading="processingId === scope.row.id"-->
<!--                  type="danger"-->
<!--                  size="mini"-->
<!--                  @click="rejectAccount(scope.row, 'DEVELOPER')"-->
<!--              >-->
<!--                拒绝-->
<!--              </el-button>-->
<!--            </template>-->
<!--          </el-table-column>-->
<!--        </el-table>-->
<!--      </el-tab-pane>-->

<!--      &lt;!&ndash; 企业账户 &ndash;&gt;-->
<!--      <el-tab-pane label="企业账户" name="COMPANY">-->
<!--        <el-table :data="companyAccounts" border style="width: 100%">-->
<!--          <el-table-column prop="username" label="用户名" width="150" />-->
<!--          <el-table-column prop="email" label="邮箱" width="200" />-->
<!--          <el-table-column prop="companyName" label="公司名称" width="200" />-->
<!--          <el-table-column prop="companySize" label="公司规模类型" />-->
<!--          <el-table-column label="营业执照" width="150">-->
<!--            <template #default="scope">-->
<!--              <a :href="scope.row.businessLicense" target="_blank">查看营业执照</a>-->
<!--            </template>-->
<!--          </el-table-column>-->
<!--          <el-table-column label="操作" width="200">-->
<!--            <template #default="scope">-->
<!--              <el-button-->
<!--                  :loading="processingId === scope.row.id"-->
<!--                  type="success"-->
<!--                  size="mini"-->
<!--                  @click="() => { console.log('操作的行数据：', scope.row); approveAccount(scope.row, 'COMPANY'); }"-->

<!--              >-->
<!--                通过-->
<!--&lt;!&ndash;                @click="approveAccount(scope.row, 'company')"&ndash;&gt;-->
<!--              </el-button>-->
<!--              <el-button-->
<!--                  :loading="processingId === scope.row.id"-->
<!--                  type="danger"-->
<!--                  size="mini"-->
<!--                  @click="rejectAccount(scope.row, 'COMPANY')"-->
<!--              >-->
<!--                拒绝-->
<!--              </el-button>-->
<!--            </template>-->
<!--          </el-table-column>-->
<!--        </el-table>-->
<!--      </el-tab-pane>-->
<!--    </el-tabs>-->
<!--  </div>-->
<!--</template>-->

<!--<script>-->
<!--import axiosInstance from "@/http/axios.js"; // 引入 axiosInstance 替代 axios-->
<!--import { ElMessage, ElMessageBox } from "element-plus";-->

<!--export default {-->
<!--  data() {-->
<!--    return {-->
<!--      activeTab: "DEVELOPER", // 当前选中的 Tab，默认为开发人员账户-->
<!--      developerAccounts: [], // 待审核的开发人员账户-->
<!--      companyAccounts: [], // 待审核的企业账户-->
<!--      processingId: null, // 当前正在处理的用户 ID-->
<!--    };-->
<!--  },-->
<!--  methods: {-->
<!--    // 获取待审核用户信息-->
<!--    async fetchPendingAccounts() {-->
<!--      try {-->
<!--        const response = await axiosInstance.get("/api/admin/getPendingUsers"); // 使用 axiosInstance-->
<!--        const data = response.data.data || { developers: [], companies: [] };-->

<!--        // 解析开发者账户数据-->
<!--        this.developerAccounts = (data.developers || []).map((item) => ({-->
<!--          id: item.basicInfo.id, // 使用 basicInfo 中的 id-->
<!--          username: item.basicInfo.username,-->
<!--          email: item.basicInfo.email,-->
<!--          phone: item.basicInfo.phone,-->
<!--          realName: item.profile.realName,-->
<!--          experience: item.profile.experience,-->
<!--        }));-->

<!--        // 解析企业账户数据-->
<!--        this.companyAccounts = (data.companies || []).map((item) => ({-->
<!--          id: item.basicInfo.id, // 使用 basicInfo 中的 id-->
<!--          username: item.basicInfo.username,-->
<!--          email: item.basicInfo.email,-->
<!--          phone: item.basicInfo.phone,-->
<!--          companyName: item.profile.companyName,-->
<!--          companySize: item.profile.size,-->
<!--          businessLicense: item.profile.businessLicense,-->
<!--        }));-->

<!--        console.log("开发者账户：", this.developerAccounts);-->
<!--        console.log("企业账户：", this.companyAccounts);-->

<!--      } catch (error) {-->
<!--        ElMessage.error("获取待审核用户失败，请稍后重试！");-->
<!--        console.error(error);-->
<!--      }-->
<!--    },-->

<!--    async handleAccountApproval(account, type, action) {-->
<!--      const successMessage = action === "approve" ? "审核通过成功！" : "审核拒绝成功！";-->

<!--      try {-->
<!--        this.processingId = account.id; // 设置当前处理中的用户 ID-->
<!--        console.log("开始审核用户", account, type, action); // 调试日志-->

<!--        // 设置请求地址-->
<!--        const endpoint =-->
<!--            action === "approve" ? "/api/admin/approveUser" : "/api/admin/rejectUser";-->

<!--        // 发起请求，使用 params 传递参数-->
<!--        const response = await axiosInstance.post(endpoint, null, { // 使用 axiosInstance-->
<!--          params: {-->
<!--            id: account.id,-->
<!--            type: type,-->
<!--          },-->
<!--        });-->

<!--        console.log("后端响应", response); // 检查响应内容-->

<!--        // 如果审核成功，从当前列表中移除用户-->
<!--        if (response.status === 200) {-->
<!--          if (type === "DEVELOPER") {-->
<!--            this.developerAccounts = this.developerAccounts.filter(-->
<!--                (item) => item.id !== account.id-->
<!--            );-->
<!--          } else if (type === "COMPANY") {-->
<!--            this.companyAccounts = this.companyAccounts.filter(-->
<!--                (item) => item.id !== account.id-->
<!--            );-->
<!--          }-->

<!--          ElMessage.success(successMessage);-->
<!--        } else {-->
<!--          ElMessage.error("审核失败，请稍后重试！");-->
<!--        }-->
<!--      } catch (error) {-->
<!--        console.error("审核失败", error); // 错误日志-->

<!--        // 如果后端返回了特定的错误信息，优先显示-->
<!--        const errorMessage = error.response?.data?.message || "审核失败，请稍后重试！";-->
<!--        ElMessage.error(errorMessage);-->
<!--      } finally {-->
<!--        this.processingId = null; // 重置当前处理中的用户 ID-->
<!--      }-->
<!--    },-->

<!--    approveAccount(account, type) {-->
<!--      this.handleAccountApproval(account, type, "approve");-->
<!--    },-->

<!--    rejectAccount(account, type) {-->
<!--      this.handleAccountApproval(account, type, "reject");-->
<!--    },-->
<!--  },-->
<!--  mounted() {-->
<!--    this.fetchPendingAccounts(); // 加载待审核用户-->
<!--  },-->
<!--};-->

<!--</script>-->


<!--<style scoped>-->
<!--.el-table {-->
<!--  margin-bottom: 20px;-->
<!--}-->

<!--a {-->
<!--  color: #409eff;-->
<!--  text-decoration: none;-->
<!--}-->

<!--a:hover {-->
<!--  text-decoration: underline;-->
<!--}-->
<!--</style>-->

<template>
  <div>
    <h1>审核用户管理</h1>

    <!-- 用户分页浏览 -->
    <table class="user-table">
      <thead>
      <tr>
        <th>用户ID</th>
        <th>用户名</th>
        <th>邮箱</th>
        <th>账号类型</th>
        <th>其他信息</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="user in paginatedUsers" :key="user.id">
        <td>{{ user.id }}</td>
        <td>{{ user.username }}</td>
        <td>{{ user.email }}</td>
        <td>{{ user.userType }}</td>
        <td v-if="user.userType === 'DEVELOPER'">
          <p>真实姓名: {{ user.realName }}</p>
          <p>经历: {{ user.experience }}</p>
        </td>
        <td v-if="user.userType === 'COMPANY'">
          <p>公司名称: {{ user.companyName }}</p>
          <p>规模: {{ user.companySize }}</p>
          <p>
            <a :href="user.businessLicense" target="_blank">查看营业执照</a>
          </p>
        </td>
        <td>
          <button class="approve-btn" @click="approveAccount(user)">
            审核通过
          </button>
          <button class="reject-btn" @click="rejectAccount(user)">
            审核拒绝
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
      <button :disabled="pagination.currentPage === totalPages" @click="changePage(1)">
        下一页
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import axiosInstance from "@/http/axios.js"; // 确保 axiosInstance 配置了后端拦截器

// 待审核用户列表与分页状态
const pendingUsers = ref([]); // 存储待审核用户
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
});

// 当前页的用户
const paginatedUsers = computed(() => {
  const start = (pagination.currentPage - 1) * pagination.pageSize;
  const end = start + pagination.pageSize;
  return pendingUsers.value.slice(start, end);
});

// 计算总页数
const totalPages = computed(() =>
    Math.ceil(pendingUsers.value.length / pagination.pageSize)
);

// 获取待审核用户
const fetchPendingUsers = async () => {
  try {
    const response = await axiosInstance.get("/api/admin/getPendingUsers");
    if (response.data && response.data.success) {
      // 确保前端正确解构后端返回的数据
      const data = response.data.data || { developers: [], companies: [] };

      // 合并开发者和企业用户数据
      pendingUsers.value = [
        ...(data.developers || []).map((item) => ({
          id: item.basicInfo.id,
          username: item.basicInfo.username,
          email: item.basicInfo.email,
          userType: "DEVELOPER",
          realName: item.profile.realName,
          experience: item.profile.experience,
        })),
        ...(data.companies || []).map((item) => ({
          id: item.basicInfo.id,
          username: item.basicInfo.username,
          email: item.basicInfo.email,
          userType: "COMPANY",
          companyName: item.profile.companyName,
          companySize: item.profile.size,
          businessLicense: item.profile.businessLicense,
        })),
      ];
    } else {
      console.error("获取待审核用户失败：", response.data.message);
      alert(response.data.message || "获取待审核用户失败，请稍后重试！");
    }
  } catch (error) {
    console.error(
        "获取待审核用户失败：",
        error.response?.data?.message || error.message
    );
    alert("加载待审核用户失败，请检查网络或稍后重试！");
  }
};

// 审核通过用户
const approveAccount = async (user) => {
  if (!confirm(`确定要审核通过该用户（ID: ${user.id}）吗？`)) return;

  try {
    const response = await axiosInstance.post("/api/admin/approveUser", null, {
      params: {
        id: user.id,
        type: user.userType,
      },
    });

    if (response.data && response.data.success) {
      alert("审核通过成功");
      // 从本地列表中移除已审核的用户
      pendingUsers.value = pendingUsers.value.filter((item) => item.id !== user.id);
    } else {
      alert(`审核通过失败：${response.data.message}`);
    }
  } catch (error) {
    console.error("审核通过失败：", error.response?.data?.message || error.message);
    alert("审核通过失败，请稍后重试！");
  }
};

// 审核拒绝用户
const rejectAccount = async (user) => {
  if (!confirm(`确定要审核拒绝该用户（ID: ${user.id}）吗？`)) return;

  try {
    const response = await axiosInstance.post("/api/admin/rejectUser", null, {
      params: {
        id: user.id,
        type: user.userType,
      },
    });

    if (response.data && response.data.success) {
      alert("审核拒绝成功");
      // 从本地列表中移除已拒绝的用户
      pendingUsers.value = pendingUsers.value.filter((item) => item.id !== user.id);
    } else {
      alert(`审核拒绝失败：${response.data.message}`);
    }
  } catch (error) {
    console.error("审核拒绝失败：", error.response?.data?.message || error.message);
    alert("审核拒绝失败，请稍后重试！");
  }
};

// 分页控制
const changePage = (direction) => {
  pagination.currentPage += direction;
};

// 初始化加载待审核用户列表
onMounted(() => {
  fetchPendingUsers();
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

/* 按钮样式 */
.approve-btn {
  background-color: #28a745;
  color: white;
  border: none;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
}

.approve-btn:hover {
  opacity: 0.9;
}

.reject-btn {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
}

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
