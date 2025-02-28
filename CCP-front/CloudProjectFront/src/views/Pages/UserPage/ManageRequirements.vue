
<template>
  <div class="manage-demands-container">
    <h2>管理我的需求</h2>

    <!-- 添加需求按钮 -->
    <button class="add-demand-btn" @click="openAddDemandModal">添加新需求</button>

    <!-- 需求表格 -->
    <table class="demand-table">
      <thead>
      <tr>
        <th>标题</th>
        <th>状态</th>
        <th>预算</th>
        <th>技能要求</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="demand in demands" :key="demand.id" class="demand-row" @click="viewDetail(demand.id)">
        <td>{{ demand.title }}</td>
        <td>
    <span class="status-badge" :class="getStatusClass(demand.status)">
      {{ getStatusText(demand.status) }}
    </span>
        </td>
        <td>￥{{ demand.budget.toFixed(2) }}</td>
        <td>
    <span v-for="skill in demand.skills" :key="skill" class="skill-tag">
      {{ skill }}
    </span>
        </td>
        <td class="action-buttons">
          <button class="action-btn edit" @click.stop="editDemand(demand)">编辑</button>
          <button class="action-btn delete" @click.stop="deleteDemand(demand.id)">删除</button>
        </td>
      </tr>

      </tbody>
    </table>

    <!-- 添加/编辑需求弹窗 -->
    <div v-if="showModal" class="modal">
      <div class="modal-content">
        <h3>{{ isEditMode ? "编辑需求" : "添加新需求" }}</h3>

        <label>
          标题:
          <input v-model="form.title" type="text" />
        </label>
        <label>
          描述:
          <textarea v-model="form.description" rows="6" placeholder="填写需求描述"></textarea>
        </label>
        <label>
          状态:
          <select v-model="form.status">
            <option value="PUBLISHED">已发布</option>
            <option value="DRAFT">草稿</option>
            <option value="COMPLETED">已完成</option>
          </select>
        </label>
        <label v-if="form.status === 'COMPLETED'">
          解决方案:
          <textarea v-model="form.solution" rows="4" placeholder="填写解决方案"></textarea>
        </label>
        <label>
          预算:
          <input v-model.number="form.budget" type="number" step="1000" min="0" />
        </label>

        <!-- 技能选择 -->
        <div>
          <h4>技能需求</h4>
          <div class="tags-container">
            <div v-for="(skill, index) in form.skills" :key="index" class="tag">
              <span>{{ skill }}</span>
              <button @click="removeSkill(index)" class="remove-tag">删除</button>
            </div>
          </div>
          <div>
            <label for="skill-select">选择技能:</label>
            <select v-model="selectedSkillId">
              <option disabled value="">请选择技能</option>
              <option v-for="skill in skillOptions" :key="skill.id" :value="skill.id">
                {{ skill.name }}
              </option>
            </select>
            <button @click="addSkill" class="btn btn-primary">添加技能</button>
          </div>
        </div>

        <div class="modal-actions">
          <button @click="saveDemand">保存</button>
          <button @click="closeModal">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import axiosInstance from "@/http/axios.js";
import { useRouter } from "vue-router";

const router = useRouter();

const demands = ref([]); // 需求数据
const skillOptions = ref([]); // 技能选项
const showModal = ref(false); // 弹窗显示
const isEditMode = ref(false); // 编辑/新增模式
const form = reactive({
  id: null,
  title: "",
  description: "",
  status: "DRAFT",
  budget: 0,
  skills: [],
  solution: "", // 解决方案
});
const selectedSkillId = ref(null); // 当前选择的技能 ID

const viewDetail = (demandId) => {
  router.push(`/demand-detail/${demandId}`);
};
// 获取状态的文本
const getStatusText = (status) => {
  switch (status) {
    case "PUBLISHED":
      return "已发布";
    case "DRAFT":
      return "草稿";
    case "COMPLETED":
      return "已完成";
    default:
      return "未知";
  }
};

// 获取状态的样式
const getStatusClass = (status) => {
  switch (status) {
    case "PUBLISHED":
      return "published";
    case "DRAFT":
      return "draft";
    case "COMPLETED":
      return "completed";
    default:
      return "";
  }
};

// 获取需求列表
const fetchDemands = async () => {
  try {
    const response = await axiosInstance.get("/api/requirements/getMyDemands");
    if (response.data.success) {
      demands.value = response.data.data;
    } else {
      alert("获取需求列表失败：" + response.data.message);
    }
  } catch (error) {
    console.error("获取需求列表失败：", error);
    alert("获取需求列表失败，请稍后重试！");
  }
};

