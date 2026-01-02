/**
 * API 统一导出
 * 使用方式：
 * import { userApi, memberApi, courseApi, orderApi } from '@/api'
 */

export { userApi } from './modules/user'
export { memberApi } from './modules/member'
export { courseApi } from './modules/course'
export { orderApi } from './modules/order'

// 导出统一服务实例
export { default as apiService } from './services/apiService'
