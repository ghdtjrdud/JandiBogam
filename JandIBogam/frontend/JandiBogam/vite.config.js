import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',  // Spring Boot 서버 주소
        changeOrigin: true,
        secure: false
        // rewrite: path => path.replace(/^\/api/, '') // 필요시 사용 (대부분 안 씀)
      }
    }
  }
})