// 获取技能选项
const fetchSkillOptions = async () => {
  try {
    const response = await axiosInstance.get("/api/admin/getSkills");
    if (response.data.success) {
      skillOptions.value = response.data.data;
    } else {
      alert("获取技能选项失败：" + response.data.message);
    }
  } catch (error) {
    console.error("获取技能选项失败：", error);
    alert("获取技能选项失败，请稍后重试！");
  }
};

// 添加技能
const addSkill = () => {
  if (!selectedSkillId.value) {
    alert("请选择技能！");
    return;
  }
  const selectedSkill = skillOptions.value.find(
      (skill) => skill.id === selectedSkillId.value
  );
  if (form.skills.includes(selectedSkill.name)) {
    alert("该技能已添加！");
    return;
  }
  form.skills.push(selectedSkill.name);
  selectedSkillId.value = null;
};

// 删除技能
const removeSkill = (index) => {
  form.skills.splice(index, 1);
};

// 打开添加需求弹窗
const openAddDemandModal = () => {
  resetForm();
  isEditMode.value = false;
  showModal.value = true;
};

// 打开编辑需求弹窗
const editDemand = (demand) => {
  form.id = demand.id;
  form.title = demand.title;
  form.description = demand.description;
  form.status = demand.status;
  form.budget = demand.budget;
  form.skills = demand.skills;
  form.solution = demand.solution || ""; // 确保有解决方案字段
  isEditMode.value = true;
  showModal.value = true;
};

// 保存需求
const saveDemand = async () => {
  const payload = { ...form };

  try {
    if (isEditMode.value) {
      const response = await axiosInstance.put(`/api/requirements/updateDemands/${form.id}`, payload);
      if (response.data.success) {
        alert("需求更新成功！");
        fetchDemands();
        closeModal();
      } else {
        alert("更新失败：" + response.data.message);
      }
    } else {
      const response = await axiosInstance.post("/api/requirements/AddDemands", payload);
      if (response.data.success) {
        alert("需求添加成功！");
        fetchDemands();
        closeModal();
      } else {
        alert("添加失败：" + response.data.message);
      }
    }
  } catch (error) {
    console.error("保存需求失败：", error);
    alert("保存需求失败，请稍后重试！");
  }
};

// 删除需求
const deleteDemand = async (id) => {
  if (!confirm("确定要删除该需求吗？")) return;

  try {
    const response = await axiosInstance.delete(`/api/requirements/deleteDemand/${id}`);
    if (response.data.success) {
      alert("需求删除成功！");
      fetchDemands();
    } else {
      alert("删除失败：" + response.data.message);
    }
  } catch (error) {
    console.error("删除需求失败：", error);
    alert("删除需求失败，请稍后重试！");
  }
};

// 关闭弹窗并重置表单
const closeModal = () => {
  showModal.value = false;
  resetForm();
};

// 重置表单
const resetForm = () => {
  form.id = null;
  form.title = "";
  form.description = "";
  form.status = "DRAFT";
  form.budget = 0;
  form.skills = [];
  form.solution = "";
};

// 初始化加载
onMounted(() => {
  fetchDemands();
  fetchSkillOptions();
});
</script>

<style scoped>
.manage-demands-container {
  padding: 20px;
}

.add-demand-btn {
  background-color: #007bff;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-bottom: 20px;
}

.add-demand-btn:hover {
  background-color: #0056b3;
}

.demand-table {
  width: 100%;
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
  background-color: #007bff;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  margin-right: 4px;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  color: white;
}

.status-badge.published {
  background-color: #28a745;
}

.status-badge.draft {
  background-color: #6c757d;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.action-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.action-btn.edit {
  background-color: #007bff;
  color: white;
}

.action-btn.edit:hover {
  background-color: #0056b3;
}

.action-btn.delete {
  background-color: #dc3545;
  color: white;
}

.action-btn.delete:hover {
  background-color: #c82333;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 40px; /* 增大弹窗尺寸 */
  border-radius: 12px;
  width: 600px; /* 增大宽度 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); /* 添加阴影 */
}

.modal-content label {
  display: block;
  margin: 10px 0;
}

.modal-content textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

/* 增加 completed 状态样式 */
.status-badge.completed {
  background-color: #17a2b8;
}
</style>
