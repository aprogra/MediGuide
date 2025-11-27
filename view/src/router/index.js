import { createRouter, createWebHistory } from 'vue-router'

// 导入视图组件
const ChatPage = () => import('../views/ChatPage.vue')
const LoginPage = () => import('../views/LoginPage.vue')
const TestPage = () => import('../views/TestPage.vue')
const TestMedicinePage = () => import('../views/TestMedicinePage.vue')
const MedicineTableTest = () => import('../views/MedicineTableTest.vue')
const MedicineDataFlowTest = () => import('../views/MedicineDataFlowTest.vue')

// 定义路由配置
const routes = [
  {
    path: '/',
    redirect: '/login' // 默认重定向到登录页面
  },
  {
    path: '/login',
    name: 'LoginPage',
    component: LoginPage,
    meta: {
      title: '登录',
      requiresAuth: false // 登录页面不需要身份验证
    }
  },
  {
    path: '/test',
    name: 'TestPage',
    component: TestPage,
    meta: {
      title: '测试页面',
      requiresAuth: false
    }
  },
  {
    path: '/test-medicine',
    name: 'TestMedicinePage',
    component: TestMedicinePage,
    meta: {
      title: '快速配药测试',
      requiresAuth: false
    }
  },
  {
    path: '/medicine-table-test',
    name: 'MedicineTableTest',
    component: MedicineTableTest,
    meta: {
      title: '药物表格测试',
      requiresAuth: false
    }
  },
  {
    path: '/medicine-data-flow-test',
    name: 'MedicineDataFlowTest',
    component: MedicineDataFlowTest,
    meta: {
      title: '药品数据流程测试',
      requiresAuth: false
    }
  },
  {
    path: '/chat/:patientId?',
    name: 'ChatPage',
    component: ChatPage,
    meta: {
      title: '医患对话',
      requiresAuth: true // 需要身份验证
    },
    props: true // 允许将路由参数作为组件的props传递
  },
  {
    // 404页面
    path: '/:pathMatch(.*)*',
    redirect: '/chat' // 未匹配的路径重定向到聊天页面
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// 全局前置守卫 - 控制访问权限和设置页面标题
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title || 'MediGuide'
  
  // 检查是否需要身份验证
  if (to.meta.requiresAuth !== false) {
    // 检查用户是否已登录
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'
    const doctorId = localStorage.getItem('doctorId')
    
    if (isLoggedIn && doctorId) {
      // 已登录，继续访问
      next()
    } else {
      // 未登录，重定向到登录页面
      next({
        path: '/login',
        query: { redirect: to.fullPath } // 记录用户原本想要访问的路径
      })
    }
  } else {
    // 不需要身份验证的页面（如登录页），直接访问
    next()
  }
})

export default router