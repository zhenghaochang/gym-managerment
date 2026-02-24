<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Edit, Male, Female } from '@element-plus/icons-vue'
import { memberApi } from '@/api/modules/member'

const loading = ref(false)
const editing = ref(false)

const genderMap = { 0: '未知', 1: '男', 2: '女' }
const userTypeMap = { 1: '超级管理员', 2: '管理员', 3: '教练', 4: '会员' }

const userInfo = ref({})
const editForm = ref({})

const genderText = computed(() => genderMap[userInfo.value.gender] || '未知')
const userTypeText = computed(() => userTypeMap[userInfo.value.userType] || '未知')
const avatarText = computed(() => (userInfo.value.realName || userInfo.value.username || '?').charAt(0))

const fetchUserInfo = async () => {
  loading.value = true
  try {
    const res = await memberApi.getMyCenterInfo()
    if (res.resCode === '00' && res.result) {
      userInfo.value = res.result
    } else {
      ElMessage.error(res.resMsg || '获取个人信息失败')
    }
  } catch (e) {
    console.error('获取个人信息失败:', e)
  } finally {
    loading.value = false
  }
}

const startEdit = () => {
  editForm.value = {
    nickName: userInfo.value.nickName,
    phone: userInfo.value.phone,
    email: userInfo.value.email,
    gender: userInfo.value.gender,
    birthday: userInfo.value.birthday,
    height: userInfo.value.height,
    weight: userInfo.value.weight
  }
  editing.value = true
}

const cancelEdit = () => { editing.value = false }

const saveEdit = async () => {
  try {
    const data = {
      id: userInfo.value.id,
      username: userInfo.value.username,
      password: userInfo.value.password,
      email: editForm.value.email,
      phone: editForm.value.phone,
      realName: userInfo.value.realName,
      nickName: editForm.value.nickName,
      avatar: userInfo.value.avatar,
      gender: editForm.value.gender,
      birthday: editForm.value.birthday || null,
      height: editForm.value.height,
      weight: editForm.value.weight,
      userType: userInfo.value.userType,
      status: userInfo.value.status
    }
    const res = await memberApi.updateMyCenterInfo(data)
    if (res.resCode === '00') {
      ElMessage.success(res.resMsg || '修改成功')
      editing.value = false
      await fetchUserInfo()
    } else {
      ElMessage.warning(res.resMsg || '修改失败')
    }
  } catch (e) {
    console.error('修改个人信息失败:', e)
    ElMessage.error('修改失败，请稍后重试')
  }
}

onMounted(() => { fetchUserInfo() })
</script>

