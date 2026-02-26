<script setup>
import { ref, computed, onMounted } from 'vue'
import { memberApi } from '@/api/modules/member'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const allUsers = ref([])
const searchForm = ref({ realName: '', userType: '' })
const currentPage = ref(1)
const pageSize = ref(10)

const userTypeMap = { 1: '超级管理员', 2: '管理员', 3: '教练', 4: '会员' }
const userTypeOptions = [
  { label: '超级管理员', value: 1 },
  { label: '管理员', value: 2 },
  { label: '教练', value: 3 },
  { label: '会员', value: 4 }
]

const filteredUsers = computed(() => {
  const filtered = allUsers.value.filter(u => {
    if (searchForm.value.realName && !u.realName?.includes(searchForm.value.realName)) return false
    if (searchForm.value.userType !== '' && u.userType !== searchForm.value.userType) return false
    return true
  })
  // 排序：优先用户类型，其次ID
  return filtered.sort((a, b) => {
    if (a.userType !== b.userType) {
      return a.userType - b.userType
    }
    return a.id - b.id
  })
})

const pagedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredUsers.value.slice(start, start + pageSize.value)
})

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await memberApi.getManagerList()
    if (res.resCode === '00') {
      allUsers.value = res.result || []
    } else {
      ElMessage.warning(res.resMsg || '获取用户列表失败')
    }
  } catch (e) {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { currentPage.value = 1 }
const handleReset = () => {
  searchForm.value = { realName: '', userType: '' }
  currentPage.value = 1
}

// 修改用户类型
const editDialogVisible = ref(false)
const editForm = ref({ userId: null, username: '', realName: '', currentType: null, newType: null })

const openEditDialog = (row) => {
  editForm.value = {
    userId: row.id,
    username: row.username,
    realName: row.realName,
    currentType: row.userType,
    newType: row.userType
  }
  editDialogVisible.value = true
}

const submitChangeUserType = async () => {
  if (editForm.value.newType === null) { ElMessage.warning('请选择用户类型'); return }
  if (editForm.value.newType === editForm.value.currentType) {
    ElMessage.warning('用户类型未改变')
    return
  }
  
  loading.value = true
  try {
    const res = await memberApi.updateUserType({
      id: editForm.value.userId,
      userType: editForm.value.newType
    })
    if (res.resCode === '00') {
      ElMessage.success(`已将「${editForm.value.realName}」的用户类型修改为「${userTypeMap[editForm.value.newType]}」`)
      const user = allUsers.value.find(u => u.id === editForm.value.userId)
      if (user) user.userType = editForm.value.newType
      editDialogVisible.value = false
    } else {
      ElMessage.warning(res.resMsg || '修改失败')
    }
  } catch (e) {
    ElMessage.error('修改用户类型失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => { loadUsers() })
</script>

<template>
  <div class="permission-page">
    <div class="page-header">
      <div class="header-left">
        <el-icon :size="22"><Key /></el-icon>
        <div>
          <h2>权限分配</h2>
          <p>修改用户类型以分配权限</p>
        </div>
      </div>
      <div class="header-right">
        <span class="total-badge">共 {{ filteredUsers.length }} 人</span>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input v-model="searchForm.realName" placeholder="请输入姓名" clearable style="width: 180px" />
      <el-select v-model="searchForm.userType" placeholder="用户类型" clearable style="width: 140px">
        <el-option label="超级管理员" :value="1" />
        <el-option label="管理员" :value="2" />
        <el-option label="教练" :value="3" />
        <el-option label="会员" :value="4" />
      </el-select>
      <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon> 搜索</el-button>
      <el-button @click="handleReset"><el-icon><Refresh /></el-icon> 重置</el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="pagedUsers" v-loading="loading" stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="60" align="center" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="realName" label="真实姓名" width="120" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="email" label="邮箱" min-width="180">
        <template #default="{ row }">{{ row.email || '--' }}</template>
      </el-table-column>
      <el-table-column prop="userType" label="当前用户类型" width="130" align="center">
        <template #default="{ row }">
          <el-tag :type="row.userType === 1 ? 'danger' : row.userType === 2 ? 'warning' : row.userType === 3 ? '' : 'info'" size="small">
            {{ userTypeMap[row.userType] || '未知' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="修改用户类型" width="180" align="center">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="openEditDialog(row)">修改权限</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrap">
      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]" :total="filteredUsers.length"
        layout="total, sizes, prev, pager, next, jumper" background />
    </div>

    <!-- 修改权限弹窗 -->
    <el-dialog v-model="editDialogVisible" title="修改用户权限" width="420px" :close-on-click-modal="false">
      <el-form label-width="100px">
        <el-form-item label="用户名">
          <span>{{ editForm.username }}</span>
        </el-form-item>
        <el-form-item label="真实姓名">
          <span>{{ editForm.realName }}</span>
        </el-form-item>
        <el-form-item label="当前用户类型">
          <el-tag :type="editForm.currentType === 1 ? 'danger' : editForm.currentType === 2 ? 'warning' : editForm.currentType === 3 ? '' : 'info'" size="small">
            {{ userTypeMap[editForm.currentType] }}
          </el-tag>
        </el-form-item>
        <el-form-item label="新用户类型" required>
          <el-select v-model="editForm.newType" placeholder="请选择用户类型" style="width: 100%">
            <el-option v-for="opt in userTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitChangeUserType">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.permission-page { padding: 24px; background: #fff; min-height: 100%; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.header-left { display: flex; align-items: center; gap: 12px; color: #303133; }
.header-left h2 { margin: 0; font-size: 20px; font-weight: 700; }
.header-left p { margin: 0; font-size: 13px; color: #909399; }
.header-right .total-badge { font-size: 14px; color: #409eff; font-weight: 600; }
.search-bar { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; flex-wrap: wrap; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 20px; }
</style>
