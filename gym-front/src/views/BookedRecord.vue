<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Tickets } from '@element-plus/icons-vue'
import { memberApi } from '@/api/modules/member'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// 预约类型：1=团课, 2=私教
const bookingTypeMap = { 1: '团课', 2: '私教' }
// 预约状态：1=已预约, 2=已取消, 3=已完成, 4=申请取消中
const statusMap = { 1: '已预约', 2: '已取消', 3: '已完成', 4: '申请取消中' }

const searchForm = ref({ courseName: '', status: '' })
const pagination = ref({ currentPage: 1, pageSize: 10 })

const statusOptions = [
  { label: '全部', value: '' },
  { label: '已预约', value: 1 },
  { label: '已取消', value: 2 },
  { label: '已完成', value: 3 },
  { label: '申请取消中', value: 4 }
]

const allData = ref([])

const coachNameMap = { 4: '刘强', 5: '陈静', 6: '赵刚' }

const formatTime = (str) => {
  if (!str) return ''
  if (typeof str === 'string' && str.includes('-')) return str
  const d = new Date(str)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

const fetchList = async () => {
  loading.value = true
  try {
    const res = await memberApi.getBookedRecordList()
    if (res.resCode === '00') {
      allData.value = (res.result || []).map(item => ({
        ...item,
        bookingTypeName: bookingTypeMap[item.bookingType] || '未知',
        statusName: statusMap[item.status] || '未知',
        coachName: item.coachName || coachNameMap[item.coachId] || '',
        bookingTimeStr: formatTime(item.bookingTime),
        createTimeStr: formatTime(item.createTime)
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
  if (searchForm.value.courseName) {
    filtered = filtered.filter(item =>
      (item.courseName || '').includes(searchForm.value.courseName)
    )
  }
  if (searchForm.value.status !== '') {
    filtered = filtered.filter(item => item.status === searchForm.value.status)
  }
  total.value = filtered.length
  const start = (pagination.value.currentPage - 1) * pagination.value.pageSize
  tableData.value = filtered.slice(start, start + pagination.value.pageSize)
}

const handleSearch = () => { pagination.value.currentPage = 1; filterData() }
const handleReset = () => { searchForm.value = { courseName: '', status: '' }; pagination.value.currentPage = 1; filterData() }
const handlePageChange = (page) => { pagination.value.currentPage = page; filterData() }
const handleSizeChange = (size) => { pagination.value.pageSize = size; pagination.value.currentPage = 1; filterData() }

const getStatusTagType = (status) => {
  if (status === 1) return 'success'
  if (status === 2) return 'info'
  if (status === 3) return ''
  if (status === 4) return 'warning'
  return 'info'
}

const handleViewDetail = (row) => {
  ElMessageBox.alert(
    `<div style="padding: 4px 0;">
      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px 32px; margin-bottom: 20px; padding-bottom: 20px; border-bottom: 2px solid #ebeef5;">
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">课程名称</div>
          <div style="color: #303133; font-size: 15px; font-weight: 600;">${row.courseName || '—'}</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">预约类型</div>
          <div style="color: #606266; font-size: 14px;">${row.bookingTypeName}</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">授课教练</div>
          <div style="color: #606266; font-size: 14px;">${row.coachName || '—'}</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">预约状态</div>
          <div style="display: inline-block; padding: 4px 12px; border-radius: 4px; font-size: 13px; font-weight: 500; background: ${row.status === 1 ? '#f0f9eb' : row.status === 4 ? '#fdf6ec' : row.status === 2 ? '#f4f4f5' : '#ecf5ff'}; color: ${row.status === 1 ? '#67c23a' : row.status === 4 ? '#e6a23c' : row.status === 2 ? '#909399' : '#409eff'};">${row.statusName}</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">预约时间</div>
          <div style="color: #606266; font-size: 14px;">${row.bookingTimeStr || '—'}</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">创建时间</div>
          <div style="color: #606266; font-size: 14px;">${row.createTimeStr || '—'}</div>
        </div>
      </div>
      ${row.remark ? `<div>
        <div style="color: #909399; font-size: 12px; margin-bottom: 10px;">备注</div>
        <div style="color: #303133; font-size: 14px; line-height: 1.8; background: #f5f7fa; padding: 16px; border-radius: 6px; border-left: 3px solid #409eff;">${row.remark}</div>
      </div>` : ''}
    </div>`,
    '预约详情',
    { dangerouslyUseHTMLString: true, confirmButtonText: '关闭' }
  )
}

onMounted(() => { fetchList() })
</script>

<template>
  <div class="record-container">
    <div class="page-header">
      <h1 class="page-title"><el-icon><Tickets /></el-icon> 预约记录</h1>
      <p class="page-subtitle">查看我的课程预约记录</p>
    </div>

    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="课程名称">
          <el-input v-model="searchForm.courseName" placeholder="请输入课程名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="预约状态">
          <el-select v-model="searchForm.status" placeholder="请选择预约状态" clearable style="width: 150px">
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
      <el-table :data="tableData" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="courseName" label="课程名称" min-width="160" />
        <el-table-column prop="bookingTypeName" label="预约类型" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.bookingType === 1 ? 'success' : 'warning'" size="small">{{ row.bookingTypeName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="coachName" label="授课教练" min-width="120" />
        <el-table-column prop="bookingTimeStr" label="预约时间" min-width="180" />
        <el-table-column prop="statusName" label="预约状态" min-width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTimeStr" label="创建时间" min-width="180" />
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
.record-container { padding: 20px; background: #f5f7fa; min-height: calc(100vh - 60px); }
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
