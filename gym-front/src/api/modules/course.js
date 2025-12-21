import productService from '../services/productService'

/**
 * 课程管理 API（使用 product 服务）
 */
export const courseApi = {
  // 获取课程列表
  getCourseList(params) {
    return productService.get('/api/course/list', { params })
  },
  
  // 获取课程详情
  getCourseDetail(id) {
    return productService.get(`/api/course/${id}`)
  },
  
  // 添加课程
  addCourse(data) {
    return productService.post('/api/course', data)
  },
  
  // 更新课程
  updateCourse(id, data) {
    return productService.put(`/api/course/${id}`, data)
  },
  
  // 删除课程
  deleteCourse(id) {
    return productService.delete(`/api/course/${id}`)
  },
  
  // 课程预约
  bookCourse(data) {
    return productService.post('/api/course/book', data)
  }
}
