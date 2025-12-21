import axios from 'axios'
import { setupInterceptors } from './interceptors'

// Order 服务 - 订单管理相关
const orderService = axios.create({
  baseURL: 'http://localhost:8083', // order 服务地址
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 设置拦截器
setupInterceptors(orderService, 'Order服务')

export default orderService
