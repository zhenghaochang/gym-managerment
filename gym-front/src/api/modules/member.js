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
  }
}
