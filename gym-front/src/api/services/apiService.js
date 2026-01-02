import axios from 'axios'
import { setupInterceptors } from './interceptors'

// 统一 API 服务 - 单体架构
const apiService = axios.create({
  baseURL: 'http://localhost:8081/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 设置拦截器
setupInterceptors(apiService, 'API服务')

export default apiService
