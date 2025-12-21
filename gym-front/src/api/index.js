/**
 * API 统一导出
 * 使用方式：
 * import { userApi, memberApi, courseApi, orderApi } from '@/api'
 */

export { userApi } from './modules/user'
export { memberApi } from './modules/member'
export { courseApi } from './modules/course'
export { orderApi } from './modules/order'

// 也可以导出服务实例，用于特殊情况
export { default as userService } from './services/userService'
export { default as productService } from './services/productService'
export { default as orderService } from './services/orderService'
