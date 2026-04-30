-- =====================================================
-- 完整資料庫建置腳本 - 依序執行所有SQL
-- =====================================================

-- 步驟 1: 建立資料庫並設定編碼
SOURCE setup_database.sql;

-- 步驟 2: 建立資料表和 Stored Procedures
SOURCE DDL.sql;

-- 步驟 3: 插入測試資料
SOURCE DML.sql;

-- 完成提示
SELECT '✓ 資料庫建置完成!' AS message;
SELECT '✓ 所有資料表已建立' AS message;
SELECT '✓ 測試資料已插入' AS message;

-- 顯示資料庫統計
SELECT
    '資料庫統計' AS category,
    (SELECT COUNT(*) FROM user) AS '使用者數量',
    (SELECT COUNT(*) FROM book) AS '書籍種類',
    (SELECT COUNT(*) FROM inventory) AS '庫存總數',
    (SELECT COUNT(*) FROM borrowing_record) AS '借閱紀錄';
