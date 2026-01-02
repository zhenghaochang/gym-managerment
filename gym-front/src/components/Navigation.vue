<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { usePermissionStore } from '@/stores/permission';

const permissionStore = usePermissionStore();

// 从 store 或 localStorage 获取用户信息
const userInfo = computed(() => {
  const storeUser = permissionStore.userInfo;
  if (storeUser && storeUser.username) {
    return {
      name: storeUser.nickName || storeUser.realName || storeUser.username,
      avatar: storeUser.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
      role: getUserRole(storeUser.userType),
      email: storeUser.email || ''
    };
  }
  // 尝试从 localStorage 获取
  const localUser = JSON.parse(localStorage.getItem('userInfo') || '{}');
  return {
    name: localUser.nickName || localUser.realName || localUser.username || '用户',
    avatar: localUser.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    role: getUserRole(localUser.userType),
    email: localUser.email || ''
  };
});

// 根据 userType 获取角色名称
const getUserRole = (userType) => {
  const roleMap = {
    1: '超级管理员',
    2: '管理员',
    3: '教练',
    4: '会员'
  };
  return roleMap[userType] || '用户';
};

// 通知数据
const notifications = ref([
  { id: 1, title: '新会员注册', content: '李四刚刚注册成为会员', time: '5分钟前', read: false },
  { id: 2, title: '课程预约', content: '王五预约了明天的瑜伽课', time: '10分钟前', read: false },
  { id: 3, title: '器材维护提醒', content: '跑步机需要进行定期维护', time: '1小时前', read: true }
]);

const unreadCount = ref(notifications.value.filter(n => !n.read).length);

// 处理通知点击
const handleNotificationClick = (notification) => {
  notification.read = true;
  unreadCount.value = notifications.value.filter(n => !n.read).length;
  ElMessage.info(`查看通知：${notification.title}`);
};

// 处理用户菜单点击
const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      ElMessage.info('个人中心');
      break;
    case 'settings':
      ElMessage.info('账号设置');
      break;
    case 'logout':
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 清除登录状态
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        
        // 清除 store 中的权限信息
        import('@/stores/permission').then(({ usePermissionStore }) => {
          const permissionStore = usePermissionStore();
          permissionStore.clearPermission();
          
          ElMessage.success('退出成功');
          
          // 跳转到登录页
          setTimeout(() => {
            window.location.href = '/login';
          }, 500);
        });
      }).catch(() => {
        // 取消退出
      });
      break;
  }
};

// 全屏切换
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen();
    ElMessage.success('已进入全屏模式');
  } else {
    document.exitFullscreen();
    ElMessage.success('已退出全屏模式');
  }
};
</script>

<template>
  <div class="navbar">
    <div class="navbar-left">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>工作台</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="navbar-right">
      <!-- 搜索框 -->
      <div class="search-box">
        <el-input
          placeholder="搜索会员、课程..."
          prefix-icon="Search"
          size="default"
          style="width: 200px"
        />
      </div>

      <!-- 全屏按钮 -->
      <el-tooltip content="全屏" placement="bottom">
        <div class="navbar-icon" @click="toggleFullscreen">
          <el-icon :size="20"><FullScreen /></el-icon>
        </div>
      </el-tooltip>

      <!-- 通知 -->
      <el-popover placement="bottom" :width="320" trigger="click">
        <template #reference>
          <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="navbar-icon">
            <el-icon :size="20"><Bell /></el-icon>
          </el-badge>
        </template>
        <div class="notification-panel">
          <div class="notification-header">
            <span>通知中心</span>
            <el-button type="text" size="small">全部已读</el-button>
          </div>
          <div class="notification-list">
            <div
              v-for="item in notifications"
              :key="item.id"
              class="notification-item"
              :class="{ unread: !item.read }"
              @click="handleNotificationClick(item)"
            >
              <div class="notification-title">{{ item.title }}</div>
              <div class="notification-content">{{ item.content }}</div>
              <div class="notification-time">{{ item.time }}</div>
            </div>
          </div>
          <div class="notification-footer">
            <el-button type="text" size="small">查看全部</el-button>
          </div>
        </div>
      </el-popover>

      <!-- 用户信息 -->
      <el-dropdown @command="handleCommand" class="user-dropdown">
        <div class="user-info">
          <el-avatar :src="userInfo.avatar" :size="36" />
          <div class="user-details">
            <span class="user-name">{{ userInfo.name }}</span>
            <span class="user-role">{{ userInfo.role }}</span>
          </div>
          <el-icon class="arrow-icon"><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <el-icon><User /></el-icon>
              个人中心
            </el-dropdown-item>
            <el-dropdown-item command="settings">
              <el-icon><Setting /></el-icon>
              账号设置
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
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
  backdrop-filter: blur(10px);
}

