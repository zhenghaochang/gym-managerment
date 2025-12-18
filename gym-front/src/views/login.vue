<script setup>
import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';

const loginFormRef = ref(null);

const loginForm = reactive({
  username: '',
  password: '',
  captcha: ''
});

// 假数据 - 模拟用户数据库
const mockUsers = [
  { username: 'admin', password: '123456', name: '管理员' },
  { username: 'zhangsan', password: '123456', name: '张三' },
  { username: '13800138000', password: '123456', name: '李四' },
  { username: 'test@qq.com', password: '123456', name: '测试用户' }
];

// 自定义验证码校验规则
const validateCaptcha = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入验证码'));
  } else if (value.toLowerCase() !== captchaText.value.toLowerCase()) {
    callback(new Error('验证码错误'));
  } else {
    callback();
  }
};

// 自定义用户名密码校验规则
const validateCredentials = (rule, value, callback) => {
  if (rule.field === 'username' && !value) {
    callback(new Error('请输入用户名'));
  } else if (rule.field === 'password' && !value) {
    callback(new Error('请输入密码'));
  } else {
    callback();
  }
};

// 表单校验规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名/手机号/邮箱', trigger: 'change' },
    { min: 3, message: '用户名至少3个字符', trigger: 'change' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'change' },
    { min: 6, message: '密码至少6个字符', trigger: 'change' }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { validator: validateCaptcha, trigger: 'blur' }
  ]
};

const captchaText = ref('ABCD');
const refreshCaptcha = () => {
  const chars = 'ABCDEFGHJKMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789';
  let result = '';
  for (let i = 0; i < 4; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  captchaText.value = result;
  // 刷新验证码后清空验证码输入框和校验状态
  loginForm.captcha = '';
  if (loginFormRef.value) {
    loginFormRef.value.clearValidate('captcha');
  }
};
refreshCaptcha();

const handleLogin = async () => {
  if (!loginFormRef.value) return;
  
  // 先进行表单校验
  loginFormRef.value.validate((valid) => {
    if (valid) {
      // 校验通过，进行登录验证
      const user = mockUsers.find(
        u => u.username === loginForm.username && u.password === loginForm.password
      );

      if (user) {
        ElMessage.success(`欢迎回来，${user.name}！`);
        console.log('登录成功，用户信息:', user);
        
        // 模拟保存登录状态
        localStorage.setItem('userInfo', JSON.stringify(user));
        
        // 延迟跳转，让用户看到成功提示
        setTimeout(() => {
          window.location.href = '/register'; // 跳转到注册页
        }, 1000);
      } else {
        ElMessage.error('用户名或密码错误');
        refreshCaptcha(); // 登录失败后刷新验证码
      }
    } else {
      // 校验失败，弹窗提示用户
      ElMessage.warning('请检查表单填写是否正确');
      return false;
    }
  });
};

const goToRegister = () => {
  window.location.href = 'register';
};

const showForgotPassword = () => {
  ElMessage.info('请联系客服找回密码');
};
</script>

<template>
  <div class="login-container">
    <div class="login-card">
      <div class="header-section">
        <div class="icon-wrapper">
          <svg class="gym-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M20.57 14.86L22 13.43 20.57 12 17 15.57 8.43 7 12 3.43 10.57 2 9.14 3.43 7.71 2 5.57 4.14 4.14 2.71 2.71 4.14l1.43 1.43L2 7.71l1.43 1.43L2 10.57 3.43 12 7 8.43 15.57 17 12 20.57 13.43 22l1.43-1.43L16.29 22l2.14-2.14 1.43 1.43 1.43-1.43-1.43-1.43L22 16.29z" fill="currentColor"/>
          </svg>
        </div>
        <h2 class="form-title">欢迎回来</h2>
        <p class="form-subtitle">登录继续你的健身之旅</p>
      </div>

      <el-form 
        ref="loginFormRef"
        :model="loginForm" 
        :rules="loginRules"
        label-position="top" 
        style="width: 100%;"
      >
        <el-form-item label="账号" prop="username" class="form-item">
          <el-input
              v-model="loginForm.username"
              placeholder="用户名/手机号/邮箱"
              size="large"
              prefix-icon="User"
          ></el-input>
        </el-form-item>

        <el-form-item label="密码" prop="password" class="form-item">
          <el-input
              type="password"
              v-model="loginForm.password"
              placeholder="请输入密码"
              size="large"
              prefix-icon="Lock"
              show-password
          ></el-input>
        </el-form-item>

        <el-form-item label="验证码" prop="captcha" class="form-item">
          <div class="captcha-section">
            <el-input
                v-model="loginForm.captcha"
                placeholder="请输入验证码"
                size="large"
                prefix-icon="Key"
            ></el-input>
            <div class="captcha-image" @click="refreshCaptcha">
              <span class="captcha-text">{{ captchaText }}</span>
              <span class="captcha-hint">点击刷新</span>
            </div>
          </div>
        </el-form-item>

        <div class="forgot-password">
          <el-button type="text" @click="showForgotPassword" class="forgot-link">
            忘记密码？
          </el-button>
        </div>

        <el-button
            type="primary"
            @click="handleLogin"
            class="main-btn"
            size="large"
        >
          立即登录
        </el-button>

        <div class="register-section">
          <span class="register-text">还没有账号？</span>
          <el-button type="text" @click="goToRegister" class="register-link">立即注册</el-button>
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
  padding: 40px 20px;
}

