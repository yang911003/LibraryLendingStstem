# 資料庫建置說明

## 📋 建置步驟

資料庫已成功建立並解決編碼問題!

### 1️⃣ 建立資料庫
```bash
mysql -u root -p --default-character-set=utf8mb4 < setup_database.sql
```

### 2️⃣ 建立資料表和 Stored Procedures
```bash
mysql -u root -p --default-character-set=utf8mb4 library_lending_system < DDL.sql
```

### 3️⃣ 插入測試資料
```bash
mysql -u root -p --default-character-set=utf8mb4 library_lending_system < DML.sql
```

### 或者一次執行所有腳本
```bash
mysql -u root -p --default-character-set=utf8mb4 < run_all.sql
```

## ✅ 已完成的編碼設定

### 資料庫層級
- 資料庫使用 `utf8mb4` 字元集
- 資料庫使用 `utf8mb4_unicode_ci` 排序規則
- 所有資料表都使用 `utf8mb4_unicode_ci`

### JDBC 連線層級
application.properties 已更新:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/library_lending_system?useSSL=false&serverTimezone=Asia/Taipei&allowPublicKeyRetrieval=true&characterEncoding=utf8mb4&useUnicode=true
```

關鍵參數:
- `characterEncoding=utf8mb4` - 設定連線字元編碼
- `useUnicode=true` - 啟用 Unicode 支援

## 🧪 測試結果

已測試註冊功能並驗證中文儲存:
- ✅ 註冊使用者名稱: "測試使用者王小明"
- ✅ 資料正確儲存並可正常查詢
- ✅ 無亂碼問題

## 📊 資料庫統計

執行成功後應包含:
- 4 個資料表: user, book, inventory, borrowing_record
- 7 個 Stored Procedures
- 10 種書籍
- 23 個庫存項目
- 5 個測試使用者

## 🔧 問題排查

如果仍遇到編碼問題:

1. **檢查 MySQL 伺服器編碼**
```sql
SHOW VARIABLES LIKE 'character%';
```

2. **檢查資料表編碼**
```sql
SELECT table_name, table_collation
FROM information_schema.tables
WHERE table_schema = 'library_lending_system';
```

3. **檢查欄位編碼**
```sql
SELECT column_name, character_set_name, collation_name
FROM information_schema.columns
WHERE table_schema = 'library_lending_system'
AND table_name = 'user';
```

## 📝 注意事項

1. 請確保 MySQL 命令列使用 `--default-character-set=utf8mb4`
2. JDBC 連線字串必須包含 `characterEncoding=utf8mb4&useUnicode=true`
3. 所有 SQL 檔案頂部都有 `SET NAMES utf8mb4;`
