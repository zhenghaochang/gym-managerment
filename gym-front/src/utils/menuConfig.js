// 菜单图标映射（根据权限代码匹配图标）
export const menuIconMap = {
  'system:manage': 'Setting',
  'user:manage': 'User',
  'permission:assign': 'Key',
  
  'member:manage': 'User',
  'member:list': 'List',
  'card:manage': 'CreditCard',
  'member:application': 'DocumentChecked',
  
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
  'member:myCenter': 'User',
  'member:order': 'ShoppingCart',
  
  'member:create-card': 'CreditCard',
  'course:purchase': 'ShoppingCart',
  'course:my-card': 'Postcard',
  'member:my-course': 'Reading',
  'member:course-booking': 'Calendar',
  'member:course-booking2': 'User',
  'member:my-application': 'Document',
  'member:booked-record': 'Tickets',
  'member:seckill': 'Lightning',
  'seckill:manage': 'Lightning'
}

// 菜单配置（权限代码 -> 菜单信息）
export const menuConfig = {
  // 会员菜单
  'member:home': { title: '我的主页', path: '/member/home', icon: 'HomeFilled', parent: null, sort: 7 },
  'member:myCenter': { title: '个人中心', path: '/member/myCenter', icon: 'User', parent: 'member:home', sort: 10 },
  'member:order': { title: '我的订单', path: '/member/order', icon: 'ShoppingCart', parent: 'member:home', sort: 40 },
  
  // 会员独立菜单（无父级）
  'member:create-card': { title: '办理会员卡', path: '/create-card', icon: 'CreditCard', parent: null, sort: 8 },
  'course:purchase': { title: '购买课程', path: '/purchase-course', icon: 'ShoppingCart', parent: null, sort: 9 },
  'course:my-card': { title: '我的会员卡', path: '/my-card', icon: 'Postcard', parent: null, sort: 10 },
  'member:my-course': { title: '我的课程', path: '/member/myCourse', icon: 'Reading', parent: null, sort: 11 },
  'member:course-booking': { title: '团课预约', path: '/member/course-booking', icon: 'Calendar', parent: null, sort: 12 },
  'member:course-booking2': { title: '私教预约', path: '/member/course-booking2', icon: 'User', parent: null, sort: 13 },
  'member:my-application': { title: '我的申请', path: '/member/myApplication', icon: 'Document', parent: null, sort: 14 },
  'member:booked-record': { title: '预约记录', path: '/member/bookedRecord', icon: 'Tickets', parent: null, sort: 15 },
  'member:seckill': { title: '限时秒杀', path: '/member/seckill', icon: 'Lightning', parent: null, sort: 16 },
  
  // 系统管理
  'system:manage': { title: '系统管理', path: '/system', icon: 'Setting', parent: null, sort: 1 },
  'user:manage': { title: '用户管理', path: '/system/user', icon: 'User', parent: 'system:manage', sort: 10 },
  'permission:assign': { title: '权限分配', path: '/system/permission', icon: 'Key', parent: 'system:manage', sort: 30 },
  
  // 会员管理（管理员）
  'member:manage': { title: '会员管理', path: '/member', icon: 'User', parent: null, sort: 2 },
  'member:list': { title: '会员信息', path: '/member/list', icon: 'List', parent: 'member:manage', sort: 10 },
  'card:manage': { title: '办卡续费', path: '/member/card', icon: 'CreditCard', parent: 'member:manage', sort: 20 },
  'member:application': { title: '退课审批', path: '/member/application', icon: 'DocumentChecked', parent: 'member:manage', sort: 30 },
  'seckill:manage': { title: '秒杀管理', path: '/member/seckill-manage', icon: 'Lightning', parent: 'member:manage', sort: 40 },
  
  // 课程管理
  'course:manage': { title: '课程管理', path: '/course', icon: 'Reading', parent: null, sort: 3 },
  'course:setting': { title: '课程设置', path: '/course/setting', icon: 'Setting', parent: 'course:manage', sort: 10 },
  'schedule:manage': { title: '排课管理', path: '/course/schedule', icon: 'Calendar', parent: 'course:manage', sort: 20 },
  'booking:manage': { title: '预约管理', path: '/course/booking', icon: 'Tickets', parent: 'course:manage', sort: 30 },
  
  // 教练菜单
  'coach:my-course': { title: '我的课程', path: '/coach/course', icon: 'Reading', parent: null, sort: 4 },
  'coach:student': { title: '学员管理', path: '/coach/student', icon: 'User', parent: 'coach:my-course', sort: 10 },
  'coach:schedule': { title: '排班管理', path: '/coach/schedule', icon: 'Calendar', parent: 'coach:my-course', sort: 20 },
  
  // 器械管理
  'equipment:manage': { title: '器械管理', path: '/equipment', icon: 'Box', parent: null, sort: 5 },
  'equipment:list': { title: '器械列表', path: '/equipment/list', icon: 'List', parent: 'equipment:manage', sort: 10 },
  'repair:manage': { title: '报修管理', path: '/equipment/repair', icon: 'Tools', parent: 'equipment:manage', sort: 20 },
  
  // 财务管理
  'finance:manage': { title: '财务管理', path: '/finance', icon: 'Wallet', parent: null, sort: 6 },
  'order:manage': { title: '订单管理', path: '/finance/order', icon: 'ShoppingCart', parent: 'finance:manage', sort: 10 },
  'income:stat': { title: '收入统计', path: '/finance/stat', icon: 'TrendCharts', parent: 'finance:manage', sort: 20 }
}

/**
 * 根据权限代码数组生成菜单树
 * @param {string[]} permissionCodes - 权限代码数组，如 ['member:home', 'member:booking']
 * @returns {Array} 菜单树
 */
export function transformPermissionsToMenusByCode(permissionCodes) {
  if (!permissionCodes || permissionCodes.length === 0) {
    return []
  }

  const menuMap = {}  // 存储所有菜单节点
  const roots = []    // 根节点

  // 遍历权限代码，创建菜单节点
  permissionCodes.forEach(code => {
    const config = menuConfig[code]
    if (!config) return

    // 创建菜单节点
    const menuItem = {
      code,
      title: config.title,
      path: config.path,
      icon: config.icon,
      sort: config.sort,
      children: []
    }

    menuMap[code] = menuItem

    // 如果有父级，确保父级也存在
    if (config.parent && menuConfig[config.parent]) {
      // 父级可能不在权限列表中，需要自动添加
      if (!menuMap[config.parent]) {
        const parentConfig = menuConfig[config.parent]
        menuMap[config.parent] = {
          code: config.parent,
          title: parentConfig.title,
          path: parentConfig.path,
          icon: parentConfig.icon,
          sort: parentConfig.sort,
          children: []
        }
      }
    }
  })

  // 建立父子关系
  Object.keys(menuMap).forEach(code => {
    const config = menuConfig[code]
    const node = menuMap[code]

    if (config.parent && menuMap[config.parent]) {
      // 添加到父级的 children
      menuMap[config.parent].children.push(node)
    } else if (!config.parent) {
      // 根节点
      roots.push(node)
    }
  })

  // 排序
  const sortMenus = (menus) => {
    menus.sort((a, b) => a.sort - b.sort)
    menus.forEach(menu => {
      if (menu.children && menu.children.length > 0) {
        sortMenus(menu.children)
      } else {
        delete menu.children
      }
    })
  }

  sortMenus(roots)

  return roots
}


