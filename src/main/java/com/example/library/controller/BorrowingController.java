package com.example.library.controller;

import com.example.library.dto.*;
import com.example.library.service.BorrowingService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 借還書控制器
 */
@RestController
@RequestMapping("/borrowing")
public class BorrowingController {

    private final BorrowingService borrowingService;

    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    /**
     * 借書
     */
    @PostMapping("/borrow")
    public ApiResponse<BorrowResponse> borrowBook(
            Authentication authentication,
            @Valid @RequestBody BorrowRequest request) {
        
        Long userId = (Long) authentication.getPrincipal();
        BorrowResponse response = borrowingService.borrowBook(userId, request);
        return ApiResponse.success(response);
    }

    /**
     * 還書
     */
    @PostMapping("/return")
    public ApiResponse<String> returnBook(
            Authentication authentication,
            @Valid @RequestBody ReturnRequest request) {
        
        Long userId = (Long) authentication.getPrincipal();
        String message = borrowingService.returnBook(userId, request.getRecordId());
        return ApiResponse.success(message, null);
    }

    /**
     * 查詢使用者借閱紀錄
     */
    @GetMapping("/records")
    public ApiResponse<List<BorrowingRecordDTO>> getUserBorrowingRecords(
            Authentication authentication,
            @RequestParam(required = false) Integer isReturned) {
        
        Long userId = (Long) authentication.getPrincipal();
        List<BorrowingRecordDTO> records = borrowingService.getUserBorrowingRecords(userId, isReturned);
        return ApiResponse.success(records);
    }

    /**
     * 查詢使用者當前借閱中的書籍
     */
    @GetMapping("/current")
    public ApiResponse<List<BorrowingRecordDTO>> getCurrentBorrowings(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<BorrowingRecordDTO> records = borrowingService.getUserBorrowingRecords(userId, 0);
        return ApiResponse.success(records);
    }

    /**
     * 查詢使用者歷史借閱紀錄
     */
    @GetMapping("/history")
    public ApiResponse<List<BorrowingRecordDTO>> getBorrowingHistory(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<BorrowingRecordDTO> records = borrowingService.getUserBorrowingRecords(userId, 1);
        return ApiResponse.success(records);
    }
}
