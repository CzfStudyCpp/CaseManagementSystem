
<template>
  <div v-loading="loading" element-loading-text="加载中..." v-if="demand">
    <div class="demand-header">
      <h1>需求详情</h1>
      <button
          :class="demand.followed ? 'btn-danger' : 'btn-primary'"
          @click="toggleFollow"
      >
        {{ demand.followed ? '取消关注' : '关注需求' }}
      </button>
    </div>

    <!-- 每条信息都在一个卡片框内 -->
    <div class="info-card">
      <p><strong>标题：</strong> {{ demand.title }}</p>
    </div>

    <div class="info-card">
      <p><strong>状态：</strong>
        <span :class="{
          'status-published': demand.status === 'PUBLISHED',
          'status-draft': demand.status === 'DRAFT',
          'status-completed': demand.status === 'COMPLETED'
        }">
          {{ demand.status === 'PUBLISHED'
            ? '已发布'
            : demand.status === 'DRAFT'
                ? '草稿'
                : '已完成' }}
        </span>
      </p>
    </div>
    <div class="info-card">
      <p><strong>预算：</strong> ￥{{ demand.budget.toFixed(2) }}</p>
    </div>

    <div class="info-card">
      <p><strong>描述：</strong> {{ demand.description }}</p>
    </div>

    <div class="info-card">
      <p><strong>技能要求：</strong></p>
      <ul class="skills-list">
        <li v-for="skill in demand.skills" :key="skill">{{ skill }}</li>
      </ul>
    </div>

    <!-- 显示解决方案，仅当状态为 "已完成" -->
    <div v-if="demand.status === 'COMPLETED'" class="info-card solution-card">
      <p><strong>解决方案：</strong></p>
      <div>{{ demand.solution || '暂无解决方案' }}</div>
    </div>


    <div class="info-card horizontal">
      <p><strong>创建时间：</strong> {{ formatDate(demand.createdAt) }}</p>
      <p><strong>最后更新时间：</strong> {{ formatDate(demand.updatedAt) }}</p>
    </div>

    <!-- 评论输入框 -->
    <div class="comment-input">
      <textarea
          v-model="commentInput"
          rows="4"
          placeholder="输入您的评论..."
      ></textarea>
      <button class="btn-primary" @click="postComment">发送</button>
    </div>

    <!-- 评论区 -->
    <div class="comment-section">
      <h2>评论区</h2>

      <!-- 评论列表 -->
      <div v-if="comments.length > 0">
        <div
            v-for="comment in comments"
            :key="comment.id"
            class="comment-card"
        >
          <p>
            <strong
                :class="{ clickable: isEnterpriseUser && comment.username !== userStore.username }"
                @click="inviteUser(comment.username)"
            >
              {{ comment.username }}
            </strong>
          </p>
          <p>{{ comment.content }}</p>
          <p class="comment-time">{{ formatDate(comment.createdAt) }}</p>
        </div>
      </div>

      <!-- 无评论时显示提示 -->
      <div v-if="comments.length === 0" class="empty-comments">
        <p>暂无评论，快来发表第一个评论吧！</p>
      </div>

      <!-- 评论分页 -->
      <div class="pagination">
        <button
            :disabled="currentPage === 1"
            @click="changePage(-1)"
        >
          上一页
        </button>
        <span>第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</span>
        <button
            :disabled="currentPage === totalPages"
            @click="changePage(1)"
        >
          下一页
        </button>
      </div>
    </div>
  </div>

  <div v-else>
    <p>无法加载需求详情，请返回重新选择。</p>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import axiosInstance from "@/http/axios.js";
import { useUserStore } from "@/stores/user/userStore.ts";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const demand = ref(null); // 需求详情数据
const comments = ref([]); // 评论数据
const commentInput = ref(""); // 评论输入框
const currentPage = ref(1); // 当前评论页
const totalPages = ref(1); // 总页数
const pageSize = 5; // 每页显示评论数量
const loading = ref(true); // 加载状态

