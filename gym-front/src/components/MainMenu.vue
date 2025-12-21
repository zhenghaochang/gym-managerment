 <script setup>
import { computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { usePermissionStore } from '@/stores/permission';

const router = useRouter();
const route = useRoute();
const permissionStore = usePermissionStore();

// 从 store 获取动态菜单
const menuItems = computed(() => permissionStore.menus);

// 当前激活的菜单
const activeMenu = computed(() => route.path);

const handleMenuClick = (path) => {
  router.push(path);
};
</script>

<template>
  <div class="sidebar">
    <div class="sidebar-header">
      <div class="logo-wrapper">
        <svg class="logo-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M20.57 14.86L22 13.43 20.57 12 17 15.57 8.43 7 12 3.43 10.57 2 9.14 3.43 7.71 2 5.57 4.14 4.14 2.71 2.71 4.14l1.43 1.43L2 7.71l1.43 1.43L2 10.57 3.43 12 7 8.43 15.57 17 12 20.57 13.43 22l1.43-1.43L16.29 22l2.14-2.14 1.43 1.43 1.43-1.43-1.43-1.43L22 16.29z" fill="currentColor"/>
        </svg>
        <div class="logo-text">
          <h2>健身房管理</h2>
          <span>Gym Management</span>
        </div>
      </div>
    </div>

    <div class="sidebar-menu">
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu-list"
        background-color="transparent"
        text-color="#a6adc8"
        active-text-color="#cdd6f4"
      >
        <template v-for="item in menuItems" :key="item.path || item.title">
          <!-- 没有子菜单的项 -->
          <el-menu-item 
            v-if="!item.children" 
            :index="item.path"
            @click="handleMenuClick(item.path)"
          >
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </el-menu-item>

          <!-- 有子菜单的项 -->
          <el-sub-menu v-else :index="item.title">
            <template #title>
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.title }}</span>
            </template>
            <el-menu-item
              v-for="child in item.children"
              :key="child.path"
              :index="child.path"
              @click="handleMenuClick(child.path)"
            >
              {{ child.title }}
            </el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </div>
  </div>
</template>

<style scoped>
.sidebar {
  width: 250px;
  height: 100vh;
  background: linear-gradient(180deg, #1e1e2e 0%, #181825 100%);
  display: flex;
  flex-direction: column;
  position: fixed;
  left: 0;
  top: 0;
  box-shadow: 4px 0 24px rgba(0, 0, 0, 0.25);
  z-index: 1000;
  border-right: 1px solid rgba(255, 255, 255, 0.05);
}

.sidebar-header {
  padding: 24px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.25);
  position: relative;
}

.sidebar-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
}

