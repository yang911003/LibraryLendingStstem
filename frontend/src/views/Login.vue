<template>
  <div class="container">
    <div class="card" style="max-width: 450px; margin: 80px auto;">
      <h2 style="text-align: center; margin-bottom: 24px; color: #2c5282; font-size: 24px; font-weight: 600;">使用者登入</h2>
      
      <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
      
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="phoneNumber">手機號碼</label>
          <input
            type="text"
            id="phoneNumber"
            v-model="phoneNumber"
            class="form-control"
            placeholder="請輸入手機號碼 (09xxxxxxxx)"
            required
          />
        </div>
        
        <div class="form-group">
          <label for="password">密碼</label>
          <input
            type="password"
            id="password"
            v-model="password"
            class="form-control"
            placeholder="請輸入密碼"
            required
          />
        </div>
        
        <button type="submit" class="btn btn-primary btn-block" :disabled="loading">
          {{ loading ? '登入中...' : '登入' }}
        </button>
      </form>
      
      <div style="text-align: center; margin-top: 20px; color: #666;">
        還沒有帳號？
        <router-link to="/register" style="color: #2c5282; text-decoration: none; font-weight: 500;">
          立即註冊
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const phoneNumber = ref('')
const password = ref('')
const errorMessage = ref('')
const loading = ref(false)

const handleLogin = async () => {
  errorMessage.value = ''
  loading.value = true
  
  try {
    const result = await authStore.login(phoneNumber.value, password.value)
    
    if (result.code === 0) {
      router.push('/books')
    } else {
      errorMessage.value = result.message
    }
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '登入失敗，請稍後再試'
  } finally {
    loading.value = false
  }
}
</script>
