import { createApp } from 'vue'
import {createPinia} from 'pinia'
import ElementPlus from 'element-plus';
import './style.css'
import App from './App.vue'
import router from './router/index.ts'

//引入axios依赖
import axios from 'axios'

//让请求携带上浏览器的cookie
axios.defaults.withCredentials=true

// // 设置路由跳转回调
// setRedirectToLoginCallback(() => {
//     router.push('/login');
// });
const pinia =createPinia();

createApp(App)
    .use(pinia)
    .use(router)
    .use(ElementPlus)
    .mount('#app')
