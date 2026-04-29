-- =====================================================
-- 圖書借閱系統 資料庫 DML (測試資料)
-- =====================================================

-- 設定字元編碼
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 1. 插入書籍資料
INSERT INTO `book` (isbn, name, author, introduction, publisher, publish_date, category) VALUES
('978-9571372853', '原子習慣', 'James Clear', '每天都進步1%，一年後，你會進步37倍；每天都退步1%，一年後，你會弱化到趨近於0！', '方智', '2019-06-01', '自我成長'),
('978-9863206552', '被討厭的勇氣', '岸見一郎, 古賀史健', '所謂的自由，就是被別人討厭。', '究竟', '2014-10-30', '心理學'),
('978-9571374642', '人生4千個禮拜', 'Oliver Burkeman', '時間不夠用，是因為你用錯了方法', '遠流', '2022-01-27', '時間管理'),
('978-9863983767', 'Clean Code', 'Robert C. Martin', 'A Handbook of Agile Software Craftsmanship', '博碩文化', '2018-12-24', '程式設計'),
('978-9864765041', 'Java SE 17技術手冊', '林信良', 'Java SE 17最新版本，完整涵蓋物件導向、Lambda、Stream、模組等重要觀念', '碁峰資訊', '2022-03-31', '程式設計'),
('978-9863126478', 'Spring Boot建置與執行', 'Mark Heckler', '使用Spring Boot建立雲端原生Java和Kotlin應用程式', '歐萊禮', '2021-08-27', '程式設計'),
('978-9571382876', '零規則', 'Reed Hastings, Erin Meyer', 'Netflix創辦人的創新管理學', '天下雜誌', '2020-10-29', '管理'),
('978-9865250171', '設計模式的解析與活用', 'Alan Shalloway, James R. Trott', 'Design Patterns Explained: A New Perspective on Object-Oriented Design', '博碩文化', '2020-06-30', '程式設計'),
('978-9863478850', 'Effective Java中文版', 'Joshua Bloch', 'Java專家必讀經典', '碁峰資訊', '2019-04-29', '程式設計'),
('978-9864344727', '重構：改善既有程式的設計', 'Martin Fowler', 'Refactoring: Improving the Design of Existing Code', '碁峰資訊', '2019-08-30', '程式設計');

-- 2. 插入庫存資料
INSERT INTO `inventory` (isbn, store_time, status, location) VALUES
-- 原子習慣 (3本)
('978-9571372853', '2024-01-15 10:00:00', 'AVAILABLE', 'A1-01'),
('978-9571372853', '2024-01-15 10:00:00', 'AVAILABLE', 'A1-02'),
('978-9571372853', '2024-01-20 14:30:00', 'AVAILABLE', 'A1-03'),

-- 被討厭的勇氣 (2本)
('978-9863206552', '2024-01-16 09:00:00', 'AVAILABLE', 'A1-04'),
('978-9863206552', '2024-01-16 09:00:00', 'AVAILABLE', 'A1-05'),

-- 人生4千個禮拜 (2本)
('978-9571374642', '2024-02-01 11:00:00', 'AVAILABLE', 'A1-06'),
('978-9571374642', '2024-02-01 11:00:00', 'AVAILABLE', 'A1-07'),

-- Clean Code (3本)
('978-9863983767', '2024-01-10 08:30:00', 'AVAILABLE', 'B2-01'),
('978-9863983767', '2024-01-10 08:30:00', 'AVAILABLE', 'B2-02'),
('978-9863983767', '2024-02-15 16:00:00', 'AVAILABLE', 'B2-03'),

-- Java SE 17技術手冊 (2本)
('978-9864765041', '2024-02-20 10:00:00', 'AVAILABLE', 'B2-04'),
('978-9864765041', '2024-02-20 10:00:00', 'AVAILABLE', 'B2-05'),

-- Spring Boot建置與執行 (2本)
('978-9863126478', '2024-03-01 09:00:00', 'AVAILABLE', 'B2-06'),
('978-9863126478', '2024-03-01 09:00:00', 'AVAILABLE', 'B2-07'),

-- 零規則 (2本)
('978-9571382876', '2024-02-10 14:00:00', 'AVAILABLE', 'A2-01'),
('978-9571382876', '2024-02-10 14:00:00', 'AVAILABLE', 'A2-02'),

-- 設計模式的解析與活用 (1本)
('978-9865250171', '2024-03-05 10:30:00', 'AVAILABLE', 'B2-08'),

-- Effective Java中文版 (2本)
('978-9863478850', '2024-01-25 11:00:00', 'AVAILABLE', 'B2-09'),
('978-9863478850', '2024-01-25 11:00:00', 'AVAILABLE', 'B2-10'),

