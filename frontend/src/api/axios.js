import axios from 'axios'
import { useAuthStore } from '../stores/auth'
import router from '../router'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 請求攔截器
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 響應攔截器
api.interceptors.response.use(
  response => {
    return response
  },
  error => {
    if (error.response) {
      // 處理 401 未授權
      if (error.response.status === 401) {
        const authStore = useAuthStore()
        authStore.clearAuth()
        router.push('/login')
      }
      
      // 處理 403 禁止訪問
      if (error.response.status === 403) {
        alert('您沒有權限執行此操作')
      }
    }
    
    return Promise.reject(error)
  }
)

export default api
