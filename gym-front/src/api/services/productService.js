import axios from 'axios'
import { setupInterceptors } from './interceptors'

// Product 服务 - 商品/课程管理相关
const productService = axios.create({
  baseURL: 'http://localhost:8082', // product 服务地址
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 设置拦截器
setupInterceptors(productService, 'Product服务')

export default productService
