<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { usePermissionStore } from '@/stores/permission'

const route = useRoute()
const permissionStore = usePermissionStore()

// 判断是否隐藏布局（登录注册页）
const hideLayout = computed(() => route.meta.hideLayout)

// 页面加载时恢复权限数据
onMounted(() => {
  if (permissionStore.menus.length === 0) {
    permissionStore.restoreFromStorage()
  }
})
</script>

<template>
  <div v-if="hideLayout" class="full-page">
    <!-- 登录注册页面，无菜单和导航栏 -->
    <router-view></router-view>
  </div>
  
  <div v-else class="app-container">
    <!-- 主系统页面，有菜单和导航栏 -->
    <MainMenu />
    <div class="main-container">
      <Navigation />
      <div class="content-wrapper">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 全屏页面（登录注册） */
.full-page {
  width: 100%;
  height: 100vh;
  overflow: auto;
}

/* 主系统布局 */
.app-container {
  display: flex;
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

.main-container {
  flex: 1;
  margin-left: 250px;
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

.content-wrapper {
  flex: 1;
  margin-top: 60px;
  padding: 20px;
  background: #f5f7fa;
  overflow-y: auto;
}
</style>
