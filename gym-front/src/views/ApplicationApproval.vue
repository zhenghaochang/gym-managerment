<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { memberApi } from '@/api/modules/member'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pendingCount = ref(0)

// 搜索表单
const searchForm = ref({
  realName: '',
  status: '',
  appType: ''
})

// 申请类型映射
const appTypeMap = {
  1: '退课申请',
  2: '预约取消申请'
}

// 审批状态映射
const statusMap = {
  1: '待处理',
  2: '审批通过',
  3: '审批驳回'
}

// 分页
const pagination = ref({
  currentPage: 1,
  pageSize: 10
})

// 申请类型选项
const appTypeOptions = [
  { label: '全部', value: '' },
  { label: '退课申请', value: 1 },
  { label: '预约取消申请', value: 2 }
]

// 审批状态选项
const statusOptions = [
  { label: '全部', value: '' },
  { label: '待处理', value: 1 },
  { label: '审批通过', value: 2 },
  { label: '审批驳回', value: 3 }
]

// 全量数据
const allData = ref([])

// 获取申请列表
const fetchApplicationList = async () => {
  loading.value = true
  try {
    const res = await memberApi.getCourseApplicationList()
    if (res.resCode === '00') {
      allData.value = (res.result || []).map(item => ({
        ...item,
        appTypeName: appTypeMap[item.appType] || '未知',
        statusName: statusMap[item.status] || '未知'
      }))
      pendingCount.value = allData.value.filter(item => item.status === 1).length
      filterData()
    } else {
      ElMessage.error(res.resMsg || '获取申请列表失败')
    }
  } catch (error) {
    console.error('获取申请列表失败:', error)
    ElMessage.error('获取申请列表失败')
  } finally {
    loading.value = false
  }
}

// 前端筛选
const filterData = () => {
  let filtered = [...allData.value]
  if (searchForm.value.realName) {
    filtered = filtered.filter(item => item.realName.includes(searchForm.value.realName))
  }
  if (searchForm.value.status !== '') {
    filtered = filtered.filter(item => item.status === searchForm.value.status)
  }
  if (searchForm.value.appType !== '') {
    filtered = filtered.filter(item => item.appType === searchForm.value.appType)
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
  searchForm.value = { realName: '', status: '', appType: '' }
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

// 审批通过
const handleApprove = async (row) => {
  try {
    await ElMessageBox.confirm(`确认通过 ${row.realName} 的${row.appTypeName}吗？`, '审批确认', {
      type: 'success',
      confirmButtonText: '通过',
      cancelButtonText: '取消'
    })
    const res = await memberApi.updateCourseApplicationStatus({
      ...row,
      status: 2
    })
    if (res.resCode === '00') {
      ElMessage.success('审批通过')
      fetchApplicationList()
    } else {
      ElMessage.error(res.resMsg || '审批失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('审批失败')
    }
  }
}

// 审批拒绝
const handleReject = async (row) => {
  try {
    const { value: refuseReason } = await ElMessageBox.prompt(
      '请输入拒绝原因（不超过200字）：',
      '审批拒绝',
      {
        confirmButtonText: '确认拒绝',
        cancelButtonText: '取消',
        inputType: 'textarea',
        inputPlaceholder: '请输入拒绝原因',
        inputValidator: (value) => {
          if (!value || value.trim() === '') return '请输入拒绝原因'
          if (value.length > 200) return '拒绝原因不能超过200个字符'
          return true
        }
      }
    )
    const res = await memberApi.updateCourseApplicationStatus({
      ...row,
      status: 3,
      refuseReason: refuseReason.trim()
    })
    if (res.resCode === '00') {
      ElMessage.success('已拒绝申请')
      fetchApplicationList()
    } else {
      ElMessage.error(res.resMsg || '审批失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('审批失败')
    }
  }
}

// 查看详情
const handleViewDetail = (row) => {
  ElMessageBox.alert(
    `<div style="padding: 4px 0;">
      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px 32px; margin-bottom: 20px; padding-bottom: 20px; border-bottom: 2px solid #ebeef5;">
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">申请单号</div>
          <div style="color: #303133; font-size: 15px; font-weight: 600;">${row.formNum}</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">申请人</div>
          <div style="color: #303133; font-size: 15px; font-weight: 600;">${row.realName}</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">申请类型</div>
          <div style="color: #606266; font-size: 14px;">${row.appTypeName}</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">审批状态</div>
          <div style="display: inline-block; padding: 4px 12px; border-radius: 4px; font-size: 13px; font-weight: 500; background: ${row.status === 1 ? '#fdf6ec' : row.status === 2 ? '#f0f9eb' : '#fef0f0'}; color: ${row.status === 1 ? '#e6a23c' : row.status === 2 ? '#67c23a' : '#f56c6c'};">${row.statusName}</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">申请时间</div>
          <div style="color: #606266; font-size: 14px;">${row.createTime}</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">关联课程ID</div>
          <div style="color: #606266; font-size: 14px;">${row.memberCourseId}</div>
        </div>
      </div>
      <div style="margin-bottom: 16px;">
        <div style="color: #909399; font-size: 12px; margin-bottom: 10px;">申请原因</div>
        <div style="color: #303133; font-size: 14px; line-height: 1.8; background: #f5f7fa; padding: 16px; border-radius: 6px; border-left: 3px solid #409eff;">${row.appReason || '无'}</div>
      </div>
      ${row.refuseReason ? `<div>
        <div style="color: #909399; font-size: 12px; margin-bottom: 10px;">驳回原因</div>
        <div style="color: #303133; font-size: 14px; line-height: 1.8; background: #fef0f0; padding: 16px; border-radius: 6px; border-left: 3px solid #f56c6c;">${row.refuseReason}</div>
      </div>` : ''}
    </div>`,
    '申请详情',
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
  if (status === 3) return 'danger'
  return 'info'
}

onMounted(() => {
  fetchApplicationList()
})
</script>

<template>
  <div class="application-container">
    <!-- 页面标题 -->
    <el-card class="header-card" shadow="never">
      <div class="header-content">
        <div class="header-left">
          <el-icon class="header-icon"><DocumentChecked /></el-icon>
          <div class="header-text">
            <h1 class="page-title">申请审批</h1>
            <p class="page-subtitle">审批会员课程申请</p>
          </div>
        </div>
        <div class="header-stats">
          <div class="stat-item">
            <span class="stat-label">待审批</span>
            <span class="stat-value warning">{{ pendingCount }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="申请人">
          <el-input
            v-model="searchForm.realName"
            placeholder="请输入申请人姓名"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="申请类型">
          <el-select
            v-model="searchForm.appType"
            placeholder="请选择申请类型"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="item in appTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="审批状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择审批状态"
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
        <el-table-column prop="formNum" label="申请单号" min-width="180" />
        <el-table-column prop="realName" label="申请人" min-width="100" />
        <el-table-column prop="appTypeName" label="申请类型" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.appType === 1 ? 'danger' : 'warning'" size="small">
              {{ row.appTypeName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="statusName" label="审批状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" min-width="180" />
        <el-table-column prop="appReason" label="申请原因" min-width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleViewDetail(row)">查看</el-button>
            <el-button v-if="row.status === 1" size="small" type="success" link @click="handleApprove(row)">通过</el-button>
            <el-button v-if="row.status === 1" size="small" type="danger" link @click="handleReject(row)">拒绝</el-button>
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
  color: #e6a23c;
  font-weight: 600;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #606266;
}

.stat-value.warning {
  color: #e6a23c;
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