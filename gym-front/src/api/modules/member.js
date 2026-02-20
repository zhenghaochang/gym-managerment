import apiService from '../services/apiService'

/**
 * 会员管理 API
 */
export const memberApi = {
  // 获取会员列表
  getMemberList(params) {
    return apiService.get('/member/list', { params })
  },
  
  // 获取会员详情
  getMemberDetail(id) {
    return apiService.get(`/member/${id}`)
  },
  
  // 添加会员
  addMember(data) {
    return apiService.post('/member', data)
  },
  
  // 更新会员信息
  updateMember(id, data) {
    return apiService.put(`/member/${id}`, data)
  },
  
  // 删除会员
  deleteMember(id) {
    return apiService.delete(`/member/${id}`)
  },

  // 获取会员卡种类列表
  getCardList() {
    return apiService.post('/card/list')
  },

  // 购买会员卡
  createOrder(data) {
    return apiService.post('/card/createOrder', data)
  },

  // 获取我的会员卡列表
  getMyCardList(data) {
    return apiService.post('/myCard/list', data)
  },

  // 申请停卡/转卡
  appApproval(data) {
    return apiService.post('/myCard/appApproval', data)
  },

  // 获取申请单列表
  getApplicationList(data) {
    return apiService.post('/userManager/application/list', data)
  },

  // 审批申请单
  approveApplication(data) {
    return apiService.post('/userManager/application/approval', data)
  },

  // 获取课程列表
  getCourseList() {
    return apiService.post('/course/list')
  },

  // 购买课程
  buyCourse(data) {
    return apiService.post('/course/buyCourse', data)
  },

  // 获取我的课程列表
  getMyCourseList() {
    return apiService.post('/myCourse/list')
  },

  // 退课
  dropClass(data) {
    return apiService.post('/myCourse/dropClass', data)
  },

  // 获取退课审批列表
  getCourseApplicationList() {
    return apiService.post('/courseManager/list')
  },

  // 退课审批（通过/拒绝）
  updateCourseApplicationStatus(data) {
    return apiService.post('/courseManager/updateStatus', data)
  }
}
