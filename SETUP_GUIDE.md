# 圖書借閱系統 - 執行指南

## 步驟 1: 安裝必要軟體

### 1.1 安裝 MySQL
1. 下載 MySQL 8.0+: https://dev.mysql.com/downloads/mysql/
2. 安裝並記住你設定的 root 密碼

### 1.2 安裝 JDK 17
1. 下載 JDK 17: https://www.oracle.com/java/technologies/downloads/#java17
2. 安裝後驗證：
   ```bash
   java -version
   ```
   應該顯示 java version "17.x.x"

### 1.3 安裝 Node.js
1. 下載 Node.js 18+: https://nodejs.org/
2. 安裝後驗證：
   ```bash
   node -version
   npm -version
   ```

## 步驟 2: 建立並初始化資料庫

### 方法 1: 使用命令列 (推薦)

```bash
# Windows: 開啟命令提示字元 (cmd)
# 進入 MySQL
mysql -u root -p

# 輸入你的 MySQL 密碼後，執行以下 SQL 指令：
```

```sql
-- 建立資料庫
CREATE DATABASE library_lending_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用資料庫
USE library_lending_system;

-- 執行 DDL (建立資料表和 Stored Procedures)
SOURCE C:/Users/Yueh Hsiang/IdeaProjects/LibraryLendingSystem/DB/DDL.sql;

-- 執行 DML (插入測試資料)
SOURCE C:/Users/Yueh Hsiang/IdeaProjects/LibraryLendingSystem/DB/DML.sql;

-- 驗證資料是否建立成功
SHOW TABLES;
SELECT COUNT(*) FROM book;
SELECT COUNT(*) FROM inventory;
```

### 方法 2: 使用 MySQL Workbench (圖形化介面)

1. 開啟 MySQL Workbench
2. 連線到你的 MySQL Server
3. 建立新的 Schema (資料庫)：
   - 右鍵點擊 "Schemas" → "Create Schema"
   - 名稱輸入：`library_lending_system`
   - Charset: `utf8mb4`
   - Collation: `utf8mb4_unicode_ci`
   - 點擊 "Apply"
4. 執行 DDL.sql：
   - File → Open SQL Script
   - 選擇 `DB/DDL.sql`
   - 點擊 ⚡ 執行
5. 執行 DML.sql：
   - File → Open SQL Script
   - 選擇 `DB/DML.sql`
   - 點擊 ⚡ 執行

## 步驟 3: 設定後端配置

編輯檔案：`src/main/resources/application.properties`

```properties
# 修改以下設定
spring.datasource.username=root
spring.datasource.password=你的MySQL密碼

# JWT密鑰建議修改為隨機字串（保持現有也可以）
jwt.secret=your-256-bit-secret-key-change-this-in-production-environment-for-security
```

## 步驟 4: 啟動後端

### 方法 1: 使用 IntelliJ IDEA (推薦)

1. 開啟 IntelliJ IDEA
2. 開啟專案資料夾：`LibraryLendingSystem`
3. 等待 Gradle 自動下載依賴 (右下角會顯示進度)
4. 找到 `LibraryLendingSystemApplication.java`
5. 右鍵點擊檔案 → "Run 'LibraryLendingSystemApplication'"

### 方法 2: 使用命令列

```bash
# Windows PowerShell 或 命令提示字元
cd C:\Users\Yueh Hsiang\IdeaProjects\LibraryLendingSystem

# 執行 Gradle 建置並啟動
gradlew.bat bootRun

# 或者先建置再執行
gradlew.bat build
java -jar build\libs\LibraryLendingSystem-0.0.1-SNAPSHOT.jar
```

### 驗證後端是否啟動成功

看到以下訊息表示成功：
```
Started LibraryLendingSystemApplication in X.XXX seconds
```

後端會在 `http://localhost:8080/api` 啟動

## 步驟 5: 安裝並啟動前端

```bash
# 開啟新的命令提示字元視窗
cd C:\Users\Yueh Hsiang\IdeaProjects\LibraryLendingSystem\frontend

# 安裝依賴 (第一次執行需要，之後不用)
npm install

# 啟動開發伺服器
npm run dev
```

看到以下訊息表示成功：
```
VITE v5.x.x  ready in xxx ms

➜  Local:   http://localhost:5173/
➜  Network: use --host to expose
```

## 步驟 6: 開啟瀏覽器使用系統

開啟瀏覽器，輸入網址：`http://localhost:5173`

### 測試流程

1. **註冊新帳號**
   - 點擊「立即註冊」
   - 輸入姓名、手機號碼（09開頭的10位數字）、密碼
   - 註冊成功後會自動跳轉到登入頁面

2. **登入系統**
   - 輸入剛才註冊的手機號碼和密碼
   - 登入成功後會跳轉到書籍列表頁面

3. **瀏覽書籍**
   - 可以搜尋書名或作者
   - 查看可借閱數量

4. **借閱書籍**
   - 點擊「借閱」按鈕
   - 選擇要借的庫存
   - 設定借閱天數（預設14天）
   - 確認借閱

5. **查看借閱紀錄**
   - 點擊導覽列的「我的借閱」
   - 查看「借閱中」的書籍
   - 查看「歷史紀錄」

6. **歸還書籍**
   - 在「借閱中」頁面
   - 點擊「歸還書籍」按鈕

## 常見問題排除

### Q1: MySQL 連線失敗
**錯誤訊息**: `Communications link failure`

**解決方法**:
1. 確認 MySQL 服務是否啟動
2. 檢查 `application.properties` 的密碼是否正確
3. 檢查防火牆是否阻擋 3306 port

### Q2: Port 8080 已被占用
**錯誤訊息**: `Port 8080 was already in use`

**解決方法**:
```properties
# 修改 application.properties
server.port=8081
```

### Q3: Gradle 下載依賴很慢
**解決方法**:
在 `build.gradle` 的 `repositories` 區塊改用阿里雲鏡像：
```gradle
repositories {
    maven { url 'https://maven.aliyun.com/repository/public/' }
    mavenCentral()
}
```

### Q4: npm install 很慢
**解決方法**:
```bash
# 使用淘寶鏡像
npm install --registry=https://registry.npmmirror.com
```

### Q5: 前端無法連接後端 API
**解決方法**:
1. 確認後端是否在 `http://localhost:8080` 執行
2. 檢查瀏覽器 Console 是否有 CORS 錯誤
3. 確認 `SecurityConfig.java` 的 CORS 設定

### Q6: Stored Procedure 執行失敗
**錯誤訊息**: `Procedure not found`

**解決方法**:
```sql
-- 重新執行 DDL.sql
USE library_lending_system;
SOURCE C:/Users/Yueh Hsiang/IdeaProjects/LibraryLendingSystem/DB/DDL.sql;
```

## 停止服務

### 停止後端
- IntelliJ IDEA: 點擊紅色方塊停止按鈕
- 命令列: 按 `Ctrl + C`

### 停止前端
- 命令列: 按 `Ctrl + C`

## 重新啟動

如果需要重新啟動，只需重複步驟 4 和步驟 5 即可（不需要重新建立資料庫）。

## 需要幫助？

如果遇到其他問題，請檢查：
1. 後端 Console 的錯誤訊息
2. 前端瀏覽器 Console 的錯誤訊息
3. MySQL 的錯誤日誌

或參考 README.md 的詳細文件。
