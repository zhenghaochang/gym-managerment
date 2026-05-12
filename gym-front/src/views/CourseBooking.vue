<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { memberApi } from '@/api/modules/member'

const loading = ref(false)

const weekDays = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
const currentWeekOffset = ref(0)

const currentWeekDates = computed(() => {
  const now = new Date()
  const dayOfWeek = now.getDay() || 7
  const monday = new Date(now)
  monday.setDate(now.getDate() - dayOfWeek + 1 + currentWeekOffset.value * 7)
  return weekDays.map((name, i) => {
    const d = new Date(monday)
    d.setDate(monday.getDate() + i)
    const month = d.getMonth() + 1
    const date = d.getDate()
    return {
      name,
      date: `${d.getFullYear()}-${String(month).padStart(2, '0')}-${String(date).padStart(2, '0')}`,
      label: `${month}/${date}`
    }
  })
})

const weekRangeText = computed(() => {
  const dates = currentWeekDates.value
  return `${dates[0].date} ~ ${dates[6].date}`
})

// 排班数据（从接口获取）
const groupSchedule = ref([])

// 课程名称映射 courseId -> courseName
const courseNameMap = ref({})
// 教练名称映射 coachId -> coachName
const coachNameMap = { 4: '刘强', 5: '陈静', 6: '赵刚' }

// 用户已购买的课程列表（完整数据）
const purchasedCourses = ref([])
// 用户已购买的课程名称集合（用于判断是否可预约）
const purchasedCourseNames = ref(new Set())

const getScheduleByDay = (dayIndex) => {
  return groupSchedule.value.filter(item => item.weekDay === dayIndex + 1)
}

// 获取某课程对应的已购买记录
const getMatchedCourses = (item) => {
  return purchasedCourses.value.filter(c => c.courseName === item.courseName)
}

const isPurchased = (item) => purchasedCourseNames.value.has(item.courseName)
const isFull = (item) => item.bookedCount >= item.capacity
const isBooked = (item) => !!item.userBooked
const isCancelling = (item) => item.recordStatus === 2
const isDisabled = (item, dateStr) => isPast(dateStr, item.startTime) || isCancelling(item) || (!isBooked(item) && (!isPurchased(item) || isFull(item)))

const getButtonText = (item, dateStr) => {
  if (isPast(dateStr, item.startTime)) return '已过期'
  if (isCancelling(item)) return '取消中'
  if (isBooked(item)) return '取消预约'
  if (!isPurchased(item)) return '未购买'
  if (isFull(item)) return '已满'
  return '预约'
}

// 选择课程弹窗
const selectDialogVisible = ref(false)
const currentScheduleItem = ref(null)
const currentDateInfo = ref(null)
const matchedCourseList = ref([])
const selectedCourseId = ref(null)

// 取消预约弹窗
const cancelDialogVisible = ref(false)
const cancelScheduleItem = ref(null)
const cancelDateInfo = ref(null)
const cancelReason = ref('')

const handleGroupBook = (item, dateInfo) => {
  // 已预约 -> 取消预约
  if (isBooked(item)) {
    // 判断当前时间是否已到上课时间，到了则不允许取消
    const now = new Date()
    const courseStart = new Date(`${dateInfo.date} ${item.startTime}:00`)
    if (now >= courseStart) {
      ElMessage.warning('课程已开始或已结束，无法取消预约')
      return
    }
    cancelScheduleItem.value = item
    cancelDateInfo.value = dateInfo
    cancelReason.value = ''
    cancelDialogVisible.value = true
    return
  }
  if (isPast(dateInfo.date, item.startTime)) return
  if (!isPurchased(item)) {
    ElMessage.warning('您尚未购买该课程，请先前往购买课程页面购买')
    return
  }
  if (isFull(item)) {
    ElMessage.warning('该时段已满员')
    return
  }
  const matched = getMatchedCourses(item)
  currentScheduleItem.value = item
  currentDateInfo.value = dateInfo
  matchedCourseList.value = matched
  selectedCourseId.value = null
  if (matched.length === 1) {
    selectedCourseId.value = matched[0].id
  }
  selectDialogVisible.value = true
}

