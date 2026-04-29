-- =====================================================
-- 圖書借閱系統 資料庫 DDL
-- =====================================================

-- 1. 使用者表 (User)
CREATE TABLE `user` (
    `user_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '使用者ID',
    `phone_number` VARCHAR(20) NOT NULL UNIQUE COMMENT '手機號碼 (登入帳號)',
    `password` VARCHAR(255) NOT NULL COMMENT '密碼 (加鹽雜湊後)',
    `salt` VARCHAR(64) NOT NULL COMMENT '密碼鹽值',
    `user_name` VARCHAR(100) NOT NULL COMMENT '使用者名稱',
    `registration_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '註冊日期時間',
    `last_login_time` DATETIME NULL COMMENT '最後登入時間',
    `is_active` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '帳號是否啟用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_phone_number` (`phone_number`),
    INDEX `idx_last_login` (`last_login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='使用者表';

-- 2. 書籍表 (Book)
CREATE TABLE `book` (
    `isbn` VARCHAR(17) PRIMARY KEY COMMENT '國際標準書號 (ISBN-13)',
    `name` VARCHAR(255) NOT NULL COMMENT '書名',
    `author` VARCHAR(200) NOT NULL COMMENT '作者',
    `introduction` TEXT COMMENT '書籍內容簡介',
    `publisher` VARCHAR(200) COMMENT '出版社',
    `publish_date` DATE COMMENT '出版日期',
    `category` VARCHAR(100) COMMENT '分類',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_name` (`name`),
    INDEX `idx_author` (`author`),
    INDEX `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='書籍表';

-- 3. 庫存表 (Inventory)
CREATE TABLE `inventory` (
    `inventory_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '庫存ID',
    `isbn` VARCHAR(17) NOT NULL COMMENT '國際標準書號',
    `store_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '書籍入庫日期時間',
    `status` ENUM('AVAILABLE', 'BORROWED', 'PROCESSING', 'LOST', 'DAMAGED', 'DISCARDED')
        NOT NULL DEFAULT 'AVAILABLE' COMMENT '書籍狀態: 在庫、出借中、整理中、遺失、損毀、廢棄',
    `location` VARCHAR(50) COMMENT '書籍存放位置',
    `condition_note` TEXT COMMENT '書籍狀態備註',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`isbn`) REFERENCES `book`(`isbn`) ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX `idx_isbn` (`isbn`),
    INDEX `idx_status` (`status`),
    INDEX `idx_store_time` (`store_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='庫存表';

-- 4. 借閱紀錄表 (Borrowing Record)
CREATE TABLE `borrowing_record` (
    `record_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '借閱紀錄ID',
    `user_id` BIGINT NOT NULL COMMENT '使用者ID',
    `inventory_id` BIGINT NOT NULL COMMENT '庫存ID',
    `borrowing_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '借出日期時間',
    `due_date` DATETIME NOT NULL COMMENT '應還日期',
    `return_time` DATETIME NULL COMMENT '歸還日期時間',
    `is_overdue` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否逾期',
    `overdue_days` INT DEFAULT 0 COMMENT '逾期天數',
    `fine_amount` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '罰款金額',
    `note` TEXT COMMENT '備註',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (`inventory_id`) REFERENCES `inventory`(`inventory_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_inventory_id` (`inventory_id`),
    INDEX `idx_borrowing_time` (`borrowing_time`),
    INDEX `idx_return_time` (`return_time`),
    INDEX `idx_user_return` (`user_id`, `return_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='借閱紀錄表';

-- =====================================================
-- 建立 Stored Procedures
-- =====================================================

-- SP1: 使用者註冊
DELIMITER $$
CREATE PROCEDURE sp_register_user(
    IN p_phone_number VARCHAR(20),
    IN p_password VARCHAR(255),
    IN p_salt VARCHAR(64),
    IN p_user_name VARCHAR(100),
    OUT p_user_id BIGINT,
    OUT p_result_code INT,
    OUT p_result_message VARCHAR(255)
)
BEGIN
    DECLARE v_count INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result_code = -1;
        SET p_result_message = '註冊失敗：系統錯誤';
    END;

    START TRANSACTION;

    -- 檢查手機號碼是否已存在
    SELECT COUNT(*) INTO v_count FROM `user` WHERE phone_number = p_phone_number;

    IF v_count > 0 THEN
        SET p_result_code = 1;
        SET p_result_message = '此手機號碼已被註冊';
        ROLLBACK;
    ELSE
        INSERT INTO `user` (phone_number, password, salt, user_name, registration_time)
        VALUES (p_phone_number, p_password, p_salt, p_user_name, NOW());

        SET p_user_id = LAST_INSERT_ID();
        SET p_result_code = 0;
        SET p_result_message = '註冊成功';
        COMMIT;
    END IF;
END$$
DELIMITER ;

-- SP2: 使用者登入驗證
DELIMITER $$
CREATE PROCEDURE sp_user_login(
    IN p_phone_number VARCHAR(20),
    OUT p_user_id BIGINT,
    OUT p_password VARCHAR(255),
    OUT p_salt VARCHAR(64),
    OUT p_user_name VARCHAR(100),
    OUT p_is_active TINYINT,
    OUT p_result_code INT,
    OUT p_result_message VARCHAR(255)
)
BEGIN
    DECLARE v_count INT;

    SELECT COUNT(*) INTO v_count FROM `user` WHERE phone_number = p_phone_number;

    IF v_count = 0 THEN
        SET p_result_code = 1;
        SET p_result_message = '使用者不存在';
    ELSE
        SELECT user_id, password, salt, user_name, is_active
        INTO p_user_id, p_password, p_salt, p_user_name, p_is_active
        FROM `user`
        WHERE phone_number = p_phone_number;

        IF p_is_active = 0 THEN
            SET p_result_code = 2;
            SET p_result_message = '帳號已被停用';
        ELSE
            -- 更新最後登入時間
            UPDATE `user` SET last_login_time = NOW() WHERE user_id = p_user_id;
            SET p_result_code = 0;
            SET p_result_message = '登入成功';
        END IF;
    END IF;
END$$
DELIMITER ;

-- SP3: 借書 (使用Transaction確保資料完整性)
DELIMITER $$
CREATE PROCEDURE sp_borrow_book(
    IN p_user_id BIGINT,
    IN p_inventory_id BIGINT,
    IN p_due_days INT,
    OUT p_record_id BIGINT,
    OUT p_result_code INT,
    OUT p_result_message VARCHAR(255)
)
BEGIN
    DECLARE v_status VARCHAR(20);
    DECLARE v_user_active TINYINT;
    DECLARE v_due_date DATETIME;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result_code = -1;
        SET p_result_message = '借書失敗：系統錯誤';
    END;

    START TRANSACTION;

    -- 檢查使用者是否啟用
    SELECT is_active INTO v_user_active FROM `user` WHERE user_id = p_user_id FOR UPDATE;
    IF v_user_active = 0 THEN
        SET p_result_code = 1;
        SET p_result_message = '帳號已被停用';
        ROLLBACK;
    ELSE
        -- 鎖定庫存記錄並檢查書籍狀態
        SELECT status INTO v_status FROM inventory WHERE inventory_id = p_inventory_id FOR UPDATE;

        IF v_status IS NULL THEN
            SET p_result_code = 2;
            SET p_result_message = '書籍不存在';
            ROLLBACK;
        ELSEIF v_status != 'AVAILABLE' THEN
            SET p_result_code = 3;
            SET p_result_message = '書籍無法借閱';
            ROLLBACK;
        ELSE
            -- 計算到期日
            SET v_due_date = DATE_ADD(NOW(), INTERVAL p_due_days DAY);

            -- 更新庫存狀態
            UPDATE inventory
            SET status = 'BORROWED', updated_at = NOW()
            WHERE inventory_id = p_inventory_id;

            -- 新增借閱紀錄
            INSERT INTO borrowing_record (user_id, inventory_id, borrowing_time, due_date)
            VALUES (p_user_id, p_inventory_id, NOW(), v_due_date);

            SET p_record_id = LAST_INSERT_ID();
            SET p_result_code = 0;
            SET p_result_message = '借書成功';
            COMMIT;
        END IF;
    END IF;
END$$
DELIMITER ;

-- SP4: 還書 (使用Transaction確保資料完整性)
DELIMITER $$
CREATE PROCEDURE sp_return_book(
    IN p_record_id BIGINT,
    OUT p_result_code INT,
    OUT p_result_message VARCHAR(255)
)
BEGIN
    DECLARE v_inventory_id BIGINT;
    DECLARE v_return_time DATETIME;
    DECLARE v_due_date DATETIME;
    DECLARE v_overdue_days INT;
    DECLARE v_fine_amount DECIMAL(10, 2);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result_code = -1;
        SET p_result_message = '還書失敗：系統錯誤';
    END;

    START TRANSACTION;

    -- 檢查借閱紀錄並鎖定
    SELECT inventory_id, return_time, due_date
    INTO v_inventory_id, v_return_time, v_due_date
    FROM borrowing_record
    WHERE record_id = p_record_id FOR UPDATE;

    IF v_inventory_id IS NULL THEN
        SET p_result_code = 1;
        SET p_result_message = '借閱紀錄不存在';
        ROLLBACK;
    ELSEIF v_return_time IS NOT NULL THEN
        SET p_result_code = 2;
        SET p_result_message = '此書已歸還';
        ROLLBACK;
    ELSE
        -- 計算逾期天數和罰款
        SET v_overdue_days = DATEDIFF(NOW(), v_due_date);
        IF v_overdue_days > 0 THEN
            SET v_fine_amount = v_overdue_days * 10.00; -- 每天罰款10元
        ELSE
            SET v_overdue_days = 0;
            SET v_fine_amount = 0.00;
        END IF;

        -- 更新借閱紀錄
        UPDATE borrowing_record
        SET return_time = NOW(),
            is_overdue = IF(v_overdue_days > 0, 1, 0),
            overdue_days = v_overdue_days,
            fine_amount = v_fine_amount,
            updated_at = NOW()
        WHERE record_id = p_record_id;

        -- 更新庫存狀態
        UPDATE inventory
        SET status = 'PROCESSING', updated_at = NOW()
        WHERE inventory_id = v_inventory_id;

        SET p_result_code = 0;
        SET p_result_message = '還書成功';
        COMMIT;
    END IF;
END$$
DELIMITER ;

-- SP5: 查詢使用者借閱紀錄
DELIMITER $$
CREATE PROCEDURE sp_get_user_borrowing_records(
    IN p_user_id BIGINT,
    IN p_is_returned TINYINT  -- 0: 未歸還, 1: 已歸還, NULL: 全部
)
BEGIN
    SELECT
        br.record_id,
        br.user_id,
        br.inventory_id,
        b.isbn,
        b.name AS book_name,
        b.author,
        br.borrowing_time,
        br.due_date,
        br.return_time,
        br.is_overdue,
        br.overdue_days,
        br.fine_amount,
        i.status AS inventory_status
    FROM borrowing_record br
    INNER JOIN inventory i ON br.inventory_id = i.inventory_id
    INNER JOIN book b ON i.isbn = b.isbn
    WHERE br.user_id = p_user_id
      AND (p_is_returned IS NULL
           OR (p_is_returned = 0 AND br.return_time IS NULL)
           OR (p_is_returned = 1 AND br.return_time IS NOT NULL))
    ORDER BY br.borrowing_time DESC;
END$$
DELIMITER ;

-- SP6: 查詢可借閱書籍
DELIMITER $$
CREATE PROCEDURE sp_get_available_books(
    IN p_keyword VARCHAR(255)  -- 搜尋關鍵字 (書名或作者)
)
BEGIN
    SELECT
        b.isbn,
        b.name,
        b.author,
        b.introduction,
        b.publisher,
        b.publish_date,
        b.category,
        COUNT(i.inventory_id) AS available_count
    FROM book b
    INNER JOIN inventory i ON b.isbn = i.isbn
    WHERE i.status = 'AVAILABLE'
      AND (p_keyword IS NULL
           OR b.name LIKE CONCAT('%', p_keyword, '%')
           OR b.author LIKE CONCAT('%', p_keyword, '%'))
    GROUP BY b.isbn, b.name, b.author, b.introduction, b.publisher, b.publish_date, b.category
    HAVING available_count > 0
    ORDER BY b.name;
END$$
DELIMITER ;

-- SP7: 查詢特定書籍的可借閱庫存
DELIMITER $$
CREATE PROCEDURE sp_get_available_inventory_by_isbn(
    IN p_isbn VARCHAR(17)
)
BEGIN
    SELECT
        i.inventory_id,
        i.isbn,
        i.store_time,
        i.status,
        i.location,
        b.name AS book_name,
        b.author
    FROM inventory i
    INNER JOIN book b ON i.isbn = b.isbn
    WHERE i.isbn = p_isbn AND i.status = 'AVAILABLE'
    ORDER BY i.store_time;
END$$
DELIMITER ;
