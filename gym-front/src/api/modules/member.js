import userService from '../services/userService'

/**
 * 会员管理 API
 */
export const memberApi = {
  // 获取会员列表
  getMemberList(params) {
    return userService.get('/api/member/list', { params })
  },
  
  // 获取会员详情
  getMemberDetail(id) {
    return userService.get(`/api/member/${id}`)
  },
  
  // 添加会员
  addMember(data) {
    return userService.post('/api/member', data)
  },
  
  // 更新会员信息
  updateMember(id, data) {
    return userService.put(`/api/member/${id}`, data)
  },
  
  // 删除会员
  deleteMember(id) {
    return userService.delete(`/api/member/${id}`)
  }
}
