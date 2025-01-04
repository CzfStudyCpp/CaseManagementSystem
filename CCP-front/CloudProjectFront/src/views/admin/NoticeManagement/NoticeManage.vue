<template>
  <div>
    <h1>公告管理</h1>

    <!-- 添加公告 -->
    <form @submit.prevent="addAnnouncement" class="add-announcement-form">
      <h3>添加公告</h3>
      <div class="form-group">
        <label for="announcementTitle">标题</label>
        <input
            type="text"
            id="announcementTitle"
            v-model="newAnnouncement.title"
            placeholder="请输入公告标题"
            required
        />
      </div>
      <div class="form-group">
        <label for="announcementContent">内容</label>
        <textarea
            id="announcementContent"
            v-model="newAnnouncement.content"
            placeholder="请输入公告内容"
            required
        ></textarea>
      </div>
      <div class="form-group">
        <label for="announcementStatus">是否发布</label>
        <select id="announcementStatus" v-model="newAnnouncement.isPublished" required>
          <option :value="true">发布</option>
          <option :value="false">草稿</option>
        </select>
      </div>
      <button type="submit" class="add-button">添加公告</button>
    </form>

    <!-- 公告列表 -->
    <div class="announcement-list">
      <h3>公告列表</h3>
      <table class="announcement-table">
        <thead>
        <tr>
          <th>公告ID</th>
          <th>标题</th>
          <th>状态</th>
          <th>最后更新</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr
            v-for="announcement in paginatedAnnouncements"
            :key="announcement.id"
            @click="openAnnouncementDetails(announcement)"
            class="announcement-row"
        >
          <td>{{ announcement.id }}</td>
          <td>{{ announcement.title }}</td>
          <td>{{ announcement.isPublished ? "已发布" : "草稿" }}</td>
          <td>{{ formatDate(announcement.lastUpdatedDate) }}</td>
          <td>
            <button
                class="delete-button"
                @click.stop="deleteAnnouncement(announcement.id)"
            >
              删除
            </button>
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

    <!-- 弹窗：公告详细信息与编辑 -->
    <div v-if="isEditing" class="modal-overlay" @click="resetAndCloseModal">
      <div class="modal-content" @click.stop>
        <h3>编辑公告</h3>
        <form @submit.prevent="updateAnnouncement">
          <div class="form-group">
            <label for="editAnnouncementTitle">标题</label>
            <input
                type="text"
                id="editAnnouncementTitle"
                v-model="editingAnnouncement.title"
                required
            />
          </div>
          <div class="form-group">
            <label for="editAnnouncementContent">内容</label>
            <textarea
                id="editAnnouncementContent"
                v-model="editingAnnouncement.content"
                required
            ></textarea>
          </div>
          <div class="form-group">
            <label for="editAnnouncementStatus">是否发布</label>
            <select id="editAnnouncementStatus" v-model="editingAnnouncement.isPublished" required>
              <option :value="true">发布</option>
              <option :value="false">草稿</option>
            </select>
          </div>
          <button type="submit" class="save-button">保存修改</button>
          <button type="button" class="cancel-button" @click="resetAndCloseModal">取消</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import axiosInstance from "@/http/axios.js";

// 公告列表和分页
const announcements = ref([]);
const pagination = ref({
  currentPage: 1,
  pageSize: 5,
});

// 新公告表单
const newAnnouncement = ref({
  title: "",
  content: "",
  isPublished: false,
});

// 编辑公告变量
const isEditing = ref(false);
const editingAnnouncement = ref({});

// 获取公告列表
const fetchAnnouncements = async () => {
  try {
    const response = await axiosInstance.get("/api/admin/getAnnouncements", {
      params: {
        page: pagination.value.currentPage,
        size: pagination.value.pageSize,
      },
    });
    if (response.data && response.data.success) {
      announcements.value = response.data.data.announcements || [];
    } else {
      alert(response.data.message || "获取公告列表失败，请稍后重试！");
    }
  } catch (error) {
    console.error("获取公告列表失败：", error.message);
    alert("加载公告列表失败，请稍后重试！");
  }
};

// 添加公告
const addAnnouncement = async () => {
  try {
    await axiosInstance.post("/api/admin/addAnnouncement", newAnnouncement.value);
    alert("公告添加成功！");
    newAnnouncement.value = {title: "", content: "", isPublished: false};
    fetchAnnouncements();
  } catch (error) {
    console.error("添加公告失败：", error.message);
    alert("添加公告失败，请稍后重试！");
  }
};

// 打开公告详情弹窗
const openAnnouncementDetails = (announcement) => {
  isEditing.value = true;
  editingAnnouncement.value = {...announcement};
};

// 更新公告
const updateAnnouncement = async () => {
  try {
    await axiosInstance.put(`/api/admin/updateAnnouncement`, editingAnnouncement.value);
    alert("公告更新成功！");
    isEditing.value = false;
    fetchAnnouncements();
  } catch (error) {
    console.error("更新公告失败：", error.message);
    alert("更新公告失败，请稍后重试！");
  }
};

// 删除公告
const deleteAnnouncement = async (id) => {
  if (!confirm("确定要删除该公告吗？")) return;
  try {
    await axiosInstance.delete(`/api/admin/deleteAnnouncement/${id}`);
    alert("公告删除成功！");
    fetchAnnouncements();
  } catch (error) {
    console.error("删除公告失败：", error.message);
    alert("删除公告失败，请稍后重试！");
  }
};

// 分页逻辑
const paginatedAnnouncements = computed(() => {
  const start = (pagination.value.currentPage - 1) * pagination.value.pageSize;
  const end = start + pagination.value.pageSize;
  return announcements.value.slice(start, end);
});
const totalPages = computed(() => Math.ceil(announcements.value.length / pagination.value.pageSize));
const changePage = (direction) => {
  pagination.value.currentPage += direction;
  fetchAnnouncements();
};

// 辅助函数：格式化日期
const formatDate = (dateString) => {
  const options = { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit' };
  return new Intl.DateTimeFormat('zh-CN', options).format(new Date(dateString));
};

// 关闭弹窗并重置数据
const resetAndCloseModal = () => {
  isEditing.value = false;
  editingAnnouncement.value = {};
};

// 初始化
onMounted(fetchAnnouncements);
</script>

<style scoped>
/* 样式调整 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
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
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.announcement-table {
  width: 100%;
  border-collapse: collapse;
  margin: 16px 0;
}

.announcement-table th,
.announcement-table td {
  border: 1px solid #ddd;
  padding: 12px 16px;
  text-align: left;
}

.announcement-table th {
  background-color: #f4f4f4;
  font-weight: bold;
}

.announcement-row:hover {
  background-color: #f9f9f9;
  cursor: pointer;
}

.delete-button,
.edit-button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
}

.delete-button:hover,
.edit-button:hover {
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
  padding: 8px 16px;
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
