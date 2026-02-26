<script setup>
import { ref, onMounted } from 'vue'
import { memberApi } from '@/api/modules/member'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const cardTypes = ref([])
const searchForm = ref({ realName: '', phone: '' })
const selectedMember = ref(null)
const showCardDialog = ref(false)

// 开卡表单
const cardForm = ref({
  userId: null,
  cardId: null,
  paymentMethod: 1,
  amount: 0
})

const paymentMethods = [
  { label: '支付宝', value: 1 },
  { label: '微信', value: 2 }
]

// 加载会员卡类型
const loadCardTypes = async () => {
  try {
    const res = await memberApi.getCardList()
    if (res.resCode === '00') {
      cardTypes.value = res.result || []
    }
  } catch (e) {
    ElMessage.error('获取会员卡类型失败')
  }
}

// 搜索会员
const handleSearchMember = async () => {
  if (!searchForm.value.realName && !searchForm.value.phone) {
    ElMessage.warning('请输入姓名或手机号')
    return
  }
  
  loading.value = true
  try {
    // TODO: 调用搜索会员接口
    // 暂时使用会员列表接口模拟
    const res = await memberApi.getUserList()
    if (res.resCode === '00') {
      const members = res.result || []
      const found = members.find(m => 
        (searchForm.value.realName && m.realName === searchForm.value.realName) ||
        (searchForm.value.phone && m.phone === searchForm.value.phone)
      )
      if (found) {
        selectedMember.value = found
        ElMessage.success('找到会员信息')
      } else {
        selectedMember.value = null
        ElMessage.warning('未找到该会员')
      }
    }
  } catch (e) {
    ElMessage.error('搜索会员失败')
  } finally {
    loading.value = false
  }
}

// 重置搜索
const handleReset = () => {
  searchForm.value = { realName: '', phone: '' }
  selectedMember.value = null
}

// 打开开卡弹窗
const openCardDialog = () => {
  if (!selectedMember.value) {
    ElMessage.warning('请先搜索并选择会员')
    return
  }
  cardForm.value = {
    userId: selectedMember.value.id,
    cardId: null,
    paymentMethod: 1,
    amount: 0
  }
  showCardDialog.value = true
}

// 选择会员卡类型时更新金额
const handleCardTypeChange = (cardId) => {
  const card = cardTypes.value.find(c => c.id === cardId)
  if (card) {
    cardForm.value.amount = card.price
  }
}

// 提交开卡
const submitCard = async () => {
  if (!cardForm.value.cardId) {
    ElMessage.warning('请选择会员卡类型')
    return
  }
  
  loading.value = true
  try {
    const res = await memberApi.createOrder({
      userId: cardForm.value.userId,
      cardId: cardForm.value.cardId,
      paymentMethod: cardForm.value.paymentMethod
    })
    if (res.resCode === '00') {
      ElMessage.success('开卡成功')
      showCardDialog.value = false
      handleReset()
    } else {
      ElMessage.warning(res.resMsg || '开卡失败')
    }
  } catch (e) {
    ElMessage.error('开卡失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCardTypes()
})
</script>

<template>
  <div class="card-manage-page">
    <div class="page-header">
      <div class="header-left">
        <el-icon :size="22"><CreditCard /></el-icon>
        <div>
          <h2>办卡续费</h2>
          <p>为会员办理会员卡或续费</p>
        </div>
      </div>
    </div>

    <!-- 搜索会员 -->
    <el-card class="search-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>搜索会员</span>
        </div>
      </template>
      <div class="search-form">
        <el-form :model="searchForm" label-width="80px">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="会员姓名">
                <el-input v-model="searchForm.realName" placeholder="请输入会员姓名" clearable />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="手机号">
                <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label=" ">
                <el-button type="primary" @click="handleSearchMember" :loading="loading">
                  <el-icon><Search /></el-icon> 搜索
                </el-button>
                <el-button @click="handleReset">
                  <el-icon><Refresh /></el-icon> 重置
                </el-button>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </el-card>

    <!-- 会员信息 -->
    <el-card v-if="selectedMember" class="member-info-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>会员信息</span>
          <el-button type="primary" size="small" @click="openCardDialog">
            <el-icon><Plus /></el-icon> 办卡/续费
          </el-button>
        </div>
      </template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="会员ID">{{ selectedMember.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ selectedMember.username }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ selectedMember.realName }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ selectedMember.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ selectedMember.email || '--' }}</el-descriptions-item>
        <el-descriptions-item label="性别">
          {{ selectedMember.gender === 1 ? '男' : selectedMember.gender === 2 ? '女' : '未知' }}
        </el-descriptions-item>
        <el-descriptions-item label="身高">{{ selectedMember.height ? selectedMember.height + ' cm' : '--' }}</el-descriptions-item>
        <el-descriptions-item label="体重">{{ selectedMember.weight ? selectedMember.weight + ' kg' : '--' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ selectedMember.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 可用会员卡类型 -->
    <el-card class="card-types-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>可用会员卡类型</span>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col v-for="card in cardTypes" :key="card.id" :span="6">
          <div class="card-type-item">
            <div class="card-name">{{ card.cardName }}</div>
            <div class="card-price">¥{{ card.price }}</div>
            <div class="card-desc">{{ card.description || '暂无描述' }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 开卡弹窗 -->
    <el-dialog v-model="showCardDialog" title="办卡/续费" width="500px" :close-on-click-modal="false">
      <el-form :model="cardForm" label-width="100px">
        <el-form-item label="会员姓名">
          <span>{{ selectedMember?.realName }}</span>
        </el-form-item>
        <el-form-item label="手机号">
          <span>{{ selectedMember?.phone }}</span>
        </el-form-item>
        <el-form-item label="会员卡类型" required>
          <el-select 
            v-model="cardForm.cardId" 
            placeholder="请选择会员卡类型" 
            style="width: 100%"
            @change="handleCardTypeChange"
          >
            <el-option 
              v-for="card in cardTypes" 
              :key="card.id" 
              :label="`${card.cardName} - ¥${card.price}`" 
              :value="card.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="支付方式" required>
          <el-radio-group v-model="cardForm.paymentMethod">
            <el-radio :label="1">支付宝</el-radio>
            <el-radio :label="2">微信</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="金额">
          <span class="amount-text">¥{{ cardForm.amount }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCardDialog = false">取消</el-button>
        <el-button type="primary" @click="submitCard" :loading="loading">确认办卡</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.card-manage-page {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #303133;
}

.header-left h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
}

.header-left p {
  margin: 0;
  font-size: 13px;
  color: #909399;
}

.search-card,
.member-info-card,
.card-types-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.search-form {
  padding: 10px 0;
}

.card-type-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  background: #fff;
  transition: all 0.3s;
  cursor: pointer;
}

.card-type-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
  transform: translateY(-2px);
}

.card-name {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 10px;
}

.card-price {
  font-size: 24px;
  font-weight: 700;
  color: #f56c6c;
  margin-bottom: 8px;
}

.card-duration {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.card-desc {
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
}

.amount-text {
  font-size: 20px;
  font-weight: 700;
  color: #f56c6c;
}
</style>
