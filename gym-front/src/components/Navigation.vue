<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute } from 'vue-router'
import { usePermissionStore } from '@/stores/permission'
import { userApi } from '@/api/modules/user'
import { menuConfig } from '@/utils/menuConfig'

const route = useRoute()
const permissionStore = usePermissionStore()

// 动态面包屑
const breadcrumbs = computed(() => {
  const path = route.path
  // 在 menuConfig 中找到当前路径对应的菜单
  const currentEntry = Object.entries(menuConfig).find(([, cfg]) => cfg.path === path)
  if (!currentEntry) return [{ title: '首页', path: '/' }]
  const [, current] = currentEntry
  const items = [{ title: '首页', path: '/' }]
  // 如果有父级菜单
  if (current.parent) {
    const parentCfg = menuConfig[current.parent]
    if (parentCfg) items.push({ title: parentCfg.title, path: '' })
  }
  items.push({ title: current.title, path: '' })
  return items
})

const userInfo = computed(() => {
  const storeUser = permissionStore.userInfo
  if (storeUser && storeUser.username) {
    return {
      name: storeUser.nickName || storeUser.realName || storeUser.username,
      avatar: storeUser.avatar || '',
      role: getUserRole(storeUser.userType),
      email: storeUser.email || ''
    }
  }
  const localUser = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return {
    name: localUser.nickName || localUser.realName || localUser.username || '用户',
    avatar: localUser.avatar || '',
    role: getUserRole(localUser.userType),
    email: localUser.email || ''
  }
})

const getUserRole = (userType) => {
  const roleMap = { 1: '超级管理员', 2: '管理员', 3: '教练', 4: '会员' }
  return roleMap[userType] || '用户'
}

const avatarText = computed(() => (userInfo.value.name || '?').charAt(0))

// 修改密码弹窗
const passwordDialogVisible = ref(false)
const passwordForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })

const openPasswordDialog = () => {
  passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  passwordDialogVisible.value = true
}

const submitChangePassword = async () => {
  if (!passwordForm.value.oldPassword) { ElMessage.warning('请输入旧密码'); return }
  if (!passwordForm.value.newPassword) { ElMessage.warning('请输入新密码'); return }
  if (passwordForm.value.newPassword.length < 6) { ElMessage.warning('新密码不能少于6位'); return }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) { ElMessage.warning('两次输入的密码不一致'); return }
  try {
    const localUser = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const res = await userApi.changePassword({
      username: localUser.username,
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    if (res.resCode === '00') {
      ElMessage.success(res.resMsg || '密码修改成功，请重新登录')
      passwordDialogVisible.value = false
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      permissionStore.clearPermission()
      setTimeout(() => { window.location.href = '/login' }, 1000)
    } else {
      ElMessage.warning(res.resMsg || '修改密码失败')
    }
  } catch (e) {
    ElMessage.error('修改密码失败，请稍后重试')
  }
}

const handleCommand = (command) => {
  if (command === 'changePassword') {
    openPasswordDialog()
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    }).then(async () => {
      try {
        const localUser = JSON.parse(localStorage.getItem('userInfo') || '{}')
        await userApi.logout({ id: localUser.id })
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        permissionStore.clearPermission()
        ElMessage.success('退出成功')
        setTimeout(() => { window.location.href = '/login' }, 500)
      } catch (e) {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        permissionStore.clearPermission()
        window.location.href = '/login'
      }
    }).catch(() => {})
  }
}
</script>

<template>
  <div class="navbar">
    <div class="navbar-left">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="index" :to="item.path ? { path: item.path } : undefined">
          {{ item.title }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="navbar-center">
      <span class="navbar-slogan">自律给我自由 · 每一滴汗水都是对平庸的反抗 · NEVER GIVE UP</span>
    </div>

    <div class="navbar-right">
      <el-dropdown @command="handleCommand" class="user-dropdown">
        <div class="user-info">
          <el-avatar v-if="userInfo.avatar" :src="userInfo.avatar" :size="36" />
          <el-avatar v-else :size="36" class="avatar-default">{{ avatarText }}</el-avatar>
          <div class="user-details">
            <span class="user-name">{{ userInfo.name }}</span>
            <span class="user-role">{{ userInfo.role }}</span>
          </div>
          <el-icon class="arrow-icon"><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="changePassword">
              <el-icon><Lock /></el-icon>
              修改密码
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="420px" :close-on-click-modal="false">
      <el-form :model="passwordForm" label-width="90px">
        <el-form-item label="旧密码" required>
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入旧密码" show-password />
        </el-form-item>
        <el-form-item label="新密码" required>
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码（至少6位）" show-password />
        </el-form-item>
        <el-form-item label="确认密码" required>
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitChangePassword">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.navbar {
  height: 60px;
  background: linear-gradient(135deg, #2a2d3a 0%, #25272f 100%);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.2);
  position: fixed;
  top: 0;
  left: 250px;
  right: 0;
  z-index: 999;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}
.navbar-left { flex-shrink: 0; }
.navbar-center { flex: 1; text-align: center; }
.navbar-slogan { font-size: 13px; color: #7f849c; font-weight: 500; letter-spacing: 1.5px; white-space: nowrap; }
.navbar-left :deep(.el-breadcrumb__inner) { color: #a6adc8; font-weight: 500; }
.navbar-left :deep(.el-breadcrumb__inner:hover) { color: #89b4fa; }
.navbar-left :deep(.el-breadcrumb__separator) { color: #7f849c; }
.navbar-right { display: flex; align-items: center; gap: 20px; }

.user-dropdown { cursor: pointer; }
.user-info {
  display: flex; align-items: center; gap: 10px;
  padding: 5px 12px; border-radius: 20px; transition: all 0.3s;
  background: rgba(255, 255, 255, 0.05);
}
.user-info:hover {
  background: rgba(102, 126, 234, 0.15);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}
.avatar-default { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: #fff; font-size: 16px; font-weight: 700; }
.user-details { display: flex; flex-direction: column; align-items: flex-start; }
.user-name { font-size: 14px; font-weight: 600; color: #cdd6f4; line-height: 1.2; }
.user-role { font-size: 12px; color: #9399b2; line-height: 1.2; }
.arrow-icon { color: #7f849c; transition: transform 0.3s; }
.user-dropdown:hover .arrow-icon { transform: rotate(180deg); color: #89b4fa; }
</style>
