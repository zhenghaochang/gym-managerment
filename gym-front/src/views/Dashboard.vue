<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { memberApi } from '@/api/modules/member'

const router = useRouter()

const userInfo = computed(() => {
  const localUser = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return {
    name: localUser.nickName || localUser.realName || localUser.username || '战士',
    realName: localUser.realName || '',
    userType: localUser.userType
  }
})

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 6) return '深夜训练'
  if (h < 12) return '晨练时刻'
  if (h < 14) return '午间充能'
  if (h < 18) return '下午突破'
  return '夜间征服'
})

const todayStr = computed(() => {
  const d = new Date()
  const weekMap = ['日', '一', '二', '三', '四', '五', '六']
  return `${d.getFullYear()}.${String(d.getMonth() + 1).padStart(2, '0')}.${String(d.getDate()).padStart(2, '0')} 星期${weekMap[d.getDay()]}`
})

const stats = ref({ cardCount: 0, courseCount: 0, bookingCount: 0, orderCount: 0 })

const loadStats = async () => {
  try {
    const [cardRes, courseRes, orderRes] = await Promise.allSettled([
      memberApi.getMyCardList({ userId: JSON.parse(localStorage.getItem('userInfo') || '{}').id }),
      memberApi.getMyCourseList(),
      memberApi.getMyOrderList()
    ])
    if (cardRes.status === 'fulfilled' && cardRes.value.resCode === '00') stats.value.cardCount = (cardRes.value.result || []).length
    if (courseRes.status === 'fulfilled' && courseRes.value.resCode === '00') stats.value.courseCount = (courseRes.value.result || []).length
    if (orderRes.status === 'fulfilled' && orderRes.value.resCode === '00') stats.value.orderCount = (orderRes.value.result || []).length
  } catch (e) { console.error('加载统计数据失败', e) }
}

const quickLinks = [
  { title: '办理会员卡', icon: 'CreditCard', path: '/create-card', desc: '选择你的战斗装备' },
  { title: '购买课程', icon: 'ShoppingCart', path: '/purchase-course', desc: '解锁新的训练计划' },
  { title: '团课预约', icon: 'Calendar', path: '/member/course-booking', desc: '加入团队作战' },
  { title: '私教预约', icon: 'User', path: '/member/course-booking2', desc: '一对一强化训练' },
  { title: '我的课程', icon: 'Reading', path: '/member/myCourse', desc: '查看训练进度' },
  { title: '我的会员卡', icon: 'Postcard', path: '/my-card', desc: '查看装备状态' }
]

const motivations = [
  '没有什么比举铁更能治愈一切',
  '痛苦是暂时的，放弃是永远的',
  '今天的汗水，明天的铠甲',
  '不逼自己一把，永远不知道自己有多强',
  '铁不会骗你，你付出多少就回报多少',
  '每一次深蹲都是对自己的尊重'
]
const randomMotivation = motivations[Math.floor(Math.random() * motivations.length)]

const goTo = (path) => router.push(path)
onMounted(() => { loadStats() })
</script>

<template>
  <div class="dashboard">
    <!-- 硬汉横幅 -->
    <div class="hero-banner">
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <div class="hero-badge">{{ greeting }}</div>
        <h1 class="hero-title">{{ userInfo.name }}，准备好了吗？</h1>
        <p class="hero-subtitle">{{ todayStr }}</p>
        <p class="hero-quote">"{{ randomMotivation }}"</p>
      </div>
      <div class="hero-deco">
        <span class="deco-text">TRAIN</span>
        <span class="deco-text deco-text-2">HARD</span>
      </div>
    </div>

    <!-- 数据统计 -->
    <div class="stats-grid">
      <div class="stat-card" @click="goTo('/my-card')">
        <div class="stat-top">
          <el-icon :size="22"><CreditCard /></el-icon>
          <span class="stat-label">会员卡</span>
        </div>
        <div class="stat-num">{{ stats.cardCount }}</div>
        <div class="stat-bar"><div class="stat-bar-fill" style="width: 100%; background: #e74c3c;"></div></div>
      </div>
      <div class="stat-card" @click="goTo('/member/myCourse')">
        <div class="stat-top">
          <el-icon :size="22"><Reading /></el-icon>
          <span class="stat-label">课程</span>
        </div>
        <div class="stat-num">{{ stats.courseCount }}</div>
        <div class="stat-bar"><div class="stat-bar-fill" style="width: 80%; background: #f39c12;"></div></div>
      </div>
      <div class="stat-card" @click="goTo('/member/bookedRecord')">
        <div class="stat-top">
          <el-icon :size="22"><Tickets /></el-icon>
          <span class="stat-label">预约</span>
        </div>
        <div class="stat-num">{{ stats.bookingCount }}</div>
        <div class="stat-bar"><div class="stat-bar-fill" style="width: 60%; background: #3498db;"></div></div>
      </div>
      <div class="stat-card" @click="goTo('/member/order')">
        <div class="stat-top">
          <el-icon :size="22"><ShoppingCart /></el-icon>
          <span class="stat-label">订单</span>
        </div>
        <div class="stat-num">{{ stats.orderCount }}</div>
        <div class="stat-bar"><div class="stat-bar-fill" style="width: 90%; background: #2ecc71;"></div></div>
      </div>
    </div>

    <!-- 快捷入口 -->
    <div class="section-header">
      <span class="section-title">⚡ 快捷操作</span>
      <span class="section-sub">NO PAIN NO GAIN</span>
    </div>
    <div class="quick-grid">
      <div v-for="(item, i) in quickLinks" :key="item.path" class="quick-card" @click="goTo(item.path)">
        <div class="quick-num">{{ String(i + 1).padStart(2, '0') }}</div>
        <div class="quick-icon">
          <el-icon :size="28"><component :is="item.icon" /></el-icon>
        </div>
        <div class="quick-info">
          <span class="quick-title">{{ item.title }}</span>
          <span class="quick-desc">{{ item.desc }}</span>
        </div>
        <div class="quick-arrow">→</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  padding: 0;
  min-height: 100vh;
  background: #ffffff;
}

