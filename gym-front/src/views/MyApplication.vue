<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Document } from '@element-plus/icons-vue'
import { memberApi } from '@/api/modules/member'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const appTypeMap = { 1: '退课申请', 2: '预约取消申请' }
const statusMap = { 1: '待处理', 2: '审批通过', 3: '审批驳回' }

const searchForm = ref({ appType: '', status: '' })

const pagination = ref({ currentPage: 1, pageSize: 10 })

const appTypeOptions = [
  { label: '全部', value: '' },
  { label: '退课申请', value: 1 },
  { label: '预约取消申请', value: 2 }
]

const statusOptions = [
  { label: '全部', value: '' },
  { label: '待处理', value: 1 },
  { label: '审批通过', value: 2 },
  { label: '审批驳回', value: 3 }
]

const allData = ref([])

const formatTime = (isoStr) => {
  if (!isoStr) return ''
  const d = new Date(isoStr)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

const fetchList = async () => {
  loading.value = true
  try {
    const res = await memberApi.getMyApplicationList()
    if (res.resCode === '00') {
      allData.value = (res.result || []).map(item => ({
        ...item,
        appTypeName: appTypeMap[item.appType] || '未知',
        statusName: statusMap[item.status] || '未知',
        applyTime: formatTime(item.createTime)
      }))
      filterData()
    } else {
      ElMessage.error(res.resMsg || '获取申请列表失败')
    }
  } catch (e) {
    console.error('获取申请列表失败:', e)
  } finally {
    loading.value = false
  }
}

const filterData = () => {
  let filtered = [...allData.value]
  if (searchForm.value.appType !== '') {
    filtered = filtered.filter(item => item.appType === searchForm.value.appType)
  }
  if (searchForm.value.status !== '') {
    filtered = filtered.filter(item => item.status === searchForm.value.status)
  }
  total.value = filtered.length
  const start = (pagination.value.currentPage - 1) * pagination.value.pageSize
  tableData.value = filtered.slice(start, start + pagination.value.pageSize)
}

const handleSearch = () => { pagination.value.currentPage = 1; filterData() }
const handleReset = () => { searchForm.value = { appType: '', status: '' }; pagination.value.currentPage = 1; filterData() }
const handlePageChange = (page) => { pagination.value.currentPage = page; filterData() }
const handleSizeChange = (size) => { pagination.value.pageSize = size; pagination.value.currentPage = 1; filterData() }

const getStatusTagType = (status) => {
  if (status === 1) return 'warning'
  if (status === 2) return 'success'
  if (status === 3) return 'danger'
  return 'info'
}

const tableRowClassName = ({ row }) => {
  if (row.status === 3) return 'row-rejected'
  return ''
}

const handleViewDetail = (row) => {
  ElMessageBox.alert(
    `<div style="padding: 4px 0;">
      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px 32px; margin-bottom: 20px; padding-bottom: 20px; border-bottom: 2px solid #ebeef5;">
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">申请单号</div>
          <div style="color: #303133; font-size: 15px; font-weight: 600;">${row.formNum}</div>
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
          <div style="color: #606266; font-size: 14px;">${row.applyTime}</div>
        </div>
      </div>
      <div style="margin-bottom: 16px;">
        <div style="color: #909399; font-size: 12px; margin-bottom: 10px;">申请原因</div>
        <div style="color: #303133; font-size: 14px; line-height: 1.8; background: #f5f7fa; padding: 16px; border-radius: 6px; border-left: 3px solid #409eff;">${row.appReason || '无'}</div>
      </div>
      ${row.refuseReason ? `<div>
        <div style="color: #909399; font-size: 12px; margin-bottom: 10px;">驳回原因</div>
        <div style="color: #f56c6c; font-size: 14px; line-height: 1.8; background: #fef0f0; padding: 16px; border-radius: 6px; border-left: 3px solid #f56c6c;">${row.refuseReason}</div>
      </div>` : ''}
    </div>`,
    '申请详情',
    { dangerouslyUseHTMLString: true, confirmButtonText: '关闭' }
  )
}

onMounted(() => { fetchList() })
</script>

<template>
  <div class="application-container">
    <div class="page-header">
      <h1 class="page-title"><el-icon><Document /></el-icon> 我的申请</h1>
      <p class="page-subtitle">查看我提交的申请记录</p>
    </div>

    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="申请类型">
          <el-select v-model="searchForm.appType" placeholder="请选择申请类型" clearable style="width: 160px">
            <el-option v-for="item in appTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="审批状态">
          <el-select v-model="searchForm.status" placeholder="请选择审批状态" clearable style="width: 150px">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
          <el-button @click="handleReset" :icon="Refresh">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe style="width: 100%" :row-class-name="tableRowClassName">
        <el-table-column prop="formNum" label="申请单号" min-width="180" />
        <el-table-column prop="appTypeName" label="申请类型" min-width="140">
          <template #default="{ row }">
            <el-tag :type="row.appType === 1 ? 'warning' : 'primary'" size="small">{{ row.appTypeName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="statusName" label="审批状态" min-width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" min-width="200" />
        <el-table-column prop="appReason" label="申请原因" min-width="200" show-overflow-tooltip />
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
.application-container { padding: 20px; background: #f5f7fa; min-height: calc(100vh - 60px); }
.page-header { margin-bottom: 20px; padding-bottom: 20px; border-bottom: 1px solid #e8e8e8; }
.page-title { display: flex; align-items: center; gap: 12px; font-size: 28px; font-weight: 900; color: #1a1a2e; margin: 0 0 8px 0; }
.page-subtitle { font-size: 14px; color: #666; margin: 0; }
.search-card { margin-bottom: 20px; border-radius: 12px; border: none; }
.search-card :deep(.el-card__body) { padding: 20px; }
.search-card :deep(.el-form) { margin-bottom: 0; }
.table-card { border-radius: 12px; border: none; }
.table-card :deep(.el-card__body) { padding: 20px; }
.pagination-wrapper { display: flex; justify-content: flex-end; margin-top: 20px; }
:deep(.el-table .row-rejected) { background-color: #fef0f0 !important; }
:deep(.el-table .row-rejected td.el-table__cell) { background-color: #fef0f0 !important; color: #f56c6c; }
</style>
