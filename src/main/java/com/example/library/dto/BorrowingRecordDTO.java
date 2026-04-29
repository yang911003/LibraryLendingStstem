package com.example.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingRecordDTO {
    private Long recordId;
    private Long userId;
    private Long inventoryId;
    private String isbn;
    private String bookName;
    private String author;
    private LocalDateTime borrowingTime;
    private LocalDateTime dueDate;
    private LocalDateTime returnTime;
    private Boolean isOverdue;
    private Integer overdueDays;
    private BigDecimal fineAmount;
    private String inventoryStatus;
}