.navbar-left {
  flex: 1;
}

.navbar-left :deep(.el-breadcrumb__inner) {
  color: #a6adc8;
  font-weight: 500;
}

.navbar-left :deep(.el-breadcrumb__inner:hover) {
  color: #89b4fa;
}

.navbar-left :deep(.el-breadcrumb__separator) {
  color: #7f849c;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.search-box {
  margin-right: 10px;
}

.search-box :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.08);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 20px;
  transition: all 0.3s;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.search-box :deep(.el-input__wrapper:hover) {
  background: rgba(255, 255, 255, 0.12);
  box-shadow: 0 2px 12px rgba(102, 126, 234, 0.2);
  border-color: rgba(137, 180, 250, 0.3);
}

.search-box :deep(.el-input__wrapper.is-focus) {
  background: rgba(255, 255, 255, 0.15);
  box-shadow: 0 2px 12px rgba(137, 180, 250, 0.3);
  border-color: rgba(137, 180, 250, 0.5);
}

.search-box :deep(.el-input__inner) {
  color: #cdd6f4;
}

.search-box :deep(.el-input__inner::placeholder) {
  color: #7f849c;
}

.search-box :deep(.el-icon) {
  color: #7f849c;
}

.navbar-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 50%;
  transition: all 0.3s;
  color: #a6adc8;
  background: rgba(255, 255, 255, 0.05);
}

.navbar-icon:hover {
  background: rgba(102, 126, 234, 0.15);
  color: #89b4fa;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

/* 通知面板 */
.notification-panel {
  max-height: 400px;
  display: flex;
  flex-direction: column;
  background: #2a2d3a;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  font-weight: 600;
  color: #cdd6f4;
}

.notification-list {
  flex: 1;
  overflow-y: auto;
  max-height: 300px;
}

.notification-item {
  padding: 12px 16px;
  cursor: pointer;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  transition: all 0.3s;
}

.notification-item:hover {
  background: rgba(102, 126, 234, 0.1);
}

.notification-item.unread {
  background: linear-gradient(90deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.1) 100%);
  border-left: 3px solid #89b4fa;
}

.notification-title {
  font-weight: 600;
  color: #cdd6f4;
  margin-bottom: 4px;
}

.notification-content {
  font-size: 13px;
  color: #a6adc8;
  margin-bottom: 4px;
}

.notification-time {
  font-size: 12px;
  color: #7f849c;
}

.notification-footer {
  padding: 8px 16px;
  text-align: center;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

/* 用户信息 */
.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 5px 12px;
  border-radius: 20px;
  transition: all 0.3s;
  background: rgba(255, 255, 255, 0.05);
}

.user-info:hover {
  background: rgba(102, 126, 234, 0.15);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.user-details {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #cdd6f4;
  line-height: 1.2;
}

.user-role {
  font-size: 12px;
  color: #9399b2;
  line-height: 1.2;
}

.arrow-icon {
  color: #7f849c;
  transition: transform 0.3s;
}

.user-dropdown:hover .arrow-icon {
  transform: rotate(180deg);
  color: #89b4fa;
}
</style>
