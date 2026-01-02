import apiService from '../services/apiService'

/**
 * 课程管理 API
 */
export const courseApi = {
  // 获取课程列表
  getCourseList(params) {
    return apiService.get('/course/list', { params })
  },
  
  // 获取课程详情
  getCourseDetail(id) {
    return apiService.get(`/course/${id}`)
  },
  
  // 添加课程
  addCourse(data) {
    return apiService.post('/course', data)
  },
  
  // 更新课程
  updateCourse(id, data) {
    return apiService.put(`/course/${id}`, data)
  },
  
  // 删除课程
  deleteCourse(id) {
    return apiService.delete(`/course/${id}`)
  },
  
  // 课程预约
  bookCourse(data) {
    return apiService.post('/course/book', data)
  }
}
