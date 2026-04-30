# 變更紀錄 - 2026-04-30

## 🎯 主要目標
解決註冊功能的中文編碼問題，確保使用者可以正常註冊包含中文的使用者名稱。

## ✅ 完成的變更

### 1. 資料庫層級修正

#### 新增檔案
- `DB/setup_database.sql` - 資料庫初始化腳本，明確設定 UTF8MB4 編碼
- `DB/run_all.sql` - 一鍵執行所有資料庫建置腳本
- `DB/README_DATABASE_SETUP.md` - 詳細的資料庫建置說明與問題排查

#### 資料庫編碼設定
```sql
CREATE DATABASE `library_lending_system`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

- ✅ 資料庫使用 `utf8mb4` 字元集
- ✅ 所有資料表使用 `utf8mb4_unicode_ci` 排序規則
- ✅ 支援完整的 Unicode 字元（中文、emoji 等）

### 2. JDBC 連線設定修正

#### 修改檔案
`src/main/resources/application.properties`

#### 變更內容
**修改前**:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/library_lending_system?useSSL=false&serverTimezone=Asia/Taipei&allowPublicKeyRetrieval=true
```

**修改後**:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/library_lending_system?useSSL=false&serverTimezone=Asia/Taipei&allowPublicKeyRetrieval=true&connectionCollation=utf8mb4_unicode_ci
```

#### 關鍵變更
- ✅ 新增 `connectionCollation=utf8mb4_unicode_ci` 參數
- ✅ 適配 MySQL Connector/J 9.x 版本（不再使用 `characterEncoding` 參數）

### 3. Hibernate 配置修正

#### 修改檔案
`src/main/resources/application.properties`

#### 變更內容
**修改前**:
```properties
spring.jpa.hibernate.ddl-auto=create
```

**修改後**:
```properties
spring.jpa.hibernate.ddl-auto=none
```

#### 原因
- 避免 Hibernate 自動重建資料表
- 使用我們手動建立的資料庫結構（包含 Stored Procedures）
- 防止與 Stored Procedures 衝突

### 4. 文檔更新

#### 修改檔案
- `README.md` - 新增編碼問題解決方案與常見問題排查

#### 新增內容
- ✅ 最近更新說明區塊
- ✅ 資料庫建置步驟的詳細說明
- ✅ MySQL Connector/J 9.x 的參數變更說明
- ✅ 常見問題排查章節（包含 3 個主要問題）
- ✅ 編碼設定的重要提示

## 🧪 測試驗證

### 測試項目
1. ✅ 資料庫建立成功（使用 UTF8MB4 編碼）
2. ✅ 資料表建立成功（4 個主要資料表）
3. ✅ Stored Procedures 建立成功（7 個程序）
4. ✅ 測試資料插入成功
5. ✅ 中文註冊功能測試通過（使用者名稱：測試使用者王小明）
6. ✅ 資料正確儲存且無亂碼

### 測試結果
```sql
-- 測試註冊
CALL sp_register_user('0900000001', 'test_password', 'test_salt', '測試使用者王小明', @user_id, @result_code, @result_msg);
-- 結果: 註冊成功 (result_code = 0)

-- 驗證資料
SELECT user_id, phone_number, user_name FROM user WHERE user_id = 6;
-- 結果: 使用者名稱正確顯示為 "測試使用者王小明"（無亂碼）
```

## 📋 資料庫建置流程

### 完整步驟
```bash
# 步驟 1: 建立資料庫
mysql -u root -p --default-character-set=utf8mb4 < DB/setup_database.sql

# 步驟 2: 建立資料表和 SP
mysql -u root -p --default-character-set=utf8mb4 library_lending_system < DB/DDL.sql

# 步驟 3: 插入測試資料
mysql -u root -p --default-character-set=utf8mb4 library_lending_system < DB/DML.sql
```

### 一鍵建置
```bash
mysql -u root -p --default-character-set=utf8mb4 < DB/run_all.sql
```

## 🔧 關鍵技術點

### MySQL Connector/J 版本差異
| 版本 | 編碼參數 |
|------|---------|
| 8.x 及更早 | `characterEncoding=utf8mb4&useUnicode=true` |
| 9.x 及以後 | `connectionCollation=utf8mb4_unicode_ci` |

**注意**: 使用錯誤的參數會導致 `Unsupported character encoding` 錯誤

### Hibernate DDL Auto 模式
| 模式 | 說明 | 使用時機 |
|------|------|---------|
| `create` | 每次啟動時刪除並重建資料表 | 開發初期 |
| `update` | 自動更新資料表結構 | 開發中期 |
| `validate` | 驗證資料表結構 | 生產環境 |
| `none` | 不做任何處理 | 使用 Stored Procedures 時 |

## 🚀 啟動應用程式

### 後端啟動
```bash
./gradlew bootRun
```

### 前端啟動
```bash
cd frontend
npm install
npm run dev
```

## 📝 注意事項

### 生產環境部署
1. 修改 `jwt.secret` 為安全的隨機字串
2. 修改資料庫密碼（不要使用預設密碼）
3. 將 `spring.jpa.hibernate.ddl-auto` 改為 `validate`
4. 啟用 HTTPS
5. 設定適當的 CORS 允許來源

### 編碼設定檢查清單
- [ ] 資料庫使用 `utf8mb4` 字元集
- [ ] 資料表使用 `utf8mb4_unicode_ci` 排序規則
- [ ] JDBC 連線字串包含 `connectionCollation=utf8mb4_unicode_ci`
- [ ] MySQL 命令列使用 `--default-character-set=utf8mb4`

## 🔗 相關檔案

- `DB/setup_database.sql` - 資料庫初始化
- `DB/DDL.sql` - 資料表結構定義
- `DB/DML.sql` - 測試資料
- `DB/run_all.sql` - 一鍵執行腳本
- `DB/README_DATABASE_SETUP.md` - 詳細建置說明
- `README.md` - 專案主要文檔
- `src/main/resources/application.properties` - 應用程式配置

## 📊 資料庫統計

建置完成後的資料庫包含:
- 4 個資料表: `user`, `book`, `inventory`, `borrowing_record`
- 7 個 Stored Procedures
- 10 種書籍資料
- 23 個庫存項目
- 5 個測試使用者

---

**變更完成日期**: 2026-04-30
**測試狀態**: ✅ 通過
**中文支援**: ✅ 正常運作