const submitCancelBooking = async () => {
  if (!cancelReason.value.trim()) {
    ElMessage.warning('请输入取消理由')
    return
  }
  try {
    const item = cancelScheduleItem.value
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const res = await memberApi.cancelGroupBooking({
      bookingRecordId: item.bookingRecordId,
      appReason: cancelReason.value.trim(),
      realName: userInfo.realName || ''
    })
    if (res.resCode === '00') {
      ElMessage.success(res.resMsg || '取消预约申请已提交')
      cancelDialogVisible.value = false
      await Promise.all([loadScheduleList(), loadBookingList()])
    } else {
      ElMessage.warning(res.resMsg || '取消失败')
    }
  } catch (e) {
    ElMessage.error('取消预约失败，请稍后重试')
  }
}

const confirmBooking = async () => {
  if (!selectedCourseId.value) {
    ElMessage.warning('请选择要使用的课程')
    return
  }
  const item = currentScheduleItem.value
  const dateInfo = currentDateInfo.value
  const selected = matchedCourseList.value.find(c => c.id === selectedCourseId.value)
  try {
    await ElMessageBox.confirm(
      `确认预约「${item.courseName}」？\n订单号：${selected.orderNum}\n日期：${dateInfo.date}（${dateInfo.name}）\n时间：${item.startTime} - ${item.endTime}\n教练：${item.coachName}\n地点：${item.location}`,
      '预约确认',
      { confirmButtonText: '确认预约', cancelButtonText: '取消', type: 'info' }
    )
    const bookingTime = `${dateInfo.date} ${item.startTime}:00`
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const res = await memberApi.bookGroupCourse({
      memberCourseId: selectedCourseId.value,
      userId: userInfo.id,
      scheduleId: item.id,
      bookingDate: dateInfo.date,
      bookingTime: bookingTime,
      status: 1
    })
    if (res.resCode === '00') {
      ElMessage.success('预约成功')
      selectDialogVisible.value = false
      // 重新加载排班数据和已购课程列表
      await Promise.all([loadScheduleList(), loadBookingList()])
    } else {
      ElMessage.warning(res.resMsg || '预约失败')
    }
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error('预约失败，请稍后重试')
    }
  }
}

const slideDirection = ref('slide-right')
const isCurrentWeek = computed(() => currentWeekOffset.value === 0)
const nextWeek = () => { slideDirection.value = 'slide-left'; currentWeekOffset.value++ }
const prevWeek = () => { if (currentWeekOffset.value > 0) { slideDirection.value = 'slide-right'; currentWeekOffset.value-- } }
const resetWeek = () => { slideDirection.value = 'slide-right'; currentWeekOffset.value = 0 }

const todayStr = computed(() => {
  const d = new Date()
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
})
const isToday = (dateStr) => dateStr === todayStr.value
const isPast = (dateStr, startTime = null) => {
  // 如果只传日期，比较日期
  if (!startTime) {
    return dateStr < todayStr.value
  }
  // 如果传了开始时间，比较日期+时间
  const now = new Date()
  const courseDateTime = new Date(`${dateStr} ${startTime}:00`)
  return now >= courseDateTime
}

// 加载课程列表，构建 courseId -> courseName 映射
const loadCourseMap = async () => {
  try {
    const res = await memberApi.getCourseList()
    if (res.resCode === '00' && res.result) {
      const map = {}
      res.result.forEach(item => { map[item.id] = item.courseName })
      courseNameMap.value = map
    }
  } catch (e) {
    console.error('获取课程列表失败', e)
  }
}

// 加载排班表（传入当前周的日期范围）
const loadScheduleList = async () => {
  try {
    const dates = currentWeekDates.value
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const res = await memberApi.getScheduleList({
      startDate: dates[0].date,
      endDate: dates[6].date,
      userId: userInfo.id
    })
    if (res.resCode === '00' && res.result) {
      groupSchedule.value = res.result.map(item => ({
        ...item,
        courseName: courseNameMap.value[item.courseId] || `课程${item.courseId}`,
        coachName: coachNameMap[item.coachId] || `教练${item.coachId}`,
        capacity: item.maxCapacity,
        bookedCount: item.bookedCount || 0
      }))
    }
  } catch (e) {
    console.error('获取排班表失败', e)
  }
}

// 加载用户已购买的团课列表
const loadBookingList = async () => {
  try {
    const res = await memberApi.getBookingList()
    if (res.resCode === '00' && res.result) {
      purchasedCourses.value = res.result
      const names = new Set(res.result.map(item => item.courseName))
      purchasedCourseNames.value = names
    }
  } catch (e) {
    console.error('获取预约列表失败', e)
  }
}

