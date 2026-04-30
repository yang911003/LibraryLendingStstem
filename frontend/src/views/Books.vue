<template>
  <div class="container">
      <h1 style="color: #2c5282; margin: 30px 0 20px 0; font-size: 28px; font-weight: 600;">館藏書籍查詢</h1>
    
    <div class="card">
      <div class="search-box">
        <input
          type="text"
          v-model="keyword"
          class="form-control"
          placeholder="搜尋書名或作者..."
        />
        <button class="btn btn-primary" @click="searchBooks">搜尋</button>
      </div>
      
      <div v-if="loading" class="loading">載入中...</div>
      
      <div v-else-if="books.length === 0" style="text-align: center; padding: 40px; color: #888;">
        沒有找到可借閱的書籍
      </div>
      
      <div v-else class="book-list">
        <div v-for="book in books" :key="book.isbn" class="book-card">
          <div style="flex: 1;">
            <div class="book-title">{{ book.name }}</div>
            <div class="book-author">作者：{{ book.author }}</div>
            <div class="book-info">ISBN：{{ book.isbn }}</div>
            <div class="book-info" v-if="book.publisher">出版社：{{ book.publisher }}</div>
            <div class="book-info" v-if="book.category">分類：{{ book.category }}</div>
            <div class="book-available">可借數量：{{ book.availableCount }} 本</div>
            <div class="book-info" style="margin-top: 10px; color: #666;">
              {{ book.introduction ? book.introduction.substring(0, 100) + '...' : '暫無簡介' }}
            </div>
          </div>
          <button class="btn btn-primary" style="margin-top: 15px; width: 100%;" @click="borrowBook(book.isbn)">
            借閱
          </button>
        </div>
      </div>F
    </div>
    
    <!-- 借閱對話框 -->
    <div v-if="showBorrowDialog" class="modal" @click.self="closeBorrowDialog">
      <div class="modal-content">
        <h3 style="margin-bottom: 20px; color: #2c5282; font-weight: 600;">選擇借閱館藏</h3>
        
        <div v-if="loadingInventory" class="loading">載入中...</div>
        
        <div v-else-if="inventoryList.length === 0" style="text-align: center; padding: 20px;">
          目前無可借閱的庫存
        </div>
        
        <div v-else>
          <div v-for="(item, index) in inventoryList" :key="item[0]"
               style="padding: 12px; border: 1px solid #d0d0d0; border-radius: 4px; margin-bottom: 10px; cursor: pointer; transition: all 0.2s;"
               :style="{
                 borderColor: selectedInventoryId === item[0] ? '#2c5282' : '#d0d0d0',
                 backgroundColor: selectedInventoryId === item[0] ? '#f0f4f8' : 'white'
               }"
               @click="selectedInventoryId = item[0]">
            <div style="font-weight: 500;">庫存編號：{{ item[0] }}</div>
            <div style="color: #666; font-size: 14px;">入庫時間：{{ formatDate(item[2]) }}</div>
            <div style="color: #666; font-size: 14px;">位置：{{ item[4] || '未指定' }}</div>
          </div>
          
          <div class="form-group" style="margin-top: 20px;">
            <label>借閱天數</label>
            <input type="number" v-model="dueDays" class="form-control" min="1" max="30" />
          </div>
          
          <div style="display: flex; gap: 10px; margin-top: 20px;">
            <button class="btn btn-primary" style="flex: 1;" @click="confirmBorrow" :disabled="!selectedInventoryId">
              確認借閱
            </button>
            <button class="btn btn-secondary" style="flex: 1;" @click="closeBorrowDialog">
              取消
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api/axios'

const keyword = ref('')
const books = ref([])
const loading = ref(false)

const showBorrowDialog = ref(false)
const selectedIsbn = ref('')
const selectedInventoryId = ref(null)
const inventoryList = ref([])
const loadingInventory = ref(false)
const dueDays = ref(14)

const searchBooks = async () => {
  loading.value = true
  try {
    const response = await api.get('/books/available', {
      params: { keyword: keyword.value || null }
    })
    books.value = response.data.data || []
  } catch (error) {
    alert('載入書籍失敗：' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const borrowBook = async (isbn) => {
  selectedIsbn.value = isbn
  selectedInventoryId.value = null
  showBorrowDialog.value = true
  
  loadingInventory.value = true
  try {
    const response = await api.get(`/books/${isbn}/inventory`)
    inventoryList.value = response.data.data || []
  } catch (error) {
    alert('載入庫存失敗：' + (error.response?.data?.message || error.message))
  } finally {
    loadingInventory.value = false
  }
}

const confirmBorrow = async () => {
  if (!selectedInventoryId.value) {
    alert('請選擇要借閱的書籍')
    return
  }
  
  try {
    const response = await api.post('/borrowing/borrow', {
      inventoryId: selectedInventoryId.value,
      dueDays: dueDays.value
    })
    
    if (response.data.code === 0) {
      alert('借閱成功！')
      closeBorrowDialog()
      searchBooks()
    } else {
      alert(response.data.message)
    }
  } catch (error) {
    alert('借閱失敗：' + (error.response?.data?.message || error.message))
  }
}

const closeBorrowDialog = () => {
  showBorrowDialog.value = false
  selectedIsbn.value = ''
  selectedInventoryId.value = null
  inventoryList.value = []
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('zh-TW')
}

onMounted(() => {
  searchBooks()
})
</script>

<style scoped>
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 24px;
  border-radius: 4px;
  max-width: 600px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}
</style>
