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
    redirect: '/login'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { requiresAuth: true }
  },
  // 系统管理（仅超级管理员）
  {
    path: '/system/user',
    name: 'SystemUser',
    component: () => import('@/views/system/User.vue'),
    meta: { requiresAuth: true, allowedUserTypes: [1] }
  },
  {
    path: '/system/role',
    name: 'SystemRole',
    component: () => import('@/views/system/Role.vue'),
    meta: { requiresAuth: true, allowedUserTypes: [1] }
  },
  {
    path: '/system/permission',
    name: 'SystemPermission',
    component: () => import('@/views/system/Permission.vue'),
    meta: { requiresAuth: true, allowedUserTypes: [1] }
  },
  // 办理会员卡（会员）
  {
    path: '/create-card',
    name: 'CreateCard',
    component: () => import('@/views/CreateCard.vue'),
    meta: { requiresAuth: true, allowedUserTypes: [4] }
  },
  // 我的会员卡（会员）
  {
    path: '/my-card',
    name: 'MyCard',
    component: () => import('@/views/MyCard.vue'),
    meta: { requiresAuth: true, allowedUserTypes: [4] }
  },
  // 购买课程（会员）
  {
    path: '/purchase-course',
    name: 'PurchaseCourse',
    component: () => import('@/views/PurchaseCourse.vue'),
    meta: { requiresAuth: true, allowedUserTypes: [4] }
  },
  // 我的课程（会员）
  {
    path: '/member/myCourse',
    name: 'MyCourse',
    component: () => import('@/views/MyCourse.vue'),
    meta: { requiresAuth: true, allowedUserTypes: [4] }
  },
  // 团课预约（会员）
  {
    path: '/member/course-booking',
    name: 'CourseBooking',
    component: () => import('@/views/CourseBooking.vue'),
    meta: { requiresAuth: true, allowedUserTypes: [4] }
  },
  // 私教预约（会员）
  {
    path: '/member/course-booking2',
    name: 'PrivateBooking',
    component: () => import('@/views/PrivateBooking.vue'),
    meta: { requiresAuth: true, allowedUserTypes: [4] }
  },
  // 我的申请（会员）
  {
    path: '/member/myApplication',
    name: 'MyApplication',
    component: () => import('@/views/MyApplication.vue'),
    meta: { requiresAuth: true, allowedUserTypes: [4] }
  },
  // 预约记录（会员）
  {
    path: '/member/bookedRecord',
    name: 'BookedRecord',
    component: () => import('@/views/BookedRecord.vue'),
    meta: { requiresAuth: true, allowedUserTypes: [4] }
  },
  // 个人中心（所有角色）
  {
    path: '/member/myCenter',
    name: 'MyCenter',
    component: () => import('@/views/MyCenter.vue'),
    meta: { requiresAuth: true, allowedUserTypes: [1, 2, 3, 4] }
  },
  // 我的订单（会员）
  {
    path: '/member/order',
    name: 'MyOrder',
    component: () => import('@/views/MyOrder.vue'),
    meta: { requiresAuth: true, allowedUserTypes: [4] }
  },
  // 会员管理（管理员、超级管理员）
  {
    path: '/member/list',
    name: 'MemberList',
    component: () => import('@/views/member/List.vue'),
    meta: { requiresAuth: true, allowedUserTypes: [1, 2] }
  },
  {
    path: '/member/card',
    name: 'CardManage',
    component: () => import('@/views/member/CardManage.vue'),
    meta: { requiresAuth: true, allowedUserTypes: [1, 2] }
  },
  {
    path: '/member/application',
    name: 'ApplicationApproval',
    component: () => import('@/views/ApplicationApproval.vue'),
    meta: { requiresAuth: true, allowedUserTypes: [1, 2] }
  },
  // 课程管理（管理员、超级管理员）
  {
    path: '/course/setting',
    name: 'CourseSetting',
    component: () => import('@/views/course/CourseSetting.vue'),
    meta: { requiresAuth: true, allowedUserTypes: [1, 2] }
  },
  {
    path: '/course/schedule',
    name: 'ScheduleManage',
    component: () => import('@/views/course/ScheduleManage.vue'),
    meta: { requiresAuth: true, allowedUserTypes: [1, 2] }
  },
  {
    path: '/course/booking',
    name: 'BookingManage',
    component: () => import('@/views/course/BookingManage.vue'),
    meta: { requiresAuth: true, allowedUserTypes: [1, 2] }
  },
  // 秒杀活动（会员端）
  {
    path: '/member/seckill',
    name: 'Seckill',
    component: () => import('@/views/Seckill.vue'),
    meta: { requiresAuth: true, allowedUserTypes: [4] }
  },
  // 秒杀活动管理（管理员端）
  {
    path: '/member/seckill-manage',
    name: 'SeckillManage',
    component: () => import('@/views/SeckillManage.vue'),
    meta: { requiresAuth: true, allowedUserTypes: [1, 2] }
  },
  // 教练端 - 排班管理
  {
    path: '/coach/schedule',
    name: 'CoachSchedule',
    component: () => import('@/views/coach/CoachSchedule.vue'),
    meta: { requiresAuth: true, allowedUserTypes: [3] }
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
  const userInfoStr = localStorage.getItem('userInfo')
  
  // 访问根路径时，根据登录状态决定跳转
  if (to.path === '/') {
    if (token) {
      next('/dashboard')
    } else {
      next('/login')
    }
    return
  }
  
  // 不需要登录的页面直接放行
  if (!to.meta.requiresAuth) {
    next()
    return
  }
  
  // 需要登录但没有 token，跳转到登录页
  if (!token) {
    next('/login')
    return
  }
  
  // 已登录，访问登录/注册页，跳转到首页
  if (to.path === '/login' || to.path === '/register') {
    next('/dashboard')
    return
  }
  
  // 权限校验
  if (to.meta.allowedUserTypes && userInfoStr) {
    try {
      const userInfo = JSON.parse(userInfoStr)
      const userType = userInfo.userType
      
      // 检查用户类型是否在允许的列表中
      if (!to.meta.allowedUserTypes.includes(userType)) {
        // 权限不足，清除登录信息并跳转到登录页
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        alert('权限不足，请使用有权限的账号登录')
        next('/login')
        return
      }
    } catch (e) {
      // userInfo 解析失败，清除并跳转登录
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      next('/login')
      return
    }
  }
  
  next()
})

export default router