<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { memberApi } from '@/api/modules/member'

const loading = ref(false)
const privateSearchKeyword = ref('')
const privateCourses = ref([])

const filteredPrivateCourses = computed(() => {
  if (!privateSearchKeyword.value) return privateCourses.value
  return privateCourses.value.filter(item =>
    item.courseName.includes(privateSearchKeyword.value) ||
    item.orderNum.includes(privateSearchKeyword.value)
  )
})

const bookingDialogVisible = ref(false)
const currentPrivateCourse = ref(null)
const bookingForm = ref({ phone: '', preferredDate: '', preferredTimeSlot: '', remark: '' })

const timeSlotOptions = [
  { label: '09:00 - 10:00', value: '09:00' },
  { label: '10:00 - 11:00', value: '10:00' },
  { label: '11:00 - 12:00', value: '11:00' },
  { label: '14:00 - 15:00', value: '14:00' },
  { label: '15:00 - 16:00', value: '15:00' },
  { label: '16:00 - 17:00', value: '16:00' },
  { label: '18:00 - 19:00', value: '18:00' },
  { label: '19:00 - 20:00', value: '19:00' },
  { label: '20:00 - 21:00', value: '20:00' }
]

const disabledDate = (time) => time.getTime() < Date.now() - 86400000

const openPrivateBooking = (course) => {
  currentPrivateCourse.value = course
  bookingForm.value = { phone: '', preferredDate: '', preferredTimeSlot: '', remark: '' }
  bookingDialogVisible.value = true
}

const buildPreferredTime = () => {
  if (!bookingForm.value.preferredDate || !bookingForm.value.preferredTimeSlot) return null
  const d = bookingForm.value.preferredDate
  const pad = (n) => String(n).padStart(2, '0')
  const dateStr = `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
  return `${dateStr} ${bookingForm.value.preferredTimeSlot}:00`
}

const submitPrivateBooking = async () => {
  if (!bookingForm.value.phone) {
    ElMessage.warning('请填写联系电话')
    return
  }
  if (!/^1\d{10}$/.test(bookingForm.value.phone)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }
  const bookingTime = buildPreferredTime()
  if (!bookingTime) {
    ElMessage.warning('请选择期望预约时间')
    return
  }
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const res = await memberApi.bookPrivateCourse({
      userId: userInfo.id,
      memberCourseId: currentPrivateCourse.value.id,
      coachId: currentPrivateCourse.value.coachId,
      bookingTime: bookingTime,
      remark: bookingForm.value.remark || ''
    })
    if (res.resCode === '00') {
      bookingDialogVisible.value = false
      await ElMessageBox.alert(
        res.resMsg || '预约成功，稍后会有教练联系，请保持电话畅通',
        '预约成功',
        { confirmButtonText: '知道了', type: 'success', center: true }
      )
      // 重新加载列表
      const listRes = await memberApi.getPrivateBookingList()
      if (listRes.resCode === '00' && listRes.result) {
        privateCourses.value = listRes.result
      }
    } else {
      ElMessage.warning(res.resMsg || '预约失败')
    }
  } catch (e) {
    ElMessage.error('预约失败，请稍后重试')
  }
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await memberApi.getPrivateBookingList()
    if (res.resCode === '00' && res.result) {
      privateCourses.value = res.result
    }
  } catch (e) {
    console.error('获取私教课程列表失败', e)
  }
  loading.value = false
})
</script>

<template>
  <div class="booking-container">
    <div class="page-header">
      <h1 class="page-title"><el-icon><User /></el-icon> 私教预约</h1>
      <p class="page-subtitle">预约私教课程，教练将与您线下协商上课时间</p>
    </div>

    <el-card class="table-card" shadow="never" v-loading="loading">
      <el-form :inline="true" style="margin-bottom: 16px;">
        <el-form-item>
          <el-input
            v-model="privateSearchKeyword"
            placeholder="搜索课程名称或教练"
            clearable
            style="width: 240px"
            :prefix-icon="Search"
          />
        </el-form-item>
      </el-form>
      <el-table :data="filteredPrivateCourses" stripe style="width: 100%">
        <el-table-column prop="courseName" label="课程名称" min-width="160" />
        <el-table-column prop="realName" label="授课教练" min-width="120" />
        <el-table-column prop="duration" label="时长(分钟)" min-width="100" />
        <el-table-column prop="orderNum" label="订单号" min-width="200" />
        <el-table-column label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '待预约' : '已预约' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link :disabled="row.status !== 1" @click="openPrivateBooking(row)">预约</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 私教预约弹窗 -->
    <el-dialog v-model="bookingDialogVisible" title="私教预约" width="420px" :close-on-click-modal="false">
      <div v-if="currentPrivateCourse" class="booking-dialog-content">
        <div class="booking-course-info">
          <h3>{{ currentPrivateCourse.courseName }}</h3>
          <p>教练：{{ currentPrivateCourse.realName }} | 时长：{{ currentPrivateCourse.duration }}分钟 | 订单号：{{ currentPrivateCourse.orderNum }}</p>
        </div>
        <el-form label-position="top">
          <el-form-item label="联系电话" required>
            <el-input v-model="bookingForm.phone" placeholder="请输入您的手机号" maxlength="11" />
          </el-form-item>
          <el-form-item label="期望预约时间段">
            <div class="time-picker-row">
              <el-date-picker
                v-model="bookingForm.preferredDate"
                type="date"
                placeholder="选择日期"
                :disabled-date="disabledDate"
                style="width: 50%"
              />
              <el-select v-model="bookingForm.preferredTimeSlot" placeholder="选择时间段" clearable style="width: 48%">
                <el-option v-for="item in timeSlotOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </div>
            <div class="time-slot-tip">该时间段为期望时间，非最终上课时间，最终上课时间以教练线下协商为准</div>
          </el-form-item>
          <el-form-item label="备注（选填）">
            <el-input v-model="bookingForm.remark" type="textarea" placeholder="如有特殊需求可在此说明" :rows="3" maxlength="200" show-word-limit />
          </el-form-item>
        </el-form>
        <p class="booking-tip">提交后教练将通过电话与您确认上课时间</p>
      </div>
      <template #footer>
        <el-button @click="bookingDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPrivateBooking">提交预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.booking-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}
.page-header { margin-bottom: 20px; padding-bottom: 20px; border-bottom: 1px solid #e8e8e8; }
.page-title { display: flex; align-items: center; gap: 12px; font-size: 28px; font-weight: 900; color: #1a1a2e; margin: 0 0 8px 0; }
.page-subtitle { font-size: 14px; color: #666; margin: 0; }

.table-card { border-radius: 12px; border: none; }
.table-card :deep(.el-card__body) { padding: 20px; }

.booking-dialog-content { padding: 0 4px; }
.booking-course-info { margin-bottom: 20px; padding-bottom: 16px; border-bottom: 1px solid #ebeef5; }
.booking-course-info h3 { margin: 0 0 6px 0; font-size: 18px; color: #1a1a2e; }
.booking-course-info p { margin: 0; font-size: 14px; color: #909399; }
.booking-tip { font-size: 13px; color: #e6a23c; margin: 12px 0 0 0; text-align: center; }
.time-slot-tip { font-size: 12px; color: #e6a23c; margin-top: 6px; line-height: 1.5; }
.time-picker-row { display: flex; gap: 8px; width: 100%; }
</style>