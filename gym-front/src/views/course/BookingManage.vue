<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Tickets } from '@element-plus/icons-vue'
import { memberApi } from '@/api/modules/member'

const loading = ref(false)
const allData = ref([])
const tableData = ref([])
const total = ref(0)

const statusMap = { 1: '已预约', 2: '申请取消中', 3: '已取消' }

const searchForm = ref({ userId: '', scheduleId: '', status: '' })
const pagination = ref({ currentPage: 1, pageSize: 10 })

const statusOptions = [
  { label: '全部', value: '' },
  { label: '已预约', value: 1 },
  { label: '申请取消中', value: 2 },
  { label: '已取消', value: 3 }
]

const fetchList = async () => {
  loading.value = true
  try {
    const res = await memberApi.getAllBookingRecords()
    if (res.resCode === '00') {
      allData.value = (res.result || []).map(item => ({
        ...item,
        statusName: statusMap[item.status] || '未知',
        bookingTimeStr: item.bookingTime || '—'
      }))
      filterData()
    } else {
      ElMessage.error(res.resMsg || '获取预约记录失败')
    }
  } catch (e) {
    console.error('获取预约记录失败:', e)
  } finally {
    loading.value = false
  }
}

const filterData = () => {
  let filtered = [...allData.value]
  if (searchForm.value.userId) {
    filtered = filtered.filter(item => String(item.userId).includes(searchForm.value.userId))
  }
  if (searchForm.value.scheduleId) {
    filtered = filtered.filter(item => String(item.scheduleId).includes(searchForm.value.scheduleId))
  }
  if (searchForm.value.status !== '') {
    filtered = filtered.filter(item => item.status === searchForm.value.status)
  }
  total.value = filtered.length
  const start = (pagination.value.currentPage - 1) * pagination.value.pageSize
  tableData.value = filtered.slice(start, start + pagination.value.pageSize)
}

const handleSearch = () => {
  pagination.value.currentPage = 1
  filterData()
}

const handleReset = () => {
  searchForm.value = { userId: '', scheduleId: '', status: '' }
  pagination.value.currentPage = 1
  filterData()
}

const handlePageChange = (page) => {
  pagination.value.currentPage = page
  filterData()
}

const handleSizeChange = (size) => {
  pagination.value.pageSize = size
  pagination.value.currentPage = 1
  filterData()
}

const getStatusTagType = (status) => {
  if (status === 1) return 'success'
  if (status === 2) return 'warning'
  if (status === 3) return 'info'
  return 'info'
}

const totalCount = computed(() => allData.value.length)

onMounted(() => {
  fetchList()
})
</script>

<template>
  <div class="booking-container">
    <!-- 页面标题 -->
    <el-card class="header-card" shadow="never">
      <div class="header-content">
        <div class="header-left">
          <el-icon class="header-icon"><Tickets /></el-icon>
          <div class="header-text">
            <h1 class="page-title">预约管理</h1>
            <p class="page-subtitle">管理所有会员的课程预约记录</p>
          </div>
        </div>
        <div class="header-stats">
          <div class="stat-item">
            <span class="stat-label">总记录</span>
            <span class="stat-value">{{ totalCount }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="用户ID">
          <el-input
            v-model="searchForm.userId"
            placeholder="请输入用户ID"
            clearable
            style="width: 160px"
          />
        </el-form-item>
        <el-form-item label="排期ID">
          <el-input
            v-model="searchForm.scheduleId"
            placeholder="请输入排期ID"
            clearable
            style="width: 160px"
          />
        </el-form-item>
        <el-form-item label="预约状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
          <el-button @click="handleReset" :icon="Refresh">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card" shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" min-width="80" />
        <el-table-column prop="userId" label="用户ID" min-width="100" />
        <el-table-column prop="memberCourseId" label="会员课程ID" min-width="120" />
        <el-table-column prop="scheduleId" label="排期ID" min-width="100" />
        <el-table-column prop="bookingDate" label="预约日期" min-width="130" />
        <el-table-column label="预约时间" min-width="180">
          <template #default="{ row }">
            {{ row.bookingTimeStr }}
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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
.booking-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.header-card {
  margin-bottom: 20px;
  border-radius: 12px;
  border: none;
}

.header-card :deep(.el-card__body) {
  padding: 20px 24px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  font-size: 28px;
  color: #667eea;
}

.header-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.page-subtitle {
  font-size: 13px;
  color: #909399;
  margin: 0;
}

.header-stats {
  display: flex;
  align-items: center;
  gap: 32px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-label {
  font-size: 14px;
  color: #667eea;
  font-weight: 600;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #667eea;
}

.search-card {
  margin-bottom: 20px;
  border-radius: 12px;
  border: none;
}

.search-card :deep(.el-card__body) {
  padding: 20px;
}

.search-card :deep(.el-form) {
  margin-bottom: 0;
}

.table-card {
  border-radius: 12px;
  border: none;
}

.table-card :deep(.el-card__body) {
  padding: 20px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
