<script setup>
import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  phone: '',
  smsCode: '',
  email: '',
  realName: '',
  nickname: '',
  avatar: null,
  gender: '',
  birthday: '',
  height: null,
  weight: null
});

const smsCountdown = ref(0);
const sendSmsCode = () => {
  if (!registerForm.phone) {
    ElMessage.error('请先填写手机号');
    return;
  }
  ElMessage.success('短信验证码已发送');
  smsCountdown.value = 60;
  const timer = setInterval(() => {
    smsCountdown.value--;
    if (smsCountdown.value <= 0) {
      clearInterval(timer);
    }
  }, 1000);
};

const handleRegister = async () => {
  try {
    if (!registerForm.username) {
      ElMessage.error('请输入用户名');
      return;
    }
    if (!registerForm.password) {
      ElMessage.error('请输入密码');
      return;
    }
    if (!registerForm.confirmPassword) {
      ElMessage.error('请确认密码');
      return;
    }
    if (!registerForm.phone) {
      ElMessage.error('请输入手机号');
      return;
    }
    if (!registerForm.smsCode) {
      ElMessage.error('请输入短信验证码');
      return;
    }
    if (!registerForm.realName) {
      ElMessage.error('请输入真实姓名');
      return;
    }
    if (!registerForm.nickname) {
      ElMessage.error('请输入昵称');
      return;
    }

    if (registerForm.password !== registerForm.confirmPassword) {
      ElMessage.error('两次输入密码不一致');
      return;
    }

    ElMessage.success('注册成功');
    console.log('注册信息:', registerForm);
    // 这里应该是路由跳转到登录页
    window.location.href = 'login';
  } catch (error) {
    console.error('注册失败:', error);
  }
};

const goToLogin = () => {
  // 这里应该是路由跳转到登录页
  window.location.href = 'login';
};

const handleAvatarChange = (file) => {
  const reader = new FileReader();
  reader.onload = (e) => {
    registerForm.avatar = e.target.result;
  };
  reader.readAsDataURL(file.raw);
};
</script>

<template>
  <div class="register-container">
    <div class="register-card">
      <h2 class="form-title">用户注册</h2>
      <el-form
          :model="registerForm"
          label-width="90px"
          style="width: 100%;"
      >
        <el-form-item label="用户名" class="form-item">
          <el-input v-model="registerForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>

        <el-form-item label="密码" class="form-item">
          <el-input type="password" v-model="registerForm.password" placeholder="请输入密码"></el-input>
        </el-form-item>

        <el-form-item label="确认密码" class="form-item">
          <el-input type="password" v-model="registerForm.confirmPassword" placeholder="请再次输入密码"></el-input>
        </el-form-item>

        <el-form-item label="手机号" class="form-item">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>

        <el-form-item label="短信验证码" class="form-item">
          <div style="display: flex;">
            <el-input v-model="registerForm.smsCode" placeholder="请输入短信验证码" style="flex: 1; margin-right: 12px;"></el-input>
            <el-button @click="sendSmsCode" :disabled="smsCountdown > 0" size="default">
              {{ smsCountdown > 0 ? `${smsCountdown}s后重发` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>

        <el-form-item label="邮箱" class="form-item">
          <el-input v-model="registerForm.email" placeholder="选填，可用于找回密码"></el-input>
        </el-form-item>

        <el-form-item label="真实姓名" class="form-item">
          <el-input v-model="registerForm.realName" placeholder="请输入真实姓名"></el-input>
        </el-form-item>

        <el-form-item label="昵称" class="form-item">
          <el-input v-model="registerForm.nickname" placeholder="请输入昵称"></el-input>
        </el-form-item>

        <el-form-item label="头像" class="form-item">
          <div class="avatar-uploader">
            <el-upload
                class="avatar-uploader"
                action="#"
                :show-file-list="false"
                :on-change="handleAvatarChange"
                :auto-upload="false"
            >
              <img v-if="registerForm.avatar" :src="registerForm.avatar" class="avatar">
              <i v-else class="el-icon-plus avatar-uploader-icon">+</i>
            </el-upload>
          </div>
        </el-form-item>

        <el-form-item label="性别" class="form-item">
          <el-select v-model="registerForm.gender" placeholder="请选择性别" style="width: 100%;">
            <el-option label="男" value="male"></el-option>
            <el-option label="女" value="female"></el-option>
            <el-option label="沃尔玛塑料袋" value="bag"></el-option>
            <el-option label="自定义" value="custom"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="生日" class="form-item">
          <el-date-picker
              v-model="registerForm.birthday"
              type="date"
              placeholder="选择日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%;"
          >
          </el-date-picker>
        </el-form-item>

        <el-form-item label="身高(cm)" class="form-item">
          <el-input-number v-model="registerForm.height" :min="100" :max="250" style="width: 100%;"></el-input-number>
        </el-form-item>

        <el-form-item label="体重(kg)" class="form-item">
          <el-input-number v-model="registerForm.weight" :min="30" :max="300" style="width: 100%;"></el-input-number>
        </el-form-item>

        <el-button
            type="primary"
            @click="handleRegister"
            class="main-btn"
            size="large"
        >
          注册
        </el-button>

        <div class="back-to-login">
          <el-button type="text" @click="goToLogin" size="small">已有账号？去登录</el-button>
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
  padding: 20px;
}

.register-card {
  width: 100%;
  max-width: 500px;
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

.avatar-uploader {
  text-align: center;
  margin-bottom: 25px;
}

.avatar-uploader .el-upload {
  border: 2px dashed #d9d9d9;
  border-radius: 10px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 32px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}

.avatar {
  width: 120px;
  height: 120px;
  display: block;
  object-fit: cover;
}

.main-btn {
  width: 100%;
  height: 45px;
  font-size: 16px;
  margin-top: 5px;
}

.back-to-login {
  text-align: center;
  margin-top: 25px;
}
</style>