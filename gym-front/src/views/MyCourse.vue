<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Reading } from '@element-plus/icons-vue'
import { memberApi } from '@/api/modules/member'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// 课程状态映射
const statusMap = {
  1: '待预约',
  2: '已使用',
  3: '已退课'
}

// 课程类型映射
const courseTypeMap = {
  1: '团课',
  2: '私教课'
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
  { label: '待预约', value: 1 },
  { label: '已使用', value: 2 },
  { label: '已退课', value: 3 }
]

// 全量数据（用于前端筛选）
const allData = ref([])

// 格式化时间
const formatTime = (isoStr) => {
  if (!isoStr) return ''
  const d = new Date(isoStr)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

// 获取课程列表
const fetchCourseList = async () => {
  loading.value = true
  try {
    const res = await memberApi.getMyCourseList()
    if (res.resCode === '00') {
      allData.value = (res.result || []).map(item => ({
        ...item,
        courseTypeName: courseTypeMap[item.courseType] || '未知',
        statusName: statusMap[item.status] || '未知',
        purchaseDate: formatTime(item.paymentTime)
      }))
      filterData()
    } else {
      ElMessage.error(res.resMsg || '获取课程列表失败')
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

// 前端筛选
const filterData = () => {
  let filtered = [...allData.value]
  if (searchForm.value.courseName) {
    filtered = filtered.filter(item =>
      item.courseName.includes(searchForm.value.courseName)
    )
  }
  if (searchForm.value.status !== '') {
    filtered = filtered.filter(item => item.status === searchForm.value.status)
  }
  total.value = filtered.length
  const start = (pagination.value.currentPage - 1) * pagination.value.pageSize
  tableData.value = filtered.slice(start, start + pagination.value.pageSize)
}

// 搜索
const handleSearch = () => {
  pagination.value.currentPage = 1
  filterData()
}

// 重置
const handleReset = () => {
  searchForm.value = {
    courseName: '',
    status: ''
  }
  pagination.value.currentPage = 1
  filterData()
}

// 分页变化
const handlePageChange = (page) => {
  pagination.value.currentPage = page
  filterData()
}

const handleSizeChange = (size) => {
  pagination.value.pageSize = size
  pagination.value.currentPage = 1
  filterData()
}

// 退课申请
const handleRefund = async (row) => {
  try {
    const { value: appReason } = await ElMessageBox.prompt(
      '请输入退课申请原因（不超过200字）：',
      '退课申请',
      {
        confirmButtonText: '提交申请',
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

    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const res = await memberApi.dropClass({
      memberCourseId: row.id,
      appReason: appReason.trim(),
      realName: userInfo.realName || ''
    })
    if (res.resCode === '00') {
      ElMessage.success('申请成功')
      fetchCourseList()
    } else {
      ElMessage.warning(res.resMsg || '申请失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退课申请失败:', error)
      ElMessage.error('退课申请失败')
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
          <div style="color: #606266; font-size: 14px;">${row.coachName}</div>
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
    <div class="page-header">
      <h1 class="page-title"><el-icon><Reading /></el-icon> 我的课程</h1>
      <p class="page-subtitle">管理你购买的课程</p>
    </div>

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
        <el-table-column prop="coachName" label="授课教练" min-width="120" />
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
              退课申请
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

.page-header {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e8e8e8;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 28px;
  font-weight: 900;
  color: #1a1a2e;
  margin: 0 0 8px 0;
}

.page-subtitle {
  font-size: 14px;
  color: #666;
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