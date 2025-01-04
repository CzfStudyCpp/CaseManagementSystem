import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'), // 配置 @ 为 src 目录
    },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080/api', // 后端地址
        changeOrigin: true, // 支持跨域
        rewrite: (path) => path.replace(/^\/api/, '') // 可选：如果需要去掉路径前缀 /api
      }
    }
  }
});
