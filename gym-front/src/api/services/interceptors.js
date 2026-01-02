import { ElMessage } from 'element-plus'

/**
 * 统一的拦截器配置
 * @param {Object} service - axios 实例
 * @param {String} serviceName - 服务名称（用于日志）
 */
export function setupInterceptors(service, serviceName = '服务') {
  // 请求拦截器
  service.interceptors.request.use(
    config => {
      // 从 localStorage 获取 token
      const token = localStorage.getItem('token')
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
      
      console.log(`[${serviceName}] 请求:`, config.method.toUpperCase(), config.url)
      return config
    },
    error => {
      console.error(`[${serviceName}] 请求错误:`, error)
      return Promise.reject(error)
    }
  )

  // 响应拦截器
  service.interceptors.response.use(
    response => {
      const res = response.data
      
      console.log(`[${serviceName}] 响应:`, response.config.url, res)
      
      // 根据后端返回格式：{ resCode: "00", resMsg: "", result: T }
      // resCode: "00"-成功 "99"-失败
      if (res.resCode === '00') {
        return res
      } else {
        // 业务错误
        ElMessage.error(res.resMsg || '请求失败')
        return Promise.reject(new Error(res.resMsg || '请求失败'))
      }
    },
    error => {
      console.error(`[${serviceName}] 响应错误:`, error)
      
      // HTTP 错误处理
      if (error.response) {
        const status = error.response.status
        const message = error.response.data?.message || error.message
        
        switch (status) {
          case 401:
            ElMessage.error('未授权，请重新登录')
            // 清除 token 并跳转到登录页
            localStorage.removeItem('token')
            localStorage.removeItem('userInfo')
            setTimeout(() => {
              window.location.href = '/login'
            }, 1000)
            break
          case 403:
            ElMessage.error('拒绝访问，权限不足')
            break
          case 404:
            ElMessage.error('请求的资源不存在')
            break
          case 500:
            ElMessage.error('服务器内部错误')
            break
          case 502:
            ElMessage.error('网关错误')
            break
          case 503:
            ElMessage.error('服务不可用')
            break
          default:
            ElMessage.error(message || `请求失败 (${status})`)
        }
      } else if (error.request) {
        // 请求已发出但没有收到响应
        ElMessage.error('网络错误，请检查网络连接')
      } else {
        // 其他错误
        ElMessage.error(error.message || '请求失败')
      }
      
      return Promise.reject(error)
    }
  )
}
