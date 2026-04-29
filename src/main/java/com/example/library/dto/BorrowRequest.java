package com.example.library.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRequest {
    @NotNull(message = "庫存ID不能為空")
    private Long inventoryId;

    private Integer dueDays = 14; // 預設借閱14天
}
