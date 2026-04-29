package com.example.library.service;

import com.example.library.dto.BookDTO;
import com.example.library.dto.BorrowRequest;
import com.example.library.dto.BorrowResponse;
import com.example.library.dto.BorrowingRecordDTO;
import com.example.library.exception.BusinessException;
import com.example.library.repository.BorrowingRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowingService {

    private final BorrowingRepository borrowingRepository;
    private final EntityManager entityManager;

    public BorrowingService(BorrowingRepository borrowingRepository, EntityManager entityManager) {
        this.borrowingRepository = borrowingRepository;
        this.entityManager = entityManager;
    }

    /**
     * 轉換時間類型
     */
    private LocalDateTime convertToLocalDateTime(Object obj) {
        if (obj == null) return null;
        if (obj instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) obj).toLocalDateTime();
        } else if (obj instanceof LocalDateTime) {
            return (LocalDateTime) obj;
        }
        return null;
    }

    /**
     * 借書
     * 使用 @Transactional 確保交易完整性
     */
    @Transactional
    public BorrowResponse borrowBook(Long userId, BorrowRequest request) {
        // 呼叫 Stored Procedure (內部已包含 Transaction 處理)
        BorrowingRepository.BorrowResult result = 
            borrowingRepository.borrowBook(userId, request.getInventoryId(), request.getDueDays());

        if (result.code != 0) {
            throw new BusinessException(result.code, result.message);
        }

        // 查詢借閱詳情
        String sql = "SELECT br.record_id, br.inventory_id, b.name, b.isbn, br.borrowing_time, br.due_date " +
                     "FROM borrowing_record br " +
                     "INNER JOIN inventory i ON br.inventory_id = i.inventory_id " +
                     "INNER JOIN book b ON i.isbn = b.isbn " +
                     "WHERE br.record_id = :recordId";
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("recordId", result.recordId);
        Object[] row = (Object[]) query.getSingleResult();

        BorrowResponse response = new BorrowResponse();
        response.setRecordId(((Number) row[0]).longValue());
        response.setInventoryId(((Number) row[1]).longValue());
        response.setBookName((String) row[2]);
        response.setIsbn((String) row[3]);
        response.setBorrowingTime(convertToLocalDateTime(row[4]));
        response.setDueDate(convertToLocalDateTime(row[5]));
        response.setMessage(result.message);

        return response;
    }

    /**
     * 還書
     * 使用 @Transactional 確保交易完整性
     */

    @Transactional
    public String returnBook(Long userId, Long recordId) {
        // 驗證是否為本人的借閱紀錄
        String checkSql = "SELECT COUNT(*) FROM borrowing_record WHERE record_id = :recordId AND user_id = :userId";
        Query checkQuery = entityManager.createNativeQuery(checkSql);
        checkQuery.setParameter("recordId", recordId);
        checkQuery.setParameter("userId", userId);
        
        Number count = (Number) checkQuery.getSingleResult();
        if (count.intValue() == 0) {
            throw new BusinessException(4, "無權限操作此借閱紀錄");
        }

        // 查詢書籍資訊
        String bookSql = "SELECT b.isbn, b.name FROM borrowing_record br " +
                        "INNER JOIN inventory i ON br.inventory_id = i.inventory_id " +
                        "INNER JOIN book b ON i.isbn = b.isbn WHERE br.record_id = :recordId";
        Query bookQuery = entityManager.createNativeQuery(bookSql);
        bookQuery.setParameter("recordId", recordId);
        Object[] bookInfo = (Object[]) bookQuery.getSingleResult();
        String isbn = (String) bookInfo[0];
        String bookName = (String) bookInfo[1];

        // 呼叫 Stored Procedure (內部已包含 Transaction 處理)
        BorrowingRepository.ReturnResult result = borrowingRepository.returnBook(recordId);

        if (result.code != 0) {
            throw new BusinessException(result.code, result.message);
        }

        return result.message;
    }

    /**
     * 查詢使用者借閱紀錄
     */
    @Transactional
    public List<BorrowingRecordDTO> getUserBorrowingRecords(Long userId, Integer isReturned) {
        return borrowingRepository.getUserBorrowingRecords(userId, isReturned);
    }

    /**
     * 查詢可借閱書籍
     */
    @Transactional
    public List<BookDTO> getAvailableBooks(String keyword) {
        return borrowingRepository.getAvailableBooks(keyword);
    }

    /**
     * 查詢特定書籍的可借閱庫存
     */
    @Transactional
    public List<Object[]> getAvailableInventoryByIsbn(String isbn) {
        String sql = "SELECT i.inventory_id, i.isbn, i.store_time, i.status, i.location, b.name, b.author " +
                     "FROM inventory i " +
                     "INNER JOIN book b ON i.isbn = b.isbn " +
                     "WHERE i.isbn = :isbn AND i.status = 'AVAILABLE' " +
                     "ORDER BY i.store_time";
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("isbn", isbn);
        
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        return results;
    }
}
