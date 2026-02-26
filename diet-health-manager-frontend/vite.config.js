import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      // 凡是 /api 开头的请求，统统代理到 Spring Boot 后端
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        // 把 /api 替换为空，这样前端请求 /api/user/login 就会变成 http://localhost:8080/user/login
        rewrite: (path) => path.replace(/^\/api/, '') 
      }
    }
  }
})