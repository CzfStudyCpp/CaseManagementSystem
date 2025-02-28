<template>
  <div class="register-form-container">
    <h2>注册信息填写</h2>

    <!-- 用户基本信息表单 -->
    <form @submit.prevent="submitForm">
      <div class="form-group">
        <label for="username">用户名</label>
        <input
            type="text"
            id="username"
            v-model="username"
            @blur="checkUsername"
            required
        />
        <p v-if="usernameError" class="error-text">{{ usernameError }}</p>
      </div>

      <div class="form-group">
        <label for="email">邮箱</label>
        <input
            type="email"
            id="email"
            v-model="email"
            @blur="checkEmail"
            required
        />
        <p v-if="emailError" class="error-text">{{ emailError }}</p>
      </div>

      <div class="form-group">
        <label for="phone">电话</label>
        <input type="text" id="phone" v-model="phone" />
      </div>

      <div class="form-group">
        <label for="user_type">账户类型</label>
        <select id="user_type" v-model="userType" required>
          <option value="developer">开发者</option>
          <option value="company">企业</option>
        </select>
      </div>

      <div v-if="userType === 'developer'">
        <h3>开发者补充信息</h3>
        <div class="form-group">
          <label for="real_name">真实姓名</label>
          <input type="text" id="real_name" v-model="realName" />
        </div>
        <div class="form-group">
          <label for="github">GitHub 链接</label>
          <input type="url" id="github" v-model="github" />
        </div>
        <div class="form-group">
          <label for="portfolio">作品集链接</label>
          <input type="url" id="portfolio" v-model="portfolio" />
        </div>
        <div class="form-group">
          <label for="experience">项目经验</label>
          <textarea id="experience" v-model="experience"></textarea>
        </div>
      </div>

      <div v-if="userType === 'company'">
        <h3>企业补充信息</h3>
        <div class="form-group">
          <label for="company_name">公司名称</label>
          <input type="text" id="company_name" v-model="companyName" required />
        </div>
        <div class="form-group">
          <label for="industry">行业</label>
          <input type="text" id="industry" v-model="industry" />
        </div>
        <div class="form-group">
          <label for="size">企业规模</label>
          <select id="size" v-model="size">
            <option value="small">小型</option>
            <option value="medium">中型</option>
            <option value="large">大型</option>
          </select>
        </div>
        <div class="form-group">
          <label for="address">公司地址</label>
          <input type="text" id="address" v-model="address" />
        </div>
        <div class="form-group">
          <label for="business_license">营业执照</label>
          <input type="file" id="business_license" @change="handleFileUpload" />
        </div>
      </div>

      <div class="form-group">
        <label for="password">密码</label>
        <input type="password" id="password" v-model="password" required />
      </div>
      <div class="form-group">
        <label for="confirm_password">确认密码</label>
        <input
            type="password"
            id="confirm_password"
            v-model="confirmPassword"
            @input="checkPasswords"
            required
        />
        <p v-if="passwordError" class="error-text">{{ passwordError }}</p>
      </div>

      <div class="form-group">
        <label for="verification_code">验证码</label>
        <input
            type="text"
            id="verification_code"
            v-model="verificationCode"
            required
        />
        <button type="button" @click="sendVerificationCode">发送验证码</button>
        <button type="button" @click="verifyCode">验证验证码</button>
        <p v-if="verificationError" class="error-text">{{ verificationError }}</p>
      </div>

      <button type="submit" :disabled="!isCodeVerified">提交注册</button>
    </form>
  </div>
</template>

<script setup>
import { ref } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";

const router = useRouter();

const username = ref("");
const email = ref("");
const phone = ref("");
const userType = ref("developer");
const realName = ref("");
const github = ref("");
const portfolio = ref("");
const experience = ref("");
const companyName = ref("");
const industry = ref("");
const size = ref("small");
const address = ref("");
const businessLicense = ref(null);
const password = ref("");
const confirmPassword = ref("");
const usernameError = ref("");
const emailError = ref("");
const passwordError = ref("");
const verificationCode = ref("");
const verificationError = ref("");
const isCodeVerified = ref(false);

const checkUsername = async () => {
  try {
    await axios.get(`/api/register/check-username?username=${username.value}`);
    usernameError.value = "";
  } catch {
    usernameError.value = "该用户名已被注册，请更换用户名";
  }
};

const checkEmail = async () => {
  try {
    await axios.get(`/api/register/check-email?email=${email.value}`);
    emailError.value = "";
  } catch {
    emailError.value = "该邮箱已被注册，请更换邮箱";
  }
};

const checkPasswords = () => {
  passwordError.value =
      password.value === confirmPassword.value ? "" : "两次输入的密码不一致";
};

const handleFileUpload = (event) => {
  businessLicense.value = event.target.files[0];
};

const sendVerificationCode = async () => {
  try {
    await axios.post("/api/register/sendVerificationCode", { email: email.value });
    verificationError.value = "";
    alert("验证码已发送到您的邮箱，请查收");
  } catch {
    verificationError.value = "验证码发送失败，请稍后重试";
  }
};

const verifyCode = async () => {
  try {
    await axios.post("/api/register/verify-code", {
      email: email.value,
      code: verificationCode.value,
    });
    isCodeVerified.value = true;
    verificationError.value = "";
    alert("验证码验证成功");
  } catch {
    isCodeVerified.value = false;
    verificationError.value = "验证码错误或已过期";
  }
};

const submitForm = async () => {
  try {
    if (passwordError.value || emailError.value || usernameError.value || verificationError.value) {
      alert("请解决所有错误后再提交");
      return;
    }

    const formData = new FormData();
    formData.append("username", username.value);
    formData.append("email", email.value);
    formData.append("phone", phone.value);
    formData.append("userType", userType.value);
    formData.append("password", password.value);

    if (userType.value === "developer") {
      formData.append("developerInfo.realName", realName.value || "");
      formData.append("developerInfo.github", github.value || "");
      formData.append("developerInfo.portfolio", portfolio.value || "");
      formData.append("developerInfo.experience", experience.value || "");
    } else if (userType.value === "company") {
      formData.append("companyInfo.companyName", companyName.value || "");
      formData.append("companyInfo.industry", industry.value || "");
      formData.append("companyInfo.companySize", size.value || "");
      formData.append("companyInfo.address", address.value || "");
      if (businessLicense.value) {
        formData.append("businessLicense", businessLicense.value);
      } else {
        alert("请上传营业执照文件");
        return;
      }
    }

    formData.append("verificationCode", verificationCode.value);

    await axios.post("/api/register/submit", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });

    alert("注册成功！等待管理员审核");
    clearForm();
    router.push("/pending");
  } catch (error) {
    console.error("注册失败", error.response?.data || error.message);
    alert(`注册失败：${error.response?.data?.detail || error.message}`);
  }
};

const clearForm = () => {
  username.value = "";
  email.value = "";
  phone.value = "";
  userType.value = "developer";
  realName.value = "";
  github.value = "";
  portfolio.value = "";
  experience.value = "";
  companyName.value = "";
  industry.value = "";
  size.value = "small";
  address.value = "";
  businessLicense.value = null;
  password.value = "";
  confirmPassword.value = "";
  verificationCode.value = "";
  usernameError.value = "";
  emailError.value = "";
  passwordError.value = "";
  verificationError.value = "";
  isCodeVerified.value = false;
};
</script>