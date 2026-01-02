<script setup>
import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import { userApi } from '@/api';

const router = useRouter();
const registerFormRef = ref(null);
const loading = ref(false);

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  phone: '',
  emailCode: '',
  email: '',
  realName: '',
  nickname: '',
  avatar: null,
  gender: '',
  birthday: '',
  height: null,
  weight: null
});

// 自定义校验规则
const validatePassword = (_rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'));
  } else if (value.length < 6) {
    callback(new Error('密码至少6个字符'));
  } else {
    // 如果确认密码已经输入，重新校验确认密码
    if (registerForm.confirmPassword) {
      registerFormRef.value.validateField('confirmPassword');
    }
    callback();
  }
};

const validateConfirmPassword = (_rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入密码'));
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'));
  } else {
    callback();
  }
};

const validatePhone = (_rule, value, callback) => {
  if (value && !/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号'));
  } else {
    callback();
  }
};

const validateEmail = (_rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入邮箱'));
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
    callback(new Error('请输入正确的邮箱格式'));
  } else {
    callback();
  }
};

// 表单校验规则
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'change' },
    { min: 3, max: 20, message: '用户名长度在3-20个字符', trigger: 'change' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'change' }
  ],
  password: [
    { required: true, validator: validatePassword, trigger: 'change' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'change' }
  ],
  phone: [
    { validator: validatePhone, trigger: 'change' }
  ],
  email: [
    { required: true, validator: validateEmail, trigger: 'change' }
  ],
  emailCode: [
    { required: true, message: '请输入邮箱验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位数字', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'change' },
    { min: 2, max: 20, message: '姓名长度在2-20个字符', trigger: 'change' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'change' },
    { min: 2, max: 20, message: '昵称长度在2-20个字符', trigger: 'change' }
  ]
};

const emailCountdown = ref(0);
const emailCodeLoading = ref(false);

const sendEmailCode = async () => {
  // 先校验邮箱
  registerFormRef.value.validateField('email', async (valid) => {
    if (valid) {
      emailCodeLoading.value = true;
      try {
        // 调用后端接口发送验证码
        console.log("注册邮箱为：",registerForm.email)
        await userApi.getEmailCode(registerForm.email);
        
        ElMessage.success('验证码已发送到您的邮箱');
        emailCountdown.value = 60;
        const timer = setInterval(() => {
          emailCountdown.value--;
          if (emailCountdown.value <= 0) {
            clearInterval(timer);
          }
        }, 1000);
      } catch (error) {
        console.error('发送验证码失败:', error);
        // 错误信息已在拦截器中显示
      } finally {
        emailCodeLoading.value = false;
      }
    } else {
      ElMessage.error('请先输入正确的邮箱');
    }
  });
};

const handleRegister = async () => {
  if (!registerFormRef.value) return;

  // 进行表单校验
  registerFormRef.value.validate(async (valid) => {
    if (valid) {

      loading.value = true;
      
      try {
        // 构建注册参数（匹配后端 RegistUserDTO）
        const registerData = {
          username: registerForm.username,
          password: registerForm.password,
          phone: registerForm.phone,
          email: registerForm.email || null,
          realName: registerForm.realName,
          nickName: registerForm.nickname,
          avatar: registerForm.avatar || null,
          gender: registerForm.gender === 'male' ? 1 : registerForm.gender === 'female' ? 2 : null,
          birthday: registerForm.birthday || null,
          height: registerForm.height || null,
          weight: registerForm.weight || null,
          userType: 4,
          code: registerForm.emailCode  // 默认注册为会员
        };
        
        // 调用注册接口
        const res = await userApi.register(registerData);
        
        // 注册成功
        ElMessage.success('注册成功，正在前往登录页');
        
        // 延迟跳转到登录页
        setTimeout(() => {
          router.push('/login');
        }, 1500);
        
      } catch (error) {
        // 注册失败，显示后端返回的错误信息
        console.error('注册失败:', error);
        // 错误信息已在拦截器中通过 ElMessage.error 显示
      } finally {
        loading.value = false;
      }
    } else {
      // 校验失败，弹窗提示
      ElMessage.warning('请检查表单填写是否正确');
      return false;
    }
  });
};

const goToLogin = () => {
  router.push('/login');
};

const handleAvatarChange = (file) => {
  // 验证文件大小（不超过2MB）
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!');
    return;
  }

  // 验证文件类型
  const isImage = file.raw.type.startsWith('image/');
  if (!isImage) {
    ElMessage.error('只能上传图片文件!');
    return;
  }

  const reader = new FileReader();
  reader.onload = (e) => {
    registerForm.avatar = e.target.result;
  };
  reader.readAsDataURL(file.raw);
};

