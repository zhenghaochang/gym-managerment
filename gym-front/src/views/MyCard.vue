<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { memberApi } from '@/api/modules/member'

const router = useRouter()
const myCards = ref([])
const loading = ref(false)
const noCard = ref(false) // 无可用会员卡

const cardStyles = [
  { gradient: 'linear-gradient(135deg, #1a1a2e 0%, #16213e 100%)', accent: '#00d4ff' },
  { gradient: 'linear-gradient(135deg, #232526 0%, #414345 100%)', accent: '#ff6b35' },
  { gradient: 'linear-gradient(135deg, #2d2d2d 0%, #1a1a1a 100%)', accent: '#ffd700' },
  { gradient: 'linear-gradient(135deg, #0f0f0f 0%, #232323 100%)', accent: '#ff0844' },
]

const getCardStyle = (index) => cardStyles[index % cardStyles.length]

// 退出登录
const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  localStorage.removeItem('permissions')
  router.push('/login')
}

// 获取会员卡列表
const fetchMyCards = async () => {
  loading.value = true
  noCard.value = false
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const res = await memberApi.getMyCardList({ userId: userInfo.id })
    
    if (res.resCode === '00') {
      // 成功，处理数据
      myCards.value = (res.result || []).map(card => ({
        id: card.id,
        cardTypeId: card.cardTypeId,
        cardNo: card.cardNumber,
        startTime: card.startTime,
        endTime: card.endTime,
        remainingTimes: card.remainingTimes,
        durationDays: card.durationDays, // 有效天数（时长卡）
        totalTimes: card.totalTimes, // 总次数（次数卡）
        status: card.status, // 1正常 2冻结 3已过期 4已用完
        createTime: card.createTime
      }))
      // 如果返回空数组也算无卡
      if (myCards.value.length === 0) {
        noCard.value = true
      }
    } else if (res.resCode === '01') {
      // 无可用会员卡
      noCard.value = true
      myCards.value = []
    } else if (res.resCode === '11') {
      // 用户权限非法，退出登录
      ElMessage.error('用户权限非法，请重新登录')
      logout()
    } else {
      ElMessage.error(res.resMsg || '获取会员卡失败')
    }
  } catch (error) {
    console.error('获取会员卡失败:', error)
    ElMessage.error('获取会员卡失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => { fetchMyCards() })

// 时长卡（status 1或2，有endTime）
const durationCards = computed(() => myCards.value.filter(c => c.endTime && (c.status === 1 || c.status === 2)))
// 次数卡（status 1或2，无endTime或有remainingTimes）
const timesCards = computed(() => myCards.value.filter(c => !c.endTime && (c.status === 1 || c.status === 2)))
// 已过期/已用完
const expiredCards = computed(() => myCards.value.filter(c => c.status === 3 || c.status === 4))

// 统计
const totalCards = computed(() => myCards.value.length)
const activeCount = computed(() => myCards.value.filter(c => c.status === 1).length)
const expiredCount = computed(() => expiredCards.value.length)

// 计算剩余天数
const getRemainDays = (endTime) => {
  if (!endTime) return 0
  const end = new Date(endTime)
  const now = new Date()
  const diff = Math.ceil((end - now) / (1000 * 60 * 60 * 24))
  return diff > 0 ? diff : 0
}

// 计算进度
const getProgress = (card) => {
  if (card.endTime && card.durationDays) {
    // 时长卡：剩余天数 / 总天数
    const remainDays = getRemainDays(card.endTime)
    return Math.max(0, Math.min(100, Math.round((remainDays / card.durationDays) * 100)))
  }
  if (card.totalTimes && card.remainingTimes !== null) {
    // 次数卡：剩余次数 / 总次数
    return Math.max(0, Math.min(100, Math.round((card.remainingTimes / card.totalTimes) * 100)))
  }
  return 0
}

const getStatusText = (status) => {
  if (status === 1) return '生效中'
  if (status === 2) return '已冻结'
  if (status === 3) return '已过期'
  if (status === 4) return '已用完'
  return '未知'
}

const getStatusType = (status) => {
  if (status === 1) return 'success'
  if (status === 2) return 'warning'
  return 'info'
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.split(' ')[0]
}

const handleStopCard = () => {
  ElMessageBox.confirm('申请停卡后，剩余时长将暂停计算，确定申请吗？', '申请停卡', { type: 'warning' })
    .then(() => ElMessage.success('停卡申请已提交'))
    .catch(() => {})
}

const handleTransferCard = () => {
  ElMessageBox.confirm('转卡需要支付手续费，确定申请吗？', '申请转卡', { type: 'warning' })
    .then(() => ElMessage.success('转卡申请已提交'))
    .catch(() => {})
}

const handleRenew = () => {
  ElMessage.info('续费功能开发中')
}
</script>

<template>
  <div class="my-card-container">
    <div class="page-header">
      <h1 class="page-title"><el-icon><Postcard /></el-icon> 我的会员卡</h1>
      <p class="page-subtitle">管理你的健身装备</p>
    </div>

    <!-- 无可用会员卡时，只显示提示语 -->
    <div v-if="noCard && !loading" class="no-card-tip">
      <el-icon class="no-card-icon"><Postcard /></el-icon>
      <p class="no-card-text">暂无可用会员卡</p>
      <p class="no-card-action">
        <router-link to="/create-card" class="go-buy-link">去购卡 →</router-link>
      </p>
    </div>

    <!-- 有会员卡 -->
    <template v-if="!noCard">
      <!-- 统计卡片 -->
      <div class="stats-row">
        <div class="stat-card">
          <el-icon class="stat-icon"><Postcard /></el-icon>
          <div class="stat-info">
            <span class="stat-num">{{ totalCards }}</span>
            <span class="stat-label">全部卡片</span>
          </div>
        </div>
        <div class="stat-card active">
          <el-icon class="stat-icon"><CircleCheck /></el-icon>
          <div class="stat-info">
            <span class="stat-num">{{ activeCount }}</span>
            <span class="stat-label">生效中</span>
          </div>
        </div>
        <div class="stat-card expired">
          <el-icon class="stat-icon"><Clock /></el-icon>
          <div class="stat-info">
            <span class="stat-num">{{ expiredCount }}</span>
            <span class="stat-label">已失效</span>
          </div>
        </div>
      </div>

      <div v-loading="loading">
        <!-- 时长卡区块 -->
        <div v-if="durationCards.length" class="card-section">
          <div class="section-header">
            <el-icon class="section-icon"><Timer /></el-icon>
            <h2 class="section-title">时长卡</h2>
            <span class="section-desc">按天计费，畅享无限次入场</span>
          </div>
          <div class="card-list">
            <div v-for="(card, index) in durationCards" :key="card.id" class="member-card"
              :class="{ inactive: card.status === 2 }"
              :style="{ '--accent': getCardStyle(index).accent, background: getCardStyle(index).gradient }">
              <div class="card-top">
                <div class="card-type"><el-icon><Timer /></el-icon><span>时长卡</span></div>
                <el-button size="small" :type="getStatusType(card.status)" disabled>
                  {{ getStatusText(card.status) }}
                </el-button>
              </div>
              <div class="card-main">
                <h3 class="card-name">会员卡</h3>
                <p class="card-no">卡号：{{ card.cardNo }}</p>
              </div>
              <div class="card-progress">
                <div class="progress-info">
                  <span>剩余 {{ getRemainDays(card.endTime) }} 天</span>
                  <span class="progress-percent">{{ getProgress(card) }}%</span>
                </div>
                <el-progress :percentage="getProgress(card)" :stroke-width="8" :show-text="false" :color="getCardStyle(index).accent" />
              </div>
              <div class="card-footer">
                <div class="card-date">到期：{{ formatDate(card.endTime) }}</div>
                <div class="card-actions">
                  <el-button size="small" text @click="handleStopCard">申请停卡</el-button>
                  <el-button size="small" text @click="handleTransferCard">申请转卡</el-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 次数卡区块 -->
        <div v-if="timesCards.length" class="card-section">
          <div class="section-header">
            <el-icon class="section-icon"><Ticket /></el-icon>
            <h2 class="section-title">次数卡</h2>
            <span class="section-desc">按次计费，灵活自由</span>
          </div>
          <div class="card-list">
            <div v-for="(card, index) in timesCards" :key="card.id" class="member-card"
              :class="{ inactive: card.status === 2 }"
              :style="{ '--accent': getCardStyle(index).accent, background: getCardStyle(index).gradient }">
              <div class="card-top">
                <div class="card-type"><el-icon><Ticket /></el-icon><span>次数卡</span></div>
                <el-button size="small" :type="getStatusType(card.status)" disabled>
                  {{ getStatusText(card.status) }}
                </el-button>
              </div>
              <div class="card-main">
                <h3 class="card-name">次数卡</h3>
                <p class="card-no">卡号：{{ card.cardNo }}</p>
              </div>
              <div class="card-progress">
                <div class="progress-info">
                  <span>剩余 {{ card.remainingTimes || 0 }} 次</span>
                  <span class="progress-percent">{{ getProgress(card) }}%</span>
                </div>
                <el-progress :percentage="getProgress(card)" :stroke-width="8" :show-text="false" :color="getCardStyle(index).accent" />
              </div>
              <div class="card-footer">
                <div class="card-date">购买于 {{ formatDate(card.createTime) }}</div>
                <div class="card-actions">
                  <el-button size="small" text @click="handleStopCard">申请停卡</el-button>
                  <el-button size="small" text @click="handleTransferCard">申请转卡</el-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 已过期区块 -->
        <div v-if="expiredCards.length" class="card-section expired-section">
          <div class="section-header">
            <el-icon class="section-icon expired"><Clock /></el-icon>
            <h2 class="section-title expired">已失效</h2>
          </div>
          <div class="card-list">
            <div v-for="card in expiredCards" :key="card.id" class="member-card expired">
              <div class="card-top">
                <div class="card-type">
                  <el-icon><component :is="card.endTime ? 'Timer' : 'Ticket'" /></el-icon>
                  <span>{{ card.endTime ? '时长卡' : '次数卡' }}</span>
                </div>
                <el-button size="small" type="info" disabled>{{ getStatusText(card.status) }}</el-button>
              </div>
              <div class="card-main">
                <h3 class="card-name">会员卡</h3>
                <p class="card-no">卡号：{{ card.cardNo }}</p>
              </div>
              <div class="card-footer">
                <div class="card-date">{{ card.endTime ? `已于 ${formatDate(card.endTime)} 到期` : `购买于 ${formatDate(card.createTime)}` }}</div>
                <el-button size="small" type="primary" @click="handleRenew">续费</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
.my-card-container { padding: 30px 40px; margin: -20px; background: #f5f7fa; min-height: calc(100vh - 60px); box-sizing: border-box; }
.page-header { margin-bottom: 30px; padding-bottom: 20px; border-bottom: 1px solid #e8e8e8; }
.page-title { display: flex; align-items: center; gap: 12px; font-size: 28px; font-weight: 900; color: #1a1a2e; margin: 0 0 8px 0; }
.page-subtitle { font-size: 14px; color: #666; margin: 0; }

.stats-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin-bottom: 30px; }
.stat-card { display: flex; align-items: center; gap: 16px; padding: 20px 24px; background: #fff; border-radius: 12px; border: 2px solid #1a1a2e; box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
.stat-card.active { border-color: #52c41a; }
.stat-card.expired { border-color: #999; }
.stat-icon { font-size: 36px; color: #1a1a2e; }
.stat-card.active .stat-icon { color: #52c41a; }
.stat-card.expired .stat-icon { color: #999; }
.stat-info { display: flex; flex-direction: column; }
.stat-num { font-size: 32px; font-weight: 900; color: #1a1a2e; line-height: 1; }
.stat-label { font-size: 14px; color: #666; margin-top: 4px; }

.card-section { margin-bottom: 30px; padding: 24px; background: #fff; border-radius: 16px; border: 1px solid #e0e0e0; }
.section-header { display: flex; align-items: center; gap: 12px; margin-bottom: 20px; padding-bottom: 16px; border-bottom: 2px solid #1a1a2e; }
.section-icon { font-size: 28px; color: #ff0844; }
.section-icon.expired { color: #999; }
.section-title { font-size: 22px; font-weight: 900; color: #1a1a2e; margin: 0; }
.section-title.expired { color: #999; }
.section-desc { font-size: 13px; color: #999; margin-left: auto; }
.expired-section { border-color: #ddd; }
.expired-section .section-header { border-bottom-color: #ddd; }

.card-list { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
@media (max-width: 1400px) { .card-list { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 1100px) { .card-list { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 768px) { .card-list { grid-template-columns: 1fr; } }

.member-card { padding: 20px; border-radius: 16px; color: #fff; position: relative; overflow: hidden; transition: all 0.3s; }
.member-card::before { content: ''; position: absolute; top: -50%; right: -50%; width: 100%; height: 100%; background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%); pointer-events: none; }
.member-card:hover { transform: translateY(-4px); box-shadow: 0 16px 32px rgba(0,0,0,0.2); }
.member-card.inactive { opacity: 0.7; }
.member-card.expired { background: linear-gradient(135deg, #555 0%, #333 100%) !important; opacity: 0.6; }

.card-top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.card-type { display: flex; align-items: center; gap: 6px; font-size: 12px; opacity: 0.8; }
.card-top .el-button { border-radius: 20px; }

.card-main { margin-bottom: 16px; }
.card-name { font-size: 24px; font-weight: 900; margin: 0 0 6px 0; letter-spacing: 2px; text-shadow: 2px 2px 4px rgba(0,0,0,0.3); }
.card-no { font-size: 12px; opacity: 0.7; margin: 0; font-family: monospace; }

.card-progress { margin-bottom: 16px; }
.progress-info { display: flex; justify-content: space-between; font-size: 13px; margin-bottom: 8px; }
.progress-percent { color: var(--accent); font-weight: 700; }
.member-card :deep(.el-progress-bar__outer) { background: rgba(255,255,255,0.2); }

.card-footer { display: flex; justify-content: space-between; align-items: center; padding-top: 14px; border-top: 1px solid rgba(255,255,255,0.2); }
.card-date { font-size: 12px; opacity: 0.7; }
.card-actions { display: flex; gap: 4px; }
.card-actions .el-button { color: rgba(255,255,255,0.8); font-size: 12px; }
.card-actions .el-button:hover { color: var(--accent); }
.card-footer > .el-button { background: rgba(255,255,255,0.2); border: 1px solid rgba(255,255,255,0.4); color: #fff; font-size: 12px; border-radius: 6px; }
.card-footer > .el-button:hover { background: var(--accent); border-color: var(--accent); }

.no-card-tip { 
  display: flex; 
  flex-direction: column; 
  align-items: center; 
  justify-content: center;
  padding: 80px 20px; 
  margin-top: 20px;
  background: #fff;
  border-radius: 16px;
  border: 1px solid #e8e8e8;
}
.no-card-icon { font-size: 72px; color: #ddd; margin-bottom: 20px; }
.no-card-text { color: #999; font-size: 16px; margin: 0 0 16px 0; }
.no-card-action { margin: 0; }
.go-buy-link { 
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: #ff0844; 
  text-decoration: none; 
  font-size: 16px;
  font-weight: 600; 
  padding: 10px 24px;
  border: 2px solid #ff0844;
  border-radius: 8px;
  transition: all 0.3s; 
}
.go-buy-link:hover { 
  background: #ff0844;
  color: #fff;
}

@media (max-width: 768px) {
  .stats-row { grid-template-columns: 1fr; }
  .card-actions { flex-direction: column; gap: 4px; }
}
</style>
