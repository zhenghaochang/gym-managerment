<template>
  <div class="dashboard">
    <h1>欢迎来到健身房管理系统</h1>
    <p>这是工作台页面</p>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { usePermissionStore } from '@/stores/permission'

const permissionStore = usePermissionStore()

onMounted(() => {
  console.log('当前用户信息:', permissionStore.userInfo)
  console.log('当前菜单:', permissionStore.menus)
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

h1 {
  color: #2c3e50;
  margin-bottom: 20px;
}

p {
  color: #606266;
  font-size: 16px;
}
</style>
