<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Timer, ShoppingCart, Wallet, ChatDotRound, Lightning } from '@element-plus/icons-vue'
import { memberApi } from '@/api/modules/member'

const loading = ref(false)
const activities = ref([])
const timers = ref({})
const pollingTimer = ref(null)
const pendingOrderNo = ref(null)
const showPayDialog = ref(false)
const selectedActivity = ref(null)
const paymentMethod = ref('wechat')
const buying = ref(false)

// 订单相关
const orderLoading = ref(false)
const myOrders = ref([])

const statusMap = { 0: '未开始', 1: '进行中', 2: '已结束' }
const orderStatusMap = { 1: '待支付', 2: '已支付', 3: '已取消' }

const cardStyles = [
  { gradient: 'linear-gradient(135deg, #ff0844 0%, #ffb199 100%)', accent: '#ff0844' },
  { gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', accent: '#667eea' },
  { gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)', accent: '#f5576c' },
  { gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)', accent: '#4facfe' },
  { gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)', accent: '#43e97b' },
  { gradient: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)', accent: '#fa709a' },
]

const fetchList = async () => {
  loading.value = true
  try {
    const res = await memberApi.getSeckillList()
    if (res.resCode === '00') {
      activities.value = (res.result || []).map((item, index) => ({
        ...item,
        statusName: statusMap[item.status] || '未知',
        style: cardStyles[index % cardStyles.length]
      }))
      activities.value.forEach(a => startCountdown(a))
    } else {
      ElMessage.error(res.resMsg || '获取活动列表失败')
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

// 获取我的秒杀订单
const fetchMyOrders = async () => {
  orderLoading.value = true
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const res = await memberApi.getMySeckillOrders({ userId: userInfo.id })
    if (res.resCode === '00') {
      myOrders.value = (res.result || []).map(item => ({
        ...item,
        statusName: orderStatusMap[item.status] || '未知'
      }))
    }
  } catch (e) {
    console.error(e)
  } finally {
    orderLoading.value = false
  }
}

// 倒计时
const countdowns = ref({})

const startCountdown = (activity) => {
  if (timers.value[activity.id]) clearInterval(timers.value[activity.id])
  const update = () => {
    const now = Date.now()
    const start = new Date(activity.startTime).getTime()
    const end = new Date(activity.endTime).getTime()
    if (activity.status === 1 && now < end) {
      countdowns.value[activity.id] = formatTime(end - now)
    } else if (activity.status === 0 && now < start) {
      countdowns.value[activity.id] = formatTime(start - now)
    } else {
      countdowns.value[activity.id] = null
      clearInterval(timers.value[activity.id])
    }
  }
  update()
  timers.value[activity.id] = setInterval(update, 1000)
}

const formatTime = (ms) => {
  const s = Math.floor(ms / 1000)
  const h = Math.floor(s / 3600)
  const m = Math.floor((s % 3600) / 60)
  const sec = s % 60
  return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(sec).padStart(2, '0')}`
}

const stockPercent = (activity) => {
  if (!activity.totalStock) return 0
  return Math.round((activity.availableStock / activity.totalStock) * 100)
}

const getOrderStatusType = (status) => {
  if (status === 1) return 'warning'
  if (status === 2) return 'success'
  if (status === 3) return 'info'
  return 'info'
}

// 秒杀抢购
const handleBuy = async (activity) => {
  if (activity.status !== 1) return
  buying.value = true
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const res = await memberApi.seckillBuy({ activityId: activity.id, userId: userInfo.id })
    if (res.resCode === '00') {
      ElMessage.success('抢购成功，请尽快支付！')
      pendingOrderNo.value = res.result?.orderNo || null
      selectedActivity.value = activity
      showPayDialog.value = true
      fetchList()
      fetchMyOrders()
    } else {
      ElMessage.warning(res.resMsg || '抢购失败')
    }
  } catch (e) {
    ElMessage.error('网络异常，请重试')
  } finally {
    buying.value = false
  }
}

// 从订单列表支付
const handlePayOrder = (order) => {
  pendingOrderNo.value = order.orderNo
  // 找到对应活动的样式
  const activity = activities.value.find(a => a.id === order.activityId)
  selectedActivity.value = {
    cardName: order.cardName || '秒杀会员卡',
    seckillPrice: order.seckillPrice,
    style: activity?.style || cardStyles[0]
  }
  showPayDialog.value = true
}

// 支付
const confirmPay = async () => {
  if (!pendingOrderNo.value) return
  try {
    await ElMessageBox.confirm(
      `确认支付 ¥${selectedActivity.value.seckillPrice} 吗？`,
      '确认支付',
      { confirmButtonText: '确认支付', cancelButtonText: '取消', type: 'info' }
    )
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const res = await memberApi.paySeckillOrder({
      orderNo: pendingOrderNo.value,
      userId: userInfo.id,
      paymentMethod: paymentMethod.value === 'alipay' ? 1 : 2
    })
    if (res.resCode === '00') {
      ElMessage.success('支付成功！')
      showPayDialog.value = false
      pendingOrderNo.value = null
      selectedActivity.value = null
      fetchMyOrders()
    } else {
      ElMessage.error(res.resMsg || '支付失败')
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('支付失败')
  }
}

onMounted(() => { fetchList(); fetchMyOrders() })
onUnmounted(() => {
  Object.values(timers.value).forEach(t => clearInterval(t))
  if (pollingTimer.value) clearInterval(pollingTimer.value)
})
</script>

<template>
  <div class="seckill-container">
    <!-- 顶部横幅 -->
    <div class="seckill-banner">
      <div class="banner-content">
        <div class="banner-icon">⚡</div>
        <h1 class="banner-title">限时秒杀</h1>
        <p class="banner-desc">限量特惠会员卡，手慢无</p>
      </div>
    </div>

    <!-- 活动列表 -->
    <div v-loading="loading" class="activity-list">
      <div v-if="!loading && activities.length === 0" class="empty-state">
        <el-empty description="暂无秒杀活动" />
      </div>

      <div v-for="(item, index) in activities" :key="item.id" class="activity-card"
        :style="{ animationDelay: `${index * 0.1}s` }">
        <!-- 左侧价格区 -->
        <div class="card-left" :style="{ background: item.style.gradient }">
          <div class="original-price">原价 ¥{{ item.originalPrice }}</div>
          <div class="seckill-price">
            <span class="price-symbol">¥</span>
            <span class="price-num">{{ item.seckillPrice }}</span>
          </div>
          <div class="discount-tag">
            {{ Math.round((item.seckillPrice / item.originalPrice) * 10) }}折
          </div>
        </div>

        <!-- 右侧信息区 -->
        <div class="card-right">
          <div class="card-top">
            <h3 class="card-name">{{ item.cardName || '秒杀会员卡' }}</h3>
            <el-tag :type="item.status === 1 ? 'danger' : item.status === 0 ? 'warning' : 'info'" size="small">
              {{ item.statusName }}
            </el-tag>
          </div>

          <div class="card-info">
            <span class="info-item">
              <el-icon><Timer /></el-icon>
              {{ item.status === 1 ? '距结束' : item.status === 0 ? '距开始' : '已结束' }}
              <span v-if="countdowns[item.id]" class="countdown">{{ countdowns[item.id] }}</span>
            </span>
          </div>

          <!-- 库存进度条 -->
          <div class="stock-bar">
            <div class="stock-info">
              <span>已抢 {{ item.totalStock - item.availableStock }} / {{ item.totalStock }}</span>
              <span>剩余 {{ item.availableStock }}</span>
            </div>
            <el-progress
              :percentage="100 - stockPercent(item)"
              :stroke-width="12"
              :color="item.style.accent"
              :show-text="false"
            />
          </div>

          <!-- 抢购按钮 -->
          <el-button
            class="buy-btn"
            :type="item.status === 1 ? 'danger' : 'info'"
            :disabled="item.status !== 1 || item.availableStock <= 0 || buying"
            :loading="buying"
            @click="handleBuy(item)"
          >
            <template v-if="item.status === 0">即将开始</template>
            <template v-else-if="item.status === 1 && item.availableStock > 0">
              <el-icon><Lightning /></el-icon> 立即抢购
            </template>
            <template v-else-if="item.availableStock <= 0">已抢光</template>
            <template v-else>已结束</template>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 我的秒杀订单 -->
    <div class="order-section" v-if="myOrders.length > 0">
      <div class="order-header">
        <h2 class="order-title">📋 我的秒杀订单</h2>
      </div>
      <div v-loading="orderLoading" class="order-list">
        <div v-for="order in myOrders" :key="order.id" class="order-card">
          <div class="order-left">
            <div class="order-no">{{ order.orderNo }}</div>
            <div class="order-info-row">
              <span class="order-label">秒杀价</span>
              <span class="order-price">¥{{ order.seckillPrice }}</span>
            </div>
            <div class="order-info-row">
              <span class="order-label">下单时间</span>
              <span>{{ order.createTime }}</span>
            </div>
          </div>
          <div class="order-right">
            <el-tag :type="getOrderStatusType(order.status)" size="default">
              {{ order.statusName }}
            </el-tag>
            <el-button
              v-if="order.status === 1"
              type="danger"
              size="small"
              @click="handlePayOrder(order)"
            >
              去支付
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 支付弹窗 -->
    <el-dialog v-model="showPayDialog" title="" width="460px" :close-on-click-modal="false" class="pay-dialog">
      <div v-if="selectedActivity" class="pay-content">
        <div class="pay-header" :style="{ background: selectedActivity.style.gradient }">
          <div class="pay-header-text">🎉 抢购成功</div>
          <div class="pay-card-name">{{ selectedActivity.cardName || '秒杀会员卡' }}</div>
          <div class="pay-price">¥{{ selectedActivity.seckillPrice }}</div>
          <div class="pay-tip">请在15分钟内完成支付，超时订单将自动取消</div>
        </div>
        <div class="pay-body">
          <div class="pay-row"><span>订单编号</span><span class="val">{{ pendingOrderNo }}</span></div>
          <div class="pay-row"><span>秒杀价</span><span class="val price">¥{{ selectedActivity.seckillPrice }}</span></div>
        </div>
        <div class="pay-methods">
          <h4>选择支付方式</h4>
          <el-radio-group v-model="paymentMethod">
            <el-radio label="wechat" size="large">
              <div class="pay-option"><el-icon><ChatDotRound /></el-icon> 微信支付</div>
            </el-radio>
            <el-radio label="alipay" size="large">
              <div class="pay-option"><el-icon><Wallet /></el-icon> 支付宝</div>
            </el-radio>
          </el-radio-group>
        </div>
      </div>
      <template #footer>
        <el-button @click="showPayDialog = false">取消</el-button>
        <el-button type="danger" @click="confirmPay">立即支付 ¥{{ selectedActivity?.seckillPrice }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.seckill-container {
  padding: 0;
  margin: -20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

/* 横幅 */
.seckill-banner {
  background: linear-gradient(135deg, #ff0844 0%, #ff6b35 50%, #ffb199 100%);
  padding: 40px 30px;
  text-align: center;
  position: relative;
  overflow: hidden;
}
.seckill-banner::before {
  content: '⚡ FLASH SALE ⚡';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 120px;
  font-weight: 900;
  color: rgba(255,255,255,0.06);
  white-space: nowrap;
  pointer-events: none;
}
.banner-icon { font-size: 48px; margin-bottom: 8px; }
.banner-title {
  font-size: 36px;
  font-weight: 900;
  color: #fff;
  margin: 0 0 8px 0;
  letter-spacing: 6px;
}
.banner-desc {
  font-size: 15px;
  color: rgba(255,255,255,0.85);
  margin: 0;
  letter-spacing: 2px;
}

/* 活动列表 */
.activity-list {
  padding: 24px 30px 40px;
  max-width: 900px;
  margin: 0 auto;
}
.empty-state { padding: 60px 0; }

/* 活动卡片 */
.activity-card {
  display: flex;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  margin-bottom: 20px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
  animation: fadeInUp 0.5s ease forwards;
  opacity: 0;
  transition: transform 0.3s, box-shadow 0.3s;
}
.activity-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0,0,0,0.12);
}
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 左侧价格 */
.card-left {
  width: 200px;
  min-width: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 28px 16px;
  color: #fff;
  position: relative;
}
.original-price {
  font-size: 13px;
  text-decoration: line-through;
  opacity: 0.7;
  margin-bottom: 4px;
}
.seckill-price { display: flex; align-items: baseline; }
.price-symbol { font-size: 20px; font-weight: 700; }
.price-num { font-size: 48px; font-weight: 900; line-height: 1; }
.discount-tag {
  margin-top: 8px;
  padding: 2px 12px;
  background: rgba(255,255,255,0.25);
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
}

/* 右侧信息 */
.card-right {
  flex: 1;
  padding: 20px 24px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-name {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
}
.card-info {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #909399;
}
.info-item {
  display: flex;
  align-items: center;
  gap: 4px;
}
.countdown {
  font-family: 'Courier New', monospace;
  font-weight: 700;
  color: #ff0844;
  font-size: 16px;
  margin-left: 4px;
}

/* 库存 */
.stock-bar { margin-top: auto; }
.stock-info {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
  margin-bottom: 6px;
}

/* 按钮 */
.buy-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 700;
  border-radius: 8px;
  letter-spacing: 2px;
}

/* 订单分区 */
.order-section {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 30px 40px;
}
.order-header {
  margin-bottom: 16px;
}
.order-title {
  font-size: 20px;
  font-weight: 700;
  color: #303133;
  margin: 0;
}
.order-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.order-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-radius: 12px;
  padding: 18px 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  transition: transform 0.2s, box-shadow 0.2s;
}
.order-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0,0,0,0.1);
}
.order-left {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.order-no {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}
.order-info-row {
  font-size: 13px;
  color: #909399;
  display: flex;
  gap: 8px;
}
.order-label {
  color: #b0b0b0;
}
.order-price {
  color: #ff0844;
  font-weight: 700;
}
.order-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 10px;
}

/* 支付弹窗 */
.pay-dialog :deep(.el-dialog__header) { display: none; }
.pay-dialog :deep(.el-dialog__body) { padding: 0; }
.pay-header {
  padding: 28px;
  text-align: center;
  color: #fff;
}
.pay-header-text { font-size: 20px; margin-bottom: 6px; }
.pay-card-name { font-size: 16px; opacity: 0.9; margin-bottom: 8px; }
.pay-price { font-size: 36px; font-weight: 900; }
.pay-tip { font-size: 12px; opacity: 0.75; margin-top: 8px; }
.pay-body { padding: 20px; background: #f9f9f9; }
.pay-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px dashed #e8e8e8;
  font-size: 14px;
  color: #666;
}
.pay-row:last-child { border-bottom: none; }
.pay-row .val { font-weight: 600; color: #333; }
.pay-row .val.price { color: #ff0844; font-size: 18px; }
.pay-methods { padding: 20px; }
.pay-methods h4 { margin: 0 0 14px 0; font-size: 14px; color: #666; }
.pay-methods :deep(.el-radio-group) { display: flex; gap: 12px; width: 100%; }
.pay-methods :deep(.el-radio) {
  flex: 1; height: auto; padding: 14px;
  border: 1px solid #e8e8e8; border-radius: 6px; margin: 0;
}
.pay-methods :deep(.el-radio.is-checked) { border-color: #ff0844; background: rgba(255,8,68,0.05); }
.pay-option { display: flex; align-items: center; gap: 6px; font-size: 14px; }
.pay-dialog :deep(.el-dialog__footer) { padding: 14px 20px 20px; }
</style>
