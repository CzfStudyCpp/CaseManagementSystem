<!--<template>-->
<!--  <div>-->
<!--    <h1>修改注册邮箱</h1>-->
<!--    <el-form :model="emailForm" label-width="100px">-->
<!--      <el-form-item label="新邮箱">-->
<!--        <el-input v-model="emailForm.email" placeholder="请输入新的邮箱地址" />-->
<!--      </el-form-item>-->
<!--      <el-form-item label="验证码">-->
<!--        <el-input v-model="emailForm.verificationCode" placeholder="请输入验证码" />-->
<!--        <el-button size="mini" @click="sendVerificationCode">发送验证码</el-button>-->
<!--      </el-form-item>-->
<!--      <el-button type="primary" @click="updateEmail">确认修改</el-button>-->
<!--    </el-form>-->
<!--  </div>-->
<!--</template>-->

<!--<script setup>-->
<!--import { ref } from "vue";-->
<!--import axiosInstance from "@/http/axios.js"; // 使用 axiosInstance 替代 axios-->


<!--const emailForm = ref({-->
<!--  email: "",-->
<!--  verificationCode: "",-->
<!--});-->

<!--const sendVerificationCode = async () => {-->
<!--  await axiosInstance.post("/api/register/sendVerificationCode", { email: emailForm.value.email });-->
<!--  alert("验证码已发送");-->
<!--};-->

<!--const updateEmail = async () => {-->
<!--  await axiosInstance.post("/api/user/change-email", emailForm.value);-->
<!--  alert("邮箱修改成功，请等待管理员审核");-->
<!--};-->

<!--</script>-->

<template>
  <div>
    <h1>修改注册邮箱</h1>
    <el-form :model="emailForm" label-width="100px">
      <!-- 绑定新邮箱输入 -->
      <el-form-item label="新邮箱" :error="emailError">
        <el-input
            v-model="emailForm.newEmail"
            placeholder="请输入新的邮箱地址"
            @blur="checkNewEmail"
        />
      </el-form-item>
      <!-- 验证码输入 -->
      <el-form-item label="验证码">
        <el-input
            v-model="emailForm.verificationCode"
            placeholder="请输入验证码"
        />
        <el-button
            size="mini"
            @click="sendVerificationCode"
            :disabled="isSendingCode || emailError !== ''"
        >
          {{ isSendingCode ? `${countdown}s后重试` : "发送验证码" }}
        </el-button>
      </el-form-item>
      <!-- 提交修改 -->
      <el-button
          type="primary"
          @click="updateEmail"
          :disabled="emailError !== ''"
      >
        确认修改
      </el-button>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useUserStore } from "@/stores/user/userStore"; // 从 Pinia 中获取用户状态
import axiosInstance from "@/http/axios.js"; // 使用 axiosInstance 替代 axios

const userStore = useUserStore(); // 获取用户的状态

const emailForm = ref({
  newEmail: "", // 新邮箱
  verificationCode: "", // 验证码
});

const emailError = ref(""); // 邮箱验证错误信息
const isSendingCode = ref(false);
const countdown = ref(60);
let timer = null;

// 检查新邮箱是否已注册
const checkNewEmail = async () => {
  emailError.value = ""; // 每次检查时重置错误信息

  if (!emailForm.value.newEmail) {
    emailError.value = "新邮箱不能为空";
    return;
  }

  try {
    // 请求后端接口，检查邮箱是否已注册
    await axiosInstance.get(`/api/register/check-email?email=${emailForm.value.newEmail}`);
    emailError.value = ""; // 清空错误信息
  } catch (error) {
    // 如果后端返回错误，设置错误信息
    emailError.value = "该邮箱已被注册，请更换邮箱";
  }
};

// 发送验证码
const sendVerificationCode = async () => {
  if (!emailForm.value.newEmail) {
    emailError.value = "新邮箱不能为空";
    return;
  }

  if (emailError.value) {
    alert(emailError.value);
    return;
  }

  try {
    isSendingCode.value = true;
    countdown.value = 60;

    const response = await axiosInstance.post("/api/user/sendChangeEmailVerificationCode", {
      email: emailForm.value.newEmail,
    });

    console.log("后端返回的响应信息:", response.data.message);
    alert(response.data.message || "验证码已发送到新邮箱");

    // 倒计时逻辑
    timer = setInterval(() => {
      countdown.value--;
      if (countdown.value <= 0) {
        clearInterval(timer);
        isSendingCode.value = false;
      }
    }, 1000);
  } catch (error) {
    console.error("后端返回的错误信息:", error.response?.data?.message);
    emailError.value = error.response?.data?.message || "发送验证码失败，请稍后重试";
    isSendingCode.value = false;
  }
};

// 更新邮箱
const updateEmail = async () => {
  if (!emailForm.value.newEmail) {
    emailError.value = "新邮箱不能为空";
    return;
  }

  if (!emailForm.value.verificationCode) {
    alert("请输入验证码！");
    return;
  }

  try {
    const response = await axiosInstance.post("/api/user/changeEmail", {
      currentEmail: userStore.userEmail, // 从 Pinia 的 userStore 获取当前邮箱
      newEmail: emailForm.value.newEmail,
      verificationCode: emailForm.value.verificationCode,
    });

    console.log("后端返回的响应信息:", response.data.message);
    alert(response.data.message || "邮箱修改成功，请等待管理员审核");

    // 重置表单
    emailForm.value = {
      newEmail: "",
      verificationCode: "",
    };
  } catch (error) {
    console.error("后端返回的错误信息:", error.response?.data?.message);
    emailError.value = error.response?.data?.message || "邮箱修改失败，请稍后重试";
  }
};
</script>

