import axios from 'axios'
import { ElMessage } from 'element-plus'
import { setupInterceptors } from './interceptors'

// User 服务 - 用户管理相关
const userService = axios.create({
  baseURL: 'http://localhost:8081', // user 服务地址
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 设置拦截器
setupInterceptors(userService, 'User服务')

export default userService