-- 重構：改善既有程式的設計 (2本)
('978-9864344727', '2024-02-28 15:00:00', 'AVAILABLE', 'B2-11'),
('978-9864344727', '2024-02-28 15:00:00', 'AVAILABLE', 'B2-12');

-- 3. 插入測試使用者 (密碼都是 "password123" 經過加鹽雜湊後的結果，實際應用時會在程式中處理)
-- 注意：以下password和salt為示例，實際部署時應該由程式產生
INSERT INTO `user` (phone_number, password, salt, user_name, registration_time, is_active) VALUES
('0912345678', 'hashed_password_1', 'salt_1', '張小明', '2024-01-10 09:00:00', 1),
('0923456789', 'hashed_password_2', 'salt_2', '李美華', '2024-01-15 10:30:00', 1),
('0934567890', 'hashed_password_3', 'salt_3', '王大衛', '2024-02-01 14:00:00', 1),
('0945678901', 'hashed_password_4', 'salt_4', '陳雅婷', '2024-02-10 11:20:00', 1),
('0956789012', 'hashed_password_5', 'salt_5', '林志明', '2024-03-01 16:00:00', 1);

-- 4. 插入借閱紀錄範例 (部分已歸還，部分未歸還)
-- 使用者1 (張小明) 的借閱紀錄
INSERT INTO `borrowing_record` (user_id, inventory_id, borrowing_time, due_date, return_time, is_overdue, overdue_days, fine_amount)
VALUES
(1, 1, '2024-03-01 10:00:00', '2024-03-15 23:59:59', '2024-03-14 15:30:00', 0, 0, 0.00),
(1, 8, '2024-03-20 11:00:00', '2024-04-03 23:59:59', NULL, 0, 0, 0.00);

-- 更新庫存狀態 (inventory_id = 8 為出借中)
UPDATE `inventory` SET status = 'BORROWED' WHERE inventory_id = 8;

-- 使用者2 (李美華) 的借閱紀錄
INSERT INTO `borrowing_record` (user_id, inventory_id, borrowing_time, due_date, return_time, is_overdue, overdue_days, fine_amount)
VALUES
(2, 4, '2024-03-10 14:00:00', '2024-03-24 23:59:59', '2024-03-22 10:00:00', 0, 0, 0.00),
(2, 11, '2024-03-25 09:30:00', '2024-04-08 23:59:59', NULL, 0, 0, 0.00);

-- 更新庫存狀態 (inventory_id = 11 為出借中)
UPDATE `inventory` SET status = 'BORROWED' WHERE inventory_id = 11;

-- 使用者3 (王大衛) 的借閱紀錄 (有逾期紀錄)
INSERT INTO `borrowing_record` (user_id, inventory_id, borrowing_time, due_date, return_time, is_overdue, overdue_days, fine_amount)
VALUES
(3, 19, '2024-02-20 13:00:00', '2024-03-05 23:59:59', '2024-03-10 16:00:00', 1, 5, 50.00);

-- 5. 測試 Stored Procedure 的範例 SQL
-- 以下為註解說明，實際使用時需在程式中呼叫

-- 測試註冊使用者
-- CALL sp_register_user('0967890123', 'hashed_pwd', 'salt_value', '測試使用者', @user_id, @result_code, @result_msg);
-- SELECT @user_id, @result_code, @result_msg;

-- 測試使用者登入
-- CALL sp_user_login('0912345678', @user_id, @password, @salt, @user_name, @is_active, @result_code, @result_msg);
-- SELECT @user_id, @password, @salt, @user_name, @is_active, @result_code, @result_msg;

-- 測試借書
-- CALL sp_borrow_book(1, 2, 14, @record_id, @result_code, @result_msg);
-- SELECT @record_id, @result_code, @result_msg;

-- 測試還書
-- CALL sp_return_book(1, @result_code, @result_msg);
-- SELECT @result_code, @result_msg;

-- 測試查詢使用者借閱紀錄
-- CALL sp_get_user_borrowing_records(1, NULL);  -- 查詢全部
-- CALL sp_get_user_borrowing_records(1, 0);     -- 查詢未歸還
-- CALL sp_get_user_borrowing_records(1, 1);     -- 查詢已歸還

-- 測試查詢可借閱書籍
-- CALL sp_get_available_books('Java');  -- 搜尋關鍵字
-- CALL sp_get_available_books(NULL);    -- 查詢全部

-- 測試查詢特定書籍的可借閱庫存
-- CALL sp_get_available_inventory_by_isbn('978-9864765041');
