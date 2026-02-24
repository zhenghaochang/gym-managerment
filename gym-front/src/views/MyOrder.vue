<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, ShoppingCart } from '@element-plus/icons-vue'
import { memberApi } from '@/api/modules/member'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const paymentMethodMap = { 1: '支付宝', 2: '微信' }
const paymentStatusMap = { 1: '待支付', 2: '支付成功', 3: '支付失败', 4: '已退款' }
const orderStatusMap = { 1: '待处理', 2: '已完成', 3: '已取消' }

const searchForm = ref({ orderNum: '', orderStatus: '' })
const pagination = ref({ currentPage: 1, pageSize: 10 })

const orderStatusOptions = [
  { label: '全部', value: '' },
  { label: '待处理', value: 1 },
  { label: '已完成', value: 2 },
  { label: '已取消', value: 3 }
]

const allData = ref([])

const fetchList = async () => {
  loading.value = true
  try {
    const res = await memberApi.getMyOrderList()
    if (res.resCode === '00') {
      allData.value = (res.result || []).map(item => ({
        ...item,
        paymentMethodName: paymentMethodMap[item.paymentMethod] || '—',
        paymentStatusName: paymentStatusMap[item.paymentStatus] || '未知',
        orderStatusName: orderStatusMap[item.orderStatus] || '未知'
      }))
      filterData()
    } else {
      ElMessage.error(res.resMsg || '获取订单列表失败')
    }
  } catch (e) {
    console.error('获取订单列表失败:', e)
  } finally {
    loading.value = false
  }
}

const filterData = () => {
  let filtered = [...allData.value]
  if (searchForm.value.orderNum) {
    filtered = filtered.filter(item => item.orderNum.includes(searchForm.value.orderNum))
  }
  if (searchForm.value.orderStatus !== '') {
    filtered = filtered.filter(item => item.orderStatus === searchForm.value.orderStatus)
  }
  total.value = filtered.length
  const start = (pagination.value.currentPage - 1) * pagination.value.pageSize
  tableData.value = filtered.slice(start, start + pagination.value.pageSize)
}

const handleSearch = () => { pagination.value.currentPage = 1; filterData() }
const handleReset = () => { searchForm.value = { orderNum: '', orderStatus: '' }; pagination.value.currentPage = 1; filterData() }
const handlePageChange = (page) => { pagination.value.currentPage = page; filterData() }
const handleSizeChange = (size) => { pagination.value.pageSize = size; pagination.value.currentPage = 1; filterData() }

const getOrderStatusTagType = (status) => {
  if (status === 1) return 'warning'
  if (status === 2) return 'success'
  if (status === 3) return 'info'
  return 'info'
}

const getPaymentStatusTagType = (status) => {
  if (status === 1) return 'warning'
  if (status === 2) return 'success'
  if (status === 3) return 'danger'
  if (status === 4) return 'info'
  return 'info'
}

const handleViewDetail = (row) => {
  ElMessageBox.alert(
    `<div style="padding: 4px 0;">
      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px 32px; margin-bottom: 20px; padding-bottom: 20px; border-bottom: 2px solid #ebeef5;">
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">订单号</div>
          <div style="color: #303133; font-size: 15px; font-weight: 600;">${row.orderNum}</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">商品名称</div>
          <div style="color: #303133; font-size: 15px; font-weight: 600;">${row.productName}</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">订单金额</div>
          <div style="color: #ff0844; font-size: 18px; font-weight: 700;">¥${row.orderAmount}</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">支付金额</div>
          <div style="color: #606266; font-size: 14px;">¥${row.paymentAmount || 0}</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">支付方式</div>
          <div style="color: #606266; font-size: 14px;">${row.paymentMethodName}</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">支付状态</div>
          <div style="display: inline-block; padding: 4px 12px; border-radius: 4px; font-size: 13px; font-weight: 500; background: ${row.paymentStatus === 2 ? '#f0f9eb' : row.paymentStatus === 1 ? '#fdf6ec' : row.paymentStatus === 3 ? '#fef0f0' : '#f4f4f5'}; color: ${row.paymentStatus === 2 ? '#67c23a' : row.paymentStatus === 1 ? '#e6a23c' : row.paymentStatus === 3 ? '#f56c6c' : '#909399'};">${row.paymentStatusName}</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">订单状态</div>
          <div style="display: inline-block; padding: 4px 12px; border-radius: 4px; font-size: 13px; font-weight: 500; background: ${row.orderStatus === 1 ? '#fdf6ec' : row.orderStatus === 2 ? '#f0f9eb' : '#f4f4f5'}; color: ${row.orderStatus === 1 ? '#e6a23c' : row.orderStatus === 2 ? '#67c23a' : '#909399'};">${row.orderStatusName}</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">支付时间</div>
          <div style="color: #606266; font-size: 14px;">${row.paymentTime || '—'}</div>
        </div>
      </div>
    </div>`,
    '订单详情',
    { dangerouslyUseHTMLString: true, confirmButtonText: '关闭' }
  )
}

onMounted(() => { fetchList() })
</script>

<template>
  <div class="order-container">
    <div class="page-header">
      <h1 class="page-title"><el-icon><ShoppingCart /></el-icon> 我的订单</h1>
      <p class="page-subtitle">查看我的消费订单记录</p>
    </div>

    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNum" placeholder="请输入订单号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.orderStatus" placeholder="请选择订单状态" clearable style="width: 150px">
            <el-option v-for="item in orderStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
          <el-button @click="handleReset" :icon="Refresh">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="orderNum" label="订单号" min-width="200" />
        <el-table-column prop="productName" label="商品名称" min-width="140" />
        <el-table-column prop="orderAmount" label="订单金额" min-width="120">
          <template #default="{ row }">
            <span style="color: #ff0844; font-weight: 600;">¥{{ row.orderAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="paymentMethodName" label="支付方式" min-width="100" />
        <el-table-column prop="paymentStatusName" label="支付状态" min-width="110">
          <template #default="{ row }">
            <el-tag :type="getPaymentStatusTagType(row.paymentStatus)" size="small">{{ row.paymentStatusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderStatusName" label="订单状态" min-width="110">
          <template #default="{ row }">
            <el-tag :type="getOrderStatusTagType(row.orderStatus)" size="small">{{ row.orderStatusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paymentTime" label="支付时间" min-width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleViewDetail(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.order-container { padding: 20px; background: #f5f7fa; min-height: calc(100vh - 60px); }
.page-header { margin-bottom: 20px; padding-bottom: 20px; border-bottom: 1px solid #e8e8e8; }
.page-title { display: flex; align-items: center; gap: 12px; font-size: 28px; font-weight: 900; color: #1a1a2e; margin: 0 0 8px 0; }
.page-subtitle { font-size: 14px; color: #666; margin: 0; }
.search-card { margin-bottom: 20px; border-radius: 12px; border: none; }
.search-card :deep(.el-card__body) { padding: 20px; }
.search-card :deep(.el-form) { margin-bottom: 0; }
.table-card { border-radius: 12px; border: none; }
.table-card :deep(.el-card__body) { padding: 20px; }
.pagination-wrapper { display: flex; justify-content: flex-end; margin-top: 20px; }
</style>
