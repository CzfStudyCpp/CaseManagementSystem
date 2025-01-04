
<!--<template>-->
<!--  <div class="user-info-display-container">-->
<!--    <h2>个人信息</h2>-->
<!--    <div class="info-group" v-if="user">-->
<!--      <p><strong>用户名：</strong>{{ user.username }}</p>-->
<!--      <p><strong>邮箱：</strong>{{ user.email }}</p>-->
<!--      <p><strong>电话：</strong>{{ user.phone }}</p>-->
<!--      <p><strong>用户类型：</strong>{{ userType }}</p>-->
<!--      <p><strong>账号状态：</strong>{{ displayStatus }}</p>-->
<!--    </div>-->
<!--    <div v-else>-->
<!--      <p>正在加载个人信息...</p>-->
<!--    </div>-->

<!--    &lt;!&ndash; 开发者补充信息 &ndash;&gt;-->
<!--    <div v-if="userType === 'DEVELOPER'" class="developer-info">-->
<!--      <h2>开发者信息</h2>-->
<!--      <p><strong>真实姓名：</strong>{{ user.realName || '未填写' }}</p>-->
<!--      <p><strong>GitHub 账号：</strong>-->
<!--        <a :href="user.github" target="_blank">{{ user.github || '未填写' }}</a>-->
<!--      </p>-->
<!--      <p><strong>作品集链接：</strong>-->
<!--        <a :href="user.portfolio" target="_blank">{{ user.portfolio || '未填写' }}</a>-->
<!--      </p>-->
<!--      <p><strong>工作经验：</strong>{{ user.experience || '未填写' }}</p>-->
<!--    </div>-->

<!--    &lt;!&ndash; 企业补充信息 &ndash;&gt;-->
<!--    <div v-if="userType === 'COMPANY'" class="company-info">-->
<!--      <h2>企业信息</h2>-->
<!--      <p><strong>公司名称：</strong>{{ user.companyName || '未填写' }}</p>-->
<!--      <p><strong>行业：</strong>{{ user.industry || '未填写' }}</p>-->
<!--      <p><strong>公司规模：</strong>{{ getCompanySizeLabel(user.size) || '未填写' }}</p>-->
<!--      <p><strong>地址：</strong>{{ user.address || '未填写' }}</p>-->
<!--      <p><strong>联系人：</strong>{{ user.contactPerson || '未填写' }}</p>-->
<!--      <p><strong>联系电话：</strong>{{ user.contactPhone || '未填写' }}</p>-->
<!--    </div>-->

<!--    <h2>技能特长</h2>-->
<!--    <ul class="skills-list" v-if="userSkills.length > 0">-->
<!--      <li v-for="skill in userSkills" :key="skill.id">-->
<!--        {{ skill.name }} - {{ getProficiencyLabel(skill.proficiency) }}-->
<!--      </li>-->
<!--    </ul>-->
<!--    <div v-else>-->
<!--      <p>没有技能信息。</p>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->

<!--<script>-->
<!--import axios from "axios";-->
<!--import { useUserStore } from "@/stores/user/userStore.ts";-->

