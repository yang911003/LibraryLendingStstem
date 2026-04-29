import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '../api/axios'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const isAuthenticated = computed(() => !!token.value)

  const setAuth = (authToken, userData) => {
    token.value = authToken
    user.value = userData
    localStorage.setItem('token', authToken)
    localStorage.setItem('user', JSON.stringify(userData))
    api.defaults.headers.common['Authorization'] = `Bearer ${authToken}`
  }

  const clearAuth = () => {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    delete api.defaults.headers.common['Authorization']
  }

  const checkAuth = () => {
    if (token.value) {
      api.defaults.headers.common['Authorization'] = `Bearer ${token.value}`
    }
  }

  const register = async (phoneNumber, password, userName) => {
    const response = await api.post('/auth/register', {
      phoneNumber,
      password,
      userName
    })
    return response.data
  }

  const login = async (phoneNumber, password) => {
    const response = await api.post('/auth/login', {
      phoneNumber,
      password
    })
    
    if (response.data.code === 0) {
      const userData = response.data.data
      setAuth(userData.token, {
        userId: userData.userId,
        phoneNumber: userData.phoneNumber,
        userName: userData.userName
      })
    }
    
    return response.data
  }

  const logout = () => {
    clearAuth()
  }

  return {
    token,
    user,
    isAuthenticated,
    setAuth,
    clearAuth,
    checkAuth,
    register,
    login,
    logout
  }
})
