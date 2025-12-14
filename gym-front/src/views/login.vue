<script setup>
import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';

const loginForm = reactive({
  username: '',
  password: '',
  captcha: ''
});

const captchaText = ref('ABCD');
const refreshCaptcha = () => {
  const chars = 'ABCDEFGHJKMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789';
  let result = '';
  for (let i = 0; i < 4; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  captchaText.value = result;
};
refreshCaptcha();

const handleLogin = async () => {
  try {
    if (!loginForm.username) {
      ElMessage.error('请输入用户名');
      return;
    }
    if (!loginForm.password) {
      ElMessage.error('请输入密码');
      return;
    }
    if (!loginForm.captcha) {
      ElMessage.error('请输入验证码');
      return;
    }
    if (loginForm.captcha.toLowerCase() !== captchaText.value.toLowerCase()) {
      ElMessage.error('验证码错误');
      return;
    }
    ElMessage.success('登录成功');
    console.log('登录信息:', loginForm);
  } catch (error) {
    console.error('登录失败:', error);
  }
};

const goToRegister = () => {
  // 这里应该是路由跳转到注册页
  window.location.href = 'register';
};

const showForgotPassword = () => {
  ElMessage.info('请联系客服找回密码');
};
</script>

<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="form-title">用户登录</h2>
      <el-form :model="loginForm" style="width: 100%;">
        <el-form-item prop="username" class="form-item">
          <el-input
              v-model="loginForm.username"
              placeholder="用户名/手机号/邮箱"
              size="large"
              prefix-icon="User"
          ></el-input>
        </el-form-item>

        <el-form-item prop="password" class="form-item">
          <el-input
              type="password"
              v-model="loginForm.password"
              placeholder="请输入密码"
              size="large"
              prefix-icon="Lock"
          ></el-input>
        </el-form-item>

        <el-form-item prop="captcha" class="form-item">
          <div class="captcha-section">
            <el-input
                v-model="loginForm.captcha"
                placeholder="请输入验证码"
                class="captcha-input"
                size="large"
            ></el-input>
            <div class="captcha-image" @click="refreshCaptcha">
              {{ captchaText }}
            </div>
          </div>
        </el-form-item>

        <el-button
            type="primary"
            @click="handleLogin"
            class="main-btn"
            size="large"
        >
          登录
        </el-button>

        <div class="btn-group">
          <el-button type="text" @click="goToRegister" size="small">立即注册</el-button>
          <el-button type="text" @click="showForgotPassword" size="small">忘记密码</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 400px;
  background: white;
  padding: 40px;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
}

.form-title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 28px;
  font-weight: 400;
}

.form-item {
  margin-bottom: 22px;
}

.captcha-section {
  display: flex;
  align-items: center;
}

.captcha-input {
  flex: 1;
  margin-right: 12px;
}

.captcha-image {
  width: 100px;
  height: 40px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  cursor: pointer;
  user-select: none;
  font-weight: bold;
  letter-spacing: 2px;
}

.main-btn {
  width: 100%;
  height: 45px;
  font-size: 16px;
  margin-top: 5px;
}

.btn-group {
  display: flex;
  justify-content: space-between;
  margin-top: 25px;
}
</style>