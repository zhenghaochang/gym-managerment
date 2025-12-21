import { defineStore } from 'pinia'
import { ref } from 'vue'

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
  }
  
  return {
    menus,
    permissions,
    userInfo,
    setMenus,
    setPermissions,
    setUserInfo,
    hasPermission,
    clearPermission
  }
})
