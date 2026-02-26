<script setup>
import { ref, computed, onMounted } from 'vue'
import { memberApi } from '@/api/modules/member'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const allUsers = ref([])
const searchForm = ref({ realName: '', userType: '', status: '' })
const currentPage = ref(1)
const pageSize = ref(10)

const userTypeMap = { 1: '超级管理员', 2: '管理员', 3: '教练', 4: '会员' }
const statusMap = { 0: '禁用', 1: '正常' }

const filteredUsers = computed(() => {
  return allUsers.value.filter(u => {
    if (searchForm.value.realName && !u.realName?.includes(searchForm.value.realName)) return false
    if (searchForm.value.userType !== '' && u.userType !== searchForm.value.userType) return false
    if (searchForm.value.status !== '' && u.status !== searchForm.value.status) return false
    return true
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
  searchForm.value = { realName: '', userType: '', status: '' }
  currentPage.value = 1
}

// 查看详情
const detailVisible = ref(false)
const detailUser = ref({})
const showDetail = (row) => { detailUser.value = row; detailVisible.value = true }

// 新增管理员
const addDialogVisible = ref(false)
const addForm = ref({ username: '', password: '', realName: '', phone: '' })
const openAddDialog = () => {
  addForm.value = { username: '', password: '', realName: '', phone: '' }
  addDialogVisible.value = true
}
const submitAddAdmin = async () => {
  if (!addForm.value.username) { ElMessage.warning('请输入用户名'); return }
  if (!addForm.value.password) { ElMessage.warning('请输入密码'); return }
  if (!addForm.value.realName) { ElMessage.warning('请输入真实姓名'); return }
  try {
    const res = await memberApi.addManager({
      username: addForm.value.username,
      password: addForm.value.password,
      phone: addForm.value.phone,
      realName: addForm.value.realName
    })
    if (res.resCode === '00') {
      ElMessage.success(res.resMsg || '新增管理员成功')
      addDialogVisible.value = false
      loadUsers()
    } else {
      ElMessage.warning(res.resMsg || '新增管理员失败')
    }
  } catch (e) {
    ElMessage.error('新增管理员失败')
  }
}

// 删除用户（仅管理员可删）
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除用户「${row.realName}」吗？删除后不可恢复。`, '确认删除', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    try {
      const res = await memberApi.deleteUser({ id: row.id })
      if (res.resCode === '00') {
        ElMessage.success(res.resMsg || '删除成功')
        loadUsers()
      } else {
        ElMessage.warning(res.resMsg || '删除失败')
      }
    } catch (e) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(() => { loadUsers() })
</script>

<template>
  <div class="user-manage-page">
    <div class="page-header">
      <div class="header-left">
        <el-icon :size="22"><Setting /></el-icon>
        <div>
          <h2>用户管理</h2>
          <p>管理系统所有用户</p>
        </div>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="openAddDialog">
          <el-icon><Plus /></el-icon> 新增管理员
        </el-button>
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
      <el-select v-model="searchForm.status" placeholder="状态" clearable style="width: 120px">
        <el-option label="正常" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
      <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon> 搜索</el-button>
      <el-button @click="handleReset"><el-icon><Refresh /></el-icon> 重置</el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="pagedUsers" v-loading="loading" stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="60" align="center" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="realName" label="真实姓名" width="100" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="email" label="邮箱" min-width="160">
        <template #default="{ row }">{{ row.email || '--' }}</template>
      </el-table-column>
      <el-table-column prop="userType" label="用户类型" width="110" align="center">
        <template #default="{ row }">
          <el-tag :type="row.userType === 1 ? 'danger' : row.userType === 2 ? 'warning' : row.userType === 3 ? '' : 'info'" size="small">
            {{ userTypeMap[row.userType] || '未知' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ statusMap[row.status] || '未知' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="120" align="center" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="showDetail(row)">查看</el-button>
          <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrap">
      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]" :total="filteredUsers.length"
        layout="total, sizes, prev, pager, next, jumper" background />
    </div>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="用户详情" width="520px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID">{{ detailUser.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ detailUser.username }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ detailUser.realName || '--' }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ detailUser.nickName || '--' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detailUser.phone || '--' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ detailUser.email || '--' }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ detailUser.gender === 1 ? '男' : detailUser.gender === 2 ? '女' : '未知' }}</el-descriptions-item>
        <el-descriptions-item label="用户类型">
          <el-tag :type="detailUser.userType === 1 ? 'danger' : detailUser.userType === 2 ? 'warning' : detailUser.userType === 3 ? '' : 'info'" size="small">
            {{ userTypeMap[detailUser.userType] || '未知' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailUser.status === 1 ? 'success' : 'danger'" size="small">{{ statusMap[detailUser.status] || '未知' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ detailUser.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间" :span="2">{{ detailUser.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 新增管理员弹窗 -->
    <el-dialog v-model="addDialogVisible" title="新增管理员" width="460px" :close-on-click-modal="false">
      <el-form :model="addForm" label-width="80px">
        <el-form-item label="用户名" required>
          <el-input v-model="addForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" required>
          <el-input v-model="addForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="真实姓名" required>
          <el-input v-model="addForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="addForm.phone" placeholder="请输入手机号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAddAdmin">确认新增</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.user-manage-page { padding: 24px; background: #fff; min-height: 100%; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.header-left { display: flex; align-items: center; gap: 12px; color: #303133; }
.header-left h2 { margin: 0; font-size: 20px; font-weight: 700; }
.header-left p { margin: 0; font-size: 13px; color: #909399; }
.header-right { display: flex; align-items: center; gap: 12px; }
.header-right .total-badge { font-size: 14px; color: #409eff; font-weight: 600; }
.search-bar { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; flex-wrap: wrap; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 20px; }
</style>
