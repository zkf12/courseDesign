import { createRouter, createWebHistory } from 'vue-router'
import { usePersistStore } from '../store/persist';
import { ElMessage } from 'element-plus'
import login from '../views/login.vue'
import manage from '../views/layout.vue'

const routes = [
  {
    path: '/login',
    component: login,
    meta: { requiresAuth: false } // 不需要登录
  },
  {
    path:'/',
    component: manage,
    redirect: '/home',
    children:[
      // 管理用户
      {
        path: '/userManagement',
        component: () => import('../views/manager/userManagement.vue'),
      },
      {
        path: '/userlog',
        component: () => import('../views/manager/logSearch.vue'),
      },
      //图表
      {
        path: '/showData',
        component: () => import('../views/manager/showData.vue'),
      },
      // 首页子路由
      {
        path: '/home',
        component: () => import('../views/home/index.vue'),
        meta: { requiresAuth: false } // 不需要登录
      },
      {
        path: '/recommend',
        component: () => import('../views/home/recommend.vue'),
        meta: { requiresAuth: true }
      },
      //店铺子路由
      // 新增商铺申请页面
      {
        path: '/shop/apply',
        name: 'ShopApply',
        component: () => import('../views/shop/ShopApply.vue'),
        meta: { requiresAuth: true }
      },
      // 新增等待审核页面
      {
        path: '/shop/waiting',
        name: 'ShopWaiting',
        component: () => import('../views/shop/ShopWaiting.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/shop/sub1',
        name: 'ShopSub1',
        component: () => import('../views/shop/ShopProductManagement.vue'),
        meta: { 
          requiresAuth: true,
          requiredPermission: 1 // 需要权限为1
        }
      },
      {
        path: '/shop/sub2',
        name: 'ShopSub2',
        component: () => import('../views/shop/SaleHistory.vue'),
        meta: { 
          requiresAuth: true,
          requiredPermission: 1 // 需要权限为1
        }
      },
      //个人中心子路由
      {
        path: '/profile/edit',
        name: 'EditProfile',
        component: () => import('../views/profile/EditProfile.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/profile/changePassword',
        name: 'ChangePassword',
        component: () => import('../views/profile/ChangePassword.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/profile/cart',
        name: 'Cart',
        component: () => import('../views/profile/Cart.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/profile/purchaseHistory',
        name: 'purchaseHistory',
        component: () => import('../views/profile/purchaseHistory.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/profile/sub5',
        name: 'ProfileSub5',
        component: () => import('../views/profile/view.vue'),
        meta: { requiresAuth: true }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/home' // 所有未知路径重定向到首页
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes:routes
})

let is401Error = false
export function set401ErrorFlag(value) {
  is401Error = value
}

router.beforeEach((to, from, next) => {
  const persistStore = usePersistStore()
  const isLoggedIn = !!persistStore.token
  const userPermission = persistStore.userInfo?.permission || 0

  // 默认访问指向首页
  if (to.path === '/' || !to.matched.length) {
    return next('/home')
  }

  // 需要登录但未登录 → 跳转到登录页
  if (to.meta.requiresAuth && !isLoggedIn) {
    ElMessage.error("用户未登录，请先登录")
    return next({ 
      path: '/login',
      query: { redirect: to.fullPath }
    })
  }

  // 已登录但访问登录页 → 跳转到首页
  if (to.path === '/login' && isLoggedIn && !is401Error) {
    ElMessage.error("用户已登录，请勿重复登录")
    return next('/')
  }

  // 检查商铺路由权限
  if (to.meta.requiredPermission) {
    if (userPermission === 0) {
      // 权限为0，跳转到申请页面
      return next('/shop/apply')
    } else if (userPermission === 2) {
      // 权限为2，跳转到等待页面
      ElMessage.warning('您的商铺申请正在审核中，请耐心等待')
      return next('/shop/waiting')
    } else if (userPermission !== 1) {
      // 权限不为1，跳回首页
      ElMessage.warning('您没有访问此页面的权限')
      return next('/home')
    }
  }

  next()
})

export default router