<!--<template>-->
<!--  <div>-->
<!--    <h1>修改密码</h1>-->

<!--    <div class="method-selection">-->
<!--      <el-button-->
<!--          type="primary"-->
<!--          :plain="method !== 'password'"-->
<!--          @click="method = 'password'"-->
<!--      >-->
<!--        通过旧密码修改-->
<!--      </el-button>-->
<!--      <el-button-->
<!--          type="primary"-->
<!--          :plain="method !== 'email'"-->
<!--          @click="method = 'email'"-->
<!--      >-->
<!--        忘记密码？改用邮箱验证-->
<!--      </el-button>-->
<!--    </div>-->

<!--    <el-form v-if="method === 'password'" :model="passwordForm" label-width="100px" class="password-form">-->
<!--      <el-form-item label="旧密码">-->
<!--        <el-input type="password" v-model="passwordForm.oldPassword" />-->
<!--      </el-form-item>-->
<!--      <el-form-item label="新密码">-->
<!--        <el-input type="password" v-model="passwordForm.newPassword" />-->
<!--      </el-form-item>-->
<!--      <el-form-item label="确认密码">-->
<!--        <el-input type="password" v-model="passwordForm.confirmPassword" />-->
<!--      </el-form-item>-->
<!--      <el-button type="primary" @click="changePasswordByOldPassword">修改密码</el-button>-->
<!--    </el-form>-->

<!--    <el-form v-else :model="emailForm" label-width="100px" class="email-form">-->
<!--      <el-form-item label="邮箱">-->
<!--        <el-input v-model="emailForm.email" placeholder="请输入邮箱" />-->
<!--      </el-form-item>-->
<!--      <el-form-item label="验证码">-->
<!--        <el-input v-model="emailForm.code" placeholder="请输入验证码" />-->
<!--        <el-button type="text" @click="sendVerificationCode" :disabled="isSendingCode">-->
<!--          {{ isSendingCode ? `${countdown}s后重试` : "发送验证码" }}-->
<!--        </el-button>-->
<!--      </el-form-item>-->
<!--      <el-form-item label="新密码">-->
<!--        <el-input type="password" v-model="emailForm.newPassword" />-->
<!--      </el-form-item>-->
<!--      <el-form-item label="确认密码">-->
<!--        <el-input type="password" v-model="emailForm.confirmPassword" />-->
<!--      </el-form-item>-->
<!--      <el-button type="primary" @click="changePasswordByEmail">修改密码</el-button>-->
<!--    </el-form>-->
<!--  </div>-->
<!--</template>-->

<!--<script setup>-->
<!--import {ref} from "vue";-->
<!--import axiosInstance from "@/http/axios.js"; // 使用已经配置好的 axiosInstance-->

<!--// 修改方式："password" 或 "email"-->
<!--const method = ref("password");-->

<!--// 旧密码修改方式的表单-->
<!--const passwordForm = ref({-->
<!--  oldPassword: "",-->
<!--  newPassword: "",-->
<!--  confirmPassword: "",-->
<!--});-->

<!--// 邮箱验证码修改方式的表单-->
<!--const emailForm = ref({-->
<!--  email: "",-->
<!--  code: "",-->
<!--  newPassword: "",-->
<!--  confirmPassword: "",-->
<!--});-->

<!--// 发送验证码相关-->
<!--const isSendingCode = ref(false);-->
<!--const countdown = ref(60);-->
<!--let timer = null;-->

<!--const sendVerificationCode = async () => {-->
<!--  if (!emailForm.value.email) {-->
<!--    alert("请输入邮箱");-->
<!--    return;-->
<!--  }-->

<!--  try {-->
<!--    isSendingCode.value = true;-->
<!--    countdown.value = 60;-->

<!--    // 使用已经配置好的 axiosInstance-->
<!--    await axiosInstance.post("/api/user/sendChangePasswordCode", {email: emailForm.value.email});-->
<!--    alert("验证码已发送到您的邮箱");-->

<!--    // 开始倒计时-->
<!--    timer = setInterval(() => {-->
<!--      countdown.value&#45;&#45;;-->
<!--      if (countdown.value <= 0) {-->
<!--        clearInterval(timer);-->
<!--        isSendingCode.value = false;-->
<!--      }-->
<!--    }, 1000);-->
<!--  } catch (error) {-->
<!--    alert("发送验证码失败，请稍后重试");-->
<!--    isSendingCode.value = false;-->
<!--  }-->
<!--};-->

<!--const changePasswordByOldPassword = async () => {-->
<!--  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {-->
<!--    alert("两次输入的新密码不一致");-->
<!--    return;-->
<!--  }-->

<!--  try {-->
<!--    // 使用已经配置好的 axiosInstance-->
<!--    await axiosInstance.post("/api/user/changePassword", passwordForm.value);-->
<!--    alert("密码修改成功");-->
<!--  } catch (error) {-->
<!--    alert("密码修改失败，请稍后重试");-->
<!--  }-->
<!--};-->