<!--export default {-->
<!--  name: "UserInfoDisplay",-->
<!--  data() {-->
<!--    return {-->
<!--      user: null, // 用户信息-->
<!--      userSkills: [], // 用户技能-->
<!--    };-->
<!--  },-->
<!--  computed: {-->
<!--    userType() {-->
<!--      // 从 Pinia 的 userStore 获取用户类型-->
<!--      const userStore = useUserStore();-->
<!--      return userStore.userType;-->
<!--    },-->
<!--    displayStatus() {-->
<!--      const statusMap = {-->
<!--        ACTIVE: "正常",-->
<!--        INACTIVE: "未激活",-->
<!--        SUSPENDED: "已冻结",-->
<!--      };-->
<!--      return statusMap[this.user?.status] || "未知"; // 安全访问防止空数据报错-->
<!--    },-->
<!--  },-->
<!--  methods: {-->
<!--    getProficiencyLabel(proficiency) {-->
<!--      const proficiencyMap = {-->
<!--        BEGINNER: "初级入门",-->
<!--        INTERMEDIATE: "中级",-->
<!--        EXPERT: "专家",-->
<!--      };-->
<!--      return proficiencyMap[proficiency] || "未知";-->
<!--    },-->
<!--    getCompanySizeLabel(size) {-->
<!--      const sizeMap = {-->
<!--        SMALL: "小型",-->
<!--        MEDIUM: "中型",-->
<!--        LARGE: "大型",-->
<!--      };-->
<!--      return sizeMap[size];-->
<!--    },-->
<!--    async fetchData() {-->
<!--      try {-->
<!--        const userStore = useUserStore();-->
<!--        const userEmail = userStore.userEmail; // 从 Pinia 获取邮箱-->
<!--        if (!userEmail || userEmail.trim() === "") {-->
<!--          alert("无法加载用户信息，请先登录！");-->
<!--          return;-->
<!--        }-->

<!--        const response = await axios.get("/api/user/getProfile", {-->
<!--          params: { email: userEmail },-->
<!--        });-->

<!--        console.log("完整的响应数据：", response);-->

<!--        if (response.data && response.data.success) {-->
<!--          const { user, userSkills } = response.data.data;-->
<!--          this.user = user || {};-->
<!--          this.userSkills = userSkills || [];-->
<!--        } else {-->
<!--          console.error("获取用户信息失败：", response.data.message);-->
<!--          alert(response.data.message || "获取用户信息失败！");-->
<!--        }-->
<!--      } catch (error) {-->
<!--        console.error("获取数据失败：", error);-->
<!--        alert("加载用户信息失败，请检查网络或稍后重试！");-->
<!--      }-->
<!--    },-->
<!--  },-->
<!--  mounted() {-->
<!--    this.fetchData();-->
<!--  },-->
<!--};-->
<!--</script>-->

<!--<style scoped>-->
<!--.user-info-display-container {-->
<!--  padding: 20px;-->
<!--  max-width: 600px;-->
<!--  margin: 0 auto;-->
<!--  background: #fff;-->
<!--  border-radius: 8px;-->
<!--  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);-->
<!--}-->

<!--.info-group p {-->
<!--  margin: 5px 0;-->
<!--}-->

<!--.skills-list {-->
<!--  list-style-type: disc;-->
<!--  padding-left: 20px;-->
<!--}-->

<!--.skills-list li {-->
<!--  margin-bottom: 5px;-->
<!--}-->

<!--.developer-info,-->
<!--.company-info {-->
<!--  margin-top: 20px;-->
<!--}-->

<!--.developer-info h2,-->
<!--.company-info h2 {-->
<!--  margin-bottom: 10px;-->
<!--}-->
<!--</style>-->


