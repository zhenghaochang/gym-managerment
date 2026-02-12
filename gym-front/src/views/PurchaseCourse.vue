<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { memberApi } from '@/api/modules/member'

const loading = ref(false)
const courses = ref([])

// 获取课程列表
const fetchCourseList = async () => {
  loading.value = true
  try {
    const res = await memberApi.getCourseList()
    
    if (res.resCode === '00') {
      courses.value = (res.result || []).map(course => ({
        id: course.id,
        courseName: course.courseName,
        courseType: course.courseType === 1 ? '团课' : '私教课',
        courseTypeCode: course.courseType,
        description: course.description,
        duration: course.duration,
        capacity: course.capacity,
        difficultyLevel: course.difficultyLevel,
        coach: course.coachName,
        coachId: course.coachId,
        status: course.status,
        image: course.coverImage || 'https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=400'
      }))
    } else {
      ElMessage.error(res.resMsg || '获取课程列表失败')
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchCourseList()
})

// 筛选条件
const filterType = ref('')
const searchKeyword = ref('')

// 课程类型选项
const courseTypes = [
  { label: '全部', value: '' },
  { label: '私教课', value: '私教课' },
  { label: '团课', value: '团课' }
]

// 按类型分组的课程
const privateCourses = computed(() => 
  filteredCourses.value.filter(c => c.courseType === '私教课')
)

const groupCourses = computed(() => 
  filteredCourses.value.filter(c => c.courseType === '团课')
)

// 获取难度等级文本
const getDifficultyText = (level) => {
  if (level === 1) return '初级'
  if (level === 2) return '中级'
  if (level === 3) return '高级'
  return ''
}

// 获取难度等级类型
const getDifficultyType = (level) => {
  if (level === 1) return 'success'
  if (level === 2) return 'warning'
  if (level === 3) return 'danger'
  return 'info'
}

// 筛选后的课程
const filteredCourses = computed(() => {
  let result = courses.value
  
  // 按类型筛选
  if (filterType.value) {
    result = result.filter(c => c.courseType === filterType.value)
  }
  
  // 按关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(c => 
      c.courseName.toLowerCase().includes(keyword) ||
      c.description.toLowerCase().includes(keyword) ||
      c.coach.toLowerCase().includes(keyword) ||
      c.tags.some(tag => tag.toLowerCase().includes(keyword))
    )
  }
  
  return result
})

