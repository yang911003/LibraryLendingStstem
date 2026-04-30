-- =====================================================
-- 圖書借閱系統 - 資料庫初始化腳本
-- 此腳本會建立資料庫並設定正確的字元編碼
-- =====================================================

-- 刪除舊資料庫 (如果存在)
DROP DATABASE IF EXISTS `library_lending_system`;

-- 建立資料庫，明確指定使用 UTF8MB4 編碼
CREATE DATABASE `library_lending_system`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- 使用新建立的資料庫
USE `library_lending_system`;

-- 設定字元編碼 (確保這個連線使用正確的編碼)
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;
SET collation_connection = utf8mb4_unicode_ci;

-- 顯示資料庫編碼設定 (用於確認)
SELECT
    DEFAULT_CHARACTER_SET_NAME,
    DEFAULT_COLLATION_NAME
FROM information_schema.SCHEMATA
WHERE SCHEMA_NAME = 'library_lending_system';

-- 提示訊息
SELECT '資料庫已成功建立，使用 UTF8MB4 編碼' AS message;
