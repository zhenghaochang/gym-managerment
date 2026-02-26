<script setup>
import { ref, computed, onMounted } from 'vue'
import { memberApi } from '@/api/modules/member'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const allUsers = ref([])
const searchForm = ref({ realName: '', status: '' })
const currentPage = ref(1)
const pageSize = ref(10)

const userTypeMap = { 1: '超级管理员', 2: '管理员', 3: '教练', 4: '会员' }
const statusMap = { 0: '禁用', 1: '正常' }

const filteredUsers = computed(() => {
  return allUsers.value.filter(u => {
    if (searchForm.value.realName && !u.realName?.includes(searchForm.value.realName)) return false
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
    const res = await memberApi.getUserList()
    if (res.resCode === '00') {
      allUsers.value = (res.result || []).filter(u => u.userType === 4)
    } else {
      ElMessage.warning(res.resMsg || '获取会员列表失败')
    }
  } catch (e) {
    ElMessage.error('获取会员列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { currentPage.value = 1 }
const handleReset = () => {
  searchForm.value = { realName: '', status: '' }
  currentPage.value = 1
}

// 查看详情
const detailVisible = ref(false)
const detailUser = ref({})
const showDetail = (row) => {
  detailUser.value = row
  detailVisible.value = true
}

// 删除会员
const handleDeleteMember = (row) => {
  ElMessageBox.confirm(`确定要删除会员「${row.realName}」吗？删除后不可恢复。`, '确认删除', {
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
  <div class="member-list-page">
    <div class="page-header">
      <div class="header-left">
        <el-icon :size="22"><User /></el-icon>
        <div>
          <h2>会员信息</h2>
          <p>管理会员信息</p>
        </div>
      </div>
      <div class="header-right">
        <span class="total-badge">共 {{ filteredUsers.length }} 人</span>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input v-model="searchForm.realName" placeholder="请输入姓名" clearable style="width: 180px" />
      <el-select v-model="searchForm.status" placeholder="状态" clearable style="width: 120px">
        <el-option label="正常" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
      <el-button type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon> 搜索
      </el-button>
      <el-button @click="handleReset">
        <el-icon><Refresh /></el-icon> 重置
      </el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="pagedUsers" v-loading="loading" stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="60" align="center" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="realName" label="真实姓名" width="100" />
      <el-table-column prop="nickName" label="昵称" width="100">
        <template #default="{ row }">{{ row.nickName || '--' }}</template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="email" label="邮箱" min-width="160">
        <template #default="{ row }">{{ row.email || '--' }}</template>
      </el-table-column>
      <el-table-column prop="gender" label="性别" width="70" align="center">
        <template #default="{ row }">
          <span>{{ row.gender === 1 ? '男' : row.gender === 2 ? '女' : '未知' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ statusMap[row.status] || '未知' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="120" align="center" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="showDetail(row)">查看</el-button>
          <el-button type="danger" link size="small" @click="handleDeleteMember(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="filteredUsers.length"
        layout="total, sizes, prev, pager, next, jumper"
        background
      />
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
        <el-descriptions-item label="生日">{{ detailUser.birthday || '--' }}</el-descriptions-item>
        <el-descriptions-item label="身高">{{ detailUser.height ? detailUser.height + ' cm' : '--' }}</el-descriptions-item>
        <el-descriptions-item label="体重">{{ detailUser.weight ? detailUser.weight + ' kg' : '--' }}</el-descriptions-item>
        <el-descriptions-item label="用户类型">
          <el-tag :type="detailUser.userType === 1 ? 'danger' : detailUser.userType === 2 ? 'warning' : detailUser.userType === 3 ? '' : 'info'" size="small">
            {{ userTypeMap[detailUser.userType] || '未知' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailUser.status === 1 ? 'success' : 'danger'" size="small">
            {{ statusMap[detailUser.status] || '未知' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ detailUser.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间" :span="2">{{ detailUser.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

  </div>
</template>

<style scoped>
.member-list-page { padding: 24px; background: #fff; min-height: 100%; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.header-left { display: flex; align-items: center; gap: 12px; color: #303133; }
.header-left h2 { margin: 0; font-size: 20px; font-weight: 700; }
.header-left p { margin: 0; font-size: 13px; color: #909399; }
.header-right .total-badge { font-size: 14px; color: #409eff; font-weight: 600; }
.search-bar { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; flex-wrap: wrap; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 20px; }
</style>
