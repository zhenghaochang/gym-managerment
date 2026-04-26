<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const userInfo = computed(() => {
  const localUser = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return {
    name: localUser.nickName || localUser.realName || localUser.username || '管理员',
    userType: localUser.userType
  }
})

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 6) return '夜间值班中'
  if (h < 12) return '早上好'
  if (h < 14) return '中午好'
  if (h < 18) return '下午好'
  return '晚上好'
})

const todayStr = computed(() => {
  const d = new Date()
  const weekMap = ['日', '一', '二', '三', '四', '五', '六']
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日 星期${weekMap[d.getDay()]}`
})

const roleName = computed(() => {
  const map = { 1: '超级管理员', 2: '管理员', 3: '教练' }
  return map[userInfo.value.userType] || '管理员'
})

const stats = ref([
  { label: '会员总数', value: '--', icon: 'User', color: '#409eff', path: '/member/list' },
  { label: '课程总数', value: '--', icon: 'Reading', color: '#67c23a', path: '/course/setting' },
  { label: '待审批', value: '--', icon: 'DocumentChecked', color: '#e6a23c', path: '/member/application' },
  { label: '今日订单', value: '--', icon: 'ShoppingCart', color: '#f56c6c', path: '/finance/order' }
])

const quickLinks = [
  { title: '会员管理', icon: 'User', path: '/member/list', desc: '查看和管理会员信息' },
  { title: '退课审批', icon: 'DocumentChecked', path: '/member/application', desc: '处理退课和取消申请' },
  { title: '课程设置', icon: 'Setting', path: '/course/setting', desc: '管理课程信息' },
  { title: '排课管理', icon: 'Calendar', path: '/course/schedule', desc: '安排课程排班' },
  { title: '订单管理', icon: 'ShoppingCart', path: '/finance/order', desc: '查看订单记录' },
  { title: '器械管理', icon: 'Box', path: '/equipment/list', desc: '管理健身器械' }
]

const goTo = (path) => router.push(path)
onMounted(() => {})
</script>

<template>
  <div class="admin-dashboard">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-left">
        <div class="welcome-badge">{{ roleName }}</div>
        <h1 class="welcome-title">{{ greeting }}，{{ userInfo.name }}</h1>
        <p class="welcome-date">{{ todayStr }}</p>
      </div>
      <div class="welcome-right">
        <span class="welcome-deco">GYM</span>
        <span class="welcome-deco welcome-deco-2">ADMIN</span>
      </div>
    </div>

    <!-- 数据概览 -->
    <!-- <div class="stats-row">
      <div v-for="item in stats" :key="item.label" class="stat-item" @click="goTo(item.path)">
        <div class="stat-icon" :style="{ background: item.color + '15', color: item.color }">
          <el-icon :size="24"><component :is="item.icon" /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ item.value }}</span>
          <span class="stat-label">{{ item.label }}</span>
        </div>
      </div>
    </div> -->

    <!-- 快捷操作 -->
    <div class="section-header">
      <span class="section-title">快捷操作</span>
    </div>
    <div class="quick-grid">
      <div v-for="item in quickLinks" :key="item.path" class="quick-item" @click="goTo(item.path)">
        <div class="quick-icon-wrap">
          <el-icon :size="26"><component :is="item.icon" /></el-icon>
        </div>
        <div class="quick-text">
          <span class="quick-name">{{ item.title }}</span>
          <span class="quick-desc">{{ item.desc }}</span>
        </div>
        <el-icon class="quick-arrow"><ArrowRight /></el-icon>
      </div>
    </div>
  </div>
</template>

<style scoped>
.admin-dashboard {
  padding: 0;
  min-height: 100vh;
  background: #f5f7fa;
}

/* ===== 欢迎横幅 ===== */
.welcome-banner {
  background: linear-gradient(135deg, #2c3e6b 0%, #34527a 50%, #3a6ea5 100%);
  border-radius: 0 0 16px 16px;
  padding: 36px 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  position: relative;
  overflow: hidden;
}
.welcome-badge {
  display: inline-block;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  padding: 3px 12px;
  border-radius: 3px;
  letter-spacing: 1px;
  margin-bottom: 10px;
}
.welcome-title {
  font-size: 26px;
  font-weight: 800;
  color: #fff;
  margin: 0 0 6px;
}
.welcome-date {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.55);
  margin: 0;
}
.welcome-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}
.welcome-deco {
  font-size: 72px;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.05);
  line-height: 0.9;
  letter-spacing: 6px;
  user-select: none;
}
.welcome-deco-2 {
  color: rgba(255, 255, 255, 0.08);
}

/* ===== 数据概览 ===== */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  padding: 0 24px;
  margin-bottom: 28px;
}
.stat-item {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: all 0.25s;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}
.stat-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}
.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.stat-info {
  display: flex;
  flex-direction: column;
}
.stat-value {
  font-size: 28px;
  font-weight: 800;
  color: #303133;
  line-height: 1.2;
}
.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 2px;
}

/* ===== 区域标题 ===== */
.section-header {
  padding: 0 24px;
  margin-bottom: 14px;
}
.section-title {
  font-size: 18px;
  font-weight: 700;
  color: #303133;
}

/* ===== 快捷操作 ===== */
.quick-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
  padding: 0 24px 40px;
}
.quick-item {
  background: #fff;
  border-radius: 12px;
  padding: 18px 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  cursor: pointer;
  transition: all 0.25s;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}
.quick-item:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}
.quick-item:hover .quick-arrow {
  color: #409eff;
}
.quick-icon-wrap {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  background: #ecf5ff;
  color: #409eff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.quick-text {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.quick-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}
.quick-desc {
  font-size: 12px;
  color: #909399;
}
.quick-arrow {
  color: #c0c4cc;
  transition: all 0.25s;
}

/* ===== 响应式 ===== */
@media (max-width: 1200px) {
  .stats-row { grid-template-columns: repeat(2, 1fr); }
  .quick-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 768px) {
  .stats-row { grid-template-columns: 1fr; }
  .quick-grid { grid-template-columns: 1fr; }
  .welcome-right { display: none; }
}
</style>
