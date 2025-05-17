import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { usePersistStore } from '../store/persist';
import { set401ErrorFlag } from '@/router'

// 创建axios实例
const service = axios.create({
  baseURL: "/api/",// 后端地址，假设后端运行在8080端口//process.env.VUE_APP_BASE_API,
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const persistStore = usePersistStore();
    console.log(persistStore.token)
    // 检查store中是否有token
    if (persistStore.token) {
      // 将token添加到请求头中
      config.headers.Authorization = persistStore.token;
    } else {
      console.warn('No token found in persist store'); // 如果没有token的警告
    }
    return config;
  },
  error => {
    console.warn('No token found in persist store11111111111'); // 如果没有token的警告
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  response => {
    return response.data;
  },
  error => {
    if(error.response.status === 401)
      {
        set401ErrorFlag(true)
        const persistStore = usePersistStore();
        persistStore.removeToken
        persistStore.removeUserInfo
        ElMessage.error('登陆信息过期，请重新登录');
        router.push('/login');
      }
    return Promise.reject(error);
  }
);

export default service;