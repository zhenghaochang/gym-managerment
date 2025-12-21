import userService from '../services/userService'

/**
 * 用户相关 API
 */
export const userApi = {
  // 登录
  login(data) {
    return userService.post('/api/user/login', data)
  },
  
  // 注册
  register(data) {
    return userService.post('/api/user/register', data)
  },
  
  // 获取用户信息
  getUserInfo() {
    return userService.get('/api/user/info')
  },
  
  // 获取用户菜单权限
  getUserMenus() {
    return userService.get('/api/user/menus')
  },
  
  // 修改密码
  changePassword(data) {
    return userService.put('/api/user/password', data)
  },
  
  // 退出登录
  logout() {
    return userService.post('/api/user/logout')
  }
}
