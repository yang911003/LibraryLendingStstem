package com.example.library.repository;

import com.example.library.dto.BookDTO;
import com.example.library.dto.BorrowingRecordDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BorrowingRepository {

    private final EntityManager entityManager;

    public BorrowingRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * 轉換時間類型
     */
    private LocalDateTime convertToLocalDateTime(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Timestamp) {
            return ((Timestamp) obj).toLocalDateTime();
        } else if (obj instanceof LocalDateTime) {
            return (LocalDateTime) obj;
        }
        return null;
    }

    /**
     * 借書 (呼叫 Stored Procedure)
     */
    public BorrowResult borrowBook(Long userId, Long inventoryId, Integer dueDays) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_borrow_book");

        query.registerStoredProcedureParameter("p_user_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_inventory_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_due_days", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_record_id", Long.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("p_result_code", Integer.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("p_result_message", String.class, ParameterMode.OUT);

        query.setParameter("p_user_id", userId);
        query.setParameter("p_inventory_id", inventoryId);
        query.setParameter("p_due_days", dueDays);

        query.execute();

        Long recordId = (Long) query.getOutputParameterValue("p_record_id");
        Integer resultCode = (Integer) query.getOutputParameterValue("p_result_code");
        String resultMessage = (String) query.getOutputParameterValue("p_result_message");

        return new BorrowResult(resultCode, resultMessage, recordId);
    }

    /**
     * 還書 (呼叫 Stored Procedure)
     */
    public ReturnResult returnBook(Long recordId) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_return_book");

        query.registerStoredProcedureParameter("p_record_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_result_code", Integer.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("p_result_message", String.class, ParameterMode.OUT);

        query.setParameter("p_record_id", recordId);

        query.execute();

        Integer resultCode = (Integer) query.getOutputParameterValue("p_result_code");
        String resultMessage = (String) query.getOutputParameterValue("p_result_message");

        return new ReturnResult(resultCode, resultMessage);
    }

    /**
     * 查詢使用者借閱紀錄 (呼叫 Stored Procedure)
     */
    @SuppressWarnings("unchecked")
    public List<BorrowingRecordDTO> getUserBorrowingRecords(Long userId, Integer isReturned) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_get_user_borrowing_records");

        query.registerStoredProcedureParameter("p_user_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_is_returned", Integer.class, ParameterMode.IN);

        query.setParameter("p_user_id", userId);
        query.setParameter("p_is_returned", isReturned);

        query.execute();

        List<Object[]> results = query.getResultList();
        List<BorrowingRecordDTO> records = new ArrayList<>();

        for (Object[] row : results) {
            BorrowingRecordDTO dto = new BorrowingRecordDTO();
            dto.setRecordId(((Number) row[0]).longValue());
            dto.setUserId(((Number) row[1]).longValue());
            dto.setInventoryId(((Number) row[2]).longValue());
            dto.setIsbn((String) row[3]);
            dto.setBookName((String) row[4]);
            dto.setAuthor((String) row[5]);
            dto.setBorrowingTime(convertToLocalDateTime(row[6]));
            dto.setDueDate(convertToLocalDateTime(row[7]));
            dto.setReturnTime(convertToLocalDateTime(row[8]));

            // 處理 Boolean/Number 類型
            if (row[9] instanceof Boolean) {
                dto.setIsOverdue((Boolean) row[9]);
            } else if (row[9] instanceof Number) {
                dto.setIsOverdue(((Number) row[9]).intValue() == 1);
            }

            dto.setOverdueDays(((Number) row[10]).intValue());
            dto.setFineAmount((BigDecimal) row[11]);
            dto.setInventoryStatus((String) row[12]);
            records.add(dto);
        }

        return records;
    }

    /**
     * 查詢可借閱書籍 (呼叫 Stored Procedure)
     */
    @SuppressWarnings("unchecked")
    public List<BookDTO> getAvailableBooks(String keyword) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_get_available_books");

        query.registerStoredProcedureParameter("p_keyword", String.class, ParameterMode.IN);
        query.setParameter("p_keyword", keyword);

        query.execute();

        List<Object[]> results = query.getResultList();
        List<BookDTO> books = new ArrayList<>();

        for (Object[] row : results) {
            BookDTO dto = new BookDTO();
            dto.setIsbn((String) row[0]);
            dto.setName((String) row[1]);
            dto.setAuthor((String) row[2]);
            dto.setIntroduction((String) row[3]);
            dto.setPublisher((String) row[4]);

            // 處理日期轉換
            if (row[5] != null) {
                if (row[5] instanceof java.sql.Date) {
                    dto.setPublishDate(((java.sql.Date) row[5]).toLocalDate());
                } else if (row[5] instanceof LocalDate) {
                    dto.setPublishDate((LocalDate) row[5]);
                }
            }

            dto.setCategory((String) row[6]);
            dto.setAvailableCount(((Number) row[7]).intValue());
            books.add(dto);
        }

        return books;
    }

    /**
     * 借書結果
     */
    public static class BorrowResult {
        public final Integer code;
        public final String message;
        public final Long recordId;

        public BorrowResult(Integer code, String message, Long recordId) {
            this.code = code;
            this.message = message;
            this.recordId = recordId;
        }
    }

    /**
     * 還書結果
     */
    public static class ReturnResult {
        public final Integer code;
        public final String message;

        public ReturnResult(Integer code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
