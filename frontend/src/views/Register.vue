<template>
  <div class="container">
    <div class="card" style="max-width: 500px; margin: 100px auto;">
      <h2 style="text-align: center; margin-bottom: 30px; color: #667eea;">註冊</h2>
      
      <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
      <div v-if="successMessage" class="success-message">{{ successMessage }}</div>
      
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="userName">姓名</label>
          <input
            type="text"
            id="userName"
            v-model="userName"
            class="form-control"
            placeholder="請輸入您的姓名"
            required
          />
        </div>
        
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
            placeholder="請輸入密碼 (至少6個字元)"
            required
          />
        </div>
        
        <div class="form-group">
          <label for="confirmPassword">確認密碼</label>
          <input
            type="password"
            id="confirmPassword"
            v-model="confirmPassword"
            class="form-control"
            placeholder="請再次輸入密碼"
            required
          />
        </div>
        
        <button type="submit" class="btn btn-primary btn-block" :disabled="loading">
          {{ loading ? '註冊中...' : '註冊' }}
        </button>
      </form>
      
      <div style="text-align: center; margin-top: 20px;">
        已有帳號？
        <router-link to="/login" style="color: #667eea; text-decoration: none; font-weight: 500;">
          立即登入
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

const userName = ref('')
const phoneNumber = ref('')
const password = ref('')
const confirmPassword = ref('')
const errorMessage = ref('')
const successMessage = ref('')
const loading = ref(false)

const handleRegister = async () => {
  errorMessage.value = ''
  successMessage.value = ''
  
  // 驗證密碼
  if (password.value !== confirmPassword.value) {
    errorMessage.value = '兩次輸入的密碼不一致'
    return
  }
  
  if (password.value.length < 6) {
    errorMessage.value = '密碼長度至少需要6個字元'
    return
  }
  
  loading.value = true
  
  try {
    const result = await authStore.register(phoneNumber.value, password.value, userName.value)
    
    if (result.code === 0) {
      successMessage.value = '註冊成功！3秒後跳轉到登入頁面...'
      setTimeout(() => {
        router.push('/login')
      }, 3000)
    } else {
      errorMessage.value = result.message
    }
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '註冊失敗，請稍後再試'
  } finally {
    loading.value = false
  }
}
</script>