const removeAvatar = () => {
  registerForm.avatar = null;
};
</script>

<template>
  <div class="register-container">
    <div class="register-card">
      <div class="header-section">
        <div class="icon-wrapper">
          <svg class="gym-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M20.57 14.86L22 13.43 20.57 12 17 15.57 8.43 7 12 3.43 10.57 2 9.14 3.43 7.71 2 5.57 4.14 4.14 2.71 2.71 4.14l1.43 1.43L2 7.71l1.43 1.43L2 10.57 3.43 12 7 8.43 15.57 17 12 20.57 13.43 22l1.43-1.43L16.29 22l2.14-2.14 1.43 1.43 1.43-1.43-1.43-1.43L22 16.29z" fill="currentColor"/>
          </svg>
        </div>
        <h2 class="form-title">加入我们</h2>
        <p class="form-subtitle">开启你的健身之旅</p>
      </div>

      <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          label-position="top"
          style="width: 100%;"
      >
        <!-- 基础信息区域 -->
        <div class="section-divider">
          <span class="divider-text">账号信息</span>
        </div>

        <el-form-item label="用户名" prop="username" class="form-item">
          <el-input 
            v-model="registerForm.username" 
            placeholder="请输入用户名" 
            size="large"
            prefix-icon="User"
          ></el-input>
        </el-form-item>

        <div class="form-row">
          <el-form-item label="密码" prop="password" class="form-item half-width">
            <el-input 
              type="password" 
              v-model="registerForm.password" 
              placeholder="请输入密码"
              size="large"
              prefix-icon="Lock"
              show-password
            ></el-input>
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPassword" class="form-item half-width">
            <el-input 
              type="password" 
              v-model="registerForm.confirmPassword" 
              placeholder="请再次输入密码"
              size="large"
              prefix-icon="Lock"
              show-password
            ></el-input>
          </el-form-item>
        </div>

        <el-form-item label="邮箱" prop="email" class="form-item">
          <el-input 
            v-model="registerForm.email" 
            placeholder="请输入邮箱"
            size="large"
            prefix-icon="Message"
          ></el-input>
        </el-form-item>

        <el-form-item label="邮箱验证码" prop="emailCode" class="form-item">
          <div class="sms-wrapper">
            <el-input 
              v-model="registerForm.emailCode" 
              placeholder="请输入邮箱验证码"
              size="large"
              prefix-icon="Key"
            ></el-input>
            <el-button 
              @click="sendEmailCode" 
              :disabled="emailCountdown > 0 || emailCodeLoading" 
              :loading="emailCodeLoading"
              size="large"
              class="sms-btn"
            >
              {{ emailCountdown > 0 ? `${emailCountdown}s` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>

        <el-form-item label="手机号（选填）" prop="phone" class="form-item">
          <el-input 
            v-model="registerForm.phone" 
            placeholder="请输入手机号"
            size="large"
            prefix-icon="Phone"
          ></el-input>
        </el-form-item>

        <!-- 个人信息区域 -->
        <div class="section-divider">
          <span class="divider-text">个人信息</span>
        </div>

        <div class="form-row">
          <el-form-item label="真实姓名" prop="realName" class="form-item half-width">
            <el-input 
              v-model="registerForm.realName" 
              placeholder="请输入真实姓名"
              size="large"
            ></el-input>
          </el-form-item>

          <el-form-item label="昵称" prop="nickname" class="form-item half-width">
            <el-input 
              v-model="registerForm.nickname" 
              placeholder="请输入昵称"
              size="large"
            ></el-input>
          </el-form-item>
        </div>

        <el-form-item label="头像（选填）" class="form-item">
          <div class="avatar-upload-wrapper">
            <el-upload
                class="avatar-uploader"
                action="#"
                :show-file-list="false"
                :on-change="handleAvatarChange"
                :auto-upload="false"
                :disabled="true"
            >
              <div class="avatar-box">
                <div class="avatar-placeholder">
                  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z" fill="currentColor"/>
                  </svg>
                  <span>暂未开放</span>
                </div>
              </div>
            </el-upload>
          </div>
        </el-form-item>

        <div class="form-row">
          <el-form-item label="性别（选填）" class="form-item half-width">
            <el-select v-model="registerForm.gender" placeholder="请选择性别" size="large">
              <el-option label="男" value="male"></el-option>
              <el-option label="女" value="female"></el-option>
              <el-option label="沃尔玛塑料袋" value="bag"></el-option>
              <el-option label="自定义" value="custom"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="生日（选填）" class="form-item half-width">
            <el-date-picker
                v-model="registerForm.birthday"
                type="date"
                placeholder="选择日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                size="large"
            >
            </el-date-picker>
          </el-form-item>
        </div>

        <div class="form-row">
          <el-form-item label="身高（选填）" class="form-item half-width">
            <el-input-number 
              v-model="registerForm.height" 
              :min="100" 
              :max="250" 
              size="large"
              controls-position="right"
              placeholder="cm"
            ></el-input-number>
          </el-form-item>

          <el-form-item label="体重（选填）" class="form-item half-width">
            <el-input-number 
              v-model="registerForm.weight" 
              :min="30" 
              :max="300" 
              size="large"
              controls-position="right"
              placeholder="kg"
            ></el-input-number>
          </el-form-item>
        </div>

        <el-button
            type="primary"
            @click="handleRegister"
            :loading="loading"
            class="main-btn"
            size="large"
        >
          {{ loading ? '注册中...' : '立即注册' }}
        </el-button>

        <div class="back-to-login">
          <span class="login-text">已有账号？</span>
          <el-button type="text" @click="goToLogin" class="login-link">立即登录</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.register-card {
  width: 100%;
  max-width: 650px;
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

/* 分隔线样式 */
.section-divider {
  position: relative;
  text-align: center;
  margin: 35px 0 25px;
}

.section-divider::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  width: 100%;
  height: 1px;
  background: linear-gradient(90deg, transparent, #e4e7ed, transparent);
}

.divider-text {
  position: relative;
  display: inline-block;
  padding: 0 20px;
  background: white;
  color: #909399;
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 1px;
}

/* 表单项样式 */
.form-item {
  margin-bottom: 20px;
}

.form-item :deep(.el-form-item__label) {
  color: #606266;
  font-weight: 500;
  font-size: 14px;
  margin-bottom: 8px;
}

.form-row {
  display: flex;
  gap: 16px;
}

.half-width {
  flex: 1;
}

/* 短信验证码样式 */
.sms-wrapper {
  display: flex;
  gap: 12px;
}

.sms-wrapper .el-input {
  flex: 1;
}

.sms-btn {
  min-width: 120px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  font-weight: 500;
}

.sms-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #5568d3 0%, #6a3f8f 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.sms-btn:disabled {
  background: #c0c4cc;
  color: white;
  cursor: not-allowed;
}

/* 头像上传样式 */
.avatar-upload-wrapper {
  display: flex;
  justify-content: center;
}

.avatar-container {
  position: relative;
  display: inline-block;
}

.avatar-uploader .el-upload {
  border: none;
  border-radius: 12px;
  cursor: pointer;
  overflow: hidden;
}

.avatar-box {
  width: 120px;
  height: 120px;
  border: 3px dashed #dcdfe6;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
  background: #fafafa;
}

.avatar-box:hover {
  border-color: #667eea;
  background: #f5f7fa;
  transform: scale(1.05);
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #8c939d;
}

.avatar-placeholder svg {
  width: 40px;
  height: 40px;
  margin-bottom: 8px;
}

.avatar-placeholder span {
  font-size: 13px;
}

.avatar-delete {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 24px;
  height: 24px;
  background: #ff4d4f;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(255, 77, 79, 0.4);
}

.avatar-delete:hover {
  background: #ff7875;
  transform: scale(1.1);
}

.avatar-delete svg {
  width: 14px;
  height: 14px;
  color: white;
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

.form-item :deep(.el-select) {
  width: 100%;
}

.form-item :deep(.el-input-number) {
  width: 100%;
}

.form-item :deep(.el-date-editor) {
  width: 100%;
}

/* 主按钮样式 */
.main-btn {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  margin-top: 30px;
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

/* 返回登录样式 */
.back-to-login {
  text-align: center;
  margin-top: 25px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.login-text {
  color: #909399;
  font-size: 14px;
  margin-right: 5px;
}

.login-link {
  font-size: 14px;
  font-weight: 500;
  color: #667eea;
  padding: 0;
}

.login-link:hover {
  color: #764ba2;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-card {
    padding: 35px 25px;
  }

  .form-row {
    flex-direction: column;
    gap: 0;
  }

  .half-width {
    width: 100%;
  }

  .form-title {
    font-size: 26px;
  }
}

</style>