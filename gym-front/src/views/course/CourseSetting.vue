<script setup>
import { ref, onMounted } from 'vue'
import { memberApi } from '@/api/modules/member'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const courseList = ref([])
const coachList = ref([])
const showDialog = ref(false)
const dialogTitle = ref('新增课程')

// 课程表单
const courseForm = ref({
  id: null,
  courseName: '',
  coachId: null,
  coachName: '',
  duration: null,
  price: null,
  capacity: null,
  courseType: 1,
  difficultyLevel: 1,
  status: 1,
  description: ''
})

const courseTypeOptions = [
  { label: '团课', value: 1 },
  { label: '私教', value: 2 }
]

const difficultyLevelOptions = [
  { label: '初级', value: 1 },
  { label: '中级', value: 2 },
  { label: '高级', value: 3 }
]

// 加载课程列表
const loadCourseList = async () => {
  loading.value = true
  try {
    const res = await memberApi.getCourseList()
    if (res.resCode === '00') {
      courseList.value = (res.result || []).sort((a, b) => {
        if (a.courseType !== b.courseType) return a.courseType - b.courseType
        return a.id - b.id
      })
    } else {
      ElMessage.warning(res.resMsg || '获取课程列表失败')
    }
  } catch (e) {
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

// 加载教练列表
const loadCoachList = async () => {
  try {
    const res = await memberApi.getManagerList()
    if (res.resCode === '00') {
      // 筛选出教练（userType = 3）
      coachList.value = (res.result || []).filter(u => u.userType === 3)
    }
  } catch (e) {
    ElMessage.error('获取教练列表失败')
  }
}

// 打开新增对话框
const handleAdd = () => {
  dialogTitle.value = '新增课程'
  courseForm.value = {
    id: null,
    courseName: '',
    coachId: null,
    coachName: '',
    duration: null,
    price: null,
    capacity: null,
    courseType: 1,
    difficultyLevel: 1,
    status: 1,
    description: ''
  }
  showDialog.value = true
}

// 打开编辑对话框
const handleEdit = (row) => {
  dialogTitle.value = '编辑课程'
  courseForm.value = { 
    ...row,
    status: Number(row.status),
    difficultyLevel: Number(row.difficultyLevel),
    courseType: Number(row.courseType)
  }
  showDialog.value = true
}

// 选择教练时更新教练姓名
const handleCoachChange = (coachId) => {
  const coach = coachList.value.find(c => c.id === coachId)
  if (coach) {
    courseForm.value.coachName = coach.realName
  }
}

// 提交表单
const submitForm = async () => {
  if (!courseForm.value.courseName) {
    ElMessage.warning('请输入课程名称')
    return
  }
  if (!courseForm.value.coachId) {
    ElMessage.warning('请选择教练')
    return
  }
  if (!courseForm.value.duration) {
    ElMessage.warning('请输入课程时长')
    return
  }
  if (!courseForm.value.price) {
    ElMessage.warning('请输入课程价格')
    return
  }
  
  loading.value = true
  try {
    let res
    if (courseForm.value.id) {
      // 编辑课程
      res = await memberApi.updateCourseSetting(courseForm.value)
    } else {
      // 新增课程
      res = await memberApi.addCourse(courseForm.value)
    }
    
    if (res.resCode === '00') {
      ElMessage.success(courseForm.value.id ? '编辑成功' : '新增成功')
      showDialog.value = false
      loadCourseList()
    } else {
      ElMessage.warning(res.resMsg || '操作失败')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    loading.value = false
  }
}

// 删除课程
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除课程「${row.courseName}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    loading.value = true
    try {
      const res = await memberApi.deleteCourse({ id: row.id })
      if (res.resCode === '00') {
        ElMessage.success('删除成功')
        loadCourseList()
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

onMounted(() => {
  loadCourseList()
  loadCoachList()
})
</script>

<template>
  <div class="course-setting-page">
    <div class="page-header">
      <div class="header-left">
        <el-icon :size="22"><Reading /></el-icon>
        <div>
          <h2>课程设置</h2>
          <p>管理课程信息、时长、教练等</p>
        </div>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon> 新增课程
        </el-button>
      </div>
    </div>

    <!-- 课程列表 -->
    <el-table :data="courseList" v-loading="loading" stripe style="width: 100%">
      <el-table-column prop="id" label="课程ID" width="80" align="center" />
      <el-table-column prop="courseName" label="课程名称" width="150" />
      <el-table-column prop="courseType" label="课程类型" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.courseType === 1 ? 'success' : 'warning'" size="small">
            {{ row.courseType === 1 ? '团课' : '私教' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="coachName" label="教练" width="120" />
      <el-table-column prop="duration" label="课程时长" width="100" align="center">
        <template #default="{ row }">{{ row.duration }} 分钟</template>
      </el-table-column>
      <el-table-column prop="price" label="价格" width="100" align="center">
        <template #default="{ row }">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column prop="capacity" label="最大容量" width="100" align="center">
        <template #default="{ row }">{{ row.capacity || '--' }} 人</template>
      </el-table-column>
      <el-table-column prop="difficultyLevel" label="难度等级" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.difficultyLevel == 1 ? 'success' : row.difficultyLevel == 2 ? 'warning' : 'danger'" size="small">
            {{ row.difficultyLevel == 1 ? '初级' : row.difficultyLevel == 2 ? '中级' : '高级' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status == 1 ? 'success' : 'info'" size="small">
            {{ row.status == 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="课程描述" min-width="200">
        <template #default="{ row }">{{ row.description || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="showDialog" :title="dialogTitle" width="600px" :close-on-click-modal="false">
      <el-form :model="courseForm" label-width="100px">
        <el-form-item label="课程名称" required>
          <el-input v-model="courseForm.courseName" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程类型" required>
          <el-radio-group v-model="courseForm.courseType">
            <el-radio :label="1">团课</el-radio>
            <el-radio :label="2">私教</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="选择教练" required>
          <el-select 
            v-model="courseForm.coachId" 
            placeholder="请选择教练" 
            style="width: 100%"
            @change="handleCoachChange"
          >
            <el-option 
              v-for="coach in coachList" 
              :key="coach.id" 
              :label="`${coach.realName} (${coach.username})`" 
              :value="coach.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="课程时长" required>
          <el-input-number 
            v-model="courseForm.duration" 
            :min="15" 
            :max="180" 
            :step="15"
            placeholder="分钟"
            style="width: 100%"
          />
          <span style="margin-left: 10px; color: #909399;">分钟</span>
        </el-form-item>
        <el-form-item label="课程价格" required>
          <el-input-number 
            v-model="courseForm.price" 
            :min="0" 
            :precision="2"
            placeholder="元"
            style="width: 100%"
          />
          <span style="margin-left: 10px; color: #909399;">元</span>
        </el-form-item>
        <el-form-item label="最大容量" v-if="courseForm.courseType === 1">
          <el-input-number 
            v-model="courseForm.capacity" 
            :min="1" 
            :max="100"
            placeholder="人"
            style="width: 100%"
          />
          <span style="margin-left: 10px; color: #909399;">人（团课必填）</span>
        </el-form-item>
        <el-form-item label="难度等级" required>
          <el-radio-group v-model="courseForm.difficultyLevel">
            <el-radio :label="1">初级</el-radio>
            <el-radio :label="2">中级</el-radio>
            <el-radio :label="3">高级</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="课程状态">
          <el-radio-group v-model="courseForm.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="课程描述">
          <el-input 
            v-model="courseForm.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入课程描述"
          />
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
.course-setting-page {
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
</style>
