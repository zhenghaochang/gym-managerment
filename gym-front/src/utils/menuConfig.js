// 菜单图标映射（根据权限代码匹配图标）
export const menuIconMap = {
  'system:manage': 'Setting',
  'user:manage': 'User',
  'role:manage': 'UserFilled',
  'permission:assign': 'Key',
  'log:view': 'Document',
  
  'member:manage': 'User',
  'member:list': 'List',
  'card:manage': 'CreditCard',
  
  'course:manage': 'Reading',
  'course:setting': 'Setting',
  'schedule:manage': 'Calendar',
  'booking:manage': 'Tickets',
  
  'coach:my-course': 'Reading',
  'coach:student': 'User',
  'coach:schedule': 'Calendar',
  
  'equipment:manage': 'Box',
  'equipment:list': 'List',
  'repair:manage': 'Tools',
  
  'finance:manage': 'Wallet',
  'order:manage': 'ShoppingCart',
  'income:stat': 'TrendCharts',
  
  'member:home': 'HomeFilled',
  'member:booking': 'Tickets',
  'member:timetable': 'Calendar',
  'member:measure': 'DataLine',
  'member:order': 'ShoppingCart'
}

// 将后端返回的权限数据转换为菜单结构
export function transformPermissionsToMenus(permissions) {
  // 1. 过滤出菜单类型的权限
  const menuPermissions = permissions.filter(p => p.resource_type === 'menu')
  
  // 2. 构建树形结构
  const menuTree = buildTree(menuPermissions)
  
  return menuTree
}

// 构建树形结构
function buildTree(permissions) {
  const map = {}
  const roots = []
  
  // 先创建所有节点的映射
  permissions.forEach(perm => {
    map[perm.id] = {
      id: perm.id,
      title: perm.permission_name,
      path: perm.url,
      icon: menuIconMap[perm.permission_code] || 'Menu',
      permissionCode: perm.permission_code,
      children: []
    }
  })
  
  // 建立父子关系
  permissions.forEach(perm => {
    const node = map[perm.id]
    if (perm.parent_id === 0) {
      // 根节点
      roots.push(node)
    } else {
      // 子节点
      const parent = map[perm.parent_id]
      if (parent) {
        parent.children.push(node)
      }
    }
  })
  
  // 清理空的 children 数组
  function cleanEmptyChildren(nodes) {
    nodes.forEach(node => {
      if (node.children && node.children.length > 0) {
        cleanEmptyChildren(node.children)
      } else {
        delete node.children
      }
    })
  }
  
  cleanEmptyChildren(roots)
  
  return roots
}