<!--const changePasswordByEmail = async () => {-->
<!--  if (emailForm.value.newPassword !== emailForm.value.confirmPassword) {-->
<!--    alert("两次输入的新密码不一致");-->
<!--    return;-->
<!--  }-->

<!--  try {-->
<!--    // 使用已经配置好的 axiosInstance-->
<!--    await axiosInstance.post("/api/user/changePasswordByEmail", emailForm.value);-->
<!--    alert("密码修改成功");-->
<!--  } catch (error) {-->
<!--    alert("密码修改失败，请稍后重试");-->
<!--  }-->
<!--};-->
<!--</script>-->

<template>
  <div>
    <h1>修改密码</h1>

    <div class="method-selection">
      <el-button
          type="primary"
          :plain="method !== 'password'"
          @click="method = 'password'"
      >
        通过旧密码修改
      </el-button>
      <el-button
          type="primary"
          :plain="method !== 'email'"
          @click="method = 'email'"
      >
        忘记密码？改用邮箱验证
      </el-button>
    </div>

    <el-form v-if="method === 'password'" :model="passwordForm" label-width="100px" class="password-form">
      <el-form-item label="旧密码">
        <el-input type="password" v-model="passwordForm.oldPassword" />
      </el-form-item>
      <el-form-item label="新密码">
        <el-input type="password" v-model="passwordForm.newPassword" />
      </el-form-item>
      <el-form-item label="确认密码">
        <el-input type="password" v-model="passwordForm.confirmPassword" />
      </el-form-item>
      <el-button type="primary" @click="changePasswordByOldPassword">修改密码</el-button>
    </el-form>

    <el-form v-else :model="emailForm" label-width="100px" class="email-form">
      <el-form-item label="邮箱">
        <el-input v-model="emailForm.email" placeholder="请输入邮箱" />
      </el-form-item>
      <el-form-item label="验证码">
        <el-input v-model="emailForm.code" placeholder="请输入验证码" />
        <el-button type="text" @click="sendVerificationCode" :disabled="isSendingCode">
          {{ isSendingCode ? `${countdown}s后重试` : "发送验证码" }}
        </el-button>
      </el-form-item>
      <el-form-item label="新密码">
        <el-input type="password" v-model="emailForm.newPassword" />
      </el-form-item>
      <el-form-item label="确认密码">
        <el-input type="password" v-model="emailForm.confirmPassword" />
      </el-form-item>
      <el-button type="primary" @click="changePasswordByEmail">修改密码</el-button>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from "vue";
import axiosInstance from "@/http/axios.js"; // 使用已经配置好的 axiosInstance

// 修改方式："password" 或 "email"
const method = ref("password");

// 旧密码修改方式的表单
const passwordForm = ref({
  oldPassword: "",
  newPassword: "",
  confirmPassword: "",
});

// 邮箱验证码修改方式的表单
const emailForm = ref({
  email: "",
  code: "",
  newPassword: "",
  confirmPassword: "",
});

// 发送验证码相关
const isSendingCode = ref(false);
const countdown = ref(60);
let timer = null;

// 发送验证码
const sendVerificationCode = async () => {
  if (!emailForm.value.email) {
    alert("请输入邮箱");
    return;
  }

  try {
    isSendingCode.value = true;
    countdown.value = 60;

    // 使用已经配置好的 axiosInstance
    const response = await axiosInstance.post("/api/user/sendChangePasswordCode", { email: emailForm.value.email });
    alert(response.data.message || "验证码已发送到您的邮箱");

    // 开始倒计时
    timer = setInterval(() => {
      countdown.value--;
      if (countdown.value <= 0) {
        clearInterval(timer);
        isSendingCode.value = false;
      }
    }, 1000);
  } catch (error) {
    alert(error.response?.data?.message || "发送验证码失败，请稍后重试");
    isSendingCode.value = false;
  }
};

// 通过旧密码修改
const changePasswordByOldPassword = async () => {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    alert("两次输入的新密码不一致");
    return;
  }

  try {
    // 使用已经配置好的 axiosInstance
    const response = await axiosInstance.post("/api/user/changePassword", passwordForm.value);

    alert(response.data.message || "密码修改成功");

    // 刷新页面并清除表单
    resetForms();
  } catch (error) {
    alert(error.response?.data?.message || "密码修改失败，请稍后重试");
  }
};

// 通过邮箱修改
const changePasswordByEmail = async () => {
  if (emailForm.value.newPassword !== emailForm.value.confirmPassword) {
    alert("两次输入的新密码不一致");
    return;
  }

  try {
    // 使用已经配置好的 axiosInstance
    const response = await axiosInstance.post("/api/user/changePasswordByEmail", emailForm.value);

    alert(response.data.message || "密码修改成功");

    // 刷新页面并清除表单
    resetForms();
  } catch (error) {
    alert(error.response?.data?.message || "密码修改失败，请稍后重试");
  }
};

// 清除表单并刷新页面
const resetForms = () => {
  passwordForm.value = {
    oldPassword: "",
    newPassword: "",
    confirmPassword: "",
  };

  emailForm.value = {
    email: "",
    code: "",
    newPassword: "",
    confirmPassword: "",
  };

  method.value = "password"; // 重置为默认方式
  window.location.reload(); // 刷新页面
};
</script>


<style scoped>
.password-form,
.email-form {
  margin-top: 20px;
}

.method-selection {
  display: flex;
  justify-content: flex-start;
  gap: 20px;
  margin-bottom: 20px;
}
</style>
