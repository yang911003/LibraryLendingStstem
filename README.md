# 圖書借閱系統 (Library Lending System)

這是一個完整的線上圖書借閱系統，使用 Spring Boot + Vue.js 實作，包含使用者註冊、登入、書籍查詢、借還書等功能。

## 技術架構

### 後端技術
- **框架**: Spring Boot 4.0.6
- **建置工具**: Gradle
- **資料庫**: MySQL 8.0+
- **安全性**: Spring Security + JWT
- **ORM**: JPA / Hibernate
- **API 風格**: RESTful API

### 前端技術
- **框架**: Vue.js 3
- **路由**: Vue Router 4
- **狀態管理**: Pinia
- **HTTP 客戶端**: Axios
- **建置工具**: Vite

### 安全特性
- ✅ 密碼加鹽 (Salt) + SHA-256 雜湊加密
- ✅ JWT Token 身份驗證
- ✅ SQL Injection 防護 (使用 Stored Procedure 和參數化查詢)
- ✅ XSS 攻擊防護 (輸入清理)
- ✅ CORS 跨域配置

## 功能特色

### 1. 使用者功能
- **註冊**: 使用手機號碼註冊帳號
- **登入**: JWT Token 身份驗證
- **密碼安全**: 加鹽雜湊儲存

### 2. 書籍管理
- **查詢書籍**: 支援關鍵字搜尋 (書名/作者)
- **即時庫存**: 顯示可借閱數量

### 3. 借還書功能
- **借書**: 選擇庫存並設定借閱天數
- **還書**: 自動計算逾期天數和罰款
- **借閱紀錄**: 查看借閱中和歷史紀錄
- **交易完整性**: 使用 Database Transaction 確保資料一致性

## 資料庫設計

### 主要資料表
1. **user** - 使用者表
2. **book** - 書籍表
3. **inventory** - 庫存表
4. **borrowing_record** - 借閱紀錄表

### Stored Procedures
- `sp_register_user` - 使用者註冊
- `sp_user_login` - 使用者登入
- `sp_borrow_book` - 借書 (含 Transaction)
- `sp_return_book` - 還書 (含 Transaction)
- `sp_get_user_borrowing_records` - 查詢借閱紀錄
- `sp_get_available_books` - 查詢可借閱書籍
- `sp_get_available_inventory_by_isbn` - 查詢書籍庫存

## 專案結構

```
LibraryLendingSystem/
├── DB/
│   ├── init_database.sql           # 資料庫自動初始化腳本
│   ├── DDL.sql                     # 資料庫結構定義
│   └── DML.sql                     # 測試資料
├── setup.bat                       # Windows 自動安裝腳本
├── setup.sh                        # Mac/Linux 自動安裝腳本
├── src/main/java/com/example/library/
│   ├── config/                     # 配置類
│   │   └── SecurityConfig.java
│   ├── controller/                 # 控制層 (REST API)
│   │   ├── AuthController.java
│   │   ├── BookController.java
│   │   └── BorrowingController.java
│   ├── service/                    # 業務層
│   │   ├── UserService.java
│   │   └── BorrowingService.java
│   ├── repository/                 # 資料層
│   │   ├── UserRepository.java
│   │   └── BorrowingRepository.java
│   ├── entity/                     # 實體類
│   │   ├── User.java
│   │   ├── Book.java
│   │   ├── Inventory.java
│   │   └── BorrowingRecord.java
│   ├── dto/                        # 資料傳輸物件
│   ├── security/                   # 安全配置
│   │   └── JwtAuthenticationFilter.java
│   ├── util/                       # 工具類
│   │   ├── PasswordUtil.java
│   │   └── JwtUtil.java
│   └── exception/                  # 異常處理
│       ├── BusinessException.java
│       └── GlobalExceptionHandler.java
├── frontend/
│   ├── src/
│   │   ├── views/                  # 頁面組件
│   │   │   ├── Login.vue
│   │   │   ├── Register.vue
│   │   │   ├── Books.vue
│   │   │   └── Borrowing.vue
│   │   ├── components/             # 通用組件
│   │   │   └── Navbar.vue
│   │   ├── stores/                 # 狀態管理
│   │   │   └── auth.js
│   │   ├── api/                    # API 封裝
│   │   │   └── axios.js
│   │   └── router/                 # 路由配置
│   │       └── index.js
│   └── package.json
├── build.gradle
└── README.md
```

## 快速開始

### 前置需求
- JDK 17+
- MySQL 8.0+
- Node.js 18+
- Maven 或 Gradle

### 自動安裝（推薦）

#### Windows 使用者
```bash
# 雙擊執行或在命令列執行
setup.bat
```

#### Mac/Linux 使用者
```bash
# 賦予執行權限
chmod +x setup.sh

# 執行安裝腳本
./setup.sh
```

