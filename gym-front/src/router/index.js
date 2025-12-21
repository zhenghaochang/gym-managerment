import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  // 登录注册页面（无布局）
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login.vue'),
    meta: { hideLayout: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register.vue'),
    meta: { hideLayout: true }
  },
  // 主系统页面（有布局）
  {
    path: '/',
    redirect: '/dashboard',
    meta: { requiresAuth: true }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { requiresAuth: true }
  },
  // 系统管理
  {
    path: '/system/user',
    name: 'SystemUser',
    component: () => import('@/views/system/User.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/system/role',
    name: 'SystemRole',
    component: () => import('@/views/system/Role.vue'),
    meta: { requiresAuth: true }
  },
  // 会员管理
  {
    path: '/member/list',
    name: 'MemberList',
    component: () => import('@/views/member/List.vue'),
    meta: { requiresAuth: true }
  },
  // 更多路由...
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.meta.requiresAuth && !token) {
    // 需要登录但没有 token，跳转到登录页
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && token) {
    // 已登录，访问登录/注册页，跳转到首页
    next('/dashboard')
  } else {
    next()
  }
})

export default router