// 模拟后端返回的权限数据（开发阶段使用）
export const mockPermissions = {
  // 超级管理员权限
  superAdmin: [
    { id: 1, permission_name: '系统管理', permission_code: 'system:manage', resource_type: 'menu', url: '/system', parent_id: 0, sort_order: 1 },
    { id: 2, permission_name: '用户管理', permission_code: 'user:manage', resource_type: 'menu', url: '/system/user', parent_id: 1, sort_order: 10 },
    { id: 3, permission_name: '角色管理', permission_code: 'role:manage', resource_type: 'menu', url: '/system/role', parent_id: 1, sort_order: 20 },
    { id: 4, permission_name: '权限分配', permission_code: 'permission:assign', resource_type: 'menu', url: '/system/permission', parent_id: 1, sort_order: 30 },
    { id: 5, permission_name: '操作日志', permission_code: 'log:view', resource_type: 'menu', url: '/system/log', parent_id: 1, sort_order: 40 },
    
    { id: 6, permission_name: '会员管理', permission_code: 'member:manage', resource_type: 'menu', url: '/member', parent_id: 0, sort_order: 2 },
    { id: 7, permission_name: '会员信息', permission_code: 'member:list', resource_type: 'menu', url: '/member/list', parent_id: 6, sort_order: 10 },
    { id: 8, permission_name: '办卡续费', permission_code: 'card:manage', resource_type: 'menu', url: '/member/card', parent_id: 6, sort_order: 20 },
    
    { id: 9, permission_name: '课程管理', permission_code: 'course:manage', resource_type: 'menu', url: '/course', parent_id: 0, sort_order: 3 },
    { id: 10, permission_name: '课程设置', permission_code: 'course:setting', resource_type: 'menu', url: '/course/setting', parent_id: 9, sort_order: 10 },
    { id: 11, permission_name: '排课管理', permission_code: 'schedule:manage', resource_type: 'menu', url: '/course/schedule', parent_id: 9, sort_order: 20 },
    { id: 12, permission_name: '预约管理', permission_code: 'booking:manage', resource_type: 'menu', url: '/course/booking', parent_id: 9, sort_order: 30 },
    
    { id: 16, permission_name: '器械管理', permission_code: 'equipment:manage', resource_type: 'menu', url: '/equipment', parent_id: 0, sort_order: 5 },
    { id: 17, permission_name: '器械列表', permission_code: 'equipment:list', resource_type: 'menu', url: '/equipment/list', parent_id: 16, sort_order: 10 },
    { id: 18, permission_name: '报修管理', permission_code: 'repair:manage', resource_type: 'menu', url: '/equipment/repair', parent_id: 16, sort_order: 20 },
    
    { id: 19, permission_name: '财务管理', permission_code: 'finance:manage', resource_type: 'menu', url: '/finance', parent_id: 0, sort_order: 6 },
    { id: 20, permission_name: '订单管理', permission_code: 'order:manage', resource_type: 'menu', url: '/finance/order', parent_id: 19, sort_order: 10 },
    { id: 21, permission_name: '收入统计', permission_code: 'income:stat', resource_type: 'menu', url: '/finance/stat', parent_id: 19, sort_order: 20 }
  ],
  
  // 管理员权限（无系统管理）
  admin: [
    { id: 6, permission_name: '会员管理', permission_code: 'member:manage', resource_type: 'menu', url: '/member', parent_id: 0, sort_order: 2 },
    { id: 7, permission_name: '会员信息', permission_code: 'member:list', resource_type: 'menu', url: '/member/list', parent_id: 6, sort_order: 10 },
    { id: 8, permission_name: '办卡续费', permission_code: 'card:manage', resource_type: 'menu', url: '/member/card', parent_id: 6, sort_order: 20 },
    
    { id: 9, permission_name: '课程管理', permission_code: 'course:manage', resource_type: 'menu', url: '/course', parent_id: 0, sort_order: 3 },
    { id: 10, permission_name: '课程设置', permission_code: 'course:setting', resource_type: 'menu', url: '/course/setting', parent_id: 9, sort_order: 10 },
    { id: 11, permission_name: '排课管理', permission_code: 'schedule:manage', resource_type: 'menu', url: '/course/schedule', parent_id: 9, sort_order: 20 },
    { id: 12, permission_name: '预约管理', permission_code: 'booking:manage', resource_type: 'menu', url: '/course/booking', parent_id: 9, sort_order: 30 },
    
    { id: 16, permission_name: '器械管理', permission_code: 'equipment:manage', resource_type: 'menu', url: '/equipment', parent_id: 0, sort_order: 5 },
    { id: 17, permission_name: '器械列表', permission_code: 'equipment:list', resource_type: 'menu', url: '/equipment/list', parent_id: 16, sort_order: 10 },
    { id: 18, permission_name: '报修管理', permission_code: 'repair:manage', resource_type: 'menu', url: '/equipment/repair', parent_id: 16, sort_order: 20 },
    
    { id: 19, permission_name: '财务管理', permission_code: 'finance:manage', resource_type: 'menu', url: '/finance', parent_id: 0, sort_order: 6 },
    { id: 20, permission_name: '订单管理', permission_code: 'order:manage', resource_type: 'menu', url: '/finance/order', parent_id: 19, sort_order: 10 },
    { id: 21, permission_name: '收入统计', permission_code: 'income:stat', resource_type: 'menu', url: '/finance/stat', parent_id: 19, sort_order: 20 }
  ],
  
  // 教练权限
  coach: [
    { id: 13, permission_name: '我的课程', permission_code: 'coach:my-course', resource_type: 'menu', url: '/coach/course', parent_id: 0, sort_order: 4 },
    { id: 14, permission_name: '学员管理', permission_code: 'coach:student', resource_type: 'menu', url: '/coach/student', parent_id: 13, sort_order: 10 },
    { id: 15, permission_name: '排班管理', permission_code: 'coach:schedule', resource_type: 'menu', url: '/coach/schedule', parent_id: 13, sort_order: 20 }
  ],
  
  // 会员权限
  member: [
    { id: 22, permission_name: '我的主页', permission_code: 'member:home', resource_type: 'menu', url: '/member/home', parent_id: 0, sort_order: 7 },
    { id: 23, permission_name: '课程预约', permission_code: 'member:booking', resource_type: 'menu', url: '/member/booking', parent_id: 22, sort_order: 10 },
    { id: 24, permission_name: '我的课表', permission_code: 'member:timetable', resource_type: 'menu', url: '/member/timetable', parent_id: 22, sort_order: 20 },
    { id: 25, permission_name: '体测记录', permission_code: 'member:measure', resource_type: 'menu', url: '/member/measure', parent_id: 22, sort_order: 30 },
    { id: 26, permission_name: '我的订单', permission_code: 'member:order', resource_type: 'menu', url: '/member/order', parent_id: 22, sort_order: 40 }
  ]
}
