<template>
  <div class="container">
    <h1 style="color: white; margin: 30px 0;">我的借閱</h1>
    
    <div class="card">
      <div class="tabs">
        <button 
          class="tab" 
          :class="{ active: activeTab === 'current' }"
          @click="activeTab = 'current'; loadRecords()"
        >
          借閱中
        </button>
        <button 
          class="tab" 
          :class="{ active: activeTab === 'history' }"
          @click="activeTab = 'history'; loadRecords()"
        >
          歷史紀錄
        </button>
      </div>
      
      <div v-if="loading" class="loading">載入中...</div>
      
      <div v-else-if="records.length === 0" style="text-align: center; padding: 40px; color: #888;">
        {{ activeTab === 'current' ? '目前沒有借閱中的書籍' : '沒有歷史借閱紀錄' }}
      </div>
      
      <div v-else class="record-list">
        <div 
          v-for="record in records" 
          :key="record.recordId" 
          class="record-card"
          :class="{ 
            'overdue': record.isOverdue && !record.returnTime,
            'returned': record.returnTime 
          }"
        >
          <div class="record-header">
            <div>
              <div class="book-title">{{ record.bookName }}</div>
              <div class="book-author">{{ record.author }}</div>
            </div>
            <div 
              class="record-status"
              :class="{
                'status-borrowed': !record.returnTime && !record.isOverdue,
                'status-overdue': record.isOverdue && !record.returnTime,
                'status-returned': record.returnTime
              }"
            >
              {{ getStatusText(record) }}
            </div>
          </div>
          
          <div style="margin-top: 15px; font-size: 14px; color: #666;">
            <div>ISBN：{{ record.isbn }}</div>
            <div>借出時間：{{ formatDateTime(record.borrowingTime) }}</div>
            <div>應還時間：{{ formatDateTime(record.dueDate) }}</div>
            <div v-if="record.returnTime">歸還時間：{{ formatDateTime(record.returnTime) }}</div>
            <div v-if="record.isOverdue" style="color: #dc3545; font-weight: 500;">
              逾期 {{ record.overdueDays }} 天，罰款：NT$ {{ record.fineAmount }}
            </div>
          </div>
          
          <button 
            v-if="!record.returnTime"
            class="btn btn-primary" 
            style="margin-top: 15px;"
            @click="returnBook(record.recordId)"
          >
            歸還書籍
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api/axios'

const activeTab = ref('current')
const records = ref([])
const loading = ref(false)

const loadRecords = async () => {
  loading.value = true
  try {
    const endpoint = activeTab.value === 'current' ? '/borrowing/current' : '/borrowing/history'
    const response = await api.get(endpoint)
    records.value = response.data.data || []
  } catch (error) {
    alert('載入借閱紀錄失敗：' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const returnBook = async (recordId) => {
  if (!confirm('確定要歸還這本書嗎？')) {
    return
  }
  
  try {
    const response = await api.post('/borrowing/return', { recordId })
    
    if (response.data.code === 0) {
      alert('歸還成功！')
      loadRecords()
    } else {
      alert(response.data.message)
    }
  } catch (error) {
    alert('歸還失敗：' + (error.response?.data?.message || error.message))
  }
}

const getStatusText = (record) => {
  if (record.returnTime) {
    return record.isOverdue ? '已歸還 (逾期)' : '已歸還'
  }
  return record.isOverdue ? '借閱中 (逾期)' : '借閱中'
}

const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  return new Date(dateTimeStr).toLocaleString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  loadRecords()
})
</script>