/* ===== 硬汉横幅 ===== */
.hero-banner {
  position: relative;
  background: linear-gradient(135deg, #2c3e6b 0%, #34527a 50%, #3a6ea5 100%);
  border-radius: 0 0 16px 16px;
  padding: 48px 40px 40px;
  overflow: hidden;
  margin-bottom: 28px;
  border-bottom: 3px solid #e74c3c;
}
.hero-overlay {
  position: absolute;
  inset: 0;
  background: repeating-linear-gradient(
    45deg,
    transparent,
    transparent 40px,
    rgba(231, 76, 60, 0.03) 40px,
    rgba(231, 76, 60, 0.03) 80px
  );
  pointer-events: none;
}
.hero-content {
  position: relative;
  z-index: 2;
}
.hero-badge {
  display: inline-block;
  background: #e74c3c;
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  padding: 4px 16px;
  border-radius: 2px;
  letter-spacing: 2px;
  text-transform: uppercase;
  margin-bottom: 16px;
}
.hero-title {
  font-size: 32px;
  font-weight: 900;
  color: #fff;
  margin: 0 0 8px;
  letter-spacing: 1px;
}
.hero-subtitle {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0 0 16px;
  font-family: 'Courier New', monospace;
  letter-spacing: 2px;
}
.hero-quote {
  font-size: 16px;
  color: rgba(231, 76, 60, 0.85);
  font-style: italic;
  font-weight: 600;
  margin: 0;
  letter-spacing: 1px;
}
.hero-deco {
  position: absolute;
  right: 40px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}
.deco-text {
  font-size: 96px;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.04);
  line-height: 0.9;
  letter-spacing: 8px;
  user-select: none;
}
.deco-text-2 {
  color: rgba(231, 76, 60, 0.08);
}

/* ===== 数据统计 ===== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  padding: 0 24px;
  margin-bottom: 32px;
}
.stat-card {
  background: #2c3e6b;
  border-radius: 10px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.25s;
  border: 1px solid rgba(255, 255, 255, 0.1);
}
.stat-card:hover {
  transform: translateY(-3px);
  border-color: rgba(231, 76, 60, 0.3);
  box-shadow: 0 8px 24px rgba(231, 76, 60, 0.1);
}
.stat-top {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  color: rgba(255, 255, 255, 0.5);
}
.stat-label {
  font-size: 14px;
  font-weight: 500;
}
.stat-num {
  font-size: 36px;
  font-weight: 900;
  color: #fff;
  margin-bottom: 12px;
  font-family: 'Courier New', monospace;
}
.stat-bar {
  height: 4px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 2px;
  overflow: hidden;
}
.stat-bar-fill {
  height: 100%;
  border-radius: 2px;
  transition: width 0.6s ease;
}

/* ===== 区域标题 ===== */
.section-header {
  display: flex;
  align-items: baseline;
  gap: 16px;
  padding: 0 24px;
  margin-bottom: 16px;
}
.section-title {
  font-size: 20px;
  font-weight: 800;
  color: #1a1a2e;
  letter-spacing: 1px;
}
.section-sub {
  font-size: 12px;
  color: rgba(231, 76, 60, 0.5);
  font-weight: 700;
  letter-spacing: 4px;
  font-family: 'Courier New', monospace;
}

/* ===== 快捷入口 ===== */
.quick-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
  padding: 0 24px 40px;
}
.quick-card {
  background: #2c3e6b;
  border-radius: 10px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  cursor: pointer;
  transition: all 0.25s;
  border: 1px solid rgba(255, 255, 255, 0.1);
  position: relative;
  overflow: hidden;
}
.quick-card:hover {
  border-color: rgba(231, 76, 60, 0.4);
  background: #34527a;
  transform: translateX(4px);
}
.quick-card:hover .quick-arrow {
  color: #e74c3c;
  transform: translateX(4px);
}
.quick-num {
  font-size: 28px;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.06);
  font-family: 'Courier New', monospace;
  min-width: 36px;
  user-select: none;
}
.quick-icon {
  width: 44px;
  height: 44px;
  border-radius: 8px;
  background: rgba(231, 76, 60, 0.12);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #e74c3c;
  flex-shrink: 0;
}
.quick-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.quick-title {
  font-size: 15px;
  font-weight: 700;
  color: #fff;
}
.quick-desc {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.35);
}
.quick-arrow {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.15);
  font-weight: 700;
  transition: all 0.25s;
}

/* ===== 响应式 ===== */
@media (max-width: 1200px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .quick-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 768px) {
  .stats-grid { grid-template-columns: 1fr; }
  .quick-grid { grid-template-columns: 1fr; }
  .hero-deco { display: none; }
  .hero-title { font-size: 24px; }
}
</style>
