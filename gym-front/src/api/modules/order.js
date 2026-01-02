import apiService from '../services/apiService'

/**
 * 订单管理 API
 */
export const orderApi = {
  // 获取订单列表
  getOrderList(params) {
    return apiService.get('/order/list', { params })
  },
  
  // 获取订单详情
  getOrderDetail(id) {
    return apiService.get(`/order/${id}`)
  },
  
  // 创建订单
  createOrder(data) {
    return apiService.post('/order', data)
  },
  
  // 取消订单
  cancelOrder(id) {
    return apiService.put(`/order/${id}/cancel`)
  },
  
  // 支付订单
  payOrder(id, data) {
    return apiService.post(`/order/${id}/pay`, data)
  }
}
