package com.example.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowResponse {
    private Long recordId;
    private Long inventoryId;
    private String bookName;
    private String isbn;
    private LocalDateTime borrowingTime;
    private LocalDateTime dueDate;
    private String message;
}