.logo-wrapper {
  display: flex;
  align-items: center;
  gap: 14px;
  animation: fadeIn 0.6s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.logo-icon {
  width: 42px;
  height: 42px;
  color: white;
  flex-shrink: 0;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
  animation: rotate 20s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.logo-text h2 {
  margin: 0;
  color: white;
  font-size: 19px;
  font-weight: 700;
  line-height: 1.2;
  letter-spacing: 0.5px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.logo-text span {
  color: rgba(255, 255, 255, 0.9);
  font-size: 11px;
  display: block;
  margin-top: 3px;
  font-weight: 500;
  letter-spacing: 0.5px;
  text-transform: uppercase;
}

.sidebar-menu {
  flex: 1;
  overflow-y: auto;
  padding: 16px 0;
}

.sidebar-menu::-webkit-scrollbar {
  width: 5px;
}

.sidebar-menu::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.02);
}

.sidebar-menu::-webkit-scrollbar-thumb {
  background: rgba(102, 126, 234, 0.2);
  border-radius: 3px;
  transition: background 0.3s;
}

.sidebar-menu::-webkit-scrollbar-thumb:hover {
  background: rgba(102, 126, 234, 0.4);
}

.sidebar-menu-list {
  border: none;
  background: transparent !important;
}

/* 菜单项样式 */
.sidebar-menu-list :deep(.el-menu-item),
.sidebar-menu-list :deep(.el-sub-menu__title) {
  height: 52px;
  line-height: 52px;
  color: #a6adc8;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin: 4px 12px;
  border-radius: 10px;
  padding-left: 20px !important;
  font-size: 14px;
  font-weight: 500;
  position: relative;
  overflow: hidden;
  background: transparent;
}

/* 图标样式 */
.sidebar-menu-list :deep(.el-menu-item .el-icon),
.sidebar-menu-list :deep(.el-sub-menu__title .el-icon) {
  font-size: 18px;
  margin-right: 12px;
  transition: all 0.3s;
  color: #7f849c;
}

/* hover 效果 */
.sidebar-menu-list :deep(.el-menu-item:hover),
.sidebar-menu-list :deep(.el-sub-menu__title:hover) {
  background: rgba(102, 126, 234, 0.08) !important;
  color: #cdd6f4 !important;
  transform: translateX(4px);
}

.sidebar-menu-list :deep(.el-menu-item:hover .el-icon),
.sidebar-menu-list :deep(.el-sub-menu__title:hover .el-icon) {
  color: #89b4fa;
  transform: scale(1.1);
}

/* 激活状态 */
.sidebar-menu-list :deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.1) 100%) !important;
  color: #cdd6f4 !important;
  font-weight: 600;
  box-shadow: 0 2px 12px rgba(102, 126, 234, 0.15);
}

.sidebar-menu-list :deep(.el-menu-item.is-active::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 24px;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  border-radius: 0 4px 4px 0;
  box-shadow: 0 0 8px rgba(102, 126, 234, 0.6);
}

.sidebar-menu-list :deep(.el-menu-item.is-active .el-icon) {
  color: #89b4fa;
}

/* 子菜单样式 */
.sidebar-menu-list :deep(.el-sub-menu) {
  margin: 4px 0;
}

.sidebar-menu-list :deep(.el-sub-menu .el-menu) {
  background: rgba(0, 0, 0, 0.15) !important;
  margin: 4px 12px;
  border-radius: 10px;
  padding: 4px 0;
  border: 1px solid rgba(255, 255, 255, 0.03);
}

.sidebar-menu-list :deep(.el-sub-menu .el-menu-item) {
  height: 44px;
  line-height: 44px;
  padding-left: 52px !important;
  margin: 2px 8px;
  font-size: 13px;
  color: #9399b2;
  position: relative;
}

.sidebar-menu-list :deep(.el-sub-menu .el-menu-item::before) {
  content: '';
  position: absolute;
  left: 32px;
  top: 50%;
  transform: translateY(-50%);
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: rgba(137, 180, 250, 0.3);
  transition: all 0.3s;
}

.sidebar-menu-list :deep(.el-sub-menu .el-menu-item:hover) {
  background: rgba(102, 126, 234, 0.08) !important;
  color: #cdd6f4;
  transform: translateX(4px);
}

.sidebar-menu-list :deep(.el-sub-menu .el-menu-item:hover::before) {
  background: #89b4fa;
  transform: translateY(-50%) scale(1.4);
  box-shadow: 0 0 8px rgba(137, 180, 250, 0.5);
}

.sidebar-menu-list :deep(.el-sub-menu .el-menu-item.is-active) {
  background: rgba(102, 126, 234, 0.12) !important;
  color: #cdd6f4 !important;
  font-weight: 600;
}

.sidebar-menu-list :deep(.el-sub-menu .el-menu-item.is-active::before) {
  background: #89b4fa;
  width: 6px;
  height: 6px;
  box-shadow: 0 0 10px rgba(137, 180, 250, 0.6);
}

/* 子菜单展开图标 */
.sidebar-menu-list :deep(.el-sub-menu__icon-arrow) {
  font-size: 12px;
  transition: transform 0.3s;
  color: #7f849c;
}

.sidebar-menu-list :deep(.el-sub-menu.is-opened .el-sub-menu__icon-arrow) {
  transform: rotate(180deg);
  color: #89b4fa;
}
</style>
