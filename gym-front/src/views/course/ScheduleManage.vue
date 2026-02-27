<script setup>
import { ref, onMounted } from 'vue'
import { memberApi } from '@/api/modules/member'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const scheduleList = ref([])
const courseList = ref([])
const courseMap = ref({}) // 课程ID到课程信息的映射
const showDialog = ref(false)
const dialogTitle = ref('新增排课')

// 排课表单
const scheduleForm = ref({
  id: null,
  courseId: null,
  coachId: null,
  weekDay: null,
  startTime: '',
  endTime: '',
  location: '',
  maxCapacity: 0,
  status: 1
})

// 搜索表单
const searchForm = ref({
  courseName: '',
  weekDay: ''
})

// 加载排课列表
const loadScheduleList = async () => {
  loading.value = true
  try {
    // TODO: 调用获取排课列表接口
    const res = await memberApi.getScheduleList(searchForm.value)
    if (res.resCode === '00') {
      scheduleList.value = (res.result || []).sort((a, b) => {
        if (a.weekDay !== b.weekDay) return a.weekDay - b.weekDay
        return (a.startTime || '').localeCompare(b.startTime || '')
      })
    } else {
      ElMessage.warning(res.resMsg || '获取排课列表失败')
    }
  } catch (e) {
    ElMessage.error('获取排课列表失败')
  } finally {
    loading.value = false
  }
}

// 加载课程列表
const loadCourseList = async () => {
  try {
    const res = await memberApi.getCourseList()
    if (res.resCode === '00') {
      const allCourses = res.result || []
      // 新增排课时只显示上架的团课
      courseList.value = allCourses.filter(c => c.courseType == 1 && c.status == 1)
      // 映射包含所有课程，用于展示排课信息
      courseMap.value = {}
      allCourses.forEach(course => {
        courseMap.value[course.id] = course
      })
    }
  } catch (e) {
    ElMessage.error('获取课程列表失败')
  }
}

// 根据courseId获取课程信息
const getCourseInfo = (courseId) => {
  return courseMap.value[courseId] || {}
}

// 搜索
const handleSearch = () => {
  loadScheduleList()
}

// 重置
const handleReset = () => {
  searchForm.value = { courseName: '', weekDay: '' }
  loadScheduleList()
}

// 打开新增对话框
const handleAdd = () => {
  dialogTitle.value = '新增排课'
  scheduleForm.value = {
    id: null,
    courseId: null,
    coachId: null,
    weekDay: null,
    startTime: '',
    endTime: '',
    location: '',
    maxCapacity: 0,
    status: 1
  }
  showDialog.value = true
}

// 打开编辑对话框
const handleEdit = (row) => {
  dialogTitle.value = '编辑排课'
  scheduleForm.value = { 
    ...row,
    status: Number(row.status)
  }
  showDialog.value = true
}

// 选择课程时更新课程信息
const handleCourseChange = (courseId) => {
  const course = courseList.value.find(c => c.id === courseId)
  if (course) {
    scheduleForm.value.coachId = course.coachId
    scheduleForm.value.maxCapacity = course.capacity || 0
  }
}

// 提交表单
const submitForm = async () => {
  if (!scheduleForm.value.courseId) {
    ElMessage.warning('请选择课程')
    return
  }
  if (!scheduleForm.value.weekDay) {
    ElMessage.warning('请选择上课日期')
    return
  }
  if (!scheduleForm.value.startTime) {
    ElMessage.warning('请选择开始时间')
    return
  }
  if (!scheduleForm.value.endTime) {
    ElMessage.warning('请选择结束时间')
    return
  }
  if (!scheduleForm.value.location) {
    ElMessage.warning('请输入上课地点')
    return
  }

  // 冲突检测：同一天、同一地点、时间段重叠
  const conflicts = scheduleList.value.filter(item => {
    // 编辑时排除自身
    if (scheduleForm.value.id && item.id === scheduleForm.value.id) return false
    // 同一天 + 同一地点
    if (item.weekDay !== scheduleForm.value.weekDay) return false
    if (item.location !== scheduleForm.value.location) return false
    // 时间段重叠判断：A开始 < B结束 且 B开始 < A结束
    return scheduleForm.value.startTime < item.endTime && item.startTime < scheduleForm.value.endTime
  })

  if (conflicts.length > 0) {
    const weekDayName = ['', '周一', '周二', '周三', '周四', '周五', '周六', '周日']
    const detail = conflicts.map(c => {
      const name = getCourseInfo(c.courseId).courseName || `课程${c.courseId}`
      return `「${name}」${weekDayName[c.weekDay]} ${c.startTime}-${c.endTime} ${c.location}`
    }).join('\n')
    ElMessage.warning(`时间和地点冲突，与以下排课冲突：\n${detail}`)
    return
  }

  loading.value = true
  try {
    const params = {
      courseId: scheduleForm.value.courseId,
      coachId: scheduleForm.value.coachId,
      weekDay: scheduleForm.value.weekDay,
      startTime: scheduleForm.value.startTime,
      endTime: scheduleForm.value.endTime,
      location: scheduleForm.value.location,
      maxCapacity: scheduleForm.value.maxCapacity,
      status: scheduleForm.value.status
    }
    let res
    if (scheduleForm.value.id) {
      // 编辑
      params.id = scheduleForm.value.id
      res = await memberApi.updateSchedule(params)
    } else {
      // 新增
      res = await memberApi.insertSchedule(params)
    }
    if (res.resCode === '00') {
      ElMessage.success(scheduleForm.value.id ? '编辑成功' : '新增成功')
      showDialog.value = false
      loadScheduleList()
    } else {
      ElMessage.warning(res.resMsg || '操作失败')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    loading.value = false
  }
}

// 删除排课
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除该排课吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    loading.value = true
    try {
      const res = await memberApi.deleteSchedule({ id: row.id })
      if (res.resCode === '00') {
        ElMessage.success(res.resMsg || '删除成功')
        loadScheduleList()
      } else {
        ElMessage.warning(res.resMsg || '删除失败')
      }
    } catch (e) {
      ElMessage.error('删除失败')
    } finally {
      loading.value = false
    }
  }).catch(() => {})
}