const isEnterpriseUser = computed(() => userStore.userType === "COMPANY");

const inviteUser = async (username) => {
  if (!isEnterpriseUser.value || username === userStore.username) {
    return;
  }

  const confirmed = confirm(`确定要向用户 "${username}" 发送协作邀请吗？`);
  if (!confirmed) return;

  try {
    const response = await axiosInstance.post("/api/requirements/inviteCollaborate", {
      demandId: demand.value.id,
      username: username,
    });

    if (response.data.success) {
      alert(`邀请已成功发送给用户 "${username}"！`);

    } else {
      alert(`发送邀请失败：${response.data.message}`);
    }
  } catch (error) {
    console.error("邀请协作失败：", error);
    ElMessage.error("邀请协作失败，请稍后重试！");
  }
};
// 日期格式化函数
const formatDate = (dateString) => {
  if (!dateString) return "无";
  try {
    const options = {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
    };
    return new Intl.DateTimeFormat("zh-CN", options).format(new Date(dateString));
  } catch (error) {
    console.error("日期格式化失败：", error);
    return "无效时间";
  }
};

// 加载需求详情
const fetchDemandDetails = async () => {
  loading.value = true;
  const demandId = route.params.id;

  if (!demandId) {
    ElMessage.error("需求 ID 无效！");
    router.push("/demand");
    return;
  }

  try {
    const response = await axiosInstance.get(`/api/requirements/detail/${demandId}`);
    if (response.data.success) {
      demand.value = response.data.data;
      demand.value.followed = await checkFollowStatus(demandId);
      await fetchComments(demandId);
    } else {
      ElMessage.error("获取需求详情失败：" + response.data.message);
      router.push("/demand");
    }
  } catch (error) {
    console.error("获取需求详情失败：", error);
    ElMessage.error("获取需求详情失败，请稍后重试！");
    router.push("/demand");
  } finally {
    loading.value = false;
  }
};

// 获取评论数据
const fetchComments = async (demandId) => {
  try {
    const response = await axiosInstance.get(`/api/requirements/${demandId}/comments`, {
      params: {
        page: currentPage.value,
        pageSize: pageSize,
      },
    });
    if (response.data.success) {
      const data = response.data.data;
      comments.value = data.comments || [];
      totalPages.value = Math.ceil((data.totalCount || 0) / pageSize);
    } else {
      ElMessage.error("获取评论失败：" + response.data.message);
    }
  } catch (error) {
    console.error("获取评论失败：", error);
    ElMessage.error("获取评论失败，请稍后重试！");
  }
};

const checkFollowStatus = async (demandId) => {
  try {
    const response = await axiosInstance.get(`/api/requirements/checkFollow/${demandId}`);
    if (response.data.success) {
      return response.data.data; // 确保返回的值是后端的 `data` 字段
    }
    return false; // 如果 `success` 为 false，默认未关注
  } catch (error) {
    console.error("检查关注状态失败：", error);
    return false; // 如果出错，默认返回未关注
  }
};

// 处理关注或取消关注
const toggleFollow = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning("请先登录！");
    router.push("/login");
    return;
  }

  if (!demand.value) {
    ElMessage.error("需求详情未加载，请稍后再试！");
    return;
  }

  try {
    const response = await axiosInstance.post(`/api/requirements/toggleFollow`, {
      demandId: demand.value.id,
      action: demand.value.followed ? "unfollow" : "follow",
    });

    if (response.data.success) {
      demand.value.followed = !demand.value.followed;
      ElMessage.success(demand.value.followed ? "已关注需求！" : "已取消关注！");
    } else {
      ElMessage.error("操作失败：" + response.data.message);
    }
  } catch (error) {
    console.error("关注操作失败：", error);
    ElMessage.error("操作失败，请稍后重试！");
  }
};

