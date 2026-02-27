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
  },

  // 获取团课预约列表（当前用户已购买的待预约团课）
  getBookingList() {
    return apiService.post('/booking/list')
  },

  // 获取团课周排班表（传入日期范围，后端统计该周的已预约人数）
  getScheduleList(data) {
    return apiService.post('/booking/scheduleList', data)
  },

  // 团课预约
  bookGroupCourse(data) {
    return apiService.post('/booking/booking', data)
  },

  // 获取私教预约列表（当前用户已购买的私教课）
  getPrivateBookingList() {
    return apiService.post('/booking/privateList')
  },

  // 私教预约
  bookPrivateCourse(data) {
    return apiService.post('/booking/bookingPrivate', data)
  },

  // 取消团课预约
  cancelGroupBooking(data) {
    return apiService.post('/booking/courseCancel', data)
  },

  // 获取所有预约记录（管理员）
  getAllBookingRecords() {
    return apiService.post('/courseManager/BookingList')
  },

  // 获取我的申请列表
  getMyApplicationList() {
    return apiService.post('/myApplication/list')
  },

  // 获取预约记录列表
  getBookedRecordList() {
    return apiService.post('/bookedRecord/list')
  },

  // 删除预约记录
  deleteBookedRecord(id) {
    return apiService.post('/bookedRecord/delete', id)
  },

  // 获取个人中心信息
  getMyCenterInfo() {
    return apiService.post('/myCenter/list')
  },

  // 修改个人信息
  updateMyCenterInfo(data) {
    return apiService.post('/myCenter/update', data)
  },

  // 获取我的订单列表
  getMyOrderList() {
    return apiService.post('/myOrder/list')
  },

  // 获取用户列表（管理员）
  getUserList() {
    return apiService.post('/UserList/list')
  },

  // 新增管理员（超级管理员）
  addManager(data) {
    return apiService.post('/UserList/addManager', data)
  },

  // 获取管理员列表（用户管理页面）
  getManagerList() {
    return apiService.post('/UserList/managerList')
  },

  // 删除用户
  deleteUser(data) {
    return apiService.post('/UserList/delete', data)
  },

  // 修改用户类型（权限分配）
  updateUserType(data) {
    return apiService.post('/UserList/updateUserType', data)
  },

  // 课程设置（新增/编辑课程）
  updateCourseSetting(data) {
    return apiService.post('/courseManager/courseSetting', data)
  },

  // 新增课程
  addCourse(data) {
    return apiService.post('/courseManager/courseAdd', data)
  },

  // 删除课程
  deleteCourse(data) {
    return apiService.post('/courseManager/deleteCourse', data)
  },

  // 新增排课
  insertSchedule(data) {
    return apiService.post('/courseManager/insertSchedule', data)
  },

  // 修改排课
  updateSchedule(data) {
    return apiService.post('/courseManager/scheduleUpdate', data)
  },

  // 删除排课
  deleteSchedule(data) {
    return apiService.post('/courseManager/deleteSchedule', data)
  }
}
