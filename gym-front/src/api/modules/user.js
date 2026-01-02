import apiService from '../services/apiService'

/**
 * 用户相关 API
 */
export const userApi = {
  // 登录
  login(data) {
    return apiService.post('/user/login', data)
  },
  
  // 注册
  register(data) {
    return apiService.post('/user/register', data)
  },
  
  // 获取邮箱验证码
  getEmailCode(email) {
    return apiService.post('/user/getEmailCode', { email })
  },
  
  // 获取用户信息
  getUserInfo() {
    return apiService.get('/user/info')
  },
  
  // 获取用户菜单权限
  getUserMenus() {
    return apiService.get('/user/menus')
  },
  
  // 修改密码
  changePassword(data) {
    return apiService.put('/user/password', data)
  },
  
  // 退出登录
  logout() {
    return apiService.post('/user/logout')
  }
}