安裝腳本會自動：
1. ✅ 檢查 MySQL 服務狀態
2. ✅ 建立資料庫 `library_lending_system`
3. ✅ 建立所有資料表和 Stored Procedures
4. ✅ 可選擇載入測試資料
5. ✅ 自動更新 `application.properties` 設定檔

### 手動安裝

#### 1. 建立資料庫

```bash
# 登入 MySQL
mysql -u root -p

# 執行初始化腳本（會自動建立資料庫、資料表和 Stored Procedures）
source DB/init_database.sql;

# 可選：載入測試資料
source DB/DML.sql;
```

#### 2. 設定後端

編輯 `src/main/resources/application.properties`：

```properties
# 資料庫連線資訊
spring.datasource.url=jdbc:mysql://localhost:3306/library_lending_system?useSSL=false&serverTimezone=Asia/Taipei
spring.datasource.username=root
spring.datasource.password=your_password

# JWT 密鑰 (請修改為安全的隨機字串)
jwt.secret=your-256-bit-secret-key-change-this-in-production
```

#### 3. 安裝前端依賴

```bash
cd frontend
npm install
```

### 啟動應用程式

#### 啟動後端
```bash
# 使用 Maven
./mvnw spring-boot:run

# 或使用 Gradle
./gradlew bootRun
```

後端服務會在 `http://localhost:8080/api` 啟動

#### 啟動前端
```bash
cd frontend
npm run dev
```

前端服務會在 `http://localhost:5173` 啟動

### 完成！
開啟瀏覽器訪問 `http://localhost:5173` 即可使用系統

## API 文檔

### 認證相關

#### 1. 註冊
```http
POST /api/auth/register
Content-Type: application/json

{
  "phoneNumber": "0912345678",
  "password": "password123",
  "userName": "張小明"
}
```

#### 2. 登入
```http
POST /api/auth/login
Content-Type: application/json

{
  "phoneNumber": "0912345678",
  "password": "password123"
}
```

### 書籍相關

#### 3. 查詢可借閱書籍
```http
GET /api/books/available?keyword=Java
```

#### 4. 查詢書籍庫存
```http
GET /api/books/{isbn}/inventory
Authorization: Bearer {token}
```

### 借還書相關

#### 5. 借書
```http
POST /api/borrowing/borrow
Authorization: Bearer {token}
Content-Type: application/json

{
  "inventoryId": 1,
  "dueDays": 14
}
```

#### 6. 還書
```http
POST /api/borrowing/return
Authorization: Bearer {token}
Content-Type: application/json

{
  "recordId": 1
}
```

#### 7. 查詢借閱紀錄
```http
GET /api/borrowing/records?isReturned=0
Authorization: Bearer {token}
```

#### 8. 查詢當前借閱
```http
GET /api/borrowing/current
Authorization: Bearer {token}
```

#### 9. 查詢歷史借閱
```http
GET /api/borrowing/history
Authorization: Bearer {token}
```

## 測試帳號

系統已預設建立測試使用者（密碼為示例，實際需要經過雜湊）：

| 手機號碼 | 姓名 |
|---------|------|
| 0912345678 | 張小明 |
| 0923456789 | 李美華 |
| 0934567890 | 王大衛 |

**注意**: 測試資料中的密碼欄位為示例，實際使用時請重新註冊帳號。

## 安全性說明

### 1. SQL Injection 防護
- 所有資料庫操作都使用 Stored Procedure
- 使用參數化查詢 (PreparedStatement)
- JPA 的 Native Query 也使用參數綁定

### 2. XSS 防護
- 所有使用者輸入都經過清理 (`sanitizeInput` 方法)
- 移除或轉義危險字元 (`<`, `>`, `"`, `'`, `/`)

### 3. 密碼安全
- 使用 SHA-256 雜湊演算法
- 每個密碼都有獨立的隨機鹽值 (Salt)
- 密碼不以明文儲存

### 4. JWT Token
- Token 包含使用者資訊
- 設定過期時間 (預設 24 小時)
- 每次請求都會驗證 Token 有效性

### 5. Transaction 管理
- 借還書操作使用 Database Transaction
- 確保資料一致性
- 失敗時自動回滾

## 注意事項

1. **生產環境部署前**:
   - 修改 `jwt.secret` 為安全的隨機字串
   - 修改資料庫密碼
   - 設定適當的 CORS 允許來源
   - 啟用 HTTPS

2. **資料庫優化**:
   - 已建立適當的索引
   - 可根據需求調整連線池大小

3. **前端部署**:
   ```bash
   cd frontend
   npm run build
   # 將 dist 目錄部署到 Web Server
   ```

## 開發團隊

本專案為圖書借閱系統示範專案。

## 授權

本專案僅供學習和參考使用。