onMounted(async () => {
  loading.value = true
  await loadCourseMap()
  await Promise.all([loadScheduleList(), loadBookingList()])
  loading.value = false
})

// 切换周时重新加载排班数据
watch(currentWeekOffset, async () => {
  loading.value = true
  await loadScheduleList()
  loading.value = false
})
</script>

<template>
  <div class="booking-container">
    <div class="page-header">
      <h1 class="page-title"><el-icon><Calendar /></el-icon> 团课预约</h1>
      <p class="page-subtitle">查看每周团课排班，预约你感兴趣的课程</p>
    </div>

    <div v-loading="loading">
      <el-card class="week-nav-card" shadow="never">
        <div class="week-nav">
          <el-button v-if="!isCurrentWeek" text @click="prevWeek"><el-icon><ArrowLeft /></el-icon> 上一周</el-button>
          <span v-else style="width: 88px;"></span>
          <div class="week-range">
            <span class="week-range-text">{{ weekRangeText }}</span>
            <el-button v-if="!isCurrentWeek" size="small" text type="primary" @click="resetWeek">回到本周</el-button>
          </div>
          <el-button text @click="nextWeek">下一周 <el-icon><ArrowRight /></el-icon></el-button>
        </div>
      </el-card>

      <transition :name="slideDirection" mode="out-in">
        <div class="timetable" :key="currentWeekOffset">
          <div v-for="(dayInfo, index) in currentWeekDates" :key="index" class="day-column">
            <div class="day-header" :class="{ 'day-today': isToday(dayInfo.date), 'day-past': isPast(dayInfo.date) }">
              <span class="day-name">{{ dayInfo.name }}</span>
              <span class="day-date">{{ dayInfo.label }}</span>
              <span v-if="isToday(dayInfo.date)" class="today-badge">今天</span>
              <span v-else-if="isPast(dayInfo.date)" class="past-badge">已过期</span>
            </div>
            <div class="day-slots" :class="{ 'day-slots-past': isPast(dayInfo.date) }">
              <div
                v-for="item in getScheduleByDay(index)"
                :key="item.id"
                class="slot-card"
                :class="{
                  'not-purchased': !isPurchased(item) && !isBooked(item),
                  full: isPurchased(item) && isFull(item) && !isBooked(item),
                  booked: isBooked(item) && !isCancelling(item),
                  cancelling: isCancelling(item),
                  'past': isPast(dayInfo.date)
                }"
              >
                <div class="slot-time">{{ item.startTime }}-{{ item.endTime }}</div>
                <div class="slot-name">{{ item.courseName }}</div>
                <div class="slot-coach">{{ item.coachName }}</div>
                <div class="slot-location">{{ item.location }}</div>
                <div class="slot-capacity">
                  <span :style="{ color: isDisabled(item, dayInfo.date) ? '#909399' : '#606266' }">{{ item.bookedCount }}/{{ item.capacity }}</span>
                  <el-progress
                    :percentage="item.capacity ? Math.round(item.bookedCount / item.capacity * 100) : 0"
                    :stroke-width="4"
                    :show-text="false"
                    :color="isDisabled(item, dayInfo.date) ? '#c0c4cc' : item.bookedCount / item.capacity > 0.8 ? '#e6a23c' : '#67c23a'"
                    style="width: 60px;"
                  />
                </div>
                <el-button
                  size="small"
                  :type="isCancelling(item) ? 'warning' : isBooked(item) ? 'success' : isDisabled(item, dayInfo.date) ? 'info' : 'primary'"
                  :disabled="isDisabled(item, dayInfo.date)"
                  round
                  class="slot-btn"
                  @click="handleGroupBook(item, dayInfo)"
                >{{ getButtonText(item, dayInfo.date) }}</el-button>
              </div>
              <div v-if="getScheduleByDay(index).length === 0" class="no-slot">暂无排课</div>
            </div>
          </div>
        </div>
      </transition>
    </div>

    <!-- 选择已购课程弹窗 -->
    <el-dialog v-model="selectDialogVisible" title="选择课程" width="500px" :close-on-click-modal="false">
      <div v-if="currentScheduleItem" class="select-dialog-content">
        <div class="select-info">
          <span>{{ currentScheduleItem.courseName }}</span>
          <span class="select-info-sub">{{ currentDateInfo?.date }}（{{ currentDateInfo?.name }}）{{ currentScheduleItem.startTime }}-{{ currentScheduleItem.endTime }}</span>
        </div>
        <p class="select-tip">请选择要使用的已购课程：</p>
        <el-radio-group v-model="selectedCourseId" class="select-radio-group">
          <el-radio
            v-for="course in matchedCourseList"
            :key="course.id"
            :value="course.id"
            class="select-radio-item"
          >
            <div class="course-detail">
              <div class="course-detail-row">
                <span class="course-detail-label">课程名称</span>
                <span class="course-detail-value">{{ course.courseName }}</span>
              </div>
              <div class="course-detail-row">
                <span class="course-detail-label">订单号</span>
                <span class="course-detail-value order-num">{{ course.orderNum }}</span>
              </div>
              <div class="course-detail-row">
                <span class="course-detail-label">状态</span>
                <el-tag size="small" :type="course.status === 1 ? 'success' : 'info'">{{ course.status === 1 ? '待预约' : '已使用' }}</el-tag>
              </div>
            </div>
          </el-radio>
        </el-radio-group>
      </div>
      <template #footer>
        <el-button @click="selectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmBooking">确认预约</el-button>
      </template>
    </el-dialog>

    <!-- 取消预约弹窗 -->
    <el-dialog v-model="cancelDialogVisible" title="取消预约" width="460px" :close-on-click-modal="false">
      <div v-if="cancelScheduleItem" class="cancel-dialog-content">
        <div class="cancel-info">
          <div class="cancel-info-row">
            <span class="cancel-info-label">课程</span>
            <span>{{ cancelScheduleItem.courseName }}</span>
          </div>
          <div class="cancel-info-row">
            <span class="cancel-info-label">日期</span>
            <span>{{ cancelDateInfo?.date }}（{{ cancelDateInfo?.name }}）</span>
          </div>
          <div class="cancel-info-row">
            <span class="cancel-info-label">时间</span>
            <span>{{ cancelScheduleItem.startTime }} - {{ cancelScheduleItem.endTime }}</span>
          </div>
          <div class="cancel-info-row">
            <span class="cancel-info-label">教练</span>
            <span>{{ cancelScheduleItem.coachName }}</span>
          </div>
        </div>
        <el-form label-position="top" style="margin-top: 16px;">
          <el-form-item label="取消理由" required>
            <el-input
              v-model="cancelReason"
              type="textarea"
              placeholder="请输入取消预约的理由"
              :rows="3"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="cancelDialogVisible = false">返回</el-button>
        <el-button type="danger" :disabled="!cancelReason.trim()" @click="submitCancelBooking">提交申请</el-button>
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

