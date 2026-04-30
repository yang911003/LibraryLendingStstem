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
│   ├── setup_database.sql          # 資料庫初始化腳本
│   ├── DDL.sql                     # 資料庫結構定義（資料表 + Stored Procedures）
│   ├── DML.sql                     # 測試資料
│   ├── run_all.sql                 # 一鍵執行所有腳本
│   └── README_DATABASE_SETUP.md    # 資料庫建置詳細說明
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

## 最近更新 (2026-04-30)

### ✅ 解決編碼問題
- 修正資料庫字元編碼設定,確保中文註冊功能正常運作
- 更新 JDBC 連線參數以支援 MySQL Connector/J 9.x
- 所有資料表使用 `utf8mb4_unicode_ci` 排序規則

## 快速開始

### 前置需求
- JDK 17+
- MySQL 8.0+
- Node.js 18+
- Gradle (已包含 Gradle Wrapper)

### 安裝步驟

#### 1. 建立資料庫

**進入 MySQL 客戶端**:
```powershell
mysql -u root -p --default-character-set=utf8mb4
之後會要求輸入自己的MYSQL密碼，輸入後按 Enter
```

**就可以執行 SQL 建立程序** (在 MySQL 提示符下):
```sql
source DB/setup_database.sql;
source DB/DDL.sql;
source DB/DML.sql;
```

> 💡 **提示**: 必須加上 `--default-character-set=utf8mb4` 以支援中文

#### 2. 設定後端（如需修改密碼）

編輯 `src/main/resources/application.properties`:

```properties(將application.properties中的spring.datasource.password改成自己的SQL密碼)
spring.datasource.password=your_mysql_password
```

其他配置已正確設定,無需修改。

#### 3. 安裝前端依賴

**PowerShell (Windows)**:
```powershell
cd frontend
Remove-Item -Recurse -Force node_modules, package-lock.json -ErrorAction SilentlyContinue
npm install
```

**Mac/Linux**:
```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
```

### 啟動應用程式

#### 啟動後端
```bash
./gradlew bootRun          # Mac/Linux
gradlew.bat bootRun        # Windows
```

後端服務會在 `http://localhost:8080` 啟動

#### 啟動前端（開啟新的終端視窗）
```bash
cd frontend
npm run dev
```

前端服務會在 `http://localhost:5173` 啟動

### 🎉 完成！
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

## 測試資料

執行 `DML.sql` 後會自動建立：
- 📚 10 種書籍（包含程式設計、自我成長等類別）
- 📦 23 個庫存項目
- 👤 5 個測試使用者

**建議**: 使用系統的註冊功能建立新帳號進行測試

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

## 常見問題

### ❌ 中文亂碼
確保建立資料庫時使用 `--default-character-set=utf8mb4` 參數

### ❌ 連線失敗
檢查 `application.properties` 中的 MySQL 密碼是否正確

### ❌ Port 衝突
修改 `application.properties` 中的 `server.port=8080` 為其他 port

### ❌ Stored Procedure 錯誤
重新執行 `DB/DDL.sql`

詳細問題排查請參考 `DB/README_DATABASE_SETUP.md`

## 技術細節

### 資料庫
- 使用 `utf8mb4_unicode_ci` 排序規則，支援中文和 emoji
- MySQL Connector/J 9.x 使用 `connectionCollation` 參數
- 7 個 Stored Procedures 處理核心業務邏輯
- 已建立適當索引優化查詢效能

### 前端部署
```bash
cd frontend
npm run build
# 將 dist 目錄部署到 Web Server (如 Nginx)
```

### 生產環境建議
- 修改 `jwt.secret` 為安全的隨機字串
- 修改資料庫密碼
- 啟用 HTTPS
- 設定適當的 CORS 來源
- 定期備份資料庫

---

**專案建立**: 圖書借閱系統示範專案
**最後更新**: 2026-04-30 - 修正編碼問題並優化資料庫建置流程
**授權**: 僅供學習和參考使用