// 购买课程
const handlePurchase = async (course) => {
  // 检查是否已下架
  if (course.status === '0') {
    ElMessage.warning('该课程已下架，无法购买')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确认购买 ${course.courseName} 吗？<br/>
      <div style="margin-top: 12px; padding: 12px; background: #f5f7fa; border-radius: 4px;">
        <div style="color: #606266; font-size: 13px; line-height: 1.8;">
          <div>课程类型：${course.courseType}</div>
          <div>课程时长：${course.duration}分钟</div>
          <div>授课教练：${course.coach}</div>
          ${course.difficultyLevel ? `<div>难度等级：${getDifficultyText(course.difficultyLevel)}</div>` : ''}
          <div style="margin-top: 8px; padding-top: 8px; border-top: 1px solid #dcdfe6;">
            <span style="font-size: 14px; font-weight: 600; color: #303133;">课程容量：</span>
            <span style="font-size: 14px; font-weight: 600; color: #409eff;">${course.capacity}人</span>
          </div>
        </div>
      </div>`,
      '确认购买',
      {
        confirmButtonText: '确认购买',
        cancelButtonText: '取消',
        type: 'warning',
        dangerouslyUseHTMLString: true
      }
    )
    
    // TODO: 调用后端购买接口
    ElMessage.success('购买成功！')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('购买失败:', error)
      ElMessage.error('购买失败')
    }
  }
}

// 查看详情
const handleViewDetail = (course) => {
  ElMessageBox.alert(
    `<div style="padding: 4px 0;">
      <div style="margin-bottom: 16px;">
        <img src="${course.image}" style="width: 100%; height: 200px; object-fit: cover; border-radius: 8px;" />
      </div>
      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px 24px; margin-bottom: 16px; padding-bottom: 16px; border-bottom: 2px solid #ebeef5;">
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 6px;">课程名称</div>
          <div style="color: #303133; font-size: 15px; font-weight: 600;">${course.courseName}</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 6px;">课程类型</div>
          <div style="color: #606266; font-size: 14px;">${course.courseType}</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 6px;">授课教练</div>
          <div style="color: #606266; font-size: 14px;">${course.coach}</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 6px;">课程时长</div>
          <div style="color: #606266; font-size: 14px;">${course.duration}分钟</div>
        </div>
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 6px;">课程容量</div>
          <div style="color: #409eff; font-size: 14px; font-weight: 600;">${course.capacity}人</div>
        </div>
        ${course.difficultyLevel ? `<div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 6px;">难度等级</div>
          <div style="color: #606266; font-size: 14px;">${getDifficultyText(course.difficultyLevel)}</div>
        </div>` : ''}
        <div>
          <div style="color: #909399; font-size: 12px; margin-bottom: 6px;">课程状态</div>
          <div style="color: ${course.status === '1' ? '#67c23a' : '#909399'}; font-size: 14px; font-weight: 600;">${course.status === '1' ? '上架中' : '已下架'}</div>
        </div>
      </div>
      <div>
        <div style="color: #909399; font-size: 12px; margin-bottom: 8px;">课程介绍</div>
        <div style="color: #303133; font-size: 14px; line-height: 1.8; background: #f5f7fa; padding: 12px; border-radius: 6px; border-left: 3px solid #409eff;">${course.description}</div>
      </div>
    </div>`,
    '课程详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '关闭'
    }
  )
}
</script>

<template>
  <div class="purchase-course-container">
    <div class="page-header">
      <h1 class="page-title"><el-icon><Reading /></el-icon> 购买课程</h1>
      <p class="page-subtitle">选择适合你的健身课程</p>
    </div>

    <!-- 筛选栏 -->
    <el-card class="filter-card" shadow="never">
      <div class="filter-row">
        <div class="filter-left">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索课程名称、教练或标签"
            clearable
            style="width: 300px"
            :prefix-icon="Search"
          />
          <el-select
            v-model="filterType"
            placeholder="课程类型"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="item in courseTypes"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </div>
        <div class="filter-right">
          <span class="course-count">共 {{ filteredCourses.length }} 门课程</span>
        </div>
      </div>
    </el-card>

    <!-- 课程列表 - 分类展示 -->
    <div v-loading="loading">
      <!-- 私教课区块 -->
      <div v-if="privateCourses.length > 0" class="course-section">
        <div class="section-header">
          <el-icon class="section-icon"><User /></el-icon>
          <h2 class="section-title">私教课程</h2>
          <span class="section-desc">一对一专业指导</span>
        </div>
        <div class="course-grid">
          <div
            v-for="course in privateCourses"
            :key="course.id"
            class="course-card"
            :class="{ 'offline': course.status === '0' }"
          >
            <div class="course-image" :style="{ backgroundImage: `url(${course.image})` }">
              <div class="course-type-badge private">{{ course.courseType }}</div>
              <div v-if="course.status === '0'" class="offline-badge">已下架</div>
            </div>
            <div class="course-content">
              <h3 class="course-name">{{ course.courseName }}</h3>
              <p class="course-desc">{{ course.description }}</p>
              <div v-if="course.difficultyLevel" class="course-difficulty">
                <el-tag :type="getDifficultyType(course.difficultyLevel)" size="small" effect="plain">
                  难度：{{ getDifficultyText(course.difficultyLevel) }}
                </el-tag>
              </div>
              <div class="course-info">
                <div class="info-item">
                  <el-icon><User /></el-icon>
                  <span>{{ course.coach }}</span>
                </div>
                <div class="info-item">
                  <el-icon><Clock /></el-icon>
                  <span>{{ course.duration }}分钟</span>
                </div>
              </div>
              <div class="course-footer">
                <div class="course-actions">
                  <el-button size="small" @click="handleViewDetail(course)" :disabled="course.status === '0'">详情</el-button>
                  <el-button size="small" type="primary" @click="handlePurchase(course)" :disabled="course.status === '0'">
                    <el-icon><ShoppingCart /></el-icon>
                    购买
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 团课区块 -->
      <div v-if="groupCourses.length > 0" class="course-section">
        <div class="section-header">
          <el-icon class="section-icon"><UserFilled /></el-icon>
          <h2 class="section-title">团课课程</h2>
          <span class="section-desc">团体训练，氛围更佳</span>
        </div>
        <div class="course-grid">
          <div
            v-for="course in groupCourses"
            :key="course.id"
            class="course-card"
            :class="{ 'offline': course.status === '0' }"
          >
            <div class="course-image" :style="{ backgroundImage: `url(${course.image})` }">
              <div class="course-type-badge group">{{ course.courseType }}</div>
              <div v-if="course.status === '0'" class="offline-badge">已下架</div>
            </div>
            <div class="course-content">
              <h3 class="course-name">{{ course.courseName }}</h3>
              <p class="course-desc">{{ course.description }}</p>
              <div v-if="course.difficultyLevel" class="course-difficulty">
                <el-tag :type="getDifficultyType(course.difficultyLevel)" size="small" effect="plain">
                  难度：{{ getDifficultyText(course.difficultyLevel) }}
                </el-tag>
              </div>
              <div class="course-info">
                <div class="info-item">
                  <el-icon><User /></el-icon>
                  <span>{{ course.coach }}</span>
                </div>
                <div class="info-item">
                  <el-icon><Clock /></el-icon>
                  <span>{{ course.duration }}分钟</span>
                </div>
              </div>
              <div class="course-footer">
                <div class="course-capacity">
                  <el-icon><UserFilled /></el-icon>
                  <span>{{ course.capacity }}人</span>
                </div>
                <div class="course-actions">
                  <el-button size="small" @click="handleViewDetail(course)" :disabled="course.status === '0'">详情</el-button>
                  <el-button size="small" type="primary" @click="handlePurchase(course)" :disabled="course.status === '0'">
                    <el-icon><ShoppingCart /></el-icon>
                    购买
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <el-empty
      v-if="!loading && filteredCourses.length === 0"
      description="暂无符合条件的课程"
      :image-size="120"
    />
  </div>
</template>

<style scoped>
.purchase-course-container {
  padding: 30px 40px;
  margin: -20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
  box-sizing: border-box;
}

.page-header {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e8e8e8;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 28px;
  font-weight: 900;
  color: #1a1a2e;
  margin: 0 0 8px 0;
}

.page-subtitle {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.filter-card {
  margin-bottom: 24px;
  border-radius: 12px;
  border: none;
}

.filter-card :deep(.el-card__body) {
  padding: 20px;
}

.filter-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-left {
  display: flex;
  gap: 12px;
}

.filter-right {
  display: flex;
  align-items: center;
}

.course-count {
  font-size: 14px;
  color: #909399;
}

.course-section {
  margin-bottom: 32px;
  padding: 24px;
  background: #fff;
  border-radius: 16px;
  border: 1px solid #e0e0e0;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #1a1a2e;
}

.section-icon {
  font-size: 28px;
  color: #ff0844;
}

.section-title {
  font-size: 22px;
  font-weight: 900;
  color: #1a1a2e;
  margin: 0;
}

.section-desc {
  font-size: 13px;
  color: #999;
  margin-left: auto;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.course-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s;
  border: 1px solid #e8e8e8;
}

.course-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
  border-color: #409eff;
}

.course-card.offline {
  opacity: 0.6;
  filter: grayscale(50%);
}

.course-card.offline:hover {
  transform: none;
  box-shadow: none;
  border-color: #e8e8e8;
}

.course-image {
  width: 100%;
  height: 200px;
  background-size: cover;
  background-position: center;
  position: relative;
}

.course-type-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 6px 12px;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  font-size: 12px;
  border-radius: 20px;
  backdrop-filter: blur(4px);
  font-weight: 600;
}

.course-type-badge.private {
  background: rgba(230, 162, 60, 0.9);
}

.course-type-badge.group {
  background: rgba(103, 194, 58, 0.9);
}

.offline-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 6px 12px;
  background: rgba(144, 147, 153, 0.9);
  color: #fff;
  font-size: 12px;
  border-radius: 20px;
  backdrop-filter: blur(4px);
  font-weight: 600;
}

.course-content {
  padding: 20px;
}

.course-name {
  font-size: 20px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 12px 0;
}

.course-desc {
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
  margin: 0 0 12px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-difficulty {
  margin-bottom: 12px;
}

.course-info {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #909399;
}

.info-item .el-icon {
  font-size: 16px;
}

.course-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.course-footer.with-capacity {
  justify-content: space-between;
}

.course-capacity {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #409eff;
  font-weight: 600;
}

.course-capacity .el-icon {
  font-size: 16px;
}

.course-actions {
  display: flex;
  gap: 8px;
}

@media (max-width: 768px) {
  .purchase-course-container {
    padding: 20px;
  }
  
  .filter-row {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .filter-left {
    flex-direction: column;
  }
  
  .filter-left .el-input,
  .filter-left .el-select {
    width: 100% !important;
  }
  
  .course-grid {
    grid-template-columns: 1fr;
  }
}
</style>