.week-nav-card { margin-bottom: 20px; border-radius: 12px; border: none; }
.week-nav-card :deep(.el-card__body) { padding: 14px 24px; }
.week-nav { display: flex; justify-content: space-between; align-items: center; }
.week-range { display: flex; align-items: center; gap: 12px; }
.week-range-text { font-size: 15px; font-weight: 600; color: #303133; }

.timetable { display: grid; grid-template-columns: repeat(7, 1fr); gap: 10px; }
.day-column { min-width: 0; }
.day-header {
  display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 2px;
  padding: 12px 8px; background: #fff; border-radius: 10px 10px 0 0;
  border: 1px solid #e4e7ed; border-bottom: 2px solid #409eff;
  height: 70px; box-sizing: border-box;
}
.day-name { font-size: 14px; font-weight: 700; color: #303133; }
.day-date { font-size: 12px; color: #909399; }
.day-today { background: #409eff; border-color: #409eff; border-bottom-color: #337ecc; }
.day-today .day-name { color: #fff; }
.day-today .day-date { color: rgba(255,255,255,0.8); }
.today-badge { font-size: 11px; color: #fff; background: rgba(255,255,255,0.25); padding: 1px 8px; border-radius: 8px; }
.day-past { background: #f5f5f5; border-color: #dcdfe6; border-bottom-color: #c0c4cc; }
.day-past .day-name { color: #b0b3b8; }
.day-past .day-date { color: #c0c4cc; }
.past-badge { font-size: 11px; color: #909399; background: #ebeef5; padding: 1px 8px; border-radius: 8px; }
.day-slots-past { opacity: 0.5; }
.slot-card.past { opacity: 0.4; background: #f5f5f5; border-color: #dcdfe6; }
.slot-card.past:hover { box-shadow: none; border-color: #dcdfe6; }
.day-slots {
  background: #fff; border: 1px solid #e4e7ed; border-top: none;
  border-radius: 0 0 10px 10px; padding: 8px; min-height: 200px;
  display: flex; flex-direction: column; gap: 8px;
}
.no-slot { text-align: center; color: #c0c4cc; font-size: 13px; padding: 30px 0; }

.slot-card {
  padding: 10px; border-radius: 8px; background: #fafbfc;
  border: 1px solid #ebeef5; transition: all 0.2s; cursor: default;
}
.slot-card:hover { border-color: #409eff; box-shadow: 0 2px 8px rgba(64,158,255,0.12); }
.slot-card.not-purchased { opacity: 0.4; background: #f5f5f5; border-color: #dcdfe6; }
.slot-card.not-purchased:hover { box-shadow: none; border-color: #dcdfe6; }
.slot-card.not-purchased .slot-time { color: #b0b3b8; }
.slot-card.full { opacity: 0.4; background: #f5f5f5; border-color: #dcdfe6; }
.slot-card.full:hover { box-shadow: none; border-color: #dcdfe6; }
.slot-card.full .slot-time { color: #b0b3b8; }
.slot-card.booked { background: #f0f9eb; border-color: #b3e19d; }
.slot-card.booked .slot-time { color: #67c23a; }
.slot-card.booked:hover { border-color: #67c23a; box-shadow: 0 2px 8px rgba(103,194,58,0.15); }
.slot-card.cancelling { background: #fdf6ec; border-color: #f5dab1; }
.slot-card.cancelling .slot-time { color: #e6a23c; }
.slot-card.cancelling:hover { border-color: #e6a23c; box-shadow: 0 2px 8px rgba(230,162,60,0.15); }

.slot-time { font-size: 13px; font-weight: 700; color: #409eff; margin-bottom: 4px; }
.slot-name { font-size: 14px; font-weight: 700; color: #303133; margin-bottom: 4px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.slot-coach { font-size: 12px; color: #606266; margin-bottom: 2px; }
.slot-location { font-size: 12px; color: #909399; margin-bottom: 6px; }
.slot-capacity { display: flex; align-items: center; gap: 6px; font-size: 12px; margin-bottom: 6px; color: #909399; }
.slot-btn { width: 100%; margin-top: 6px; }

/* 选择课程弹窗 */
.select-dialog-content { padding: 0 4px; }
.select-info { font-size: 16px; font-weight: 700; color: #303133; margin-bottom: 4px; display: flex; flex-direction: column; gap: 4px; }
.select-info-sub { font-size: 13px; font-weight: 400; color: #909399; }
.select-tip { font-size: 14px; color: #606266; margin: 16px 0 12px; }
.select-radio-group { display: flex; flex-direction: column; gap: 10px; width: 100%; }
.select-radio-item {
  display: flex; align-items: flex-start; padding: 12px 16px;
  border: 1px solid #ebeef5; border-radius: 8px; background: #fafbfc;
  height: auto; margin-right: 0;
}
.select-radio-item:hover { border-color: #409eff; background: #f0f7ff; }
.course-detail { display: flex; flex-direction: column; gap: 6px; }
.course-detail-row { display: flex; align-items: center; gap: 8px; }
.course-detail-label { font-size: 12px; color: #909399; min-width: 56px; }
.course-detail-value { font-size: 13px; color: #303133; }
.course-detail-value.order-num { font-family: monospace; font-size: 12px; color: #606266; }

/* 取消预约弹窗 */
.cancel-dialog-content { padding: 0 4px; }
.cancel-info { background: #fafbfc; border-radius: 8px; padding: 12px 16px; border: 1px solid #ebeef5; }
.cancel-info-row { display: flex; align-items: center; gap: 8px; padding: 4px 0; font-size: 14px; color: #303133; }
.cancel-info-label { font-size: 13px; color: #909399; min-width: 40px; }

@media (max-width: 1200px) { .timetable { grid-template-columns: repeat(4, 1fr); } }
@media (max-width: 768px) { .timetable { grid-template-columns: repeat(2, 1fr); } }

.slide-left-enter-active, .slide-left-leave-active,
.slide-right-enter-active, .slide-right-leave-active { transition: all 0.35s ease; }
.slide-left-enter-from { opacity: 0; transform: translateX(40px); }
.slide-left-leave-to { opacity: 0; transform: translateX(-40px); }
.slide-right-enter-from { opacity: 0; transform: translateX(-40px); }
.slide-right-leave-to { opacity: 0; transform: translateX(40px); }
</style>