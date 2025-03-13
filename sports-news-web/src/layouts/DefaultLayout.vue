<template>
  <div class="layout">
    <el-container>
      <!-- 头部导航 -->
      <el-header class="header">
        <div class="header-content">
          <!-- Logo -->
          <router-link to="/" class="logo">
            体育新闻网
          </router-link>
          <!-- 导航菜单 -->
          <el-menu mode="horizontal" :router="true" class="menu">
            <el-menu-item index="/">🏠 首页</el-menu-item>
            <el-menu-item index="/products">🛒 商城</el-menu-item>
            <template v-if="userStore.token">
              <el-menu-item index="/cart">🛍 购物车</el-menu-item>
              <el-menu-item index="/orders">📦 我的订单</el-menu-item>
              <el-menu-item index="/profile">👤 个人中心</el-menu-item>
              <el-menu-item v-if="isAdmin" index="/admin">⚙️ 后台管理</el-menu-item>
              <el-menu-item @click="handleLogout">🚪 退出</el-menu-item>
            </template>
            <template v-else>
              <el-menu-item index="/login">🔑 登录</el-menu-item>
              <el-menu-item index="/register">📝 注册</el-menu-item>
            </template>
          </el-menu>
        </div>
      </el-header>

      <!-- 页面内容 -->
      <el-main class="main-content">
        <router-view></router-view>
      </el-main>

      <!-- 页脚 -->
      <el-footer class="footer">
        <p>© 2024 体育新闻网 版权所有</p>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const isAdmin = computed(() => localStorage.getItem('userRole') === '1')

// 退出登录
const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

// 监听滚动，添加动态效果
const isScrolled = ref(false)
onMounted(() => {
  window.addEventListener('scroll', () => {
    isScrolled.value = window.scrollY > 50
  })
})
</script>

<style scoped>
/* 布局 */
.layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: linear-gradient(to bottom, #ffffff, #f5f5f5);
}

/* 头部导航 */
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-bottom: 2px solid #eee;
  transition: all 0.3s ease-in-out;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
}

/* Logo */
.logo {
  font-size: 26px;
  font-weight: bold;
  color: #409eff;
  text-decoration: none;
  transition: transform 0.3s ease-in-out, color 0.3s;
}
.logo:hover {
  transform: scale(1.1);
  color: #66b1ff;
}

/* 导航菜单 */
.menu {
  flex: 1;
  display: flex;
  justify-content: flex-end;
  background: transparent;
  border: none;
}

.menu >>> .el-menu-item {
  font-size: 16px;
  color: #333;
  transition: color 0.3s, transform 0.3s;
}
.menu >>> .el-menu-item:hover {
  color: #409eff;
  transform: translateY(-3px);
}

/* 页面主体 */
.main-content {
  flex: 1;
  padding: 80px 20px 20px; /* 上方留空，防止被固定导航遮挡 */
  animation: fadeIn 0.6s ease-in-out;
}

/* 页脚 */
.footer {
  text-align: center;
  color: #666;
  padding: 15px 0;
  background: #f5f5f5;
}

/* 动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
