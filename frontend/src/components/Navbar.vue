<template>
  <nav class="navbar">
    <div class="navbar-content">
      <div class="navbar-brand">圖書館借閱管理系統</div>
      <div class="navbar-menu">
        <router-link to="/books">書籍查詢</router-link>
        <router-link to="/borrowing">借閱紀錄</router-link>
        <span class="navbar-user">{{ authStore.user?.userName || '使用者' }}</span>
        <button class="btn btn-secondary" @click="handleLogout">登出</button>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { useAuthStore } from '../stores/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
</script>