// 发布评论
const postComment = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning("请先登录！");
    router.push("/login");
    return;
  }

  if (!commentInput.value.trim()) {
    ElMessage.warning("评论内容不能为空！");
    return;
  }

  try {
    const response = await axiosInstance.post(`/api/requirements/addComments`, {
      requirementId: Number(route.params.id), // 确保传递的 ID 是数字
      content: commentInput.value,
    });

    if (response.data.success) {
      ElMessage.success("评论发布成功！");
      // 将新评论添加到评论列表
      const newComment = {
        id: Date.now(), // 临时 ID
        username: userStore.username, // 从用户 Store 获取当前用户名
        content: commentInput.value,
        createdAt: new Date().toLocaleString("zh-CN", {
          year: "numeric",
          month: "2-digit",
          day: "2-digit",
          hour: "2-digit",
          minute: "2-digit",
          second: "2-digit",
        }), // 格式化为本地时间
      };
      comments.value.unshift(newComment); // 将新评论添加到评论列表
      commentInput.value = ""; // 清空输入框
    } else {
      ElMessage.error("评论发布失败：" + response.data.message);
    }
  } catch (error) {
    console.error("评论发布失败：", error);
    ElMessage.error("评论发布失败，请稍后重试！");
  }
};


// 评论分页
const changePage = (direction) => {
  const newPage = currentPage.value + direction;
  if (newPage > 0 && newPage <= totalPages.value) {
    currentPage.value = newPage;
    fetchComments(demand.value.id);
  }
};

// 页面加载时获取数据
onMounted(() => {
  fetchDemandDetails();
});
</script>



<style scoped>
/* 标题和关注按钮 */
.demand-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
 .clickable {
   cursor: pointer;
   color: #409eff;
   text-decoration: underline;
 }

.clickable:hover {
  color: #66b1ff;
}
.btn-primary {
  background-color: #409eff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
}

.btn-danger {
  background-color: #f56c6c;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
}

.btn-primary:disabled,
.btn-danger:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

/* 信息卡片 */
.info-card {
  padding: 15px;
  margin-bottom: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background-color: white;
}

.info-card.horizontal {
  display: flex; /* 使用 Flexbox 实现水平布局 */
  justify-content: space-between; /* 两个段落在两端对齐 */
  align-items: center; /* 垂直方向居中 */
}

.info-card.horizontal p {
  margin: 0; /* 去掉默认的段落间距 */
}

@media (max-width: 600px) {
  .info-card.horizontal {
    flex-direction: column; /* 小屏幕时改为垂直布局 */
    align-items: flex-start; /* 左对齐 */
  }

  .info-card.horizontal p {
    margin-bottom: 8px; /* 添加段落间距 */
  }
}

/* 技能列表 */
.skills-list {
  list-style-type: none;
  padding: 0;
}

.skills-list li {
  background-color: #f0f0f0;
  display: inline-block;
  padding: 5px 10px;
  border-radius: 4px;
  margin-right: 5px;
  margin-bottom: 5px;
}

/* 评论输入框 */
.comment-input {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
}

.comment-input textarea {
  margin-bottom: 10px;
  padding: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  resize: none;
}

.comment-input button {
  align-self: flex-end;
  padding: 10px 20px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.comment-input button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

/* 评论区 */
.comment-section {
  margin-top: 30px;
}

.comment-card {
  padding: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.comment-card:last-child {
  border-bottom: none;
}

.comment-time {
  font-size: 12px;
  color: #666;
  margin-top: 5px;
}

/* 空评论提示 */
.empty-comments {
  text-align: center;
  margin: 20px 0;
  font-size: 14px;
  color: #666;
}

/* 分页控件 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}

.pagination button {
  background-color: #409eff;
  color: white;
  border: none;
  padding: 5px 10px;
  margin: 0 10px;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

/* 解决方案样式 */
.solution-card {
  background-color: #f0f8ff;
  border-left: 5px solid #007bff;
  padding: 15px;
  margin-top: 20px;
}
</style>
