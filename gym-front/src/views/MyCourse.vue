<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Reading } from '@element-plus/icons-vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// 课程状态映射
const statusMap = {
  1: '待签到',
  2: '已签到',
  3: '已退课'
}

// 搜索表单
const searchForm = ref({
  courseName: '',
  status: ''
})

// 分页
const pagination = ref({
  currentPage: 1,
  pageSize: 10
})

// 课程状态选项
const statusOptions = [
  { label: '全部', value: '' },
  { label: '待签到', value: 1 },
  { label: '已签到', value: 2 },
  { label: '已退课', value: 3 }
]

// 模拟数据
const mockData = [
  {
    id: 1,
    orderNum: 'CO202602120001',
    courseName: '私教课-增肌',
    courseType: 2,
    courseTypeName: '私教课',
    coach: '陈静',
    duration: 90,
    price: 300,
    purchaseDate: '2026-02-10 14:30:00',
    status: 1,
    statusName: '待签到'
  },
  {
    id: 2,
    orderNum: 'CO202602110001',
    courseName: '瑜伽',
    courseType: 1,
    courseTypeName: '团课',
    coach: '陈静',
    duration: 60,
    price: 250,
    purchaseDate: '2026-02-09 10:20:00',
    signInDate: '2026-02-11 18:00:00',
    status: 2,
    statusName: '已签到'
  },
  {
    id: 3,
    orderNum: 'CO202602080001',
    courseName: '动感单车',
    courseType: 1,
    courseTypeName: '团课',
    coach: '刘强',
    duration: 60,
    price: 250,
    purchaseDate: '2026-02-08 16:45:00',
    status: 1,
    statusName: '待签到'
  },
  {
    id: 4,
    orderNum: 'CO202601250001',
    courseName: '力量训练',
    courseType: 1,
    courseTypeName: '团课',
    coach: '刘强',
    duration: 90,
    price: 250,
    purchaseDate: '2026-01-25 09:15:00',
    refundDate: '2026-01-26 10:30:00',
    refundReason: '临时有事无法参加',
    status: 3,
    statusName: '已退课'
  }
]

// 获取课程列表
const fetchCourseList = async () => {
  loading.value = true
  try {
    // TODO: 对接后端接口
    setTimeout(() => {
      let filtered = [...mockData]
      if (searchForm.value.courseName) {
        filtered = filtered.filter(item =>
          item.courseName.includes(searchForm.value.courseName)
        )
      }
      if (searchForm.value.status !== '') {
        filtered = filtered.filter(item => item.status === searchForm.value.status)
      }
      tableData.value = filtered
      total.value = filtered.length
      loading.value = false
    }, 300)
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.value.currentPage = 1
  fetchCourseList()
}

// 重置
const handleReset = () => {
  searchForm.value = {
    courseName: '',
    status: ''
  }
  pagination.value.currentPage = 1
  fetchCourseList()
}

// 分页变化
const handlePageChange = (page) => {
  pagination.value.currentPage = page
  fetchCourseList()
}

const handleSizeChange = (size) => {
  pagination.value.pageSize = size
  pagination.value.currentPage = 1
  fetchCourseList()
}

// 退课
const handleRefund = async (row) => {
  try {
    const { value: reason } = await ElMessageBox.prompt(
      '请输入退课原因（不超过200字）：',
      '退课申请',
      {
        confirmButtonText: '确认退课',
        cancelButtonText: '取消',
        inputType: 'textarea',
        inputPlaceholder: '请输入退课原因',
        inputValidator: (value) => {
          if (!value || value.trim() === '') return '请输入退课原因'
          if (value.length > 200) return '退课原因不能超过200个字符'
          return true
        }
      }
    )

    // TODO: 调用后端退课接口
    console.log('退课原因:', reason)
    ElMessage.success('退课成功')
    fetchCourseList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退课失败:', error)
      ElMessage.error('退课失败')
    }
  }
}

