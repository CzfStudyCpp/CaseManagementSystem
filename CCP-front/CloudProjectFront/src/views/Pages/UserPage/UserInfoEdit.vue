
<template>
  <div class="user-info-edit-container">
    <h2>编辑个人信息</h2>
    <form @submit.prevent="saveUserInfo">
      <!-- 用户基本信息 -->
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
    <div v-if="userType === 'DEVELOPER'" class="developer-info">
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
    <div v-if="userType === 'COMPANY'" class="company-info">
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
      <!-- 个人信息保存按钮 -->
      <div class="form-actions">
        <button type="button" class="btn btn-primary" @click="resetUserInfo">取消</button>
        <button type="submit" class="btn btn-submit">保存个人信息</button>
      </div>
    </form>

    <!-- 技能信息 -->
    <h3>编辑技能</h3>
    <div class="tags-container">
      <div v-for="(skill, index) in userSkills" :key="skill.id" class="tag">
        <span>{{ skill.name }}</span>
        <select v-model="skill.proficiency" class="skill-proficiency-select">
          <option value="BEGINNER">初级入门</option>
          <option value="INTERMEDIATE">中级</option>
          <option value="EXPERT">专家</option>
        </select>
        <button @click="removeSkill(skill.id)" class="remove-tag">删除</button>
      </div>
    </div>

    <!-- 添加技能 -->
    <div class="form-group">
      <label for="skills">添加技能</label>
      <select v-model="selectedSkillId">
        <option disabled value="">请选择技能</option>
        <option v-for="skill in skillOptions" :key="skill.id" :value="skill.id">
          {{ skill.name }}
        </option>
      </select>
    </div>
    <div class="form-group">
      <label for="proficiency">熟练度</label>
      <select v-model="selectedProficiency">
        <option value="BEGINNER">初级入门</option>
        <option value="INTERMEDIATE">中级</option>
        <option value="EXPERT">专家</option>
      </select>
    </div>
    <button type="button" @click="addSkill" class="btn btn-primary">
      添加技能
    </button>

    <!-- 保存技能按钮 -->
    <div class="form-actions">
      <button type="button" class="btn btn-primary" @click="resetSkills">取消</button>
      <button type="button" class="btn btn-submit" @click="saveSkills">保存技能</button>
    </div>
  </div>
</template>
<script>
import axiosInstance from "@/http/axios.js"; // 使用已经配置好的 axiosInstance
import { useUserStore } from "@/stores/user/userStore.ts";

export default {
  name: "UserInfoEdit",
  data() {
    return {
      originalUser: null, // 原始用户数据，用于取消时还原
      editableUser: {}, // 可编辑的用户信息
      editableDeveloperProfile: {}, // 可编辑的开发者信息
      editableCompanyProfile: {}, // 可编辑的企业信息
      userSkills: [], // 用户技能
      skillOptions: [], // 技能选项
      selectedSkillId: null,
      selectedProficiency: "BEGINNER",
      usernameError: null, // 用户名验证错误信息
    };
  },
  computed: {
    userType() {
      // 从 Pinia 的 userStore 获取用户类型
      const userStore = useUserStore();
      return userStore.userType;
    },
  },
  methods: {
    async checkUsername() {
      const username = this.editableUser.username;

      if (!username) {
        this.usernameError = "用户名不能为空";
        return;
      }

      if (username.includes("@")) {
        this.usernameError = "用户名不能包含 @ 符号";
        return;
      }

      try {
        await axiosInstance.get(`/api/register/check-username?username=${username}`);
        this.usernameError = ""; // 清空错误信息
      } catch (error) {
        this.usernameError = "该用户名已被注册，请更换用户名";
      }
    },

    async fetchData() {
      try {
        const userStore = useUserStore();
        const userEmail = userStore.userEmail;
        if (!userEmail) {
          alert("未获取到用户邮箱，请重新登录！");
          return;
        }

        const response = await axiosInstance.get("/api/user/getProfile", {
          params: { email: userEmail },
        });

        if (response.data && response.data.success) {
          const { userProfile, userSkills, skills, developerProfile, companyProfile } = response.data.data;

          this.originalUser = { ...userProfile };
          this.editableUser = { ...userProfile };
          this.userSkills = userSkills || [];
          this.skillOptions = skills || [];
          this.editableDeveloperProfile = { ...developerProfile };
          this.editableCompanyProfile = { ...companyProfile };

        } else {
          console.error("获取用户信息失败：", response.data.message);
          alert("获取用户信息失败，请稍后重试！");
        }
      } catch (error) {
        console.error("获取数据失败：", error);
        alert("加载用户数据失败，请检查网络！");
      }
    },

    async saveUserInfo() {
      if (this.usernameError) {
        alert("请修正用户名错误后再保存！");
        return;
      }

      try {
        const userStore = useUserStore();
        const userEmail = userStore.userEmail;
        if (!userEmail) {
          alert("未获取到用户邮箱，请重新登录！");
          return;
        }

        const payload = {
          email: userEmail, // 邮箱字段放在最前面
          editableUser: this.editableUser, // 用户基本信息
          developerProfile: this.editableDeveloperProfile, // 开发者补充信息
          companyProfile: this.editableCompanyProfile, // 企业补充信息
        };

        const response = await axiosInstance.put("/api/user/updateProfile", payload);

        if (response.data && response.data.success) {
          alert("用户信息保存成功！");
          this.originalUser = { ...this.editableUser };
        } else {
          console.error("保存用户信息失败：", response.data.message);
          alert("保存用户信息失败，请稍后重试！");
        }
      } catch (error) {
        console.error("保存失败：", error);
        alert("保存失败，请检查网络！");
      }
    },

    resetUserInfo() {
      this.editableUser = { ...this.originalUser };
      this.editableDeveloperProfile = { ...this.editableDeveloperProfile };
      this.editableCompanyProfile = { ...this.editableCompanyProfile };
    },

    addSkill() {
      if (!this.selectedSkillId) {
        alert("请选择技能！");
        return;
      }
      const selectedSkill = this.skillOptions.find(
          (skill) => skill.id === this.selectedSkillId
      );
      if (this.userSkills.some((skill) => skill.id === this.selectedSkillId)) {
        alert("该技能已存在！");
        return;
      }

      this.userSkills.push({
        id: selectedSkill.id,
        name: selectedSkill.name,
        proficiency: this.selectedProficiency,
      });

      this.selectedSkillId = null;
      this.selectedProficiency = "BEGINNER";
    },

    async saveSkills() {
      try {
        const userStore = useUserStore();
        const userEmail = userStore.userEmail;
        if (!userEmail) {
          alert("未获取到用户邮箱，请重新登录！");
          return;
        }

        const payload = {
          email: userEmail,
          skills: this.userSkills.map((skill) => ({
            skill_id: skill.id,
            proficiency: skill.proficiency,
          })),
        };

        const response = await axiosInstance.put("/api/user/updateSkills", payload);

        if (response.data && response.data.success) {
          alert("技能保存成功！");
        } else {
          console.error("保存技能失败：", response.data.message);
          alert("保存技能失败，请稍后重试！");
        }
      } catch (error) {
        console.error("保存失败：", error);
        alert("保存技能失败，请检查网络！");
      }
    },

    removeSkill(skillId) {
      this.userSkills = this.userSkills.filter((skill) => skill.id !== skillId);
    },

    resetSkills() {
      this.fetchData(); // 重新加载数据
    },
  },

  mounted() {
    this.fetchData();
  },
};
</script>


<style scoped>
.error-text {
  color: red;
  font-size: 14px;
  margin-top: 5px;
}
</style>


