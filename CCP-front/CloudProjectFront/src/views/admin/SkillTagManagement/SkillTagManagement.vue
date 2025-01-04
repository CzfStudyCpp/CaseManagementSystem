

<template>
  <div class="skill-tag-management">
    <h2>技能标签管理</h2>

    <!-- 添加新技能标签 -->
    <form @submit.prevent="addSkillTag" class="add-skill-form">
      <div class="form-group">
        <label for="skillName">技能名称</label>
        <input
            type="text"
            id="skillName"
            v-model="newSkill.name"
            placeholder="输入技能名称"
            required
        />
      </div>
      <div class="form-group">
        <label for="skillDescription">技能描述</label>
        <textarea
            id="skillDescription"
            v-model="newSkill.description"
            placeholder="输入技能描述"
        ></textarea>
      </div>
      <div class="form-group">
        <label for="skillCategory">技能类别</label>
        <input
            type="text"
            id="skillCategory"
            v-model="newSkill.category"
            placeholder="输入技能类别"
        />
      </div>
      <button type="submit" class="add-button">添加技能</button>
    </form>

    <!-- 技能标签列表 -->
    <div class="skill-tag-list">
      <h3>已添加的技能标签</h3>
      <ul>
        <li v-for="skill in skills" :key="skill.id" class="skill-item">
          <div>
            <strong>{{ skill.name }}</strong> - {{ skill.category }}
            <p>{{ skill.description }}</p>
          </div>
          <div class="action-buttons">
            <button @click="editSkillTag(skill)" class="edit-button">编辑</button>
            <button @click="deleteSkillTag(skill.id)" class="delete-button">删除</button>
          </div>
        </li>
      </ul>
    </div>

    <!-- 编辑技能标签模态框 -->
    <div v-if="isEditing" class="modal">
      <div class="modal-content">
        <h3>编辑技能标签</h3>
        <form @submit.prevent="updateSkillTag">
          <div class="form-group">
            <label for="editSkillName">技能名称</label>
            <input
                type="text"
                id="editSkillName"
                v-model="editingSkill.name"
                required
            />
          </div>
          <div class="form-group">
            <label for="editSkillDescription">技能描述</label>
            <textarea
                id="editSkillDescription"
                v-model="editingSkill.description"
            ></textarea>
          </div>
          <div class="form-group">
            <label for="editSkillCategory">技能类别</label>
            <input
                type="text"
                id="editSkillCategory"
                v-model="editingSkill.category"
            />
          </div>
          <button type="submit" class="save-button">保存修改</button>
          <button type="button" @click="cancelEditing" class="cancel-button">
            取消
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axiosInstance from "@/http/axios.js"; // 确保引入配置了拦截器的 axios 实例


const skills = ref([]); // Holds the list of skills
const newSkill = ref({
  name: "",
  description: "",
  category: "",
});

// 编辑模式相关变量
const isEditing = ref(false); // 控制编辑模态框的显示
const editingSkill = ref({}); // 当前正在编辑的技能标签
// Fetch existing skill tags from the database
const fetchSkills = async () => {
  try {
    const response = await axiosInstance.get("/api/admin/getSkills"); // 使用 axiosInstance
    skills.value = response.data.data || []; // 更新列表
  } catch (error) {
    console.error("Failed to fetch skills:", error);
    alert("获取技能标签失败，请稍后重试！");
  }
};

const editSkillTag = (skill) => {
  editingSkill.value = { ...skill }; // 将选中的技能信息复制到 editingSkill
  isEditing.value = true; // 显示编辑模态框
};

const cancelEditing = () => {
  isEditing.value = false; // 隐藏编辑模态框
  editingSkill.value = {}; // 清空正在编辑的技能数据
};
// Add a new skill tag
const addSkillTag = async () => {
  if (!newSkill.value.name.trim()) {
    alert("技能名称不能为空！");
    return;
  }

  try {
    await axiosInstance.post("/api/admin/addSkills", newSkill.value); // 使用 axiosInstance
    alert("技能标签添加成功！");
    newSkill.value = { name: "", description: "", category: "" }; // 重置表单
    fetchSkills(); // 重新获取技能标签
  } catch (error) {
    console.error("Failed to add skill:", error);
    alert("添加技能标签失败，请稍后重试！");
  }
};

// Update a skill tag
const updateSkillTag = async () => {
  try {
    await axiosInstance.put(`/api/admin/updateSkills/${editingSkill.value.id}`, editingSkill.value); // 使用 axiosInstance
    alert("技能标签更新成功！");
    isEditing.value = false; // 关闭模态框
    fetchSkills(); // 重新获取技能标签
  } catch (error) {
    console.error("Failed to update skill:", error);
    alert("更新技能标签失败，请稍后重试！");
  }
};

// Delete a skill tag
const deleteSkillTag = async (skillId) => {
  if (!confirm("确定要删除该技能标签吗？")) return;

  try {
    await axiosInstance.delete(`/api/admin/deleteSkills/${skillId}`); // 使用 axiosInstance
    alert("技能标签删除成功！");
    fetchSkills(); // 重新获取技能标签
  } catch (error) {
    console.error("Failed to delete skill:", error);
    alert("删除技能标签失败，请稍后重试！");
  }
};
// Fetch skills on component mount
onMounted(() => {
  fetchSkills();
});
</script>
<style src="@/styles/admin/skillTagManagement.css"></style>