<template>
  <div class="center-container" v-loading="loading">
    <div class="page-header">
      <h1 class="page-title"><el-icon><User /></el-icon> 个人中心</h1>
      <p class="page-subtitle">查看和编辑个人信息</p>
    </div>

    <div class="profile-layout">
      <!-- 左侧头像卡片 -->
      <el-card class="avatar-card" shadow="never">
        <div class="avatar-section">
          <div class="avatar-wrapper">
            <el-avatar v-if="userInfo.avatar" :size="100" :src="userInfo.avatar" />
            <el-avatar v-else :size="100" class="avatar-default">{{ avatarText }}</el-avatar>
          </div>
          <h2 class="user-realname">{{ userInfo.realName }}</h2>
          <p class="user-nickname">{{ userInfo.nickName || '暂无昵称' }}</p>
          <el-tag :type="userInfo.status === 1 ? 'success' : 'danger'" size="small">{{ userInfo.status === 1 ? '正常' : '停用' }}</el-tag>
          <div class="user-meta">
            <div class="meta-item">
              <span class="meta-label">角色</span>
              <span class="meta-value">{{ userTypeText }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">注册时间</span>
              <span class="meta-value">{{ userInfo.createTime }}</span>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 右侧信息卡片 -->
      <el-card class="info-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span class="card-title">基本信息</span>
            <el-button v-if="!editing" type="primary" :icon="Edit" size="small" @click="startEdit">编辑</el-button>
            <div v-else class="edit-actions">
              <el-button size="small" @click="cancelEdit">取消</el-button>
              <el-button type="primary" size="small" @click="saveEdit">保存</el-button>
            </div>
          </div>
        </template>

        <!-- 查看模式 -->
        <div v-if="!editing" class="info-grid">
          <div class="info-item">
            <span class="info-label">用户名</span>
            <span class="info-value">{{ userInfo.username }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">真实姓名</span>
            <span class="info-value">{{ userInfo.realName }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">昵称</span>
            <span class="info-value">{{ userInfo.nickName || '—' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">性别</span>
            <span class="info-value">
              <el-icon v-if="userInfo.gender === 1" style="color: #409eff;"><Male /></el-icon>
              <el-icon v-else-if="userInfo.gender === 2" style="color: #f56c6c;"><Female /></el-icon>
              {{ genderText }}
            </span>
          </div>
          <div class="info-item">
            <span class="info-label">手机号</span>
            <span class="info-value">{{ userInfo.phone || '—' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">邮箱</span>
            <span class="info-value">{{ userInfo.email || '—' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">生日</span>
            <span class="info-value">{{ userInfo.birthday || '—' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">身高</span>
            <span class="info-value">{{ userInfo.height ? userInfo.height + ' cm' : '—' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">体重</span>
            <span class="info-value">{{ userInfo.weight ? userInfo.weight + ' kg' : '—' }}</span>
          </div>
        </div>

        <!-- 编辑模式 -->
        <el-form v-else :model="editForm" label-width="80px" class="edit-form">
          <el-form-item label="昵称">
            <el-input v-model="editForm.nickName" placeholder="请输入昵称" maxlength="50" />
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group v-model="editForm.gender">
              <el-radio :value="1">男</el-radio>
              <el-radio :value="2">女</el-radio>
              <el-radio :value="0">未知</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="editForm.phone" placeholder="请输入手机号" maxlength="20" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="editForm.email" placeholder="请输入邮箱" maxlength="100" />
          </el-form-item>
          <el-form-item label="生日">
            <el-date-picker v-model="editForm.birthday" type="date" placeholder="选择生日" value-format="YYYY-MM-DD" style="width: 100%" />
          </el-form-item>
          <el-form-item label="身高(cm)">
            <el-input-number v-model="editForm.height" :min="0" :max="300" :precision="1" :step="0.5" style="width: 100%" />
          </el-form-item>
          <el-form-item label="体重(kg)">
            <el-input-number v-model="editForm.weight" :min="0" :max="500" :precision="1" :step="0.5" style="width: 100%" />
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.center-container { padding: 20px; background: #f5f7fa; min-height: calc(100vh - 60px); }
.page-header { margin-bottom: 20px; padding-bottom: 20px; border-bottom: 1px solid #e8e8e8; }
.page-title { display: flex; align-items: center; gap: 12px; font-size: 28px; font-weight: 900; color: #1a1a2e; margin: 0 0 8px 0; }
.page-subtitle { font-size: 14px; color: #666; margin: 0; }

.profile-layout { display: grid; grid-template-columns: 280px 1fr; gap: 20px; }

.avatar-card { border-radius: 12px; border: none; }
.avatar-card :deep(.el-card__body) { padding: 32px 24px; }
.avatar-section { display: flex; flex-direction: column; align-items: center; text-align: center; }
.avatar-wrapper { margin-bottom: 16px; }
.avatar-default { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: #fff; font-size: 36px; font-weight: 700; }
.user-realname { font-size: 22px; font-weight: 700; color: #1a1a2e; margin: 0 0 4px 0; }
.user-nickname { font-size: 14px; color: #909399; margin: 0 0 12px 0; }
.user-meta { width: 100%; margin-top: 20px; padding-top: 20px; border-top: 1px solid #ebeef5; }
.meta-item { display: flex; justify-content: space-between; padding: 8px 0; }
.meta-label { font-size: 13px; color: #909399; }
.meta-value { font-size: 13px; color: #303133; font-weight: 500; }

.info-card { border-radius: 12px; border: none; }
.info-card :deep(.el-card__header) { padding: 16px 24px; border-bottom: 1px solid #f0f0f0; }
.info-card :deep(.el-card__body) { padding: 24px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { font-size: 16px; font-weight: 700; color: #1a1a2e; }
.edit-actions { display: flex; gap: 8px; }

.info-grid { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 24px; }
.info-item { display: flex; flex-direction: column; gap: 6px; padding: 12px 16px; background: #fafbfc; border-radius: 8px; border: 1px solid #f0f0f0; }
.info-label { font-size: 12px; color: #909399; }
.info-value { font-size: 15px; color: #303133; font-weight: 500; display: flex; align-items: center; gap: 4px; }

.edit-form { max-width: 500px; }

@media (max-width: 768px) {
  .profile-layout { grid-template-columns: 1fr; }
  .info-grid { grid-template-columns: 1fr 1fr; }
}
</style>
