import { createApp } from 'vue'
import App from './App.vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import routes from './router'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 创建 Vue 应用
const app = createApp(App)

// 挂载 Pinia（修复 `getActivePinia()` 报错）
const pinia = createPinia()
app.use(pinia)

// 挂载 Element Plus UI 框架
app.use(ElementPlus)

// 全局注册 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}


// 挂载 Vue Router
app.use(routes)


// 挂载应用
app.mount('#app')