// 查看详情
const handleViewDetail = (row) => {
  ElMessageBox.alert(
    `<div style="padding: 4px 0;">
      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px 32px; margin-bottom: 20px; padding-bottom: 20px; border-bottom: 2px solid #ebeef5;">
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">订单号</div>
          <div style="color: #303133; font-size: 15px; font-weight: 600;">${row.orderNum}</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">课程名称</div>
          <div style="color: #303133; font-size: 15px; font-weight: 600;">${row.courseName}</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">课程类型</div>
          <div style="color: #606266; font-size: 14px;">${row.courseTypeName}</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">授课教练</div>
          <div style="color: #606266; font-size: 14px;">${row.coach}</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">课程时长</div>
          <div style="color: #606266; font-size: 14px;">${row.duration}分钟</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">课程价格</div>
          <div style="color: #ff0844; font-size: 18px; font-weight: 700;">¥${row.price}</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">购买时间</div>
          <div style="color: #606266; font-size: 14px;">${row.purchaseDate}</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">课程状态</div>
          <div style="display: inline-block; padding: 4px 12px; border-radius: 4px; font-size: 13px; font-weight: 500; background: ${row.status === 1 ? '#fdf6ec' : row.status === 2 ? '#f0f9eb' : '#f4f4f5'}; color: ${row.status === 1 ? '#e6a23c' : row.status === 2 ? '#67c23a' : '#909399'};">${row.statusName}</div>
        </div>
        ${row.signInDate ? `<div style="grid-column: 1 / -1;">
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">签到时间</div>
          <div style="color: #606266; font-size: 14px;">${row.signInDate}</div>
        </div>` : ''}
        ${row.refundDate ? `<div style="grid-column: 1 / -1;">
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">退课时间</div>
          <div style="color: #606266; font-size: 14px;">${row.refundDate}</div>
        </div>` : ''}
      </div>
      ${row.refundReason ? `<div>
        <div style="color: #909399; font-size: 12px; margin-bottom: 10px;">退课原因</div>
        <div style="color: #303133; font-size: 14px; line-height: 1.8; background: #f5f7fa; padding: 16px; border-radius: 6px; border-left: 3px solid #409eff;">${row.refundReason}</div>
      </div>` : ''}
    </div>`,
    '课程详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '关闭',
      customClass: 'detail-dialog'
    }
  )
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  if (status === 1) return 'warning'
  if (status === 2) return 'success'
  if (status === 3) return 'info'
  return 'info'
}

onMounted(() => {
  fetchCourseList()
})
</script>

<template>
  <div class="application-container">
    <!-- 页面标题 -->
    <el-card class="header-card" shadow="never">
      <div class="header-content">
        <div class="header-left">
          <el-icon class="header-icon"><Reading /></el-icon>
          <div class="header-text">
            <h1 class="page-title">我的课程</h1>
            <p class="page-subtitle">管理你购买的课程</p>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="课程名称">
          <el-input
            v-model="searchForm.courseName"
            placeholder="请输入课程名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="课程状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择课程状态"
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
      <el-table
        :data="tableData"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="orderNum" label="订单号" min-width="180" />
        <el-table-column prop="courseName" label="课程名称" min-width="160" />
        <el-table-column prop="courseTypeName" label="课程类型" min-width="120">
          <template #default="{ row }">
            <el-tag :type="row.courseType === 2 ? 'warning' : 'success'" size="small">
              {{ row.courseTypeName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="coach" label="授课教练" min-width="120" />
        <el-table-column prop="duration" label="时长(分钟)" min-width="120" />
        <el-table-column prop="price" label="价格" min-width="120">
          <template #default="{ row }">
            <span style="color: #ff0844; font-weight: 600;">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="purchaseDate" label="购买时间" min-width="200" />
        <el-table-column prop="statusName" label="课程状态" min-width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              size="small"
              type="primary"
              link
              @click="handleViewDetail(row)"
            >
              查看
            </el-button>
            <el-button
              v-if="row.status === 1"
              size="small"
              type="danger"
              link
              @click="handleRefund(row)"
            >
              退课
            </el-button>
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
.application-container {
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