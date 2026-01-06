<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { memberApi } from '@/api/modules/member'

const cardStyles = [
  { gradient: 'linear-gradient(135deg, #1a1a2e 0%, #16213e 100%)', accent: '#00d4ff', icon: 'Trophy' },
  { gradient: 'linear-gradient(135deg, #232526 0%, #414345 100%)', accent: '#ff6b35', icon: 'Medal' },
  { gradient: 'linear-gradient(135deg, #2d2d2d 0%, #1a1a1a 100%)', accent: '#ffd700', icon: 'Timer' },
  { gradient: 'linear-gradient(135deg, #0f0f0f 0%, #232323 100%)', accent: '#ff0844', icon: 'Aim' },
  { gradient: 'linear-gradient(135deg, #1e3c72 0%, #2a5298 100%)', accent: '#4fc3f7', icon: 'Stopwatch' },
  { gradient: 'linear-gradient(135deg, #134e5e 0%, #71b280 100%)', accent: '#81c784', icon: 'Star' },
  { gradient: 'linear-gradient(135deg, #4a1c40 0%, #7b2d5b 100%)', accent: '#e91e63', icon: 'Flag' },
  { gradient: 'linear-gradient(135deg, #2c3e50 0%, #4ca1af 100%)', accent: '#26c6da', icon: 'Promotion' },
]

const durationCards = ref([])
const timesCards = ref([])
const loading = ref(false)
const selectedCard = ref(null)
const showPayDialog = ref(false)
const paymentMethod = ref('wechat')

