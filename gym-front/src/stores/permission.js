import { defineStore } from 'pinia'
import { ref } from 'vue'
import { transformPermissionsToMenusByCode } from '@/utils/menuConfig'

export const usePermissionStore = defineStore('permission', () => {
  const menus = ref([])
  const permissions = ref(new Set())
  const userInfo = ref(null)
  
  // 设置菜单
  function setMenus(menuList) {
    menus.value = menuList
  }
  
  // 设置权限
  function setPermissions(permList) {
    permissions.value = new Set(permList)
    // 同时保存到 localStorage
    localStorage.setItem('permissions', JSON.stringify(permList))
  }
  
  // 设置用户信息
  function setUserInfo(info) {
    userInfo.value = info
  }
  
  // 检查权限
  function hasPermission(perm) {
    return permissions.value.has(perm)
  }
  
  // 清除权限信息
  function clearPermission() {
    menus.value = []
    permissions.value = new Set()
    userInfo.value = null
    localStorage.removeItem('permissions')
  }
  
  // 从 localStorage 恢复数据（页面刷新时调用）
  function restoreFromStorage() {
    const token = localStorage.getItem('token')
    if (!token) return false
    
    // 恢复用户信息
    const storedUserInfo = localStorage.getItem('userInfo')
    if (storedUserInfo) {
      userInfo.value = JSON.parse(storedUserInfo)
    }
    
    // 恢复权限和菜单
    const storedPermissions = localStorage.getItem('permissions')
    if (storedPermissions) {
      const permList = JSON.parse(storedPermissions)
      permissions.value = new Set(permList)
      menus.value = transformPermissionsToMenusByCode(permList)
      return true
    }
    
    return false
  }
  
  return {
    menus,
    permissions,
    userInfo,
    setMenus,
    setPermissions,
    setUserInfo,
    hasPermission,
    clearPermission,
    restoreFromStorage
  }
})
