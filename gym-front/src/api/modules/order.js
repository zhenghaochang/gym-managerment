import orderService from '../services/orderService'

/**
 * 订单管理 API（使用 order 服务）
 */
export const orderApi = {
  // 获取订单列表
  getOrderList(params) {
    return orderService.get('/api/order/list', { params })
  },
  
  // 获取订单详情
  getOrderDetail(id) {
    return orderService.get(`/api/order/${id}`)
  },
  
  // 创建订单
  createOrder(data) {
    return orderService.post('/api/order', data)
  },
  
  // 取消订单
  cancelOrder(id) {
    return orderService.put(`/api/order/${id}/cancel`)
  },
  
  // 支付订单
  payOrder(id, data) {
    return orderService.post(`/api/order/${id}/pay`, data)
  }
}
