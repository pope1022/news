import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('../layouts/DefaultLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('../views/user/Home.vue')
      },
      {
        path: 'news/:id',
        name: 'NewsDetail',
        component: () => import('../views/user/NewsDetail.vue')
      },
      {
        path: 'products',
        name: 'Products',
        component: () => import('../views/user/Products.vue')
      },
      {
        path: 'products/:id',
        name: 'ProductDetail',
        component: () => import('../views/user/ProductDetail.vue')
      },
      {
        path: 'cart',
        name: 'Cart',
        component: () => import('../views/user/Cart.vue')
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('../views/user/Orders.vue')
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/user/Profile.vue')
      }
    ]
  },
  {
    path: '/admin',
    component: () => import('../layouts/AdminLayout.vue'),
    meta: { requiresAdmin: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('../views/admin/Dashboard.vue')
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('../views/admin/UserManagement.vue')
      },
      {
        path: 'news',
        name: 'NewsManagement',
        component: () => import('../views/admin/NewsManagement.vue')
      },
      {
        path: 'categories',
        name: 'CategoryManagement',
        component: () => import('../views/admin/CategoryManagement.vue')
      },
      {
        path: 'products',
        name: 'ProductManagement',
        component: () => import('../views/admin/ProductManagement.vue')
      },
      {
        path: 'orders',
        name: 'OrderManagement',
        component: () => import('../views/admin/OrderManagement.vue')
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userRole = localStorage.getItem('userRole')

  // 如果访问的是管理员路由且没有管理员权限，跳转到登录页面
  if (to.meta.requiresAdmin && (!token || userRole !== '1')) {
    next('/login')
  } 
  // 如果用户已登录并尝试访问登录页面，跳转到首页
  else if (to.path === '/login' && token) {
    next('/')
  } 
  // 其他情况，正常访问
  else {
    next()
  }
})

export default router 