.login-card {
  width: 100%;
  max-width: 480px;
  background: white;
  padding: 50px 45px;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: slideUp 0.5s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 头部样式 */
.header-section {
  text-align: center;
  margin-bottom: 40px;
}

.icon-wrapper {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  margin-bottom: 20px;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.gym-icon {
  width: 36px;
  height: 36px;
  color: white;
}

.form-title {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 32px;
  font-weight: 600;
  letter-spacing: -0.5px;
}

.form-subtitle {
  margin: 0;
  color: #8492a6;
  font-size: 15px;
  font-weight: 400;
}

/* 表单项样式 */
.form-item {
  margin-bottom: 24px;
}

.form-item :deep(.el-form-item__label) {
  color: #606266;
  font-weight: 500;
  font-size: 14px;
  margin-bottom: 8px;
}

/* 验证码样式 */
.captcha-section {
  display: flex;
  gap: 12px;
  align-items: center;
}

.captcha-section .el-input {
  flex: 1;
}

.captcha-image {
  width: 120px;
  height: 50px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  cursor: pointer;
  user-select: none;
  border: 2px solid #e4e7ed;
  transition: all 0.3s;
  position: relative;
  padding: 4px;
}

.captcha-image:hover {
  border-color: #667eea;
  background: linear-gradient(135deg, #e8ecf1 0%, #dce1e8 100%);
  transform: scale(1.05);
}

.captcha-text {
  font-weight: bold;
  font-size: 16px;
  letter-spacing: 3px;
  color: #667eea;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
  line-height: 1.2;
}

.captcha-hint {
  font-size: 10px;
  color: #909399;
  margin-top: 3px;
  line-height: 1;
}

/* 忘记密码 */
.forgot-password {
  text-align: right;
  margin-top: -10px;
  margin-bottom: 20px;
}

.forgot-link {
  font-size: 13px;
  color: #909399;
  padding: 0;
}

.forgot-link:hover {
  color: #667eea;
}

/* 输入框样式优化 */
.form-item :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  transition: all 0.3s;
}

.form-item :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c4cc inset;
}

.form-item :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #667eea inset;
}

/* 主按钮样式 */
.main-btn {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  margin-top: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 10px;
  letter-spacing: 1px;
  transition: all 0.3s;
}

.main-btn:hover {
  background: linear-gradient(135deg, #5568d3 0%, #6a3f8f 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.main-btn:active {
  transform: translateY(0);
}

/* 注册区域样式 */
.register-section {
  text-align: center;
  margin-top: 30px;
  padding-top: 25px;
  border-top: 1px solid #f0f0f0;
}

.register-text {
  color: #909399;
  font-size: 14px;
  margin-right: 5px;
}

.register-link {
  font-size: 14px;
  font-weight: 500;
  color: #667eea;
  padding: 0;
}

.register-link:hover {
  color: #764ba2;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-card {
    padding: 35px 25px;
  }

  .form-title {
    font-size: 26px;
  }

  .captcha-image {
    width: 100px;
  }
}
</style>