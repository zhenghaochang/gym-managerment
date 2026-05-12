<script setup>
import { ref, computed, watch, onMounted } from 'vue'
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

const getScheduleByDay = (dayIndex) => {
  return groupSchedule.value.filter(item => item.weekDay === dayIndex + 1)
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
const isPast = (dateStr) => dateStr < todayStr.value

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

onMounted(async () => {
  loading.value = true
  await loadCourseMap()
  await loadScheduleList()
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
      <h1 class="page-title"><el-icon><Calendar /></el-icon> 排班管理</h1>
      <p class="page-subtitle">查看每周团课排班情况</p>
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
                :class="{ 'past': isPast(dayInfo.date) }"
              >
                <div class="slot-time">{{ item.startTime }}-{{ item.endTime }}</div>
                <div class="slot-name">{{ item.courseName }}</div>
                <div class="slot-coach">{{ item.coachName }}</div>
                <div class="slot-location">{{ item.location }}</div>
                <div class="slot-capacity">
                  <span>{{ item.bookedCount }}/{{ item.capacity }}</span>
                  <el-progress
                    :percentage="item.capacity ? Math.round(item.bookedCount / item.capacity * 100) : 0"
                    :stroke-width="4"
                    :show-text="false"
                    :color="item.bookedCount / item.capacity > 0.8 ? '#e6a23c' : '#67c23a'"
                    style="width: 60px;"
                  />
                </div>
              </div>
              <div v-if="getScheduleByDay(index).length === 0" class="no-slot">暂无排课</div>
            </div>
          </div>
        </div>
      </transition>
    </div>
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
.day-slots {
  background: #fff; border: 1px solid #e4e7ed; border-top: none;
  border-radius: 0 0 10px 10px; padding: 8px; min-height: 200px;
  display: flex; flex-direction: column; gap: 8px;
}
.no-slot { text-align: center; color: #c0c4cc; font-size: 13px; padding: 30px 0; }

.slot-card {
  padding: 10px; border-radius: 8px; background: #fafbfc;
  border: 1px solid #ebeef5; transition: all 0.2s;
}
.slot-card:hover { border-color: #409eff; box-shadow: 0 2px 8px rgba(64,158,255,0.12); }

.slot-time { font-size: 13px; font-weight: 700; color: #409eff; margin-bottom: 4px; }
.slot-name { font-size: 14px; font-weight: 700; color: #303133; margin-bottom: 4px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.slot-coach { font-size: 12px; color: #606266; margin-bottom: 2px; }
.slot-location { font-size: 12px; color: #909399; margin-bottom: 6px; }
.slot-capacity { display: flex; align-items: center; gap: 6px; font-size: 12px; color: #606266; }

@media (max-width: 1200px) { .timetable { grid-template-columns: repeat(4, 1fr); } }
@media (max-width: 768px) { .timetable { grid-template-columns: repeat(2, 1fr); } }

.slide-left-enter-active, .slide-left-leave-active,
.slide-right-enter-active, .slide-right-leave-active { transition: all 0.35s ease; }
.slide-left-enter-from { opacity: 0; transform: translateX(40px); }
.slide-left-leave-to { opacity: 0; transform: translateX(-40px); }
.slide-right-enter-from { opacity: 0; transform: translateX(-40px); }
.slide-right-leave-to { opacity: 0; transform: translateX(40px); }
</style>