const fetchCardList = async () => {
  loading.value = true
  try {
    const res = await memberApi.getCardList()
    if (res.result && res.result.length > 0) {
      const allCards = res.result.map((card, index) => {
        const style = cardStyles[index % cardStyles.length]
        return {
          id: card.id,
          name: card.cardName,
          price: card.price,
          duration: card.cardCategory === '1' ? card.durationDays : card.totalTimes,
          unit: card.cardCategory === '1' ? '天' : '次',
          features: card.description || '',
          cardCategory: card.cardCategory,
          ...style
        }
      })
      durationCards.value = allCards.filter(c => c.cardCategory === '1')
      timesCards.value = allCards.filter(c => c.cardCategory !== '1')
    }
  } catch (error) {
    console.error('获取会员卡列表失败:', error)
    ElMessage.error('获取会员卡列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => { fetchCardList() })

const selectCard = (card) => { selectedCard.value = card }
const handleBuy = (card) => { selectedCard.value = card; showPayDialog.value = true }
const confirmPay = async () => {
  try {
    await ElMessageBox.confirm(`确认支付 ¥${selectedCard.value.price} 购买${selectedCard.value.name}？`, '确认支付',
      { confirmButtonText: '确认支付', cancelButtonText: '取消', type: 'info' }
    )
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const res = await memberApi.createOrder({
      userId: userInfo.id,
      cardId: selectedCard.value.id,
      paymentMethod: paymentMethod.value === 'alipay' ? 1 : 2
    })
    if (res.resCode === '00') {
      ElMessage.success(res.result || '购买成功！会员卡已激活')
      showPayDialog.value = false
      selectedCard.value = null
    } else {
      ElMessage.error(res.resMsg || '购买失败')
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('购买失败，请稍后重试')
    }
  }
}
</script>

<template>
  <div class="create-card-container">
    <div class="page-header">
      <h1 class="page-title">选择你的<span class="highlight">战斗装备</span></h1>
      <p class="page-subtitle">NO PAIN NO GAIN · 成为更强的自己</p>
      <div class="header-tags">
        <span class="tag"><el-icon><Aim /></el-icon> 无限入场</span>
        <span class="tag"><el-icon><Trophy /></el-icon> 全器械</span>
        <span class="tag"><el-icon><Timer /></el-icon> 即刻生效</span>
      </div>
    </div>
    <div v-loading="loading" class="cards-wrapper">
      <div v-if="durationCards.length" class="card-section">
        <div class="section-header">
          <el-icon class="section-icon"><Timer /></el-icon>
          <h2 class="section-title">时长卡</h2>
          <span class="section-desc">按天计费，畅享无限次入场</span>
        </div>
        <div class="card-list">
          <div v-for="(card, index) in durationCards" :key="card.id" class="card-item"
            :class="{ selected: selectedCard?.id === card.id }"
            :style="{ '--accent': card.accent, animationDelay: `${index * 0.1}s` }" @click="selectCard(card)">
            <div class="card-header" :style="{ background: card.gradient }">
              <el-icon class="card-icon"><component :is="card.icon" /></el-icon>
              <h3 class="card-name">{{ card.name }}</h3>
              <div class="card-duration">
                <span class="duration-num">{{ card.duration }}</span>
                <span class="duration-unit">{{ card.unit }}</span>
              </div>
            </div>
            <div class="card-body">
              <div class="card-price"><span class="price-symbol">¥</span><span class="price-num">{{ card.price }}</span></div>
              <div class="card-features"><p class="features-text">{{ card.features }}</p></div>
              <el-button type="primary" class="buy-btn" @click.stop="handleBuy(card)">立即开卡 →</el-button>
            </div>
          </div>
        </div>
      </div>
      <div v-if="timesCards.length" class="card-section">
        <div class="section-header">
          <el-icon class="section-icon"><Ticket /></el-icon>
          <h2 class="section-title">次数卡</h2>
          <span class="section-desc">按次计费，灵活自由</span>
        </div>
        <div class="card-list">
          <div v-for="(card, index) in timesCards" :key="card.id" class="card-item"
            :class="{ selected: selectedCard?.id === card.id }"
            :style="{ '--accent': card.accent, animationDelay: `${index * 0.1}s` }" @click="selectCard(card)">
            <div class="card-header" :style="{ background: card.gradient }">
              <el-icon class="card-icon"><component :is="card.icon" /></el-icon>
              <h3 class="card-name">{{ card.name }}</h3>
              <div class="card-duration">
                <span class="duration-num">{{ card.duration }}</span>
                <span class="duration-unit">{{ card.unit }}</span>
              </div>
            </div>
            <div class="card-body">
              <div class="card-price"><span class="price-symbol">¥</span><span class="price-num">{{ card.price }}</span></div>
              <div class="card-features"><p class="features-text">{{ card.features }}</p></div>
              <el-button type="primary" class="buy-btn" @click.stop="handleBuy(card)">立即开卡 →</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="guarantee-section">
      <div class="guarantee-item"><el-icon><CircleCheck /></el-icon><span>安全支付</span></div>
      <div class="guarantee-item"><el-icon><Wallet /></el-icon><span>随时退款</span></div>
      <div class="guarantee-item"><el-icon><Service /></el-icon><span>24H客服</span></div>
      <div class="guarantee-item"><el-icon><Present /></el-icon><span>新人礼包</span></div>
    </div>
    <el-dialog v-model="showPayDialog" title="" width="480px" :close-on-click-modal="false" class="pay-dialog">
      <div v-if="selectedCard" class="pay-dialog-content">
        <div class="dialog-header" :style="{ background: selectedCard.gradient }">
          <el-icon class="dialog-icon"><component :is="selectedCard.icon" /></el-icon>
          <h3>{{ selectedCard.name }}</h3>
          <p>{{ selectedCard.duration }}{{ selectedCard.unit }}有效期</p>
        </div>
        <div class="order-info">
          <div class="order-row"><span>会员卡类型</span><span class="value">{{ selectedCard.name }}</span></div>
          <div class="order-row"><span>有效期</span><span class="value">{{ selectedCard.duration }}{{ selectedCard.unit }}</span></div>
          <div class="order-row total"><span>应付金额</span><span class="value price">¥{{ selectedCard.price }}</span></div>
        </div>
        <div class="payment-methods">
          <h4>选择支付方式</h4>
          <el-radio-group v-model="paymentMethod">
            <el-radio label="wechat" size="large"><div class="pay-option"><el-icon><ChatDotRound /></el-icon> 微信支付</div></el-radio>
            <el-radio label="alipay" size="large"><div class="pay-option"><el-icon><Wallet /></el-icon> 支付宝</div></el-radio>
          </el-radio-group>
        </div>
      </div>
      <template #footer>
        <el-button @click="showPayDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmPay" class="confirm-btn">立即支付 ¥{{ selectedCard?.price }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.create-card-container { padding: 30px 40px; margin: -20px; background: #fff; box-sizing: border-box; }
.page-header { text-align: center; margin-bottom: 40px; }
.page-title { font-size: 38px; font-weight: 900; color: #1a1a1a; margin: 0 0 12px 0; letter-spacing: 2px; }
.page-title .highlight { background: linear-gradient(90deg, #ff0844 0%, #ff6b35 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; }
.page-subtitle { font-size: 14px; color: #999; margin: 0 0 20px 0; letter-spacing: 6px; text-transform: uppercase; }
.header-tags { display: flex; justify-content: center; gap: 20px; flex-wrap: wrap; }
.header-tags .tag { display: flex; align-items: center; gap: 6px; padding: 8px 16px; background: #f5f5f5; border: 1px solid #eee; border-radius: 4px; font-size: 13px; color: #666; font-weight: 600; }
.cards-wrapper { margin-bottom: 40px; }
.card-section { margin-bottom: 40px; padding: 24px; background: linear-gradient(135deg, #f8f9fa 0%, #fff 100%); border-radius: 16px; border: 1px solid #e0e0e0; }
.section-header { display: flex; align-items: center; gap: 12px; margin-bottom: 24px; padding-bottom: 16px; border-bottom: 2px solid #1a1a2e; }
.section-icon { font-size: 32px; color: #ff0844; }
.section-title { font-size: 28px; font-weight: 900; color: #1a1a2e; margin: 0; letter-spacing: 2px; text-transform: uppercase; }
.section-desc { font-size: 14px; color: #666; margin-left: auto; }
.card-list { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; min-height: 100px; }
@media (max-width: 1200px) { .card-list { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 768px) { .card-list { grid-template-columns: 1fr; } }
.card-item { background: #fff; border-radius: 16px; overflow: hidden; transition: all 0.3s ease; cursor: pointer; position: relative; border: 2px solid #1a1a2e; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12); animation: fadeInUp 0.5s ease forwards; opacity: 0; }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
.card-item:hover { transform: translateY(-10px) scale(1.02); box-shadow: 0 24px 48px rgba(0, 0, 0, 0.2); border-color: var(--accent); }
.card-item.selected { border-color: var(--accent); box-shadow: 0 0 0 4px rgba(255, 8, 68, 0.2); }
.card-header { padding: 32px 20px; text-align: center; color: #fff; position: relative; }
.card-header::after { content: ''; position: absolute; bottom: 0; left: 50%; transform: translateX(-50%); width: 60px; height: 4px; background: var(--accent); border-radius: 2px; }
.card-icon { font-size: 48px; margin-bottom: 12px; filter: drop-shadow(0 4px 8px rgba(0,0,0,0.3)); }
.card-name { font-size: 26px; font-weight: 900; margin: 0 0 16px 0; letter-spacing: 3px; text-transform: uppercase; text-shadow: 2px 2px 4px rgba(0,0,0,0.3); }
.card-duration { display: flex; align-items: baseline; justify-content: center; gap: 4px; }
.duration-num { font-size: 56px; font-weight: 900; line-height: 1; color: var(--accent); text-shadow: 2px 2px 8px rgba(0,0,0,0.4); }
.duration-unit { font-size: 18px; color: rgba(255,255,255,0.8); font-weight: 700; }
.card-body { padding: 24px 20px; background: #fafafa; }
.card-price { text-align: center; margin-bottom: 14px; }
.price-symbol { font-size: 20px; font-weight: 800; color: #ff0844; }
.price-num { font-size: 42px; font-weight: 900; color: #ff0844; }
.card-features { margin-bottom: 18px; min-height: 36px; }
.features-text { margin: 0; font-size: 13px; color: #666; line-height: 1.8; text-align: center; }
.buy-btn { width: 100%; height: 48px; font-size: 15px; font-weight: 800; border-radius: 8px; border: none; background: linear-gradient(90deg, #1a1a2e 0%, #2d2d2d 100%) !important; color: #fff !important; letter-spacing: 2px; text-transform: uppercase; transition: all 0.3s; }
.buy-btn:hover { background: linear-gradient(90deg, var(--accent) 0%, var(--accent) 100%) !important; transform: scale(1.02); box-shadow: 0 8px 20px rgba(0,0,0,0.2); }
.guarantee-section { display: flex; justify-content: center; gap: 50px; padding: 24px 30px; background: linear-gradient(90deg, #1a1a2e 0%, #2d2d2d 100%); border-radius: 12px; }
@media (max-width: 768px) { .guarantee-section { flex-wrap: wrap; gap: 20px; } }
.guarantee-item { display: flex; align-items: center; gap: 10px; color: #fff; font-size: 14px; font-weight: 600; }
.guarantee-item .el-icon { font-size: 22px; }
.pay-dialog :deep(.el-dialog__header) { display: none; }
.pay-dialog :deep(.el-dialog__body) { padding: 0; }
.dialog-header { padding: 24px; text-align: center; color: #fff; }
.dialog-icon { font-size: 40px; margin-bottom: 10px; }
.dialog-header h3 { margin: 0 0 6px 0; font-size: 22px; }
.dialog-header p { margin: 0; opacity: 0.8; font-size: 14px; }
.order-info { padding: 20px; background: #f9f9f9; }
.order-row { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px dashed #e8e8e8; font-size: 14px; color: #666; }
.order-row:last-child { border-bottom: none; }
.order-row .value { font-weight: 600; color: #333; }
.order-row.total { padding-top: 14px; margin-top: 6px; border-top: 1px solid #ddd; border-bottom: none; }
.order-row.total .value.price { font-size: 26px; color: #ff0844; }
.payment-methods { padding: 20px; }
.payment-methods h4 { margin: 0 0 14px 0; font-size: 14px; color: #666; }
.payment-methods :deep(.el-radio-group) { display: flex; gap: 12px; width: 100%; }
.payment-methods :deep(.el-radio) { flex: 1; height: auto; padding: 14px; border: 1px solid #e8e8e8; border-radius: 6px; margin: 0; }
.payment-methods :deep(.el-radio.is-checked) { border-color: #ff0844; background: rgba(255, 8, 68, 0.05); }
.pay-option { display: flex; align-items: center; gap: 6px; font-size: 14px; }
.pay-dialog :deep(.el-dialog__footer) { padding: 14px 20px 20px; }
.confirm-btn { background: linear-gradient(90deg, #ff0844 0%, #ff6b35 100%) !important; border: none !important; font-weight: 600; }
</style>
