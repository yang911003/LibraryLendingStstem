圖書借閱系統 (Library Lending System)

前後端分離的圖書借閱系統，用 Spring Boot + Vue 3 做的，有會員登入、書籍查詢、借還書功能。

## 技術架構

### 後端
- Spring Boot 4.0.6
- Gradle
- MySQL 8.0+
- Spring Security + JWT
- JPA / Hibernate
- RESTful API

### 前端
- Vue.js 3
- Vue Router 4
- Pinia
- Axios
- Vite

### 安全性
- 密碼加鹽 + SHA-256 雜湊
- JWT Token 驗證
- 用 Stored Procedure 防 SQL Injection
- 有做 XSS 防護
- CORS 跨域設定

## 功能

### 使用者
- 手機號碼註冊
- JWT Token 登入
- 密碼加鹽雜湊存儲

### 書籍管理
- 關鍵字搜尋（書名/作者）
- 即時庫存顯示

### 借還書
- 選庫存、設借閱天數
- 查借閱紀錄
- 用 Transaction 保證資料一致

## 資料庫

### 資料表
1. user - 使用者
2. book - 書籍
3. inventory - 庫存
4. borrowing_record - 借閱紀錄

### Stored Procedures
- sp_register_user - 註冊
- sp_user_login - 登入
- sp_borrow_book - 借書（含 Transaction）
- sp_return_book - 還書（含 Transaction）
- sp_get_user_borrowing_records - 借閱紀錄
- sp_get_available_books - 可借書籍
- sp_get_available_inventory_by_isbn - 書籍庫存

## 專案結構

```
LibraryLendingSystem/
├── DB/
│   ├── setup_database.sql          # 資料庫初始化腳本
│   ├── DDL.sql                     # 資料庫結構定義（資料表 + Stored Procedures）
│   ├── DML.sql                     # 測試資料
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



### 解決編碼問題
- 修正資料庫編碼設定，中文註冊正常
- 更新 JDBC 參數支援 MySQL Connector/J 9.x
- 全部資料表用 utf8mb4_unicode_ci

## 專案開始

### 需要的環境
- JDK 17+
- MySQL 8.0+
- Node.js 18+
- Gradle（專案裡有 Gradle Wrapper）

### 安裝步驟

#### 1. 建資料庫

進 MySQL：
```powershell
#記得加 `--default-character-set=utf8mb4` 不然中文會亂碼
mysql -u root -p --default-character-set=utf8mb4
# 輸入密碼後按 Enter
```

執行 SQL：
```sql
source DB/setup_database.sql;
source DB/DDL.sql;
source DB/DML.sql;
```



#### 2. 改後端密碼（要改）

編輯 `src/main/resources/application.properties`：

```properties
# 把這行改成自己的 MySQL 密碼
spring.datasource.password=your_mysql_password
```

其他設定不用動。

#### 3. 裝前端套件

```bash
cd frontend
npm install
```

### 啟動

#### 後端
```bash
./gradlew bootRun          # Mac/Linux
gradlew.bat bootRun        # Windows
```

後端跑在 `http://localhost:8080`

#### 前端（開新終端機）
```bash
cd frontend
npm run dev
```

前端跑在 `http://localhost:5173`

### 完成
打開 `http://localhost:5173` 就能用了

**測試資料說明：**

DML.sql 會自動建立以下測試資料：

## 測試帳號

系統已預設建立測試使用者（密碼為示例，實際需要經過雜湊）：

| 手機號碼 | 姓名 |
| 0912345678 | 張小明 |
| 0923456789 | 李美華 |
| 0934567890 | 王大衛 |



## 安全性

### SQL Injection
- 全部用 Stored Procedure
- 參數化查詢（PreparedStatement）
- JPA Native Query 也有參數綁定

### XSS
- 輸入都會過濾（sanitizeInput）
- 擋掉危險字元（`<`, `>`, `"`, `'`, `/`）

### 密碼
- SHA-256 雜湊
- 每個密碼有自己的 Salt
- 不存明文

### JWT Token
- Token 裡有使用者資訊
- 預設 24 小時過期
- 每次請求都驗證

### Transaction
- 借還書用 Database Transaction
- 保證資料一致
- 失敗會 rollback

## 常見問題

### 中文亂碼
建資料庫時要加 `--default-character-set=utf8mb4`

### 連不上
檢查 `application.properties` 裡的 MySQL 密碼對不對

### Port 被佔用
改 `application.properties` 的 `server.port=8080` 換個 port

### Stored Procedure 錯誤
重跑 `DB/DDL.sql`

更多問題看 `DB/README_DATABASE_SETUP.md`

## 技術細節

### 資料庫
- 用 `utf8mb4_unicode_ci`，支援中文和 emoji
- MySQL Connector/J 9.x 用 `connectionCollation` 參數
- 7 個 Stored Procedures 處理核心功能
- 有建索引優化查詢

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

