<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Lightning } from '@element-plus/icons-vue'
import { memberApi } from '@/api/modules/member'

const loading = ref(false)
const allData = ref([])
const tableData = ref([])
const total = ref(0)

const statusMap = { 0: '未开始', 1: '进行中', 2: '已结束' }
const searchForm = ref({ status: '' })
const pagination = ref({ currentPage: 1, pageSize: 10 })

const statusOptions = [
  { label: '全部', value: '' },
  { label: '未开始', value: 0 },
  { label: '进行中', value: 1 },
  { label: '已结束', value: 2 }
]

// 弹窗
const dialogVisible = ref(false)
const dialogTitle = ref('新增秒杀活动')
const form = ref({
  cardId: '',
  cardName: '',
  seckillPrice: '',
  originalPrice: '',
  totalStock: '',
  startTime: '',
  endTime: ''
})
const formRef = ref(null)

const rules = {
  cardId: [{ required: true, message: '请输入会员卡ID', trigger: 'blur' }],
  cardName: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
  seckillPrice: [{ required: true, message: '请输入秒杀价', trigger: 'blur' }],
  originalPrice: [{ required: true, message: '请输入原价', trigger: 'blur' }],
  totalStock: [{ required: true, message: '请输入库存数量', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

// 获取列表
const fetchList = async () => {
  loading.value = true
  try {
    const res = await memberApi.getSeckillList()
    if (res.resCode === '00') {
      allData.value = (res.result || []).map(item => ({
        ...item,
        statusName: statusMap[item.status] ?? '未知'
      }))
      filterData()
    } else {
      ElMessage.error(res.resMsg || '获取列表失败')
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const filterData = () => {
  let filtered = [...allData.value]
  if (searchForm.value.status !== '') {
    filtered = filtered.filter(item => item.status === searchForm.value.status)
  }
  total.value = filtered.length
  const start = (pagination.value.currentPage - 1) * pagination.value.pageSize
  tableData.value = filtered.slice(start, start + pagination.value.pageSize)
}

const handleSearch = () => { pagination.value.currentPage = 1; filterData() }
const handleReset = () => { searchForm.value = { status: '' }; pagination.value.currentPage = 1; filterData() }
const handlePageChange = (page) => { pagination.value.currentPage = page; filterData() }
const handleSizeChange = (size) => { pagination.value.pageSize = size; pagination.value.currentPage = 1; filterData() }

const getStatusTagType = (status) => {
  if (status === 0) return 'warning'
  if (status === 1) return 'danger'
  if (status === 2) return 'info'
  return 'info'
}

const activeCount = computed(() => allData.value.filter(i => i.status === 1).length)

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增秒杀活动'
  form.value = { cardId: '', cardName: '', seckillPrice: '', originalPrice: '', totalStock: '', startTime: '', endTime: '' }
  dialogVisible.value = true
}

// 提交
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  try {
    const res = await memberApi.createSeckill(form.value)
    if (res.resCode === '00') {
      ElMessage.success('创建成功')
      dialogVisible.value = false
      fetchList()
    } else {
      ElMessage.error(res.resMsg || '创建失败')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

// 开关
const handleToggle = async (row) => {
  const action = row.status === 1 ? '关闭' : '开启'
  try {
    await ElMessageBox.confirm(`确认${action}「${row.cardName}」秒杀活动吗？`, '操作确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const newStatus = row.status === 1 ? 2 : 1
    const res = await memberApi.toggleSeckill({ id: row.id, status: newStatus })
    if (res.resCode === '00') {
      ElMessage.success(`已${action}`)
      fetchList()
    } else {
      ElMessage.error(res.resMsg || '操作失败')
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除「${row.cardName}」秒杀活动吗？删除后无法恢复。`, '删除确认', {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'danger'
    })
    const res = await memberApi.deleteSeckill({ id: row.id })
    if (res.resCode === '00') {
      ElMessage.success('删除成功')
      fetchList()
    } else {
      ElMessage.error(res.resMsg || '删除失败')
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => { fetchList() })
</script>

<template>
  <div class="seckill-manage-container">
    <!-- 页面标题 -->
    <el-card class="header-card" shadow="never">
      <div class="header-content">
        <div class="header-left">
          <el-icon class="header-icon"><Lightning /></el-icon>
          <div class="header-text">
            <h1 class="page-title">秒杀活动管理</h1>
            <p class="page-subtitle">创建和管理会员卡秒杀活动</p>
          </div>
        </div>
        <div class="header-stats">
          <div class="stat-item">
            <span class="stat-label">进行中</span>
            <span class="stat-value danger">{{ activeCount }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="活动状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 150px">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
          <el-button @click="handleReset" :icon="Refresh">重置</el-button>
          <el-button type="danger" @click="handleAdd" :icon="Plus">新增活动</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card" shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" min-width="60" />
        <el-table-column prop="cardName" label="活动名称" min-width="140" />
        <el-table-column prop="cardId" label="会员卡ID" min-width="90" />
        <el-table-column label="原价" min-width="90">
          <template #default="{ row }">¥{{ row.originalPrice }}</template>
        </el-table-column>
        <el-table-column label="秒杀价" min-width="90">
          <template #default="{ row }">
            <span style="color: #ff0844; font-weight: 700;">¥{{ row.seckillPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column label="库存" min-width="110">
          <template #default="{ row }">
            {{ row.availableStock }} / {{ row.totalStock }}
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" min-width="160" />
        <el-table-column prop="endTime" label="结束时间" min-width="160" />
        <el-table-column label="状态" min-width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="160">
          <template #default="{ row }">
            <el-button
              size="small"
              :type="row.status === 1 ? 'warning' : 'success'"
              link
              @click="handleToggle(row)"
              :disabled="row.status === 2"
            >
              {{ row.status === 1 ? '关闭' : '开启' }}
            </el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 新增弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="活动名称" prop="cardName">
          <el-input v-model="form.cardName" placeholder="如：月卡限时秒杀" />
        </el-form-item>
        <el-form-item label="会员卡ID" prop="cardId">
          <el-input v-model="form.cardId" placeholder="关联的会员卡种ID" />
        </el-form-item>
        <el-form-item label="原价" prop="originalPrice">
          <el-input v-model="form.originalPrice" placeholder="原价（元）" type="number" />
        </el-form-item>
        <el-form-item label="秒杀价" prop="seckillPrice">
          <el-input v-model="form.seckillPrice" placeholder="秒杀价（元）" type="number" />
        </el-form-item>
        <el-form-item label="库存数量" prop="totalStock">
          <el-input v-model="form.totalStock" placeholder="秒杀库存" type="number" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间"
            format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束时间"
            format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确认创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.seckill-manage-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}
.header-card { margin-bottom: 20px; border-radius: 12px; border: none; }
.header-card :deep(.el-card__body) { padding: 20px 24px; }
.header-content { display: flex; justify-content: space-between; align-items: center; }
.header-left { display: flex; align-items: center; gap: 12px; }
.header-icon { font-size: 28px; color: #ff0844; }
.header-text { display: flex; flex-direction: column; gap: 2px; }
.page-title { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
.page-subtitle { font-size: 13px; color: #909399; margin: 0; }
.header-stats { display: flex; align-items: center; gap: 32px; }
.stat-item { display: flex; flex-direction: column; align-items: center; gap: 4px; }
.stat-label { font-size: 14px; color: #ff0844; font-weight: 600; }
.stat-value { font-size: 24px; font-weight: 600; color: #606266; }
.stat-value.danger { color: #ff0844; }
.search-card { margin-bottom: 20px; border-radius: 12px; border: none; }
.search-card :deep(.el-card__body) { padding: 20px; }
.search-card :deep(.el-form) { margin-bottom: 0; }
.table-card { border-radius: 12px; border: none; }
.table-card :deep(.el-card__body) { padding: 20px; }
.pagination-wrapper { display: flex; justify-content: flex-end; margin-top: 20px; }
</style>
