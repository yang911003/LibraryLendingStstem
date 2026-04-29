package com.example.library.controller;

import com.example.library.dto.ApiResponse;
import com.example.library.dto.BookDTO;
import com.example.library.service.BorrowingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 書籍控制器
 */
@RestController
@RequestMapping("/books")
public class BookController {

    private final BorrowingService borrowingService;

    public BookController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    /**
     * 查詢可借閱書籍
     */
    @GetMapping("/available")
    public ApiResponse<List<BookDTO>> getAvailableBooks(
            @RequestParam(required = false) String keyword) {
        List<BookDTO> books = borrowingService.getAvailableBooks(keyword);
        return ApiResponse.success(books);
    }

    /**
     * 查詢特定書籍的可借閱庫存
     */
    @GetMapping("/{isbn}/inventory")
    public ApiResponse<List<Object[]>> getAvailableInventory(@PathVariable String isbn) {
        List<Object[]> inventory = borrowingService.getAvailableInventoryByIsbn(isbn);
        return ApiResponse.success(inventory);
    }
}
