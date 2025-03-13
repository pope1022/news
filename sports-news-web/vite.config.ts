import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')  // 配置别名
    }
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:6666',  // 后端服务器地址
        changeOrigin: true,  // 是否改变请求源 
      },
      '/uploads': {
        target: 'http://localhost:6666',
        changeOrigin: true
      }
    },
  },
});