<template>
  <div class="user-info-display-container">
    <h2>个人信息</h2>
    <div class="info-group" v-if="user">
      <p><strong>用户名：</strong>{{ user.username }}</p>
      <p><strong>邮箱：</strong>{{ user.email }}</p>
      <p><strong>电话：</strong>{{ user.phone }}</p>
      <p><strong>用户类型：</strong>{{ userType }}</p>
      <p><strong>账号状态：</strong>{{ displayStatus }}</p>
    </div>
    <div v-else>
      <p>正在加载个人信息...</p>
    </div>

    <!-- 开发者补充信息 -->
    <div v-if="userType === 'DEVELOPER'" class="developer-info">
      <h2>开发者信息</h2>
      <p><strong>真实姓名：</strong>{{ developerProfile?.realName || '未填写' }}</p>
      <p><strong>GitHub 账号：</strong>
        <a :href="developerProfile?.github" target="_blank">{{ developerProfile?.github || '未填写' }}</a>
      </p>
      <p><strong>作品集链接：</strong>
        <a :href="developerProfile?.portfolio" target="_blank">{{ developerProfile?.portfolio || '未填写' }}</a>
      </p>
      <p><strong>工作经验：</strong>{{ developerProfile?.experience || '未填写' }}</p>
    </div>

    <!-- 企业补充信息 -->
    <div v-if="userType === 'COMPANY'" class="company-info">
      <h2>企业信息</h2>
      <p><strong>公司名称：</strong>{{ companyProfile?.companyName || '未填写' }}</p>
      <p><strong>行业：</strong>{{ companyProfile?.industry || '未填写' }}</p>
      <p><strong>公司规模：</strong>{{ companyProfile?.size || '未填写' }}</p>
      <p><strong>地址：</strong>{{ companyProfile?.address || '未填写' }}</p>
      <p><strong>联系人：</strong>{{ companyProfile?.contactPerson || '未填写' }}</p>
      <p><strong>联系电话：</strong>{{ companyProfile?.contactPhone || '未填写' }}</p>
    </div>

    <h2>技能特长</h2>
    <ul class="skills-list" v-if="userSkills.length > 0">
      <li v-for="skill in userSkills" :key="skill.id">
        {{ skill.name }} - {{ getProficiencyLabel(skill.proficiency) }}
      </li>
    </ul>
    <div v-else>
      <p>没有技能信息。</p>
    </div>
  </div>
</template>

<script>
import axiosInstance from "@/http/axios.js"; // 确保 axiosInstance 配置了拦截器

import {useUserStore} from "@/stores/user/userStore.ts";

export default {
  name: "UserInfoDisplay",
  data() {
    return {
      user: null, // 用户信息
      userSkills: [], // 用户技能
      developerProfile: null, // 开发者信息
      companyProfile: null, // 企业信息
    };
  },
  computed: {
    // 从 Pinia 中获取用户类型
    userType() {
      const userStore = useUserStore();
      return userStore.userType;
    },
    displayStatus() {
      const statusMap = {
        ACTIVE: "正常",
        INACTIVE: "未激活",
        SUSPENDED: "已冻结",
      };
      return statusMap[this.user?.status] || "未知"; // 安全访问防止空数据报错
    },
  },
  methods: {
    getProficiencyLabel(proficiency) {
      const proficiencyMap = {
        BEGINNER: "初级入门",
        INTERMEDIATE: "中级",
        EXPERT: "专家",
      };
      return proficiencyMap[proficiency] || "未知";
    },
    async fetchData() {
      try {
        const userStore = useUserStore();
        const userEmail = userStore.userEmail; // 从 Pinia 获取邮箱
        const token = userStore.userToken; // 从 Pinia 获取用户 Token

        if (!userEmail || userEmail.trim() === "") {
          alert("无法加载用户信息，请先登录！");
          return;
        }

        if (!token) {
          alert("用户未登录或 Token 缺失，请重新登录！");
          return;
        }

        // 使用 axiosInstance 发送请求，同时附加 token
        const response = await axiosInstance.get("/api/user/getProfile", {
          params: {email: userEmail},
          headers: {
            Authorization: `Bearer ${token}`, // 在请求头中附加 Token
          },
        });

        if (response.data && response.data.success) {
          const {userProfile, userSkills, developerProfile, companyProfile} = response.data.data;
          this.user = userProfile || {};
          this.userSkills = userSkills || [];
          this.developerProfile = developerProfile || null;
          this.companyProfile = companyProfile || null;
        } else {
          console.error("获取用户信息失败：", response.data.message);
          alert(response.data.message || "获取用户信息失败！");
        }
      } catch (error) {
        console.error("获取数据失败：", error);
        alert("加载用户信息失败，请检查网络或稍后重试！");
      }
    },
  },
  mounted() {
    this.fetchData();
  },
};
</script>
