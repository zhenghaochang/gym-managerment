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
  }
}