onMounted(async () => {
  await loadCourseList() // 先加载课程列表，建立映射
  loadScheduleList() // 再加载排课列表
})
</script>

<template>
  <div class="schedule-manage-page">
    <div class="page-header">
      <div class="header-left">
        <el-icon :size="22"><Calendar /></el-icon>
        <div>
          <h2>排课管理</h2>
          <p>管理团课的上课时间和地点</p>
        </div>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon> 新增排课
        </el-button>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input v-model="searchForm.courseName" placeholder="课程名称" clearable style="width: 200px" />
      <el-select v-model="searchForm.weekDay" placeholder="选择周几" clearable style="width: 140px">
        <el-option label="周一" :value="1" />
        <el-option label="周二" :value="2" />
        <el-option label="周三" :value="3" />
        <el-option label="周四" :value="4" />
        <el-option label="周五" :value="5" />
        <el-option label="周六" :value="6" />
        <el-option label="周日" :value="7" />
      </el-select>
      <el-button type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon> 搜索
      </el-button>
      <el-button @click="handleReset">
        <el-icon><Refresh /></el-icon> 重置
      </el-button>
    </div>

    <!-- 排课列表 -->
    <el-table :data="scheduleList" v-loading="loading" stripe style="width: 100%">
      <el-table-column prop="id" label="排课ID" min-width="80" align="center" />
      <el-table-column label="课程名称" min-width="150">
        <template #default="{ row }">
          {{ getCourseInfo(row.courseId).courseName || row.courseName || '--' }}
        </template>
      </el-table-column>
      <el-table-column label="上课日期" min-width="100" align="center">
        <template #default="{ row }">
          {{ ['', '周一', '周二', '周三', '周四', '周五', '周六', '周日'][row.weekDay] || '--' }}
        </template>
      </el-table-column>
      <el-table-column label="上课时间" min-width="150" align="center">
        <template #default="{ row }">
          {{ row.startTime }} - {{ row.endTime }}
        </template>
      </el-table-column>
      <el-table-column prop="location" label="上课地点" min-width="160" />
      <el-table-column label="教练" min-width="100">
        <template #default="{ row }">
          {{ getCourseInfo(row.courseId).coachName || '--' }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" min-width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status == 1 ? 'success' : 'info'" size="small">
            {{ row.status == 1 ? '可预约' : '已取消' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="150" align="center">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="showDialog" :title="dialogTitle" width="600px" :close-on-click-modal="false">
      <el-form :model="scheduleForm" label-width="100px">
        <el-form-item label="选择课程" required>
          <el-select 
            v-model="scheduleForm.courseId" 
            placeholder="请选择团课" 
            style="width: 100%"
            @change="handleCourseChange"
            :disabled="!!scheduleForm.id"
          >
            <el-option 
              v-for="course in courseList" 
              :key="course.id" 
              :label="`${course.courseName} (容量:${course.capacity}人)`" 
              :value="course.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="上课日期" required>
          <el-select v-model="scheduleForm.weekDay" placeholder="请选择周几" style="width: 100%">
            <el-option label="周一" :value="1" />
            <el-option label="周二" :value="2" />
            <el-option label="周三" :value="3" />
            <el-option label="周四" :value="4" />
            <el-option label="周五" :value="5" />
            <el-option label="周六" :value="6" />
            <el-option label="周日" :value="7" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" required>
          <el-time-picker 
            v-model="scheduleForm.startTime" 
            placeholder="选择开始时间"
            format="HH:mm"
            value-format="HH:mm"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" required>
          <el-time-picker 
            v-model="scheduleForm.endTime" 
            placeholder="选择结束时间"
            format="HH:mm"
            value-format="HH:mm"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="上课地点" required>
          <el-input v-model="scheduleForm.location" placeholder="请输入上课地点，如：一楼瑜伽室" />
        </el-form-item>
        <el-form-item label="最大容量">
          <el-input-number 
            v-model="scheduleForm.maxCapacity" 
            :min="1" 
            :max="100"
            style="width: 100%"
            :disabled="true"
          />
          <span style="margin-left: 10px; color: #909399;">人（根据课程自动设置）</span>
        </el-form-item>
        <el-form-item label="排课状态" v-if="scheduleForm.id">
          <el-radio-group v-model="scheduleForm.status">
            <el-radio :label="1">可预约</el-radio>
            <el-radio :label="0">已取消</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="loading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.schedule-manage-page {
  padding: 24px;
  background: #fff;
  min-height: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #303133;
}

.header-left h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
}

.header-left p {
  margin: 0;
  font-size: 13px;
  color: #909399;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}
</style>
